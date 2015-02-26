package io.silksmith.extra.angular.source



import org.gradle.api.Named
import org.gradle.api.file.SourceDirectorySet
import org.gradle.util.Configurable


interface AngularSourceSet extends Named, Configurable<AngularSourceSet>{


	SourceDirectorySet getTemplates()
}
