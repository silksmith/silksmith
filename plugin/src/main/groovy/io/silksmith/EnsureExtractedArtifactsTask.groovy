package io.silksmith

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.TaskAction

class EnsureExtractedArtifactsTask extends DefaultTask {

	//TODO: dependsOn
	Configuration configuration

	@TaskAction
	def ensure() {

		//TODO: inject
		def service = new DefaultExtraArtifactService([project:project])

		configuration.resolvedConfiguration.resolvedArtifacts.each { ResolvedArtifact resolvedArtifact ->
			service.ensurePackage( resolvedArtifact)
		}
	}
}
