package io.silksmith

import io.silksmith.content.WebPackContentResolveService
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.source.WebSourceElements

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.UnionFileCollection
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

			SourceLookupService sourceLookupService = project.plugins.findPlugin(SilkSmithBasePlugin).sourceLookupService

			FileCollection[] colllections = allComps.collect { ResolvedComponentResult r ->

				WebSourceElements source = sourceLookupService.get(r.id)

				source.js
			}
			UnionFileCollection union = new UnionFileCollection(colllections)
			return union.files
		}
	}
}
