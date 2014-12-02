package io.silksmith.source

import io.silksmith.SourceLookupService
import io.silksmith.SourceType

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.UnionFileCollection
import org.gradle.api.internal.file.collections.FileCollectionAdapter
import org.gradle.api.internal.file.collections.MinimalFileSet

class WebDependencyFileCollection extends FileCollectionAdapter{


	private Configuration configuration

	WebDependencyFileCollection(Configuration configuration, SourceLookupService sourceLookupService, SourceType type) {

		super(new WebDepFileSet([configuration: configuration, sourceLookupService: sourceLookupService, type:type]))
	}

	static class WebDepFileSet implements MinimalFileSet{
		SourceType type
		Configuration configuration
		SourceLookupService sourceLookupService


		@Override
		public String getDisplayName() {
			return "SilkDepFileSet "
		}

		@Override
		public Set<File> getFiles() {

			def components = configuration.incoming.resolutionResult.allComponents - configuration.incoming.resolutionResult.root

			FileCollection[] colllections = components.collect { ResolvedComponentResult r ->

				WebSourceElements source = sourceLookupService.get(r.id)
				source[type.name()]
			}
			UnionFileCollection union = new UnionFileCollection(colllections)

			return union.files
		}
	}
}
