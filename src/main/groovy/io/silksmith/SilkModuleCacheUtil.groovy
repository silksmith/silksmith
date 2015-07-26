package io.silksmith

import io.silksmith.bundling.task.SilkArchive

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

class SilkModuleCacheUtil {


	public static final DEFAULT_BASE_LOCATION = "${System.properties['user.home']}/.silksmith/repo"
	public static final BASE_LOCATION_SYSTEM_PROPERTY_KEY ="silksmith.cacheLocation"

	static def getBaseLocation() {

		System.properties[BASE_LOCATION_SYSTEM_PROPERTY_KEY]?:DEFAULT_BASE_LOCATION
	}
	static def pathInCache(ResolvedArtifact resolvedArtifact) {

		pathInCache(resolvedArtifact.moduleVersion.id.group, resolvedArtifact.moduleVersion.id.name, resolvedArtifact.moduleVersion.id.version)
	}
	static def pathInCache(ModuleComponentIdentifier id) {
		pathInCache(id.group,id.module,id.version)
	}
	static def pathInCache( group, name, version) {
		"${getBaseLocation()}/$group/$name/$version"
	}
	static def pathInCache( group, name, version, type) {
		"${pathInCache(group, name, version)}/$type"
	}

	static def externsPathInCache(ModuleComponentIdentifier id) {
		"${pathInCache(id)}/$SilkArchive.EXTERNS_DIR"
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
