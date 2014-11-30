package io.silksmith.bundling

import groovy.json.JsonBuilder

import org.gradle.api.NamedDomainObjectContainer


class SilkManifest {

	def version = 1
	final NamedDomainObjectContainer<StaticsUsageDescriptor> staticsUsages

	SilkManifest(NamedDomainObjectContainer<StaticsUsageDescriptor> staticsUsages) {
		this.staticsUsages = staticsUsages
	}

	def toJson() {
		JsonBuilder json = new JsonBuilder()
		json {
			version version
			usage staticsUsages.asMap
		}
		return json.toString()
	}
}
