package io.silksmith.js.closure.task





import io.silksmith.SourceLookupService
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceSet

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

import org.eclipse.jetty.server.Handler
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import com.sun.nio.file.SensitivityWatchEventModifier


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


		boolean watch = project.hasProperty('watch')
		server.start()
		WebDriver driver = new FirefoxDriver()

		driver.get("${server.server.URI}TEST/MOCHA")

		if(watch) {

			try {


				def keysAndPath = [:]

				WatchService watchService = FileSystems.getDefault().newWatchService()
				testSourceSet.js.srcDirs.collect({ File srcDirFile ->

					if(srcDirFile.isDirectory()) {
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

				while (true) {

					WatchKey key = watchService.take()

					logger.info("Files changed")
					//Poll all the events queued for the key
					for ( WatchEvent<?> event: key.pollEvents()){


						WatchEvent.Kind kind = event.kind()

						logger.debug("Refresshing ($kind)")
						driver.navigate().refresh()

					}
					//reset is invoked to put the key back to ready state
					boolean valid = key.reset()
					//If the key is invalid, just exit.
					if ( !valid ) {
						logger.warn("$key is invalid, ending watch")
						break
					}
				}
			}catch(Exception e) {
				logger.error("An error occured while watching", e)
			}finally {
				driver.quit()
				server.stop()
			}

		}else {
			def ok = executeTestInBrowser(driver)
			driver.quit()
			server.stop()
			if(!ok) {
				throw new GradleException("Some tasks did not pass")
			}
		}





	}

	private executeTestInBrowser(WebDriver driver) {
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

