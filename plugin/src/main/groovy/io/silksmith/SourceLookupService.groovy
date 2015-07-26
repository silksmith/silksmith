package io.silksmith

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.internal.file.FileResolver

import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.ModuleWebSourceElements;
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet;

class SourceLookupService {

	Project project

	FileResolver fileResolver


	WebSourceElements get(ProjectComponentIdentifier id) {

		Project p = project.findProject(id.projectPath)
		SilkSmithExtension ext = p.extensions.getByType(SilkSmithExtension)
		WebSourceSet set = ext.source[SourceSet.MAIN_SOURCE_SET_NAME]
	}
	WebSourceElements get(ModuleComponentIdentifier id) {

		ModuleWebSourceElements module = new ModuleWebSourceElements([id:id, resolver:fileResolver])
	}
}
