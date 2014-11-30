package io.silksmith

import io.silksmith.content.WebPackContent
import io.silksmith.content.WebPackContentResolveService

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.internal.file.collections.FileCollectionAdapter
import org.gradle.api.internal.file.collections.MinimalFileSet

class WebDependencyFileCollection extends FileCollectionAdapter{


	private Configuration configuration

	WebDependencyFileCollection(Configuration configuration, Project project) {

		super(new WebDepFileSet([configuration: configuration, project: project]))
	}

	static class WebDepFileSet implements MinimalFileSet{

		Configuration configuration
		Project project


		@Override
		public String getDisplayName() {
			return "SilkDepFileSet "
		}

		@Override
		public Set<File> getFiles() {

			WebPackContentResolveService webPackContentResolveService = new WebPackContentResolveService([project:project])

			def allComps = configuration.incoming.resolutionResult.allComponents - configuration.incoming.resolutionResult.root


			return allComps.collect { ResolvedComponentResult r ->


				WebPackContent webPackContentResolveServiceFrom = webPackContentResolveService.from(r.id)

				webPackContentResolveServiceFrom.JSDirectory
			}.flatten().grep({ it.exists() }).collect({
				project.fileTree(it).files
			}).flatten() as Set
		}
	}
}
