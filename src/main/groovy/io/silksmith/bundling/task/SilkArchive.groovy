package io.silksmith.bundling.task

import io.silksmith.Constants
import io.silksmith.bundling.SilkManifest
import io.silksmith.bundling.StaticsUsageDescriptor

import org.gradle.util.ConfigureUtil

import org.gradle.api.internal.file.collections.FileTreeAdapter
import org.gradle.api.internal.file.collections.MapFileTree
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.bundling.Zip
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
		manifest = new SilkManifest(project.container(StaticsUsageDescriptor))


		rootSpec.from({
			MapFileTree manifestSource = new MapFileTree(temporaryDirFactory, fileSystem)
			manifestSource.add "silk.json", {OutputStream output ->

				OutputStreamWriter wtr= new OutputStreamWriter(output)
				def manifestToJson = manifest.toJson()
				println "manifst: $manifestToJson"
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
	public SilkArchive manifest(Closure<?> configureClosure) {
		if (getManifest() == null) {
			manifest = new SilkManifest(project.container(StaticsUsageDescriptor))
		}
		ConfigureUtil.configure(configureClosure, getManifest())
		return this
	}
}
