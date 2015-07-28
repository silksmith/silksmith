package io.silksmith.bundling.task

import org.gradle.api.internal.file.collections.FileTreeAdapter
import org.gradle.api.internal.file.collections.MapFileTree;
import org.gradle.api.tasks.bundling.Zip
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure
import io.silksmith.Constants;
import io.silksmith.bundling.SilkManifest
import io.silksmith.bundling.StaticDescriptor;
import io.silksmith.bundling.StaticsUsageDescriptor;
/**
 * Packages all web source elements
 * @author bruchmann
 *
 */
class SilkArchive extends Zip {

	public static final String SCSS_DIR = Constants.SRC_TYPE_SCSS
	public static final String STATICS_DIR = Constants.SRC_TYPE_STATICS
	public static final String JS_DIR =  Constants.SRC_TYPE_JS
	public static final String EXTERNS_DIR =  Constants.SRC_TYPE_EXTERNS
	public static final String DEFAULT_EXTENSION = 'silk'


	private SilkManifest manifest

	SilkArchive() {
		extension = DEFAULT_EXTENSION
		manifest = new SilkManifest(project.container(StaticsUsageDescriptor), project.container(StaticDescriptor))


		rootSpec.from({
			MapFileTree manifestSource = new MapFileTree(temporaryDirFactory, fileSystem)
			manifestSource.add "silk.json", {OutputStream output ->

				OutputStreamWriter wtr= new OutputStreamWriter(output)
				def manifestToJson = manifest.toJson()

				wtr<< manifestToJson
				wtr.flush()
			}
			return new FileTreeAdapter(manifestSource)
		})
	}


	public SilkManifest getManifest() {
		return manifest
	}

	public void setManifest(SilkManifest manifest) {
		this.manifest = manifest
	}
	public void js(Closure c) {
		into(JS_DIR,c)
	}
	public void externs(Closure c) {
		into(EXTERNS_DIR,c)
	}
	public void statics(Closure c) {
		into(STATICS_DIR,c)
	}
	public void scss(Closure c) {
		into(SCSS_DIR,c)
	}
	public void jsLicense(Closure c){
		license(JS_DIR,c)
	}
	public void scssLicense(Closure c){
		license(SCSS_DIR,c)
	}
	public void staticsLicense(Closure c){
		license(STATICS_DIR,c)
	}
	public void externsLicense(Closure c){
		license(EXTERNS_DIR,c)
	}
	public void license(type,Closure c){
		into("license/$type")
	}
	public SilkArchive manifest(Closure<?> configureClosure) {
		if (getManifest() == null) {
			manifest = new SilkManifest(project.container(StaticsUsageDescriptor))
		}
		ConfigureUtil.configure(configureClosure, getManifest())
		return this
	}
}
