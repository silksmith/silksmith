package io.silksmith.css.sass.task

import io.silksmith.SourceLookupService
import io.silksmith.css.sass.SassRunner
import io.silksmith.css.sass.SassRunner.SassMode
import io.silksmith.development.server.css.SassRunnerProvider

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction


class ScssCompile extends DefaultTask implements SassRunnerProvider{

	Configuration jrubyConfig

	Configuration configuration

	@InputDirectory
	def gemInstallDir

	@Optional
	@InputDirectory
	def inputDir

	@OutputDirectory
	def outputDir

	SourceLookupService sourceLookupService

	@Lazy
	SassRunner sassRunner = {
		[
			sourceLookupService:sourceLookupService,
			project:project,
			jrubyConfig:jrubyConfig,
			configuration:configuration,
			outputDir:project.file(outputDir),
			inputDir: project.file(inputDir),
			gemInstallDir:project.file(gemInstallDir)
		]
	}()

	@TaskAction
	def compile() {
		SassMode mode = project.hasProperty("watch")?SassMode.watch:SassMode.update
		sassRunner.run(mode)
	}
}
