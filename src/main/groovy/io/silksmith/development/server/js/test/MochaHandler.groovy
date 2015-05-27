package io.silksmith.development.server.js.test

import groovy.json.JsonBuilder
import groovy.text.SimpleTemplateEngine
import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.development.server.files.FilePathBuilder
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

class MochaHandler extends AbstractHandler{

	def mochaVersion = "2.2.5"

	def mochaCssPath = "/META-INF/resources/webjars/mocha/${mochaVersion}/mocha.css"
	def mochaJSPath = "/META-INF/resources/webjars/mocha/${mochaVersion}/mocha.js"
	def mochaTemplatePath = "/templates/mocha.html"

	Project project

	SourceLookupService sourceLookupService

	WebSourceSet testSourceSet
	def globals = []


	private staticsPaths(FilePathBuilder filePathBuilder, ModuleComponentIdentifier mCID, WebSourceElements wse) {
		//TODO: usage descriptor
		return wse.statics.grep({it.path.endsWith(".js")}).collect({File f ->
			filePathBuilder.staticsPathFor(mCID, f)
		})

	}
	private staticsPaths(FilePathBuilder filePathBuilder, ProjectComponentIdentifier pCID, WebSourceSet wse) {
		def staticsJSPaths = []
		wse.staticsDirs.unique().sort().eachWithIndex { File srcDir,int index ->
			staticsJSPaths += project.fileTree(srcDir).collect({ File f ->
				if(wse.statics.contains(f) && f.path.endsWith(".js") ){
					return filePathBuilder.staticsPathFor(pCID, wse, srcDir, index, f)
				}
			}).grep()
		}
		staticsJSPaths
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		if(target == "/TEST/MOCHA") {

			//TODO: inject
			FilePathBuilder filePathBuilder = new FilePathBuilder([project:project])

			def config = project.configurations.getByName(testSourceSet.configurationName)

			def components = ComponentUtil.getOrdered(config)


			def staticsJSPaths  = components.collect { ComponentIdentifier cId ->

				//todo: move insde staticsPaths method
				def wse = sourceLookupService.get(cId)

				staticsPaths(filePathBuilder, cId, wse)
			}.flatten()

			def paths = []
			testSourceSet.js.srcDirs.unique().sort().eachWithIndex { File srcDir,int index ->
				paths += project.fileTree(srcDir).collect({ File f ->
					if(testSourceSet.js.contains(f)) {
						return filePathBuilder.jsPathFor(project.path, testSourceSet, srcDir, index, f)
					}
				}).grep()
			}

			JsonBuilder globalsJSON = new JsonBuilder(globals)
			response.contentType = 'text/html'

			def engine = new SimpleTemplateEngine()
			def template = engine.createTemplate(getClass().getResource(mochaTemplatePath)).make(["staticsJSPaths": staticsJSPaths, "paths": paths, "globalsJSON": globalsJSON])

			response.writer << template.toString()

			baseRequest.handled = true
		}else if(target == "/TEST/MOCHA/mocha.css") {

			response.outputStream << getClass().getResourceAsStream(mochaCssPath)

			baseRequest.handled = true
		}else if(target == "/TEST/MOCHA/mocha.js") {

			response.outputStream << getClass().getResourceAsStream(mochaJSPath)

			baseRequest.handled = true
		}
	}
}
