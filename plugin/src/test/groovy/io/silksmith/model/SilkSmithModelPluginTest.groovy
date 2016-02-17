package io.silksmith.model

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.language.base.ProjectSourceSet
import org.gradle.model.ModelMap
import org.gradle.model.internal.type.ModelType
import org.gradle.model.internal.type.ModelTypes
import org.gradle.platform.base.BinaryContainer
import org.gradle.platform.base.ComponentSpecContainer



class SilkSmithModelPluginTest {
	
	
	def realize(String name) {
		project.modelRegistry.find(name, ModelType.UNTYPED)
	}

	ModelMap<Task> realizeTasks() {
		project.modelRegistry.find("tasks", ModelTypes.modelMap(Task))
	}

	ComponentSpecContainer realizeComponents() {
		project.modelRegistry.find("components", ComponentSpecContainer)
	}

	ProjectSourceSet realizeSourceSets() {
		project.modelRegistry.find("sources", ProjectSourceSet)
	}

	BinaryContainer realizeBinaries() {
		def binaries = project.modelRegistry.find("binaries", BinaryContainer)
		// Currently some rules take the task container as subject but actually mutate the binaries
		realizeTasks()
		return binaries
	}

	def dsl(@DelegatesTo(Project) Closure closure) {
		closure.delegate = project
		closure()
		project.bindAllModelRules()
	}
	
    def "adds javascript source sets" () {
        when:
        dsl {
            apply plugin: SilkSmithModelPlugin
            
        }

        then:
        def component = realizeComponents().s
        component.sources.coffeeScript instanceof JavaScriptSourceSet
//        component.sources.coffeeScript.source.srcDirs == [project.file('app/assets')] as Set
//        component.sources.coffeeScript.source.includes == ["**/*.coffee"] as Set
    }

}
