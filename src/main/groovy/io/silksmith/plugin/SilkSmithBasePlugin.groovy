
package io.silksmith.plugin

import io.silksmith.Constants
import io.silksmith.EnsureExtractedArtifactsTask
import io.silksmith.SourceLookupService
import io.silksmith.SourceType
import io.silksmith.source.WebDependencyFileCollection
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.apache.commons.lang3.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.plugins.BasePlugin
import org.gradle.internal.reflect.Instantiator

class SilkSmithBasePlugin implements Plugin<Project> {

	static final String ENSURE_EXTRACTED_ARTIFACTS = "EnsureExtractedArtifacts"

	private final Instantiator instantiator
	private final FileResolver fileResolver


	SourceLookupService sourceLookupService

	final static SRC_FOLDER_NAME = "src"


	final static JS_FOLDER_NAME = Constants.SRC_TYPE_JS
	final static EXTERNS_FOLDER_NAME = Constants.SRC_TYPE_EXTERNS
	final static STATICS_FOLDER_NAME = Constants.SRC_TYPE_STATICS
	final static SCSS_FOLDER_NAME = Constants.SRC_TYPE_SCSS


	@Inject
	public SilkSmithBasePlugin(Instantiator instantiator, FileResolver fileResolver) {


		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}

	void apply(final Project project) {


		this.sourceLookupService = new SourceLookupService([project:project, fileResolver:fileResolver])

		project.plugins.apply(BasePlugin)

		SilkSmithExtension ext = project.extensions.create(SilkSmithExtension.NAME, SilkSmithExtension, project, instantiator, fileResolver)

		ext.source.all { WebSourceSet sourceSet ->

			def sourceSetConfigurationName = sourceSet.configurationName
			def taskBaseName = sourceSet.taskBaseName
			def config = project.configurations.findByName(sourceSetConfigurationName)

			if(!config) {
				config = project.configurations.create(sourceSetConfigurationName)
			}
			sourceSet.js.srcDir "$SRC_FOLDER_NAME/$sourceSet.name/$JS_FOLDER_NAME"
			sourceSet.externs.srcDir "$SRC_FOLDER_NAME/$sourceSet.name/$EXTERNS_FOLDER_NAME"
			sourceSet.statics.srcDir "$SRC_FOLDER_NAME/$sourceSet.name/$STATICS_FOLDER_NAME"
			sourceSet.scss.srcDir "$SRC_FOLDER_NAME/$sourceSet.name/$SCSS_FOLDER_NAME"

			sourceSet.dependencyExternsPath = new WebDependencyFileCollection(config, sourceLookupService, SourceType.externs)

			sourceSet.dependencyJSPath = new WebDependencyFileCollection(config, sourceLookupService, SourceType.js)

			sourceSet.dependencyStaticsPath = new WebDependencyFileCollection(config, sourceLookupService, SourceType.statics)

			sourceSet.dependencyScssPath = new WebDependencyFileCollection(config, sourceLookupService, SourceType.scss)


			sourceSet.runtimeJSPath = sourceSet.dependencyJSPath + sourceSet.js

			EnsureExtractedArtifactsTask ensureExtractedArtifactsTask = project.task(getSourceSetNamedTask(sourceSet, ENSURE_EXTRACTED_ARTIFACTS), type: EnsureExtractedArtifactsTask ){ configuration  = config }
		}
	}

	public static String getSourceSetNamedTask(WebSourceSet sourceset, String taskBaseName) {

		StringUtils.uncapitalize("${sourceset.taskBaseName}${StringUtils.capitalize(taskBaseName)}")
	}
}
