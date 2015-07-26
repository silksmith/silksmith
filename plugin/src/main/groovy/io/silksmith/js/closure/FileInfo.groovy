package io.silksmith.js.closure


class FileInfo {

	def file
	def provides = []
	def requires = []
	def isModule = false

	@Override
	public String toString() {
		"$file: provides: $provides, requires: $requires, isModule: $isModule"
	}
}

