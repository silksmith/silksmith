package io.silksmith

import io.silksmith.bundling.task.SilkArchive

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

class SilkModuleCacheUtil {


	public static final BASE_LOCATION = "${System.properties['user.home']}/.silksmith/repo"
	static def pathInCache(ResolvedArtifact resolvedArtifact) {

		pathInCache(resolvedArtifact.moduleVersion.id.group, resolvedArtifact.moduleVersion.id.name, resolvedArtifact.moduleVersion.id.version)
	}
	static def pathInCache(ModuleComponentIdentifier id) {
		pathInCache(id.group,id.module,id.version)
	}
	static def pathInCache( group, name, version) {
		"$BASE_LOCATION/$group/$name/$version"
	}

	static def jsPathInCache(ModuleComponentIdentifier id) {
		"${pathInCache(id)}/$SilkArchive.JS_DIR"
	}
	static def staticsPathInCache(ModuleComponentIdentifier id) {
		"${pathInCache(id)}/$SilkArchive.STATICS_DIR"
	}
	static def scssPathInCache(ModuleComponentIdentifier id) {
		"${pathInCache(id)}/$SilkArchive.SCSS_DIR"
	}
}
