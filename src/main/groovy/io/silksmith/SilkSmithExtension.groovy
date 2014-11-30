package io.silksmith


import io.silksmith.source.WebSourceSetContainer
import io.silksmith.source.internal.DefaultWebSourceSetContainer

import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil

import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver

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
