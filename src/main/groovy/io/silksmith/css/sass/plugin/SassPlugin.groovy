package io.silksmith.css.sass.plugin

import io.silksmith.css.sass.task.GemInstallTask
import io.silksmith.css.sass.task.ScssCompile
import io.silksmith.plugin.SilkSmithExtension;
import io.silksmith.source.WebSourceSet

import org.apache.commons.lang3.StringUtils

import org.gradle.api.Plugin
import org.gradle.api.Project

class SassPlugin implements Plugin<Project>{

	@Override
	public void apply(Project project) {

		project.configurations { jruby }

		project.dependencies { jruby 'org.jruby:jruby-complete:1.7.16.1' }


		GemInstallTask installRubTask = project.task("installSass", type:GemInstallTask){
			jrubyConfig = project.configurations.jruby
		}
		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)
		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName

			def config = project.configurations.findByName(sourceSetConfigurationName)


			project.task(StringUtils.uncapitalize("${taskBaseName}SassCompile"),type: ScssCompile){
				jrubyConfig = project.configurations.jruby
				gemInstallDir = project.file("$project.buildDir/gem")
				input = sourceSet.scss.srcDirs.unique().sort().first() //TODO: fix call to work with input dirs
				output = project.file("$project.buildDir/css/$sourceSetConfigurationName")
				configuration = config
				dependsOn installRubTask
				onlyIf {  input  }
			}
		}
	}
}
