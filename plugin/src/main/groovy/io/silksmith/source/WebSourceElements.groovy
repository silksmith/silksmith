package io.silksmith.source

import java.io.File
import java.util.Set

import org.gradle.api.file.FileCollection

public interface WebSourceElements {

	FileCollection getScss()

	FileCollection getJs()
	FileCollection getExterns()

	FileCollection getStatics()

	//TODO: elementType, elementTypeDirs instead of hardcoded js, scss, etc...
	//FileCollection elementType(String type)

	public Set<File> getJsDirs()
	public Set<File> getExternsDirs()

	public Set<File> getStaticsDirs()

	public Set<File> getScssDirs()


}
