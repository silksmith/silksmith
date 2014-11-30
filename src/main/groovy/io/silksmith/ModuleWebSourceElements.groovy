package io.silksmith

import io.silksmith.source.WebSourceElements;

import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.FileResolver


class ModuleWebSourceElements implements WebSourceElements{

	FileResolver resolver
	ModuleComponentIdentifier id


	@Override
	public FileCollection getScss() {
		return resolver.resolveFilesAsTree(SilkModuleCacheUtil.scssPathInCache(id))
	}

	@Override
	public FileCollection getJs() {
		return resolver.resolveFilesAsTree(SilkModuleCacheUtil.jsPathInCache(id))
	}

	@Override
	public FileCollection getStatics() {
		return resolver.resolveFilesAsTree(SilkModuleCacheUtil.staticsPathInCache(id))
	}
	@Override
	public Set<File> getScssDirs() {

		return [
			resolver.resolve(SilkModuleCacheUtil.scssPathInCache(id))] as Set
	}

	@Override
	public Set<File> getJsDirs() {
		return [
			resolver.resolve(SilkModuleCacheUtil.jsPathInCache(id))] as Set
	}

	@Override
	public Set<File> getStaticsDirs() {
		return [
			resolver.resolve(SilkModuleCacheUtil.staticsPathInCache(id))] as Set
	}
}
