package io.silksmith

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact


class DefaultExtraArtifactService implements ExtractedArtifactService {

	public void ensurePackage(Project p, ResolvedArtifact resolvedArtifact) {

		if(!resolvedArtifact.file.exists()) {
			throw new GradleException("File of $resolvedArtifact does not exist")
		}

		ModuleVersionIdentifier id = resolvedArtifact.moduleVersion.id
		File pathFile = p.file(SilkModuleCacheUtil.pathInCache(id.group, id.name, id.version))

		if(pathFile.exists()) {
			pathFile.delete()
		}
		p.delete(pathFile)
		pathFile.mkdirs()

		p.copy {
			from p.zipTree(resolvedArtifact.file)
			into pathFile
		}
	}
}
