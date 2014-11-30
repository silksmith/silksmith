package io.silksmith.source

import org.gradle.util.Configurable

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet

interface WebSourceSet extends Named, Configurable<WebSourceSet>,WebSourceElements {

	SourceDirectorySet getScss()

	SourceDirectorySet scss(Action<SourceDirectorySet> action)

	SourceDirectorySet getJs()

	SourceDirectorySet js(Action<SourceDirectorySet> action)

	SourceDirectorySet getStatics()

	SourceDirectorySet statics(Action<SourceDirectorySet> action)


	String getConfigurationName()
	String getTaskBaseName()
	FileCollection getDependencyJSPath()
	void setDependencyJSPath(FileCollection jsPath)

	FileCollection getRuntimeJSPath()
	void setRuntimeJSPath(FileCollection jsPath)
}
