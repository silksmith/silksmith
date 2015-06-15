
package io.silksmith.plugin

import io.silksmith.ComponentUtil
import io.silksmith.SourceLookupService
import io.silksmith.bundling.task.SilkArchive
import io.silksmith.css.gss.SassGssPlugin;
import io.silksmith.css.sass.plugin.SassPlugin
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.js.closure.ClosureCompilerPlugin
import io.silksmith.publishing.SilkSmithLibrary
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet
import io.silksmith.source.WebSourceSetContainer

import javax.inject.Inject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator



class SilkSmithPlugin implements Plugin<Project> {

	public static final String CONFIGURATION_NAME = "web"
	private final Instantiator instantiator
	private final FileResolver fileResolver

	public static final String ASSEMBLE_WEBAPP_TASK_NAME = "assembleWebapp"
	WebSourceSet main
	WebSourceSet test


	SourceLookupService sourceLookupService

	@Inject
	public SilkSmithPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}

	void apply(final Project project) {
		project.apply plugin:SilkSmithBasePlugin



		sourceLookupService = project.plugins.getPlugin(SilkSmithBasePlugin).sourceLookupService


		configureSourceSets(project)

		configureArchivesAndComponent(project)
		applyTasks(project)

		project.apply plugin:ClosureCompilerPlugin
		project.apply plugin:SassPlugin
		project.apply plugin:SassGssPlugin
	}

	void configureSourceSets(Project project) {
		main = createSourceSet(project, SourceSet.MAIN_SOURCE_SET_NAME)
		test = createSourceSet(project, SourceSet.TEST_SOURCE_SET_NAME)
		Configuration mainConfig = project.configurations.getByName(main.configurationName)
		Configuration testConfig = project.configurations.getByName(test.configurationName)

		testConfig.extendsFrom mainConfig
	}
	WebSourceSet createSourceSet(Project project, name) {
		WebSourceSetContainer container = project.extensions.getByType(SilkSmithExtension).source
		container.create(name)
	}
	void applyTasks(final Project project) {


		def config = project.configurations.getByName(CONFIGURATION_NAME)
		def mainSourceSet = getMainSourceSet(project)

		project.task('server', type: WorkspaceServerTask, group: 'Development', description: 'Servers all required dependencies') {
			sourceSet = mainSourceSet
			configuration = config


			dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(main, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
			sourceLookupService = this.sourceLookupService
		}


		//set default task
		project.defaultTasks SilkSmithBasePlugin.getSourceSetNamedTask(main, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)

		project.task(ASSEMBLE_WEBAPP_TASK_NAME, type: Copy){
			dependsOn config
			from {
				ComponentUtil.getOrdered(config).collect( {
					sourceLookupService.get(it)

				}).collect( { WebSourceElements wse ->
					wse.staticsDirs

				}).flatten()
			}

			into("$project.buildDir/webapp")

		}
	}


	private void configureArchivesAndComponent(final Project project) {

		SilkArchive pack = project.tasks.create("pack", SilkArchive)
		pack.setGroup(BasePlugin.BUILD_GROUP)

		pack.setDescription("Assembles a silk archive containing the main sources.")

		WebSourceSet mainSourceSet = getMainSourceSet(project)

		pack.js{ from mainSourceSet.js }
		pack.statics { from mainSourceSet.statics }
		pack.scss  { from mainSourceSet.scss }
		pack.externs  { from mainSourceSet.externs }

		ArchivePublishArtifact artifact = new ArchivePublishArtifact(pack)
		Configuration configuration = project.configurations.getByName(CONFIGURATION_NAME)

		configuration.artifacts.add(artifact)
		project.extensions.getByType(DefaultArtifactPublicationSet).addCandidate(artifact)
		project.components.add(new SilkSmithLibrary(artifact, configuration.allDependencies))
	}
	def getMainSourceSet(Project project) {
		project.extensions.getByType(SilkSmithExtension).source.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
	}
}
