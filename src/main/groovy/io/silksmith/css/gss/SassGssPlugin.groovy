package io.silksmith.css.gss

import io.silksmith.css.sass.plugin.SassPlugin;
import io.silksmith.plugin.SilkSmithBasePlugin;
import io.silksmith.plugin.SilkSmithExtension;
import io.silksmith.plugin.SilkSmithPlugin;
import io.silksmith.source.WebSourceSet;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet;

class SassGssPlugin implements Plugin<Project> {

	public final static String GSS_BASE_TASK = "gss"
	@Override
	public void apply(Project project) {

		SilkSmithExtension silkSmithExtension = project.extensions.findByType(SilkSmithExtension)

		WebSourceSet wss = silkSmithExtension.source.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
		def confName = wss.configurationName
		def assembleCSSTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(wss,SassPlugin.ASSEMBLE_CSS_BASE_NAME)

		def gssTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(wss,SassGssPlugin.GSS_BASE_TASK)
		GSSTask gssTask = project.task(gssTaskName, type: GSSTask) {

			source project.files(project.tasks.getByName(assembleCSSTaskName))

			outputFile = project.file("$project.buildDir/gss/$confName/css/")
			renameFile = project.file("$project.buildDir/gss/$confName/js/renaming.js")
		}
	}
}
