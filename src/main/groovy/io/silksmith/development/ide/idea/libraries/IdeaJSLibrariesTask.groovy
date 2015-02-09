package io.silksmith.development.ide.idea.libraries

import groovy.xml.MarkupBuilder
import io.silksmith.SilkModuleCacheUtil

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.tasks.TaskAction

class IdeaJSLibrariesTask extends DefaultTask{

	Configuration configuration

	public static String output = ".idea/libraries"

	@TaskAction
	def generate() {

		configuration.incoming.resolutionResult.allComponents
				.grep({ResolvedComponentResult r-> r.id instanceof ModuleComponentIdentifier})
				.each {ResolvedComponentResult r->
					generate(r.id)
				}
	}
	def generate(ModuleComponentIdentifier cid) {

		def name = "$cid.group-$cid.module-$cid.version"
		def outputFile = new File("$project.projectDir/$output/${name}.xml")
		def file = new File(SilkModuleCacheUtil.externsPathInCache(cid))
		outputFile.parentFile.mkdirs()
		outputFile.withWriter { writer ->


			def xml = new MarkupBuilder(writer)

			xml.component(name: "libraryTable"){
				library(type:"javaScript", name:name){
					properties(){
						sourceFilesUrls(){
							item(url:"file://$file")
						}
					}
					CLASSES(){
						root(url:"file://$file")
					}
				}
			}
		}
	}
}
