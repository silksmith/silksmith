package io.silksmith.plugin

import javax.inject.Inject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilder
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import io.silmith.tooling.model.DefaultModel
import io.silmith.tooling.model.SilksmithModel;
class ToolingPlugin implements Plugin<Project>{

	private ToolingModelBuilderRegistry registry
	@Inject
	ToolingPlugin(ToolingModelBuilderRegistry registry){
		this.registry = registry
	}
	@Override
	public void apply(Project target) {
		registry.register(new SilksmithToolingModelBuilder());
		
	}
	private static class SilksmithToolingModelBuilder implements ToolingModelBuilder {
		@Override
		public boolean canBuild(String modelName) {
			return modelName.equals(SilksmithModel.class.getName());
		}

		@Override
		public Object buildAll(String modelName, Project project) {

			return new DefaultModel()
		}
	}

}
