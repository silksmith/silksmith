package io.silksmith.publishing

import io.silksmith.bundling.task.SilkArchive

import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact

class SilkSmithArchivePublishArtifact extends ArchivePublishArtifact {

	def SilkArchive archive
	SilkSmithArchivePublishArtifact(SilkArchive archive){
		super(archive)
		this.archive = archive
	}
}
