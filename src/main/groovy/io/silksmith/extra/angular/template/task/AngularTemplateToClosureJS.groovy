package io.silksmith.extra.angular.template.task

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class AngularTemplateToClosureJS extends DefaultTask{


	def srcDirs
	@OutputDirectory
	def dest

	@TaskAction
	def compile() {

		def compiler = new NGTemplateToClosureJSCompiler()
		compiler.compile(project, srcDirs, dest)


		//
		//compiler.compile(project,
	}
}
