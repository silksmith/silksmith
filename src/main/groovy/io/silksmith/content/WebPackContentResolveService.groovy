package io.silksmith.content

import org.gradle.api.Project
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

/**
 * 
 * @author bruchmann ProjectComponentIdentifier ModuleComponentIdentifier
 *
 *         ComponentIdentifier
 */
public class WebPackContentResolveService {

	Project project


	WebPackContent from(ComponentIdentifier componentIdentifier) {
		if (componentIdentifier instanceof ProjectComponentIdentifier) {

			return new ProjectWebpackContent([project:project,id:componentIdentifier])
		} else if (componentIdentifier instanceof ModuleComponentIdentifier) {

			return new ModuleWebpackContent([project:project,id:componentIdentifier])
		}
		throw new IllegalArgumentException("Unknown ComponentIdentifier type")
	}
}
