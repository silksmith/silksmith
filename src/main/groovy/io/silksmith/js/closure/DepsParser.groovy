package io.silksmith.js.closure

class DepsParser {



	//XXX:not super correct since goog.provide("stuf') would be possible
	def providePattern = /goog\.provide\(["']([\w\.]*)["']\);?/
	def requirePattern = /goog\.require\(["']([\w\.]*)["']\);?/

	//	def providePattern = ~/goog\.provide\("([\w\.]*)"\);*/
	//	def requirePattern = ~/goog\.require\("([\w\.]*)"\);*/
	//	def providePattern2 = ~/goog\.provide\('([\w\.]*)'\);*/
	//	def requirePattern2 = ~/goog\.require\('([\w\.]*)'\);*/

	def symbolCollector = { m, symbol -> return symbol }

	def parse(files) {

		files.collect({
			def provideMatcher = it.text =~providePattern
			def provides = provideMatcher.collect symbolCollector


			def requireMatcher = it.text =~requirePattern
			def requires = requireMatcher.collect symbolCollector

			new FileInfo(file:it, provides: provides.sort(), requires:requires.sort())
		}).sort({it.file.path})
	}
}

