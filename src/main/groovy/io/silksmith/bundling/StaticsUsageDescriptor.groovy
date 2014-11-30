package io.silksmith.bundling



class StaticsUsageDescriptor  {

	def name

	def files = []

	StaticsUsageDescriptor(name) {
		this.name = name
	}

	def file(file) {
		files << file
	}
}
