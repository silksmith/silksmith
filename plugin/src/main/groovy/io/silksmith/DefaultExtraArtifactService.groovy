package io.silksmith

import org.gradle.api.GradleException;
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact


class DefaultExtraArtifactService implements ExtractedArtifactService {

	Project project

	public void ensurePackage(ResolvedArtifact resolvedArtifact) {

		if(!resolvedArtifact.file.exists()) {
			throw new GradleException("File of $resolvedArtifact does not exist")
		}

		ModuleVersionIdentifier id = resolvedArtifact.moduleVersion.id
		File pathFile = project.file(SilkModuleCacheUtil.pathInCache(id.group, id.name, id.version))

		if(pathFile.exists()) {
			pathFile.delete()
		}
		project.delete(pathFile)
		pathFile.mkdirs()

		project.copy {
			from project.zipTree(resolvedArtifact.file)
			into pathFile
		}
	}
}
