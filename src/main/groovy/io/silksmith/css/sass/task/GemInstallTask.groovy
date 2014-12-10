package io.silksmith.css.sass.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GemInstallTask extends DefaultTask {


	Configuration jrubyConfig

	@OutputDirectory
	def gemInstallDir

	@Input
	def gems = [:]

	def gem(String name, version=null) {
		gems[name] = version
	}

	@TaskAction
	def install() {


		gemInstallDir.mkdirs()

		gems.each { gem ->
			logger.info "Installing $gem"
			project.javaexec({
				main = "org.jruby.Main"
				classpath = jrubyConfig
				def jRubyArgs = [
					"-S",
					"gem",
					"install",
					"-i",
					"$gemInstallDir"
				]

				jRubyArgs << "$gem.key"
				if(gem.value) {
					jRubyArgs << "$gem.value"
				}
				jRubyArgs <<"--no-rdoc"
				jRubyArgs << "--no-ri"

				args = jRubyArgs

				environment 'PATH', "$gemInstallDir/bin"
				environment 'GEM_PATH', gemInstallDir

				println "Executing ${commandLine.join(' ')}"
			})
		}


		//		ScriptEngineManager manager = new ScriptEngineManager()
		//		ScriptEngine engine = manager.getEngineByName("jruby")
		//
		//		def script = """
		//			require 'rubygems'
		//			require 'rubygems/dependency_installer'
		//			installer = Gem::DependencyInstaller.new :install_dir=> '$gemInstallDir'
		//
		//			installer.install('sass')
		//		"""
		//		engine.eval(script)
		//engine.eval("gem install -i '$project.buildDir/gems' --no-rdoc --no-ri sass")


	}
}
