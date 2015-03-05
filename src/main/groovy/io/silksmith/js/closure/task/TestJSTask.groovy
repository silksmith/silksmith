package io.silksmith.js.closure.task

import com.sun.nio.file.SensitivityWatchEventModifier
import io.silksmith.SourceLookupService
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceSet
import org.eclipse.jetty.server.Handler
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import java.nio.file.*

class TestJSTask extends DefaultTask {

	def jsExecution = """return (function(rootSuite){

  function addStat(suite){
    var testResults = suite.tests.map(function(t){
      return {
        pending: t.pending,
        timedOut: t.timedOut,
        title: t.title,
        type: t.type,
		state : t.state
      };
    });

    return {
      title: suite.title,
      pending : suite.pending,
      root: suite.root,
      tests : testResults,
      suites : suite.suites.map(addStat.bind(this))
    };
  }
  return addStat(rootSuite);
}(mocha.suite));
"""

	SourceLookupService sourceLookupService


	WebSourceSet testSourceSet



	@Lazy
	WorkspaceServer server  = {

		Configuration configuration = project.configurations[testSourceSet.configurationName]
		[
			project:project,
			sourceSet: testSourceSet,
			configuration:configuration,

			sourceLookupService:sourceLookupService
		]
	}()


	def handler(Handler handler) {
		def s = server
		server.handler(handler)
	}
	@TaskAction
	def test() {

        def drivers = []
        def ok = false

        try {
            boolean watch = project.hasProperty('watch')
            boolean firefox = project.hasProperty('firefox')
            boolean chrome = project.hasProperty('chrome')

            server.start()

            if (!firefox && !chrome) {
                firefox = true;
            }

            if (firefox) {
                drivers << new FirefoxDriver()
            }
            if (chrome) {
                String chromeDriverUrl = project.hasProperty('chromeDriverUrl') ? project.property("chromeDriverUrl") : null
                String chromeDriverExe = project.hasProperty('chromeDriverExe') ? project.property("chromeDriverExe") : null

                if (chromeDriverUrl) {
                    // use existing chrome driver server
                    drivers << new RemoteWebDriver(new URL(chromeDriverUrl), DesiredCapabilities.chrome());
                } else if (chromeDriverExe) {
                    // auto start chrome driver server
                    def driverService = new ChromeDriverService.Builder().usingAnyFreePort().usingDriverExecutable(new File(chromeDriverExe)).build()
                    drivers << new ChromeDriver(driverService);
                } else if (System.hasProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY)) {
                    // auto start chrome driver server with exe set in system properties
                    drivers << new ChromeDriver()
                } else {
                    logger.warn("No chrome driver executable found. Download it and set its location via '-PchromeDriverExe=/Applications/chromedriver' or pass the URL of a running driver server via '-PchromeDriverUrl=http://localhost:9515'")
                    logger.warn("Download URL for the Chrome driver executable: http://chromedriver.storage.googleapis.com")
                }
            }

            if (drivers.empty) {
                logger.error("No drivers available. Specify one or more by adding '-Pfirefox' or '-Pchrome'")
                return;
            }

            drivers.each {it.get("${server.server.URI}TEST/MOCHA")}

            if (watch) {
                def keysAndPath = [:]

                WatchService watchService = FileSystems.getDefault().newWatchService()
                testSourceSet.js.srcDirs.collect({ File srcDirFile ->

                    if (srcDirFile.isDirectory()) {
                        def dirs = [srcDirFile]
                        srcDirFile.eachDirRecurse dirs.&add
                        return dirs
                    }
                }).grep().flatten().collect({
                    Paths.get(it.toURI())
                }).each({ Path srcDirPath ->

                    srcDirPath.register(
                            watchService,
                            [
                                    StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_DELETE,
                                    StandardWatchEventKinds.ENTRY_CREATE
                            ] as WatchEvent.Kind[], SensitivityWatchEventModifier.HIGH
                    )
                })

                def th = Thread.start {
                    while (true) {
                        logger.lifecycle("Watching")
                        WatchKey key = watchService.take()

                        logger.lifecycle("Files changed")

                        boolean refresh = false;

                        //Poll all the events queued for the key
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind kind = event.kind()
                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }
                            refresh = true;
                        }

                        if (refresh) {
                            logger.lifecycle("Refreshing")
                            drivers.each {it.navigate().refresh()}
                            logger.lifecycle("Refreshed")
                        }

                        //reset is invoked to put the key back to ready state
                        boolean valid = key.reset()
                        //If the key is invalid, just exit.
                        if (!valid) {
                            logger.warn("$key is invalid, ending watch")
                            break
                        }

                        sleep(200);
                    }
                }
                th.join()

            } else {
                ok = drivers.collect({ executeTestInBrowser((WebDriver) it) }).every({ it })
            }

        } catch (Exception e) {
            logger.error("An error occured while executing tests", e)
        } finally {
            drivers.each {it.quit()}
            server.stop()
        }

        if (!ok) {
            throw new GradleException("Some tests did not pass")
        }
	}

	def executeTestInBrowser(WebDriver driver) {
		def condition = { WebDriver d ->
			JavascriptExecutor jsExec = d as JavascriptExecutor

			def result = jsExec.executeScript(jsExecution)

			def suiteComplete

			suiteComplete = { suite ->


				def allTestsComplete = suite.tests.every({
					def complete = it.timedOut!=null || it.state !=null || it.pending !=null

					return complete
				})
				return allTestsComplete && suite.suites.every(suiteComplete)
			}

			return result.suites.every(suiteComplete)
		} as ExpectedCondition<Boolean>
		//println ((JavascriptExecutor)driver).executeScript()
		(new WebDriverWait(driver, 1000)).until(condition)



		JavascriptExecutor jsExec = driver as JavascriptExecutor

		def result = jsExec.executeScript(jsExecution)

		def suiteWalker

		boolean ok = true
		suiteWalker = { suite ->


			def testsOk = suite.tests.each({

				logger.debug "Checking $suite.title / it.title"
				if(it.pending) {
					logger.warn "Test '$it.title' is pending"
				}
				if(it.state == "failed") {
					logger.error "Test '$it.title' failed!"
				}
				if(it.timedOut) {
					logger.error "Test '$it.title' timeout!"
				}
				def testOk = it.state == "passed" || it.pending

				logger.debug "Result OK: $testOk"

				ok = ok && testOk
			})
			suite.suites.each(suiteWalker)
		}

		result.suites.each(suiteWalker)
		return ok
	}
}

