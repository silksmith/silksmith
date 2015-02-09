package io.silksmith.extra.angular

import io.silksmith.extra.angular.source.AngularSourceSetContainer
import io.silksmith.extra.angular.source.internal.DefaultAngularSourceSetContainer

import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

class AngularExtension {

	public static String NAME = "angular"


	final AngularSourceSetContainer source
	AngularExtension(Project project, Instantiator instantiator, FileResolver fileResolver) {
		source = instantiator.newInstance(DefaultAngularSourceSetContainer, project, instantiator, fileResolver)
	}

	void source(Closure closure) {
		ConfigureUtil.configure(closure, source)
	}
}
