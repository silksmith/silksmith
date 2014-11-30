package io.silksmith.content

import io.silksmith.SilkModuleCacheUtil

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier




class ModuleWebpackContent implements WebPackContent {

	ModuleComponentIdentifier id
	Project project


	@Override
	public Set<File> getJSDirectory() {
		return project.file( SilkModuleCacheUtil.jsPathInCache(id))
	}

	@Override
	public Set<File> getStaticsDirectory() {
		return project.file( SilkModuleCacheUtil.staticsPathInCache(id))
	}

	@Override
	public Set<File> getScssDirectory() {
		return project.file( SilkModuleCacheUtil.scssPathInCache(id))
	}
}
