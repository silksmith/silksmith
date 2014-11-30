package io.silksmith.css.sass.task

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GemInstallTask extends DefaultTask {


	Configuration jrubyConfig

	@OutputDirectory
	def gemInstallDir = project.file("$project.buildDir/gem")

	@Input
	def gems = ["sass"]

	@TaskAction
	def install() {


		gemInstallDir.mkdirs()

		gems.each { gemName ->
			logger.info "Installing $gemName"
			project.javaexec({
				main = "org.jruby.Main"
				classpath = jrubyConfig
				args = [
					"-S",
					"gem",
					"install",
					"-i",
					"$gemInstallDir",
					"$gemName",
					"--no-rdoc",
					"--no-ri"
				]
				environment 'PATH', "$gemInstallDir/bin"
				environment 'GEM_PATH', gemInstallDir
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
