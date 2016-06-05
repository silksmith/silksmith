package io.silksmith.gradle.css

import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.model.ModelMap
import org.gradle.model.Path
import org.gradle.model.RuleSource
import org.gradle.platform.base.*

class ClosureStyleSheetsPlugin extends RuleSource {


    @ComponentType
    void registerGSS(TypeBuilder<GSSSourceSet> builder) {}

    @ComponentType
    void registerComponent(TypeBuilder<GSSComponent> builder) {}

    @ComponentType
    void registerBinary(TypeBuilder<GSSBinary> builder) {}


    @ComponentBinaries
    void generateGSSBinaries(ModelMap<GSSBinary> binaries, VariantComponentSpec component, @Path("buildDir") File buildDir) {
        binaries.create("exploded") { binary ->
            outputDir = new File(buildDir, "${component.name}/${binary.name}")
        }
    }


    @BinaryTasks
    void processGSS(ModelMap<Task> tasks, final GSSBinary binary) {
        binary.inputs.withType(GSSSourceSet) { gssSourceSet ->
            def taskName = binary.tasks.taskName("compile", gssSourceSet.name)
            def outputDir = new File(binary.outputDir, gssSourceSet.name)
            tasks.create(taskName, GSSTask) { compileTask ->
                compileTask.source = gssSourceSet.source
                compileTask.outputFile = new File(outputDir,'style.css')
                compileTask.renameFile = new File(outputDir,'mapping.json')
                compileTask.sourcemapFile = new File(outputDir,'sourcemap.css.map')
            }
        }
    }
}