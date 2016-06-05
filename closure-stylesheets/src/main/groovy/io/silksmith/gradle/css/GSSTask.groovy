

package io.silksmith.gradle.css

import static java.nio.charset.StandardCharsets.UTF_8

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import com.google.common.css.AbstractCommandLineCompiler
import com.google.common.css.DefaultExitCodeHandler
import com.google.common.css.ExitCodeHandler
import com.google.common.css.GssFunctionMapProvider
import com.google.common.css.JobDescription
import com.google.common.css.JobDescriptionBuilder
import com.google.common.css.MinimalSubstitutionMap;
import com.google.common.css.OutputRenamingMapFormat
import com.google.common.css.SourceCode
import com.google.common.css.SplittingSubstitutionMap;
import com.google.common.css.SubstitutionMap;
import com.google.common.css.SubstitutionMapProvider
import com.google.common.css.Vendor
import com.google.common.css.JobDescription.InputOrientation
import com.google.common.css.JobDescription.OutputFormat
import com.google.common.css.JobDescription.OutputOrientation
import com.google.common.css.compiler.commandline.ClosureCommandLineCompiler
import com.google.common.css.compiler.commandline.DefaultCommandLineCompiler
import com.google.common.css.compiler.commandline.RenamingType
import com.google.common.css.compiler.commandline.ClosureCommandLineCompiler.OutputInfo
import com.google.common.css.compiler.commandline.DefaultCommandLineCompiler.CompilerErrorManager
import com.google.common.css.compiler.gssfunctions.DefaultGssFunctionMapProvider
import com.google.common.io.Files

class GSSTask extends SourceTask {

    public static interface SilksmithGssFunctionMapProvider extends  GssFunctionMapProvider, Serializable {}
    public static class SilksmithDefaultGssFunctionMapProvider extends DefaultGssFunctionMapProvider implements SilksmithGssFunctionMapProvider {}

    public static interface SilksmithSubstitutionMapProvider extends  SubstitutionMapProvider, Serializable {}
    public static class SilksmithDefaultSubstitutionMapProvider  implements SilksmithSubstitutionMapProvider {
        @Override
        public SubstitutionMap get() {
            return new SplittingSubstitutionMap(new MinimalSubstitutionMap());
        }
    }
    @Input
    @Optional
    def InputOrientation inputOrientation =  InputOrientation.LTR
    @Input
    @Optional
    def OutputOrientation outputOrientation = OutputOrientation.LTR
    @Input
    @Optional
    def OutputFormat outputFormat = JobDescription.OutputFormat.COMPRESSED
    @Input
    @Optional
    def String copyrightNotice
    @Input
    @Optional
    def List<String> trueConditions = []
    @Input
    @Optional
    def boolean allowUnrecognizedFunctions
    @Input
    @Optional
    def List<String> allowedNonStandardFunctions = []
    @Input
    @Optional
    def List<String> allowedUnrecognizedProperties = []
    @Input
    @Optional
    def boolean allowUnrecognizedProperties
    @Input
    @Optional
    def Vendor vendor
    @Input
    @Optional
    def List<String> excludedClassesFromRenaming= []
    @Input
    @Optional
    def String cssRenamingPrefix = ""
    @Input
    def OutputRenamingMapFormat outputRenamingMapFormat = OutputRenamingMapFormat.JSON

    @Input
    def SilksmithSubstitutionMapProvider substitutionMapProvider = new SilksmithDefaultSubstitutionMapProvider()
    @Input
    def SilksmithGssFunctionMapProvider gssFunctionMapProvider = new SilksmithDefaultGssFunctionMapProvider()

    @Optional
    @OutputFile
    def outputFile
    @Optional
    @OutputFile
    def renameFile
    @Optional
    @OutputFile
    def sourcemapFile

    @TaskAction
    def compile() {

        if(outputFile){
            outputFile.parentFile.mkdirs()
        }
        if(renameFile){
            renameFile.parentFile.mkdirs()
        }
        if(sourcemapFile){
            sourcemapFile.parentFile.mkdirs()
        }
        ExitCodeHandler exitCodeHandler = new DefaultExitCodeHandler();

        CompilerErrorManager errorManager = new CompilerErrorManager();

        def jobDescription = buildJobDescription()
        DefaultCommandLineCompiler compiler = new DefaultCommandLineCompiler(
                jobDescription, exitCodeHandler, errorManager);

        //compiler.compile();
        println sourcemapFile
        String compilerOutput = compiler.execute(renameFile,sourcemapFile);


        if (outputFile == null) {
            System.out.print(compilerOutput);
        } else {
            outputFile.withWriter {
                it << compilerOutput
            }

        }
    }
    private JobDescription buildJobDescription() {

        JobDescriptionBuilder builder = new JobDescriptionBuilder();
        builder.setInputOrientation(inputOrientation);
        builder.setOutputOrientation(outputOrientation);
        builder.setOutputFormat(outputFormat);
        builder.setCopyrightNotice(copyrightNotice);
        builder.setTrueConditionNames(trueConditions);
        builder.setAllowUnrecognizedFunctions(allowUnrecognizedFunctions);
        builder.setAllowedNonStandardFunctions(allowedNonStandardFunctions);
        builder.setAllowedUnrecognizedProperties(allowedUnrecognizedProperties);
        builder.setAllowUnrecognizedProperties(allowUnrecognizedProperties);
        builder.setVendor(vendor);
        builder.setAllowKeyframes(true);
        builder.setAllowWebkitKeyframes(true);
        builder.setProcessDependencies(true);
        builder.setExcludedClassesFromRenaming(excludedClassesFromRenaming);
        builder.setSimplifyCss(true);
        builder.setEliminateDeadStyles(true);
        builder.setCssSubstitutionMapProvider(substitutionMapProvider);
        builder.setCssRenamingPrefix(cssRenamingPrefix);
        builder.setOutputRenamingMapFormat(outputRenamingMapFormat);
        builder.setCreateSourceMap(true)




        builder.setGssFunctionMapProvider(gssFunctionMapProvider);

        source.collect( { File it ->
            new SourceCode(it.path,it.text )
        }).each(builder.&addInput)

        return builder.getJobDescription();
    }
}