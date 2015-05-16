package io.silksmith.css.sass

import io.silksmith.SourceLookupService
import io.silksmith.development.server.css.CSSOutput
import io.silksmith.source.ModuleWebSourceElements
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.process.ExecResult

class SassRunner implements CSSOutput {

	public static enum SassMode{
		watch, update
	}
	SourceLookupService sourceLookupService

	Project project

	Configuration jrubyConfig

	Configuration configuration

	File outputDir



	File gemInstallDir

	def run(SassMode mode = SassMode.update) {
		//-I
		def importPathsArgs = []

		def inOuts = []
		outputDir.parentFile.mkdirs()
		importPathsArgs = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			sourceLookupService.get(rcr.id)

		}).grep({it instanceof ModuleWebSourceElements}).collect({WebSourceElements webSourceElements ->

			webSourceElements.scssDirs

		}).collect({Set<File> set ->

			set.collect({
				if(!it.exists()) {
					return []
				}
				["-I", it.path]
			})
		}).grep().flatten()

		inOuts = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			sourceLookupService.get(rcr.id)

		}).grep({it instanceof WebSourceSet}).collect({WebSourceElements webSourceElements ->

			webSourceElements.scssDirs

		}).collect({Set<File> set ->

			set.collect({
				if(!it.exists()) {
					return []
				}
				"$it.path:$outputDir" })
		}).grep().flatten()


		def sassArgs = [
			"-S",
			"sass",
			"--$mode",
			"--scss",
		]
		sassArgs += importPathsArgs

		sassArgs += inOuts

		ExecResult result = project.javaexec({
			main = "org.jruby.Main"
			classpath = jrubyConfig

			args = sassArgs
			environment 'PATH', "$gemInstallDir/bin"
			environment 'GEM_PATH', gemInstallDir

			println "Executing ${commandLine.join(' ')}"
		})
		result.assertNormalExitValue()
	}
	@Override
	public File getOutput() {
		return outputDir
	}
}
