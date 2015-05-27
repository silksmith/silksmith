package io.silksmith.source.internal

import io.silksmith.source.WebSourceSet

import org.apache.commons.lang3.StringUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil
import org.gradle.util.GUtil



class DefaultWebSourceSet implements WebSourceSet {

	private final String name
	private final String displayName
	private final DefaultSourceDirectorySet js
	private final DefaultSourceDirectorySet externs

	private final DefaultSourceDirectorySet statics
	private final DefaultSourceDirectorySet scss


	private FileCollection dependencyJSPath
	private FileCollection dependencyExternsPath
	private FileCollection dependencyStaticsPath
	private FileCollection dependencyScssPath

	private FileCollection runtimeJSPath
	DefaultWebSourceSet(String name, Project project, Instantiator instantiator, FileResolver fileResolver) {
		this.name = name
		this.displayName = GUtil.toWords(name)
		this.js = new DefaultSourceDirectorySet(name, String.format("%s JavaScript source", displayName), fileResolver)
		this.externs = new DefaultSourceDirectorySet(name, String.format("%s JavaScript Externs source", displayName), fileResolver)
		this.scss = new DefaultSourceDirectorySet(name, String.format("%s SCSS source", displayName), fileResolver)
		this.statics = new DefaultSourceDirectorySet(name, String.format("%s Statics source", displayName), fileResolver)
	}

	String getName() {
		name
	}

	SourceDirectorySet getJs() {
		js
	}

	SourceDirectorySet js(Action<SourceDirectorySet> action) {
		action.execute(js)
		js
	}

	SourceDirectorySet getExterns() {
		externs
	}
	SourceDirectorySet externs(Action<SourceDirectorySet> action) {
		action.execute(externs)
		externs
	}

	SourceDirectorySet getScss() {
		scss
	}
	SourceDirectorySet scss(Action<SourceDirectorySet> action) {
		action.execute(scss)
		scss
	}

	SourceDirectorySet getStatics() {
		statics
	}

	SourceDirectorySet statics(Action<SourceDirectorySet> action) {
		action.execute(statics)
		statics
	}

	WebSourceSet configure(Closure closure) {
		ConfigureUtil.configure(closure, this, false)
	}

	@Override
	public String getTaskBaseName() {
		return name.equals(SourceSet.MAIN_SOURCE_SET_NAME) ? "" : GUtil.toCamelCase(name)
	}

	@Override
	public String getConfigurationName() {
		return StringUtils.uncapitalize(String.format("%sWeb", getTaskBaseName()))
	}

	@Override
	public FileCollection getDependencyJSPath() {
		this.dependencyJSPath
	}
	@Override
	public void setDependencyJSPath(FileCollection dependencyJSPath) {
		this.dependencyJSPath = dependencyJSPath
	}

	@Override
	public FileCollection getRuntimeJSPath() {
		return runtimeJSPath
	}

	@Override
	public void setRuntimeJSPath(FileCollection jsPath) {
		this.runtimeJSPath = jsPath
	}

	@Override
	public Set<File> getJsDirs() {
		return this.js.srcDirs
	}

	@Override
	public Set<File> getStaticsDirs() {
		return this.statics.srcDirs
	}

	@Override
	public Set<File> getScssDirs() {
		return this.scss.srcDirs
	}

	@Override
	public Set<File> getExternsDirs() {
		return this.externs.srcDirs
	}

	@Override
	public FileCollection getDependencyExternsPath() {
		return dependencyExternsPath
	}

	@Override
	public void setDependencyExternsPath(FileCollection files) {
		this.dependencyExternsPath = files
	}

	@Override
	public FileCollection getDependencyStaticsPath() {
		return this.dependencyStaticsPath
	}

	@Override
	public void setDependencyStaticsPath(FileCollection staticsPath) {
		this.dependencyStaticsPath = staticsPath
	}

	@Override
	public FileCollection getDependencyScssPath() {

		return this.dependencyScssPath
	}

	@Override
	public void setDependencyScssPath(FileCollection scssPath) {
		this.dependencyScssPath = scssPath
	}
}
