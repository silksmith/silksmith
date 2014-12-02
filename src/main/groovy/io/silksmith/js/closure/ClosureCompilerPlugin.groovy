package io.silksmith.js.closure

import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.apache.commons.lang3.StringUtils
import org.gradle.internal.reflect.Instantiator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver


class ClosureCompilerPlugin implements Plugin<Project>{
	private Instantiator instantiator
	private FileResolver fileResolver
	@Inject
	public ClosureCompilerPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}
	@Override
	public void apply(Project project) {

		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)
		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName

			def config = project.configurations.findByName(sourceSetConfigurationName)


			project.task(StringUtils.uncapitalize("${taskBaseName}ClosureCompileJS"),type: ClosureCompileTask){
				source  sourceSet.dependencyJSPath
				sourceSet.js.srcDirs.each { source it }

				dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
				dest = "$project.buildDir/compiled/$sourceSetConfigurationName/${project.name}.js"
			}
		}
	}
}
