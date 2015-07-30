package io.silksmith.bundling

class StaticDescriptor {

	private String name
	def mimeType

	StaticDescriptor(name) {
		this.name = name
	}

	def js() {
		mimeType = "text/javascript"
	}
	def css() {
		mimeType = "text/css"
	}
}
