package io.silksmith.plugin


import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator

import groovy.lang.Closure
import io.silksmith.source.WebSourceSetContainer

class SilkSmithExtension {

	public static final NAME = "silksmith"

	final WebSourceSetContainer source


	SilkSmithExtension(Project project, Instantiator instantiator, FileResolver fileResolver) {
		source = instantiator.newInstance(DefaultWebSourceSetContainer, project, instantiator, fileResolver)
	}

	void source(Closure closure) {
		ConfigureUtil.configure(closure, source)
	}
}
