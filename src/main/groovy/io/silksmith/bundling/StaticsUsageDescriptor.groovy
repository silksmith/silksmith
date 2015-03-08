package io.silksmith.bundling

class StaticsUsageDescriptor  {

	private String name
	def files = []

	StaticsUsageDescriptor(name) {
		this.name = name
	}

	def file(file) {
		files << file
	}
}
