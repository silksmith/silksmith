package io.silksmith.development.server.closure

import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.development.server.DocumentWriteUtil
import io.silksmith.development.server.files.FilePathBuilder
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
/**
 * Mimics the /my.app.js request
 * @author bruchmann
 *
 */
class ClosureJSDevelopmentHandler extends AbstractHandler{

	final def DOCUMENT_WRITE_GOOG_REQUIRE_ENTRY_POINT = { entryPoint -> """
document.write('<script>goog.require("$entryPoint")</script>');
""" }
	final def CLOSURE_SETTINGS = { """CLOSURE_BASE_PATH="/";
""" }


	def basePathJS =""
	ClosureJSOutput jsOutput

	Configuration configuration

	Project project
	
	@Lazy
	def handlerPath = {
		"$basePathJS/$jsOutput.dest.name"
	}()

	SourceLookupService sourceLookupService

	//TODO: inject
	FilePathBuilder builder = new FilePathBuilder([project:project])


	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		if(target == handlerPath) {

			def writeDocumentWritePath = {String path ->
				if(path.endsWith(".js") ) {

					response.writer << DocumentWriteUtil.js(path)
				}
			}

			response.contentType = 'application/javascript'
			response.writer << CLOSURE_SETTINGS.call()

			def components = ComponentUtil.getOrdered(configuration)

			components.each( { ComponentIdentifier cId ->

				WebSourceElements webSources = sourceLookupService.get(cId)
				handleComponent(cId, webSources, writeDocumentWritePath)
			})

			if(jsOutput.entryPoint) {
				response.writer << DOCUMENT_WRITE_GOOG_REQUIRE_ENTRY_POINT.call(jsOutput.entryPoint)
			}

			baseRequest.handled = true
		}
	}
	private handleComponent(ModuleComponentIdentifier cId, WebSourceElements webSources, Closure writeDocumentWritePath) {
		webSources.statics.each {
			def path = builder.staticsPathFor(cId, it)
			writeDocumentWritePath( path)
		}
	}

	private handleComponent(ProjectComponentIdentifier cId, WebSourceSet webSources, Closure writeDocumentWritePath) {

		def statics = webSources.statics
		webSources.staticsDirs.unique().sort().eachWithIndex {dir, index->
			project.fileTree(dir).each { file ->
				if(statics.contains(file)) {
					def path  = builder.staticsPathFor(cId,webSources,dir, index, file)
					writeDocumentWritePath(path)
				}
			}
		}
	}
}
