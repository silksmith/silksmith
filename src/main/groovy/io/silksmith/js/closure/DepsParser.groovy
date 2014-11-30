package io.silksmith.js.closure

class DepsParser {


	def providePattern = ~/goog\.provide\("([\w\.]*)"\);*/
	def requirePattern = ~/goog\.require\("([\w\.]*)"\);*/

	def providePattern2 = ~/goog\.provide\('([\w\.]*)'\);*/
	def requirePattern2 = ~/goog\.require\('([\w\.]*)'\);*/

	def symbolCollector = { m, symbol -> return symbol }

	def parse(files) {

		files.collect({
			def provideMatcher = it.text =~providePattern
			if(!provideMatcher.matches()) {
				provideMatcher = it.text =~providePattern2
			}

			def provides = provideMatcher.collect symbolCollector


			def requireMatcher = it.text =~requirePattern
			if(!requireMatcher.matches()) {
				requireMatcher = it.text =~requirePattern2
			}

			def requires = requireMatcher.collect symbolCollector

			new FileInfo(file:it, provides: provides.sort(), requires:requires.sort())
		}).sort({it.file.path})
	}
}

