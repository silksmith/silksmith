package io.silksmith.gradle.javascript

import org.gradle.api.Task
import org.gradle.model.ModelMap
import org.gradle.model.Path
import org.gradle.model.RuleSource
import org.gradle.platform.base.BinaryTasks
import org.gradle.platform.base.ComponentBinaries
import org.gradle.platform.base.ComponentType
import org.gradle.platform.base.TypeBuilder
import org.gradle.platform.base.VariantComponentSpec


class JavaScriptRule extends RuleSource {


    @ComponentType
    void registerSourceSet(TypeBuilder<JavaScriptSourceSet> builder) {}
    @ComponentType
    void registerExterns(TypeBuilder<ExternsSourceSet> builder) {}

    @ComponentType
    void registerComponent(TypeBuilder<JavaScriptAppSpec> builder) {}

    @ComponentType
    void registerBinary(TypeBuilder<JavaScriptAppBinary> builder) {}

    @ComponentBinaries
    void generateBinaries(ModelMap<JavaScriptAppBinary> binaries, JavaScriptAppSpec component, @Path("buildDir") File buildDir) {
        binaries.create("compiled") { binary ->
            outputDir = new File(buildDir, "${component.name}/${binary.name}")
        }
    }


    @BinaryTasks
    void compileJavaScriptTasks(ModelMap<Task> tasks, final JavaScriptAppBinary binary) {
        binary.inputs.withType(JavaScriptSourceSet) { sourceSet ->
            def taskName = binary.tasks.taskName("compile", sourceSet.name)
            def outputDir = new File(binary.outputDir, sourceSet.name)
            tasks.create(taskName, ClosureCompileTask) { compileTask ->
                compileTask.source = sourceSet.source
                compileTask.outputFile = new File(outputDir,'app.js')
                compileTask.sourceMap = new File(outputDir,'sourcemap.js.map')
            }
        }
    }
}
