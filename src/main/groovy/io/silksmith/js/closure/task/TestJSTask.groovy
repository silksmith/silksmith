package io.silksmith.js.closure.task





import io.silksmith.SilkSmithExtension
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceSet

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import com.sun.nio.file.SensitivityWatchEventModifier

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction


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

	boolean watch = false
	@TaskAction
	def test() {

		WebSourceSet sourceSet = project.extensions.getByType(SilkSmithExtension).source["test"]

		Configuration configuration = project.configurations["testWeb"]

		configuration.each { println "TEST $it" }

		def server = new WorkspaceServer([project:project, sourceSet: sourceSet, configuration:configuration, resourceBase : project.projectDir])

		server.start()


		WebDriver driver = new FirefoxDriver()


		driver.get("$server.server.URI/TEST/MOCHA")

		if(watch) {

			def keysAndPath = [:]



			WatchService watchService = FileSystems.getDefault().newWatchService()
			sourceSet.js.srcDirs.collect({ File srcDirFile ->

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
			driver.quit()
		}else {
			def ok = executeTestInBrowser(driver)
			driver.quit()
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

