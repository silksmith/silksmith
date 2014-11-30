package io.silksmith.development.server.files

import io.silksmith.source.WebSourceSet

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier


class FilePathBuilder {

	Project project

	def jsPathFor(String path,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(path, wss, Pack.JS_DIR, srcDir,srcDirIndex, file)
	}
	def jsPathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(pcid, wss, Pack.JS_DIR, srcDir,srcDirIndex, file)
	}

	def jsPathFor(ModuleComponentIdentifier mcid,  File file) {
		pathFor(mcid,Pack.JS_DIR, file)
	}
	def staticsPathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(pcid, wss, Pack.STATICS_DIR, srcDir,srcDirIndex, file)
	}
	def staticsPathFor(ModuleComponentIdentifier mcid,  File file) {
		pathFor(mcid,Pack.STATICS_DIR, file)
	}
	def pathFor(String projectPath,  WebSourceSet wss, String type, File srcDir, int srcDirIndex,File file) {
		def path = file.path - srcDir.path - "/"
		"/FILES/project/$projectPath/$wss.name/$type/$srcDirIndex/$path"
	}
	def pathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss, String type, File srcDir, int srcDirIndex,File file) {
		pathFor(pcid.projectPath,wss, type, srcDir, srcDirIndex, file)
	}
	def pathFor(ModuleComponentIdentifier mcid, String type, File file) {
		def path = file.path - CacheUtil.pathInCache(mcid) - "/$type/"
		"/FILES/module/$mcid.group/$mcid.module/$mcid.version/$type/$path"
	}
}
