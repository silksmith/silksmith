package io.silksmith.bundling

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.util.ConfigureUtil;

import groovy.json.JsonBuilder;
import groovy.lang.Closure
import io.silksmith.SourceType;;


class SilkManifest {

	def version = 1


	final NamedDomainObjectContainer<StaticsUsageDescriptor> usage

	final NamedDomainObjectContainer<StaticDescriptor> statics
	
	final NamedDomainObjectContainer<MetaInfo> metaInfoContainer
	
	SilkManifest(NamedDomainObjectContainer<StaticsUsageDescriptor> staticsUsages, NamedDomainObjectContainer<StaticDescriptor> staticsDescriptor, NamedDomainObjectContainer<MetaInfo> metaInfoContainer) {
		this.usage = staticsUsages

		this.statics = staticsDescriptor
		
		this.metaInfoContainer = metaInfoContainer
	}

	def toJson() {
		JsonBuilder json = new JsonBuilder()
		json {
			version  version
			statics  statics.asMap
			usage  usage.asMap
			metainfo  metaInfoContainer.asMap
		}
		

		return json.toPrettyString()
	}
	def usage(Closure c) {
		ConfigureUtil.configure(c, this.usage)
	}
	def statics(Closure c) {
		ConfigureUtil.configure(c, this.statics)
	}
	def metainfo(Closure c){
		ConfigureUtil.configure(c, this.metaInfoContainer)
	}
}
