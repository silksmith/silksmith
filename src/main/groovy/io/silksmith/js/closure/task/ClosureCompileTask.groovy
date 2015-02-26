
package io.silksmith.js.closure.task


import io.silksmith.development.server.closure.ClosureJSOutput
import io.silksmith.js.closure.SilkSmithCommandLineRunner

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import com.google.javascript.jscomp.CompilationLevel

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

	@Optional
	@OutputFile
	def externExport
	@InputFiles
	FileCollection externs = new SimpleFileCollection()

	//CompilerOptions options = new CompilerOptions()

	private options = []

	def args = []

	def args(String arg) {
		args << arg
	}

	def externs(FileCollection fileCollection) {
		externs +=fileCollection
	}
	File getDest() {
		project.file(dest)
	}

	@TaskAction
	def run() {



		List<String> closureArgs = []

		closureArgs += [
			"--compilation_level",
			"ADVANCED_OPTIMIZATIONS"
		]
		closureArgs += [
			"--closure_entry_point",
			"$entryPoint"
		]
		closureArgs += [
			"--output_wrapper",
			"(function(){%output%})();"
		]

		closureArgs << "--manage_closure_dependencies"
		closureArgs << "--generate_exports"
		closureArgs << "--use_types_for_optimization" // sets ambiguate / disambiguate properties


		closureArgs += [
			"--language_in",
			"ECMASCRIPT5"
		]

		closureArgs += [
			"--summary_detail_level",
			"3"
		]
		closureArgs += ["--warning_level", "VERBOSE"]
		closureArgs += ["--logging_level", "WARNING"]

		//closureArgs << "--version" closure compiler will not run if version flag is set!

		// sets error/warn level for specific types of problems
		closureArgs += [
			"--jscomp_warning",
			"accessControls"
		]
		closureArgs += [
			"--jscomp_warning",
			"ambiguousFunctionDecl"
		]
		closureArgs += [
			"--jscomp_error",
			"checkRegExp"
		]
		closureArgs += [
			"--jscomp_warning",
			"checkStructDictInheritance"
		]
		closureArgs += [
			"--jscomp_warning",
			"checkTypes"
		]
		closureArgs += [
			"--jscomp_error",
			"checkVars"
		]
		closureArgs += [
			"--jscomp_warning",
			"deprecated"
		]
		closureArgs += [
			"--jscomp_warning",
			"duplicateMessage"
		]
		closureArgs += [
			"--jscomp_warning",
			"es5Strict"
		]
		closureArgs += [
			"--jscomp_warning",
			"externsValidation"
		]
		closureArgs += [
			"--jscomp_error",
			"invalidCasts"
		]
		closureArgs += [
			"--jscomp_error",
			"missingProperties"
		]
		closureArgs += [
			"--jscomp_warning",
			"misplacedTypeAnnotation"
		]
		closureArgs += [
			"--jscomp_warning",
			"nonStandardJsDocs"
		]
		// produces tons of weird errors..
		//closureArgs += ["--jscomp_warning", "reportUnknownTypes"]
		closureArgs += [
			"--jscomp_warning",
			"suspiciousCode"
		]
		closureArgs += [
			"--jscomp_error",
			"strictModuleDepCheck"
		]
		closureArgs += [
			"--jscomp_error",
			"typeInvalidation"
		]
		closureArgs += [
			"--jscomp_error",
			"undefinedNames"
		]
		closureArgs += [
			"--jscomp_error",
			"undefinedVars"
		]
		closureArgs += [
			"--jscomp_error",
			"unknownDefines"
		]
		closureArgs += [
			"--jscomp_error",
			"uselessCode"
		]
		closureArgs += [
			"--jscomp_error",
			"visibility"
		]

		// sets output filenames


		closureArgs += [
			"--js_output_file",
			dest
		]
		if(externExport) {
			closureArgs += [
				"--extern_exports_path",
				externExport
			]
		}
		if(sourceMap) {
			closureArgs += [
				"--create_source_map",
				sourceMap
			]
		}

		closureArgs << "--source_map_format=V3"


		args += closureArgs
		source.each {
			args << "--js"
			args << "$it.path"
		}

		externs.each {
			args << "--externs"
			args << "$it.path"
		}



		//Result result = compiler.compile(jsExterns, jsSources, options)

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

		logger.info("Running closure with args: $args")
		def commandLineRunner  = new SilkSmithCommandLineRunner(args)

		//		// Advanced mode is used here, but additional options could be set, too.
		//		compileLevel.setOptionsForCompilationLevel(commandLineRunner.options)
		//
		//
		//		commandLineRunner.options.languageIn = LanguageMode.ECMASCRIPT5
		//		//options.generateExports = true
		//
		//		if(entryPoint) {
		//
		//			commandLineRunner.options.dependencyOptions.entryPoints << entryPoint
		//
		//			commandLineRunner.options.dependencyOptions.dependencyPruning = true
		//			commandLineRunner.options.dependencyOptions.dependencySorting = true
		//			commandLineRunner.options.dependencyOptions.moocherDropping = true
		//			//options.newTypeInference = true
		//		}

		options.each commandLineRunner.&options

		if(commandLineRunner.shouldRunCompiler()) {

			commandLineRunner.compileJS()
		}else {
			println "Command Line Runner should not run"
		}
		if(commandLineRunner.hasErrors()) {
			throw new GradleException("Error occured during build")
		}


	}

	def options(Closure c) {
		options << c

	}
}
