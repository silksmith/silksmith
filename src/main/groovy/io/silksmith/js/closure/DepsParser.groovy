package io.silksmith.js.closure

class DepsParser {

	// See: https://github.com/google/closure-library/blob/master/closure/bin/build/source.py
	def providePattern = /(?m)^\s*goog\.provide\(\s*['"](.+)['"]\s*\)/
	def modulePattern = /(?m)^\s*goog\.module\(\s*['"](.+)['"]\s*\)/
	def requirePattern = /(?m)^\s*(?:(?:var|let|const)\s+[a-zA-Z\_\$][a-zA-Z0-9\$\_]*\s*=\s*)?goog\.require\(\s*[\'"](.+)[\'"]\s*\)/

	def symbolCollector = { m, symbol -> return symbol }

	def parse(files) {

		files.collect({
			def provideMatcher = it.text =~providePattern
			def provides = provideMatcher.collect symbolCollector

			def requireMatcher = it.text =~requirePattern
			def requires = requireMatcher.collect symbolCollector

			def moduleMatcher = it.text =~modulePattern
			def modules = moduleMatcher.collect symbolCollector

			provides += modules

			new FileInfo(file:it, provides: provides.sort(), requires:requires.sort(), isModule:!modules.empty)
		}).sort({it.file.path})
	}
}

