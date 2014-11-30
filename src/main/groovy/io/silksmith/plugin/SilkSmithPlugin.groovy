
package io.silksmith.plugin

import io.silksmith.SilkSmithExtension
import io.silksmith.SilkSmithLibrary
import io.silksmith.bundling.task.SilkArchive
import io.silksmith.css.sass.plugin.SassPlugin
import io.silksmith.development.task.WorkspaceServerTask
import io.silksmith.js.closure.ClosureCompilerPlugin
import io.silksmith.js.closure.task.TestJSTask
import io.silksmith.source.WebSourceSet
import io.silksmith.source.WebSourceSetContainer

import javax.inject.Inject

import org.gradle.internal.reflect.Instantiator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.SourceSet



class SilkSmithPlugin implements Plugin<Project> {

	public static final String CONFIGURATION_NAME = "web"
	private final Instantiator instantiator
	private final FileResolver fileResolver


	WebSourceSet main
	WebSourceSet test
	private final static String CLOSURE_BASE_JS_DEPENDENCY = "closure-base"



	@Inject
	public SilkSmithPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}

	void apply(final Project project) {
		project.apply plugin:SilkSmithBasePlugin
		project.apply plugin:ClosureCompilerPlugin
		project.apply plugin:SassPlugin


		configureSourceSets(project)

		configureArchivesAndComponent(project)
		applyTasks(project)
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

		def compiledJSName = "${project.name}.js"

		def config = project.configurations.getByName(CONFIGURATION_NAME)

		def mainSourceSet = getMainSourceSet(project)

		project.task('server', type: WorkspaceServerTask, group: 'Development', description: 'Servers all required dependencies') {
			sourceSet = mainSourceSet
			configuration = config
			dependsOn "ensureExtractedArtifacts"
		}


		project.task("testJS", type: TestJSTask){
			dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(test, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
		}
		project.task("watchTestJS", type: TestJSTask){
			watch = true
			dependsOn SilkSmithBasePlugin.getSourceSetNamedTask(test, SilkSmithBasePlugin.ENSURE_EXTRACTED_ARTIFACTS)
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
