package io.silksmith.util

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

class DepsUtil {

	static def orderedDeps(Configuration config) {

		ResolvedComponentResult root = config.incoming.resolutionResult.root
		def add
		def ensureResolved = { (ResolvedDependencyResult)it}
		def list = [] as Set
		add = { ResolvedComponentResult component ->

			//component.dependencies.each ensureResolved >>
		}
	}
}
