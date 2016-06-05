package io.silksmith.gradle.javascript

import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.jscomp.SourceMap
import org.gradle.api.GradleException
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction


class ClosureCompileTask extends SourceTask{


    @OutputFile
    def File outputFile;

    @Optional
    @OutputFile
    def File sourceMap;

    def CompilationLevel compilationLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS

    @TaskAction
    void compile(){
        println "COMPILING"

        Compiler compiler = new Compiler();

        CompilerOptions options = new CompilerOptions();




        // To get the complete set of externs, the logic in
        // CompilerRunner.getDefaultExterns() should be used here.


//        JSSourceFile extern = JSSourceFile.fromCode("externs.js",
//                "function alert(x) {}");
//
//        // The dummy input name "input.js" is used here so that any warnings or
//        // errors will cite line numbers in terms of input.js.
//        JSSourceFile input = JSSourceFile.fromCode("input.js", code);

        if(sourceMap!=null){
            sourceMap.parentFile.mkdirs()

            options.sourceMapOutputPath = ""
            println "Generating sourcemap to: $options.sourceMapOutputPath"
        }
        compilationLevel.setOptionsForCompilationLevel(
                options);

        def externs = []
        def sources = source.collect {
            SourceFile.fromFile(it);
        }
        // compile() returns a Result, but it is not needed here.

        def result = compiler.compile(externs, sources, options);

        println "SourceMap: $result.sourceMap"
        result.warnings.each {
            logger.warn(it)
        }
        result.errors.each {
            logger.error(it)
        }
        if(result.success){

            outputFile.parentFile.mkdirs();

            def sourceMapPath = "app.js.map";
            outputFile.withWriter {writer ->
                writer.println(compiler.toSource())

                writer.println("//@ sourceMappingURL=_javascript?sourcemap=${sourceMapPath}")

            }



            StringBuilder sb = new StringBuilder();
            result.sourceMap.appendTo(sb, "sourceMap");
            sourceMap.withWriter {
                it.println(sb);
            }


        }else{
            throw new GradleException('Compilation failed');
        }

        // The compiler is responsible for generating the compiled code; it is not
        // accessible via the Result.



    }
}
