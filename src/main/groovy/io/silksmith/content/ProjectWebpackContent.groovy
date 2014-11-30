package io.silksmith.content

import io.silksmith.source.WebSourceSet

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.tasks.SourceSet



class ProjectWebpackContent implements WebPackContent {

	String sourceSetName = SourceSet.MAIN_SOURCE_SET_NAME
	Project project
	ProjectComponentIdentifier id
	@Override
	public Set<File> getJSDirectory() {

		Project p = project.project(id.projectPath)
		WebSourceSet ws = p.extensions.webcrafttools.source[sourceSetName]
		ws.js.srcDirs
	}

	@Override
	public Set<File> getStaticsDirectory() {
		Project p = project.project(id.projectPath)
		WebSourceSet ws = p.extensions.webcrafttools.source[sourceSetName]
		ws.statics.srcDirs
	}

	@Override
	public Set<File> getScssDirectory() {
		Project p = project.project(id.projectPath)
		WebSourceSet ws = p.extensions.webcrafttools.source[sourceSetName]
		ws.scss.srcDirs
	}
}
