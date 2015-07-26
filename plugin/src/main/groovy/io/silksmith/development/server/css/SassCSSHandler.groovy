package io.silksmith.development.server.css

import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.css.sass.SassRunner.SassMode
import io.silksmith.source.WebSourceElements

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
/**
 * 
 * @author bruchmann
 * Serves all files that are in the sass runners output dir
 * Starts sass runner in watch mode
 *
 */
public class SassCSSHandler extends AbstractHandler {



	@Delegate
	SassRunnerProvider sassRunnerProvider

	def basePathCSS ="/"
	SourceLookupService sourceLookupService

	Configuration configuration

	private Thread runnerThread

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		def outputDir = sassRunner.output
		def file = new File( outputDir,target-basePathCSS)

		def acceptHeader = baseRequest.getHeader("Accept")
		def cssRequest = false
		if(acceptHeader) {

			cssRequest = "text/css" in acceptHeader.split(",")
		}

		if(cssRequest && file.exists()){
			response.contentType = 'text/css'
			def toOutput = { response.outputStream << it }
			file.withInputStream toOutput

			def components = ComponentUtil.getOrdered(configuration)

			//TODO: if multiple files exist in output folder, specify the main file where to append statics css
			components.each( { ComponentIdentifier cId ->
				//TODO: apply usage discriptor
				WebSourceElements webSources = sourceLookupService.get(cId)
				webSources.statics.grep({
					it.path.endsWith(".css")
				}).each({ it.withInputStream toOutput })
			})
			baseRequest.handled = true
		}
	}

	@Override
	protected void doStart() throws Exception {

		super.doStart()
		runnerThread = Thread.start {
			sassRunner.run(SassMode.watch)
		}
	}
	@Override
	protected void doStop() throws Exception {
		//
		super.doStop()

		//runnerThread.inter
	}

}
