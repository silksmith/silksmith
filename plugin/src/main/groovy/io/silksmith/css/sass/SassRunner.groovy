package io.silksmith.css.sass

import io.silksmith.SourceLookupService
import io.silksmith.development.server.css.CSSOutput
import io.silksmith.source.ModuleWebSourceElements
import io.silksmith.source.WebSourceElements
import io.silksmith.source.WebSourceSet

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.jruby.embed.ScriptingContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SassRunner implements CSSOutput {

	private static Logger logger = LoggerFactory.getLogger(SassRunner)
	public static enum SassMode{
		watch, update
	}
	SourceLookupService sourceLookupService

	Project project

	Configuration configuration

	File outputDir

	File gemInstallDir

	
	def run(SassMode mode = SassMode.update) {

		def relativeInstallPath = rubyPath(gemInstallDir)

		//-I
		def importPathsArgs = []

		def inOuts = []
		outputDir.mkdirs()
		importPathsArgs = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			sourceLookupService.get(rcr.id)

		}).grep({it instanceof ModuleWebSourceElements}).collect({WebSourceElements webSourceElements ->

			webSourceElements.scssDirs

		}).collect({Set<File> set ->

			set.collect({
				if(!it.exists()) {
					return []
				}
				["-I",rubyPath( it.path)]
			})
		}).grep().flatten()

		inOuts = configuration.incoming.resolutionResult.allComponents.collect({ ResolvedComponentResult rcr ->

			sourceLookupService.get(rcr.id)

		}).grep({it instanceof WebSourceSet}).collect({WebSourceElements webSourceElements ->

			webSourceElements.scssDirs

		}).collect({Set<File> set ->

			set.collect({
				if(!it.exists()) {
					return []
				}
				"${rubyPath(it.path)}:${rubyPath(outputDir)}" })
		}).grep().flatten()


		def sassArgs = [

				"--$mode"
			//"--scss",
		]
	
		sassArgs += importPathsArgs

		sassArgs += inOuts


		def scssScript = """
puts 'Running SCSS'
require 'sass'
require 'sass/exec'
puts ARGV
opts = Sass::Exec::SassScss.new(ARGV, :scss)
opts.parse!"""

		ScriptingContainer container = new ScriptingContainer();
		
		
		container.environment = ['GEM_PATH':relativeInstallPath, 'GEM_HOME':relativeInstallPath]
		
		container.argv = sassArgs
		def scssResult = container.runScriptlet(scssScript)

		logger.info("SCSS script result: {}",scssResult)

	}
	@Override
	public File getOutput() {
		return outputDir
	}
	
	private rubyPath(path){
		project.relativePath(path).replace(File.separator, '/')
	}
}
