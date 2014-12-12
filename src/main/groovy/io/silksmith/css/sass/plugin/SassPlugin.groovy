package io.silksmith.css.sass.plugin

import io.silksmith.css.sass.task.GemInstallTask
import io.silksmith.css.sass.task.ScssCompile
import io.silksmith.development.server.css.SassCSSHandler
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import org.gradle.api.Plugin
import org.gradle.api.Project

class SassPlugin implements Plugin<Project>{

	@Override
	public void apply(Project project) {

		project.configurations { jruby }

		project.dependencies { jruby 'org.jruby:jruby-complete:1.7.16.1' }



		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)

		def lookupService  = project.plugins.getPlugin(SilkSmithBasePlugin).sourceLookupService

		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName
			def config = project.configurations.findByName(sourceSetConfigurationName)


			if(sourceSet.scss.srcDirs.any({ File f ->f.exists()})) {

				def installSassGemTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet,"SassGems")
				GemInstallTask installGemTask = project.task(installSassGemTaskName, type:GemInstallTask){
					gem "sass"
					jrubyConfig = project.configurations.jruby
					gemInstallDir = project.file("$project.projectDir/.gems/$name")
				}

				ScssCompile compileTask = project.task(SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet,"CompileScss"),type: ScssCompile){
					jrubyConfig = project.configurations.jruby
					gemInstallDir = installGemTask.gemInstallDir
					println "TODO: sass input dir takes only first src dir"
					inputDir = sourceSet.scss.srcDirs.unique().sort().first() //TODO: fix call to work with input dirs

					sourceLookupService = lookupService
					outputDir = project.file("$project.buildDir/css/$sourceSetConfigurationName")
					configuration = config
					dependsOn installGemTask
				}
				project.tasks.withType(WorkspaceServerTask){


					dependsOn installGemTask

					SassCSSHandler cssHandler = new SassCSSHandler([

						sassRunnerProvider : compileTask,
						configuration:config,
						sourceLookupService:lookupService
					])

					handler cssHandler
				}
			}
		}
	}
}
