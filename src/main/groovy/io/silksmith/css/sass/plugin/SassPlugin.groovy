package io.silksmith.css.sass.plugin

import io.silksmith.ComponentUtil
import io.silksmith.css.sass.task.GemInstallTask
import io.silksmith.css.sass.task.ScssCompile
import io.silksmith.development.server.css.SassCSSHandler
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import org.gradle.api.Plugin
import org.gradle.api.Project

class SassPlugin implements Plugin<Project>{

	public static final String COMPILE_SCSS_BASE_NAME = "CompileSCSS"
	public static final String SASS_GEMS_BASE_NAME = "SassGems"
	public static final String ASSEMBLE_CSS_BASE_NAME = "AssembleCSS"
	@Override
	public void apply(Project project) {

		project.configurations { jruby }

		project.dependencies { jruby 'org.jruby:jruby-complete:1.7.16.1' }



		SilkSmithExtension ext = project.extensions.findByType(SilkSmithExtension)

		def webSourceLookupService  = project.plugins.getPlugin(SilkSmithBasePlugin).sourceLookupService

		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName
			def config = project.configurations.findByName(sourceSetConfigurationName)


			if(sourceSet.scss.srcDirs.any({ File f ->f.exists()})) {

				def installSassGemTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet,SASS_GEMS_BASE_NAME)
				GemInstallTask installGemTask = project.task(installSassGemTaskName, type:GemInstallTask){
					gem "sass"
					jrubyConfig = project.configurations.jruby
					gemInstallDir = project.file("$project.projectDir/.gems/$name")
				}

				ScssCompile compileTask = project.task(SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet,COMPILE_SCSS_BASE_NAME),type: ScssCompile){
					jrubyConfig = project.configurations.jruby
					gemInstallDir = installGemTask.gemInstallDir
					println "TODO: sass input dir takes only first src dir"
					inputDir = sourceSet.scss.srcDirs.unique().sort().first() //TODO: fix call to work with input dirs

					sourceLookupService = webSourceLookupService
					outputDir = project.file("$project.buildDir/css/$sourceSetConfigurationName")
					configuration = config
					dependsOn installGemTask
					dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
				}

				project.tasks.withType(WorkspaceServerTask){
					dependsOn installGemTask

					SassCSSHandler cssHandler = new SassCSSHandler([
						sassRunnerProvider : compileTask,
						configuration:config,
						sourceLookupService:webSourceLookupService
					])

					handler cssHandler
				}


				def assembleCSSTaskName = SilkSmithBasePlugin.getSourceSetNamedTask(sourceSet,ASSEMBLE_CSS_BASE_NAME)
				def assembleCSSoutputDir = project.file("$project.buildDir/assembledCSS/$sourceSet.name")
				def assembleCSSTask = project.task(assembleCSSTaskName)<< {
					assembleCSSoutputDir.mkdirs()

					def cssFiles = project.fileTree(dir: compileTask.outputDir, include: "**/*.css").files
					def cssMainOutput = cssFiles.first()
					if(cssFiles.size()>1) {
						logger.warn("SCSS output dir has more than one file, concating css only in first, ($cssMainOutput)")
					}
					def outputFile = project.file("$assembleCSSoutputDir/$cssMainOutput.name")
					outputFile.withWriter { Writer writer ->

						ComponentUtil.getOrdered(compileTask.configuration).collect( { webSourceLookupService.get(it) }).each( { WebSourceElements wse ->
							wse.statics.files.each { File f ->
								if(f.path.endsWith(".css")) {
									f.withReader { Reader reader ->
										writer << reader << '\n'
									}
								}
							}
						})
						cssMainOutput.withReader{ Reader reader ->
							writer << reader << '\n'
						}
					}
				}
				assembleCSSTask.dependsOn compileTask
				assembleCSSTask.outputs.dir assembleCSSoutputDir
			}
		}
	}
}
