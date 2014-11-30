package io.silksmith.source

import org.gradle.api.file.FileCollection

public interface WebSourceElements {

	FileCollection getScss()
	FileCollection getJs()
	FileCollection getStatics()

	//TODO: elementType, elementTypeDirs instead of hardcoded js, scss, etc...
	//FileCollection elementType(String type)


	public Set<File> getJsDirs()

	public Set<File> getStaticsDirs()

	public Set<File> getScssDirs()
}
