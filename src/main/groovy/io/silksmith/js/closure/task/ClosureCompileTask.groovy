
package io.silksmith.js.closure.task


import io.silksmith.development.server.closure.ClosureJSOutput

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil

import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.SourceFile

class ClosureCompileTask extends SourceTask implements ClosureJSOutput{


	@Input
	CompilationLevel compileLevel = CompilationLevel.ADVANCED_OPTIMIZATIONS

	@Optional
	@Input
	def entryPoint
	@OutputFile
	def dest
	@Optional
	@OutputFile
	def sourceMap

	FileCollection externs = new SimpleFileCollection()

	CompilerOptions options = new CompilerOptions()

	def externs(FileCollection fileCollection) {
		externs +=fileCollection
	}
	File getDest() {
		project.file(dest)
	}

	@TaskAction
	def run() {


		Compiler compiler = new Compiler()

		// Advanced mode is used here, but additional options could be set, too.
		compileLevel.setOptionsForCompilationLevel(
				options)

		if(entryPoint) {

			options.dependencyOptions.entryPoints << entryPoint
			options.dependencyOptions.dependencyPruning = true
			options.dependencyOptions.dependencySorting = true
			options.dependencyOptions.moocherDropping = true
			//options.newTypeInference = true


		}
		def jsSources = source.collect({ SourceFile.fromFile(it) })

		def jsExterns = externs.collect({ SourceFile.fromFile(it)})


		Result result = compiler.compile(jsExterns, jsSources, options)

		//		if(result.errors) {
		//
		//			logger.error("There where {} errors in your code!", result.errors.size())
		//			result.errors.each {JSError error ->
		//				logger.error error.toString()
		//			}
		//
		//		}
		//		if(result.warnings) {
		//
		//			logger.warn("There where {} warnings in your code!", result.warnings.size())
		//			result.warnings.each { JSError warning ->
		//				logger.warn warning.toString()
		//			}
		//		}

		// The compiler is responsible for generating the compiled code; it is not
		// accessible via the Result.



		if(result.errors.length) {
			throw new GradleException("Error occured during build")
		}

		getDest().text = compiler.toSource()

	}

	def options(Closure c) {
		ConfigureUtil.configure(c,options)
	}
}
