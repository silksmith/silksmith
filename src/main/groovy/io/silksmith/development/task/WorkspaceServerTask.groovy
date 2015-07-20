package io.silksmith.development.task

import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import org.eclipse.jetty.proxy.ProxyServlet
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil


/**
 * 
 * 
 */
class WorkspaceServerTask extends DefaultTask{


	WebSourceSet sourceSet
	Configuration configuration
	SourceLookupService sourceLookupService

	@Lazy
	WorkspaceServer server = {
		[project:project,
			sourceSet: sourceSet,
			configuration:configuration,
			sourceLookupService:sourceLookupService
		]
	}()
	@Lazy
	ServletContextHandler proxyHandler = {
		println "init proxyHandler"
		ServletContextHandler contextHandler = new ServletContextHandler();
		
		server.handler contextHandler
		contextHandler
	}()
	@Lazy
	ServletHandler servletHandler = {
		println "init proxyHandler's servlethandler"
		ServletHandler handler = new ServletHandler();
		
		proxyHandler.servletHandler = handler
		handler
	}()
	void handler(Handler handler) {

		def s = server//XXX: prevents server lazy double init
		server.handler(handler)
	}
	def dir(File dir) {
		ResourceHandler handler = new ResourceHandler()
		handler.resourceBase = dir.path

		server.handler handler
		handler
	}
	def dir(File directory, Closure closure) {
		def handler = dir(directory)
		ConfigureUtil.configure(closure, handler)
		handler
	}
	
	def proxy(proxyTo, prefixProxy="") {	
		
		ServletHolder holder = servletHandler.addServletWithMapping(ProxyServlet.Transparent.class, "/*");
		
		holder.setInitParameter("proxyTo", proxyTo);
		if(prefixProxy){
			holder.setInitParameter("prefix", prefixProxy);
		}
		
	}

	@TaskAction
	def start(){

		ComponentUtil.getOrdered(configuration).collect(sourceLookupService.&get).each {WebSourceElements wse ->
			wse.staticsDirs.each {  dir it }
		}
		server.start()
		server.join()
	}
}
