package io.silksmith

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.TaskAction

class EnsureExtractedArtifactsTask extends DefaultTask {


	Configuration configuration

	@TaskAction
	def ensure() {

		def service = new DefaultExtraArtifactService()



		configuration.resolvedConfiguration.resolvedArtifacts.each { ResolvedArtifact resolvedArtifact ->


			service.ensurePackage(project, resolvedArtifact)
		}
	}
}
