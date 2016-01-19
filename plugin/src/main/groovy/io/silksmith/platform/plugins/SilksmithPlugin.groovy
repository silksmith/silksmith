package io.silksmith.platform.plugins

import io.silksmith.platform.DefaultSilksmithWebAppSpec;
import io.silksmith.platform.SilksmithWebAppSpec

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
import org.gradle.platform.base.ComponentType
import org.gradle.platform.base.ComponentTypeBuilder

class SilksmithPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		//project.extensions.create("silksmithConfigurations", SilksmithConfigurations, project.configurations, project.dependencies);
		
		project.apply "plugin" : ClosureJavaScriptPlugin
		

	}
	

}
