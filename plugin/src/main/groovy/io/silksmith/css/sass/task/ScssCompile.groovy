package io.silksmith.css.sass.task

import io.silksmith.SourceLookupService
import io.silksmith.css.sass.SassRunner
import io.silksmith.css.sass.SassRunner.SassMode
import io.silksmith.development.server.css.SassRunnerProvider

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction


class ScssCompile extends SourceTask implements SassRunnerProvider{

	

	Configuration configuration

	@InputDirectory
	def gemInstallDir


	@OutputDirectory
	def outputDir

	SourceLookupService sourceLookupService

	@Lazy
	SassRunner sassRunner = {
		[
			sourceLookupService:sourceLookupService,
			project:project,
			
			configuration:configuration,
			outputDir:project.file(outputDir),

			gemInstallDir:project.file(gemInstallDir)
		]
	}()

	@TaskAction
	def compile() {
		SassMode mode = project.hasProperty("watch")?SassMode.watch:SassMode.update
		sassRunner.run(mode)
	}
}
