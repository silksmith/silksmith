package io.silksmith.css.sass.task

import io.silksmith.content.WebPackContent
import io.silksmith.content.WebPackContentResolveService

import org.gradle.process.ExecResult

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction


class ScssCompile extends DefaultTask{


	Configuration jrubyConfig

	Configuration configuration

	@InputDirectory
	def gemInstallDir
	@Optional
	@InputDirectory
	def input

	@OutputDirectory
	def output

	WebPackContentResolveService webPackContentResolveService = new WebPackContentResolveService([project:project])
	@TaskAction
	def compile() {


		//-I
		def importPathsArgs = []
		importPathsArgs = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			webPackContentResolveService.from(rcr.id)

		}).collect({WebPackContent wpc ->

			wpc.scssDirectory
		}).collect({Set<File> set ->
			set.collect({
				["-I", it.path]
			})
		}).grep().flatten()


		def sassArgs = [
			"-S",
			"sass",
			"--update",
			"--scss",
		]
		sassArgs += importPathsArgs

		sassArgs << "$input:$output"
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
}
