package io.silksmith.publishing

import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact

import io.silksmith.bundling.task.SilkArchive

class SilkSmithArchivePublishArtifact extends ArchivePublishArtifact {

	def SilkArchive archive
	SilkSmithArchivePublishArtifact(SilkArchive archive){
		super(archive)
		this.archive = archive
	}
}
