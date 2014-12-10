package io.silksmith.development.server

import io.silksmith.SourceLookupService
import io.silksmith.development.server.files.FilesHandler
import io.silksmith.source.WebSourceSet

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
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

	Set<Handler> handlers = [] as Set
	void handler(Handler handler) {

		handlers << handler
	}
	void start() {


		HandlerList handlerList = new HandlerList()
		handlers << new FilesHandler([project:project])

		handlerList.handlers = handlers
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
