package io.silksmith

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

class ComponentUtil {

	public static List<ComponentIdentifier> getOrdered(Configuration configuration) {
		ResolvedComponentResult rootDependency = configuration.incoming.resolutionResult.root

		def recursiveAdd
		def list = [] as Set
		recursiveAdd = { ResolvedComponentResult resolved ->

			resolved.dependencies.collect({ResolvedDependencyResult it -> it.selected})
			.each(recursiveAdd)
			list << resolved
		}
		recursiveAdd(rootDependency)

		def toId = { ResolvedComponentResult it ->  it.id }

		def components = list.collect(toId)
		return components
	}
}
