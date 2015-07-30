package io.silksmith.development.server

import io.silksmith.SourceLookupService
import io.silksmith.development.server.files.FilesHandler
import io.silksmith.source.WebSourceSet

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.util.ConfigureUtil;
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by bruchmann on 23/10/14.
 */
class WorkspaceServer {

	WebSourceSet sourceSet
	Project project
	int port = 10101

	Configuration configuration

	SourceLookupService sourceLookupService
	private Server server

	private static final Logger logger = LoggerFactory.getLogger(WorkspaceServer)



	public WorkspaceServer(){
		server = new Server(port)
	}

	List<Handler> handlers = []
	void handler(Handler handler) {

		handlers << handler
	}
	private List<Closure> handlerListConfigs = []; 
	void handlerList(Closure c){
		
		handlerListConfigs << c
	}
	void start() {

		HandlerList handlerList = new HandlerList()		
		handlers << new FilesHandler([project:project])
		handlerList.handlers = handlers
		
		handlerListConfigs.each {
			ConfigureUtil.configure(it, handlerList);
		}
		
		server.handler = handlerList

		server.start()

		println "Started Development Server at $server.URI"
	}
	void join() {
		server.join()
	}
	void stop() {
		server.stop()
	}
}
