package io.silksmith

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

import io.silksmith.bundling.task.SilkArchive;

class SilkModuleCacheUtil {

	
	public static final BASE_LOCATION_SYSTEM_PROPERTY_KEY ="silksmith.cacheLocation"

	static File getBaseLocation() {

		def userHome = System.properties['user.home']
		
		if(System.properties[BASE_LOCATION_SYSTEM_PROPERTY_KEY]){
		 return new File(System.properties[BASE_LOCATION_SYSTEM_PROPERTY_KEY])
		}
		return new File(userHome,".silksmith/repo");
	}
	static File pathInCache(ResolvedArtifact resolvedArtifact) {

		pathInCache(resolvedArtifact.moduleVersion.id.group, resolvedArtifact.moduleVersion.id.name, resolvedArtifact.moduleVersion.id.version)
	}
	static File pathInCache(ModuleComponentIdentifier id) {
		pathInCache(id.group,id.module,id.version)
	}
	static File pathInCache( group, name, version) {
		return new File(getBaseLocation(),"$group/$name/$version")
	}
	static File pathInCache( group, name, version, type) {
		return new File(pathInCache(group, name, version),type)
		
	}

	static File externsPathInCache(ModuleComponentIdentifier id) {
		return new File(pathInCache(id), SilkArchive.EXTERNS_DIR)
		
	}
	static File jsPathInCache(ModuleComponentIdentifier id) {
		return new File(pathInCache(id), SilkArchive.JS_DIR)
		
	}
	static File staticsPathInCache(ModuleComponentIdentifier id) {
		return new File(pathInCache(id), SilkArchive.STATICS_DIR)
		
	}
	static File scssPathInCache(ModuleComponentIdentifier id) {
		return new File(pathInCache(id), SilkArchive.SCSS_DIR)
		
	}
}
