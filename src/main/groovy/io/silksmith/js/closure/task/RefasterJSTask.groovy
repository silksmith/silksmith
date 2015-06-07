package io.silksmith.js.closure.task

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import com.google.javascript.jscomp.CommandLineRunner
import com.google.javascript.jscomp.ErrorManager
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.refactoring.ApplySuggestedFixes
import com.google.javascript.refactoring.RefactoringDriver
import com.google.javascript.refactoring.RefasterJsScanner
import com.google.javascript.refactoring.SuggestedFix

class RefasterJSTask extends SourceTask {

	@Input
	def File refasterJsTemplate
	@Input
	def includeDefaultExterns = true
	@Input
	def boolean dryRun = true
	@Input
	def boolean verbose = true
	@InputFiles
	def FileCollection externs

	@TaskAction
	def refactor() {

		def externsSourceFiles = externs.collect { SourceFile.fromFile(it) }
		def sourceFiles = source.collect { SourceFile.fromFile(it) }

		RefasterJsScanner scanner = new RefasterJsScanner()
		scanner.loadRefasterJsTemplate(refasterJsTemplate.path)


		def driverBuilder = new RefactoringDriver.Builder(scanner)
		if(includeDefaultExterns) {
			def defaultExterns = CommandLineRunner.getDefaultExterns()
			logger.info "using JS Default Externs:"
			defaultExterns.each {
				logger.info "$it"
			}
			driverBuilder.addExterns(defaultExterns )
		}


		RefactoringDriver driver = driverBuilder
				.addExterns(externsSourceFiles)
				.addInputs(sourceFiles)
				.build()
				logger.info("JS Other Externs Files:")
				externsSourceFiles.each {
					logger.info "$it" 
				}
				logger.info("JS Source Files:")
				externsSourceFiles.each {
					logger.info "$it"
				}

		

		println("Compiling JavaScript code and searching for suggested fixes.")
		List<SuggestedFix> fixes = driver.drive()

		if (!verbose) {
			// When running in quiet mode, the Compiler's error manager will not have printed
			// this information itself.
			ErrorManager errorManager = driver.compiler.errorManager
			println("Compiler results: ${errorManager.errorCount} errors and  ${errorManager.warningCount} warnings.")
		}
		println("Found ${fixes.size()} suggested fixes.")
		if (dryRun) {
			if (!fixes.empty) {
				println("SuggestedFixes: ")
				println(fixes)
			}
		} else {
			println("Modifying affected files: ")
			def affectedFiles = fixes.collect( {
				it.replacements.keySet()
			}).flatten().unique().each { println it }


			ApplySuggestedFixes.applySuggestedFixesToFiles(fixes)
		}
	}
}
