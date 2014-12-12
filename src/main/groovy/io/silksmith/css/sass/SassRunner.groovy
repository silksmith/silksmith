package io.silksmith.css.sass

import io.silksmith.SourceLookupService
import io.silksmith.development.server.css.CSSOutput
import io.silksmith.source.WebSourceElements

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

	File inputDir

	File gemInstallDir

	def run(SassMode mode = SassMode.update) {
		//-I
		def importPathsArgs = []
		outputDir.parentFile.mkdirs()
		importPathsArgs = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			sourceLookupService.get(rcr.id)

		}).collect({WebSourceElements webSourceElements ->

			webSourceElements.scssDirs

		}).collect({Set<File> set ->
			set.collect({
				["-I", it.path]
			})
		}).grep().flatten()


		def sassArgs = [
			"-S",
			"sass",
			"--$mode",
			"--scss",
		]
		sassArgs += importPathsArgs

		sassArgs << "$inputDir:$outputDir"

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
