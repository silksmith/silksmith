package io.silksmith.development.server

import io.silksmith.SourceLookupService
import io.silksmith.development.server.closure.DepsJSHandler
import io.silksmith.development.server.closure.DevelopmentHandler
import io.silksmith.development.server.files.FilesHandler
import io.silksmith.development.server.js.test.MochaHandler
import io.silksmith.source.WebSourceSet

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration


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

	File resourceBase

	public WorkspaceServer(){
		server = new Server(port)
	}
	void start() {



		HandlerList handlerList = new HandlerList()
		def handlers = []

		handlers << new FilesHandler([project:project])

		def developmentHandler = new DevelopmentHandler([project:project, configuration:configuration, sourceLookupService:sourceLookupService])
		handlers << developmentHandler

		def depsJSHandler = new DepsJSHandler([project:project, configuration:configuration])
		handlers << depsJSHandler

		handlers << new MochaHandler([project:project])
		ResourceHandler resourceHandler = new ResourceHandler()
		resourceHandler.resourceBase = resourceBase.path
		handlers << resourceHandler


		handlerList.handlers = handlers

		server.handler = handlerList
		server.start()

		println "Started Development Server at $server.URI"
	}
	void join() {
		server.join()
	}
}
