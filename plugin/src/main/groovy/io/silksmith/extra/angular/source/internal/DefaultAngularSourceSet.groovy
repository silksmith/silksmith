package io.silksmith.extra.angular.source.internal

import io.silksmith.extra.angular.source.AngularSourceSet

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil

public class DefaultAngularSourceSet implements AngularSourceSet {


	String name
	SourceDirectorySet templates

	String displayName

	DefaultAngularSourceSet(String name, Project project, Instantiator instantiator, FileResolver fileResolver) {

		this.name = name
		this.displayName = GUtil.toWords(name)

		this.templates = new DefaultSourceDirectorySet(name, String.format("%s Angular Template source", displayName), fileResolver)
	}
	SourceDirectorySet getTemplates() {
		templates
	}

	SourceDirectorySet templates(Action<SourceDirectorySet> action) {
		action.execute(templates)
		templates
	}
	@Override
	public String getName() {
		return this.name
	}

	@Override
	public AngularSourceSet configure(Closure closure) {
		ConfigureUtil.configure(closure, this, false)
		this
	}
}
