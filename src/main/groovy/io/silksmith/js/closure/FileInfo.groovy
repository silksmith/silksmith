package io.silksmith.js.closure


class FileInfo {

	def file
	def provides = []
	def requires = []

	@Override
	public String toString() {

		"$file: provides: $provides, requires: $requires"
	}
}

