package io.silksmith.development.task

import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.ResourceHandler
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

	@TaskAction
	def start(){

		ComponentUtil.getOrdered(configuration).collect(sourceLookupService.&get).each {WebSourceElements wse ->
			wse.staticsDirs.each {  dir it }
		}
		server.start()
		server.join()
	}
}
