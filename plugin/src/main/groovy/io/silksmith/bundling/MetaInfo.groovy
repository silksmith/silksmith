package io.silksmith.bundling;
class MetaInfo {
	private String name
	MetaInfo(name){
		this.name = name
	}
	def origins = []
	def license
	
	def origin(o){
		origins << o
	}
}