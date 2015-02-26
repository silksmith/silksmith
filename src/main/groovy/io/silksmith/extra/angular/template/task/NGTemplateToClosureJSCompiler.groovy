package io.silksmith.extra.angular.template.task

import org.gradle.api.Project



class NGTemplateToClosureJSCompiler {



	def compile(Project p,  inputDirs, File outputDir) {

		def baseNameSpace = ""
		def fileEnding = ".html"
		def moduleName = ""
		inputDirs.each { File inputDir->
			p.fileTree(inputDir).each { File inputFile ->

				def inputFilePath = inputFile.path - inputDir.path -"/"
				def outputFileBase = inputFilePath - fileEnding
				def outputFileName = "${outputFileBase}Template.js"
				def outputFile = new File(outputDir, outputFileName)
				outputFile.parentFile.mkdirs()

				def symbol = outputFileBase.replaceAll("/", ".").replaceAll("-", "") + "Template"
				if(baseNameSpace) {
					symbol = "${baseNameSpace}.$symbol"
				}
				outputFile.withWriter { Writer out ->

					out.println "goog.provide('$symbol');"
					out.println """
/**
* GENERATED FILE! DO NOT MODIFY
* see: $inputFilePath
* @ngInject
* @param {!angular.\$templateCache} \$templateCache
*/
$symbol = function(\$templateCache) {
var tmpl = ''"""
					inputFile.eachLine { String line ->
						def escapedLine = line.replaceAll( /'/, "\\\\'")

						out.println "+ '$escapedLine'"
					}
					out << ";"
					out.println """
\$templateCache.put('$inputFilePath', tmpl);
};"""
				}
			}
		}
	}
}
