package io.silksmith.development.server.files

import java.io.File

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

import io.silksmith.SilkModuleCacheUtil;
import io.silksmith.bundling.task.SilkArchive;
import io.silksmith.source.WebSourceSet


class FilePathBuilder {

	Project project

	def jsPathFor(String path,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(path, wss, SilkArchive.JS_DIR, srcDir,srcDirIndex, file)
	}
	def jsPathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(pcid, wss, SilkArchive.JS_DIR, srcDir,srcDirIndex, file)
	}

	def jsPathFor(ModuleComponentIdentifier mcid,  File file) {
		pathFor(mcid,SilkArchive.JS_DIR, file)
	}
	def staticsPathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss,  File srcDir, int srcDirIndex,File file) {
		pathFor(pcid, wss, SilkArchive.STATICS_DIR, srcDir,srcDirIndex, file)
	}
	def staticsPathFor(ModuleComponentIdentifier mcid,  File file) {
		pathFor(mcid,SilkArchive.STATICS_DIR, file)
	}
	def pathFor(String projectPath,  WebSourceSet wss, String type, File srcDir, int srcDirIndex,File file) {
		
//		def path =  file.path - srcDir.path - "/"
		
		def path = relativize(file, srcDir)
		"/FILES/project/$projectPath/$wss.name/$type/$srcDirIndex/$path"
	}
	def pathFor(ProjectComponentIdentifier pcid,  WebSourceSet wss, String type, File srcDir, int srcDirIndex,File file) {
		pathFor(pcid.projectPath,wss, type, srcDir, srcDirIndex, file)
	}
	def pathFor(ModuleComponentIdentifier mcid, String type, File file) {
		println "Pathfor:"
		println file.path
		File pathInCache = SilkModuleCacheUtil.pathInCache(mcid)
		println pathInCache
		def path = relativize(file,new File(pathInCache,type))//file.path - pathInCache - "/$type/"
		"/FILES/module/$mcid.group/$mcid.module/$mcid.version/$type/$path"
	}
	
	private String relativize(File full, File root){
		
		
		// Print the relative path of 'full' in relation to 'root'
		// Notice that the full path is passed as a parameter to the root.
//		def relPath= new File(  )
//		relPath
		root.toURI().relativize( full.toURI() ).toString()
	}
	/*CLOSURE_BASE_PATH="/";

document.write('<script src="/FILES/module/io.silksmith.libs/closure-base/v20150604/statics/C:\Users\IEUser\.silksmith\repo\io.silksmith.libs\closure-base\v20150604\statics\closure\goog\base.js"></script>');

document.write('<script src="/FILES/module/io.silksmith.libs/jquery/1.11.2+smith.0/statics/C:\Users\IEUser\.silksmith\repo\io.silksmith.libs\jquery\1.11.2+smith.0\statics\jquery.min.js"></script>');

document.write('<script>goog.require("todo.init")</script>');

	 * 
	 */
}
