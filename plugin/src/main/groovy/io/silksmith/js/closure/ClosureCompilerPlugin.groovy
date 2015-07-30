package io.silksmith.js.closure

import io.silksmith.ComponentUtil
import io.silksmith.SilkModuleCacheUtil
import io.silksmith.bundling.task.SilkArchive
import io.silksmith.development.ide.idea.libraries.IdeaJSLibrariesTask
import io.silksmith.development.server.closure.ClosureJSDevelopmentHandler
import io.silksmith.development.server.closure.DepsJSHandler
import io.silksmith.development.server.js.test.MochaHandler
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.js.closure.task.RefasterJSTask
import io.silksmith.js.closure.task.TestJSTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.apache.commons.lang3.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator


class ClosureCompilerPlugin implements Plugin<Project>{
	private Instantiator instantiator
	private FileResolver fileResolver

	public static final ClOSURE_COMPILE_JS_BASE_NAME = "ClosureCompileJS"
	public static final ASSEMBLE_JS_BASE_NAME = "AssembleJS"

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


		project.task("ideaExterns", type:IdeaJSLibrariesTask){ configuration = mainConfig }

		def mainCompileTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(mainSourceSet, ClOSURE_COMPILE_JS_BASE_NAME)
		ClosureCompileTask mainCompileTask = project.task(mainCompileTaskName,type: ClosureCompileTask){
			source  mainSourceSet.dependencyJSPath //TODO: use runtime path?
			mainSourceSet.js.srcDirs.each { source it }

			externs mainSourceSet.dependencyExternsPath
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

		def mainAssembleJSTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(mainSourceSet, ASSEMBLE_JS_BASE_NAME)
		def assembleOutputDir = project.file("$project.buildDir/assembledJS/$mainSourceSet.name")
		def assembleJSTask = project.task(mainAssembleJSTaskName)<< {
			assembleOutputDir.mkdirs()

			def outputFile = project.file("$assembleOutputDir/$mainCompileTask.dest.name")
			outputFile.withWriter { Writer writer ->

				ComponentUtil.getOrdered(mainConfig).grep({ComponentIdentifier ci ->
					if(ci instanceof ModuleComponentIdentifier) {
						//TODO: remove hardcoded, maybe add devConfig that is removed on assemble
						return ci.module != "closure-base"
					}
					true
				}).collect( {
					webSourceLookupService.get(it)

				}).each( { WebSourceElements wse ->
					wse.statics.files.each { File f ->
						if(f.path.endsWith(".js")) {
							f.withReader { Reader reader ->
								writer << reader << '\n'
							}
						}
					}

				})
				mainCompileTask.dest.withReader{ Reader reader ->
					writer << '\n//APP\n' << reader << '\n'
				}
			}
		}
		assembleJSTask.inputs.files mainSourceSet.dependencyStaticsPath
		assembleJSTask.dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(mainSourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
		assembleJSTask.inputs.files mainCompileTask

		assembleJSTask.outputs.dir assembleOutputDir

		def refasterjsBaseDir = project.file("refasterjs")


		if(refasterjsBaseDir.exists()) {
			def refasterAllTask = project.task("refasterAll"){

			}
			project.fileTree(refasterjsBaseDir).forEach {File file ->

				def excludeTests = project.hasProperty('excludeTests')
				def refactorName = file.path - "$refasterjsBaseDir.path/" - ".js"
				refactorName = refactorName.replace("/","_")
				//TODO: should we include the test js sources as well?
				def refasterTask = project.task("refaster${StringUtils.capitalize(refactorName)}", type: RefasterJSTask){


					source  mainSourceSet.js
					if(excludeTests) {
						externs = mainSourceSet.dependencyExternsPath + mainSourceSet.externs.asFileTree
					}else {
						externs = mainSourceSet.dependencyExternsPath + testWebSourceSet.dependencyExternsPath + mainSourceSet.externs.asFileTree
						source testWebSourceSet.js
					}
					
					dryRun = project.hasProperty('dryRun')
					refasterJsTemplate = file

					project.afterEvaluate({
						Dependency closureBaseDep = mainConfig.dependencies.find( { it.name == 'closure-base'})
						def closureBaseJSFile = project.fileTree(SilkModuleCacheUtil.pathInCache(closureBaseDep.group, closureBaseDep.name, closureBaseDep.version,SilkArchive.STATICS_DIR)).singleFile

						baseJS = closureBaseJSFile
						
						externs.each {
							println it
						}
					})




				}
				refasterAllTask.dependsOn refasterTask
			}
		}

	}
}
