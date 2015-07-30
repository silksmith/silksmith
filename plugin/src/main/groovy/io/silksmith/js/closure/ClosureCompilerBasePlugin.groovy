package io.silksmith.js.closure

import io.silksmith.development.server.closure.ClosureJSDevelopmentHandler
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator


class ClosureCompilerBasePlugin implements Plugin<Project>{
	private Instantiator instantiator
	private FileResolver fileResolver

	public static final ClOSURE_COMPILE_JS_BASE_NAME = "ClosureCompileJS"

	@Inject
	public ClosureCompilerPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}
	@Override
	public void apply(Project project) {

		//TODO: should apply SilkSmithBasePlugin?

		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)
		def sourceLookupService = project.plugins.getPlugin(SilkSmithBasePlugin).sourceLookupService
		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName

			def config = project.configurations.findByName(sourceSetConfigurationName)

			ClosureCompileTask closureCompileJS = project.task(SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet, ClOSURE_COMPILE_JS_BASE_NAME),type: ClosureCompileTask){
				source  sourceSet.dependencyJSPath
				sourceSet.js.srcDirs.each { source it }

				dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
				dest = "$project.buildDir/compiled/$sourceSetConfigurationName/${project.name}.js"
			}
			project.tasks.withType(WorkspaceServerTask){
				def closureJSHandler = new ClosureJSDevelopmentHandler([project:project, configuration:config, sourceLookupService:sourceLookupService, jsOutput:closureCompileJS])
				handler closureJSHandlers
			}
		}
	}
}
