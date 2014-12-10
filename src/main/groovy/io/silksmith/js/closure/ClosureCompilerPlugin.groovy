package io.silksmith.js.closure

import io.silksmith.development.server.closure.ClosureJSDevelopmentHandler
import io.silksmith.development.server.closure.DepsJSHandler
import io.silksmith.development.server.js.test.MochaHandler
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.js.closure.task.TestJSTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator


class ClosureCompilerPlugin implements Plugin<Project>{
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


		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)
		def webSourceLookupService = project.plugins.getPlugin(SilkSmithBasePlugin).sourceLookupService
		WebSourceSet mainSourceSet = ext.source.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
		WebSourceSet testWebSourceSet = ext.source.getByName(SourceSet.TEST_SOURCE_SET_NAME)
		Configuration mainConfig = project.configurations.getByName(mainSourceSet.configurationName)
		Configuration testConfig = project.configurations.getByName(testWebSourceSet.configurationName)


		def mainCompileTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(mainSourceSet, ClOSURE_COMPILE_JS_BASE_NAME)
		project.task(mainCompileTaskName,type: ClosureCompileTask){
			source  mainSourceSet.dependencyJSPath //TODO: use runtime path?
			mainSourceSet.js.srcDirs.each { source it }

			dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(mainSourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
			dest = "$project.buildDir/compiled/$mainSourceSet.configurationName/${project.name}.js"
		}

		project.task("testJS", type: TestJSTask){

			dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(testWebSourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
			testSourceSet = testWebSourceSet
			sourceLookupService = webSourceLookupService

			handler(new MochaHandler([project:project, testSourceSet:testWebSourceSet, sourceLookupService:webSourceLookupService]))
			handler(new DepsJSHandler([project:project, configuration:testConfig]))
		}

		project.tasks.withType(WorkspaceServerTask){

			handler(new DepsJSHandler([project:project, configuration:mainConfig]))

			def closureCompileTask = project.tasks.getByName(mainCompileTaskName)
			handler(new ClosureJSDevelopmentHandler([project:project, configuration:mainConfig, sourceLookupService:webSourceLookupService, jsOutput:closureCompileTask]))
		}
	}
}
