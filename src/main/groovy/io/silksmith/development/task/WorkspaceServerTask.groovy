package io.silksmith.development.task

import io.silksmith.SourceLookupService
import io.silksmith.development.server.WorkspaceServer
import io.silksmith.source.WebSourceSet

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction

/**
 * 
 * 
 */
class WorkspaceServerTask extends DefaultTask{


	WebSourceSet sourceSet
	Configuration configuration
	SourceLookupService sourceLookupService
	@TaskAction
	def start(){

		def server = new WorkspaceServer([project:project, sourceSet: sourceSet, configuration:configuration, resourceBase: project.projectDir, sourceLookupService:sourceLookupService])

		server.start()
		server.join()
	}
}
