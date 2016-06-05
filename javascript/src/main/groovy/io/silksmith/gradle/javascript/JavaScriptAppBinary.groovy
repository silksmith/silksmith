package io.silksmith.gradle.javascript

import org.gradle.model.Managed
import org.gradle.platform.base.BinarySpec

@Managed
interface JavaScriptAppBinary extends BinarySpec{

    File getOutputDir()
    void setOutputDir(File outputDir)
}