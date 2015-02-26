package io.silksmith.js.closure

import org.gradle.util.ConfigureUtil

import com.google.javascript.jscomp.CommandLineRunner
import com.google.javascript.jscomp.CompilerOptions

class SilkSmithCommandLineRunner extends CommandLineRunner {

	def CompilerOptions options
	SilkSmithCommandLineRunner(args){
		super(args)
	}
	@Override
	public CompilerOptions createOptions() {

		this.options = super.createOptions()

		return this.options
	}
	def options(Closure c) {
		ConfigureUtil.configure(c, this.options)
	}
	def compileJS() {
		doRun()
	}
}
