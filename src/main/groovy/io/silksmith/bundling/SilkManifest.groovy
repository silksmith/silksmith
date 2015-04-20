package io.silksmith.bundling

import groovy.json.JsonBuilder

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.util.ConfigureUtil


class SilkManifest {

	def version = 1


	final NamedDomainObjectContainer<StaticsUsageDescriptor> usage

	final NamedDomainObjectContainer<StaticDescriptor> statics
	SilkManifest(NamedDomainObjectContainer<StaticsUsageDescriptor> staticsUsages, NamedDomainObjectContainer<StaticDescriptor> staticsDescriptor) {
		this.usage = staticsUsages

		this.statics = staticsDescriptor
	}

	def toJson() {
		JsonBuilder json = new JsonBuilder()
		json {
			version  version
			statics  statics.asMap
			usage  usage.asMap
		}

		return json.toPrettyString()
	}
	def usage(Closure c) {
		ConfigureUtil.configure(c, this.usage)
	}
	def statics(Closure c) {
		ConfigureUtil.configure(c, this.statics)
	}
}
