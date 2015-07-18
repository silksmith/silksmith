package io.silksmith.js.closure;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilerOptions;
import groovy.lang.Closure;
import org.gradle.util.ConfigureUtil;

import java.io.IOException;
import java.util.Arrays;

public class SilkSmithCommandLineRunner extends CommandLineRunner {

    private Closure[] optionsConfigurers;

    public SilkSmithCommandLineRunner(String[] args, Closure[] optionsConfigurers) {
        super(args);
        this.optionsConfigurers = optionsConfigurers;
    }

    @Override
    protected void setRunOptions(CompilerOptions options) throws IOException, FlagUsageException {
        super.setRunOptions(options);
        Arrays.stream(this.optionsConfigurers).forEach((c) -> ConfigureUtil.configure(c, options));
    }

    public void compileJS() throws IOException, FlagUsageException {
        doRun();
    }
}
