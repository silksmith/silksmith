package io.silksmith.gradle.javascript

import io.silksmith.gradle.javascript.tooling.DefaultJavaScriptModel
import io.silksmith.gradle.tooling.model.javascript.JavaScriptModel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilder
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import javax.inject.Inject;
/**
 * Created by bruchmann on 05/06/16.
 */
class JavaScriptPlugin implements Plugin<Project> {

    private final ToolingModelBuilderRegistry registry;

    /**
     * Need to use a {@link ToolingModelBuilderRegistry} to register the custom tooling model, so inject this into
     * the constructor.
     */
    @Inject
    public JavaScriptPlugin(ToolingModelBuilderRegistry registry) {
        this.registry = registry;
    }

    public void apply(Project project) {
        // Register a builder for the custom tooling model
        project.apply([
                plugin: JavaScriptRule
        ])
        registry.register(new JavaScriptModelBuilder());
    }

    private static class JavaScriptModelBuilder implements ToolingModelBuilder {
        public boolean canBuild(String modelName) {
            // The default name for a model is the name of the Java interface
            return modelName.equals(JavaScriptModel.class.getName());
        }

        public Object buildAll(String modelName, Project project) {

            ClosureCompileTask task = project.tasks.getByName("compileMainCompiledJs")
            return new DefaultJavaScriptModel(task.outputFile.path, task.sourceMap.path);
        }
    }
}
