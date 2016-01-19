

package io.silksmith.platform.plugins;


import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.language.javascript.ClosureJavaScriptSourceSet
import io.silksmith.language.javascript.ExternsSourceSet
import io.silksmith.language.javascript.internal.DefaultClosureJavaScriptSourceSet
import io.silksmith.language.javascript.internal.DefaultExternsSourceSet
import io.silksmith.platform.DefaultSilksmithWebAppBinarySpec
import io.silksmith.platform.DefaultSilksmithWebAppSpec
import io.silksmith.platform.SilksmithWebAppBinarySpec
import io.silksmith.platform.SilksmithWebAppSpec

import org.gradle.api.Incubating
import org.gradle.api.Task
import org.gradle.internal.service.ServiceRegistry
import org.gradle.language.base.internal.LanguageSourceSetInternal
import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.Path
import org.gradle.model.RuleSource
import org.gradle.platform.base.BinaryTasks
import org.gradle.platform.base.BinaryType
import org.gradle.platform.base.BinaryTypeBuilder
import org.gradle.platform.base.ComponentBinaries
import org.gradle.platform.base.ComponentType
import org.gradle.platform.base.ComponentTypeBuilder
import org.gradle.platform.base.LanguageType
import org.gradle.platform.base.LanguageTypeBuilder

/**
 * Plugin for adding javascript processing to a Play application.  Registers "javascript" language support with the {@link org.gradle.language.javascript.JavaScriptSourceSet}.
 */
@SuppressWarnings("UnusedDeclaration")
@Incubating
public class ClosureJavaScriptPlugin extends RuleSource {

	@ComponentType
	void registerComponentSpec(ComponentTypeBuilder<SilksmithWebAppSpec> builder){
		builder.defaultImplementation(DefaultSilksmithWebAppSpec)
	}
	@Mutate
	void createDefaultSpec(ModelMap<SilksmithWebAppSpec> builder) {
		builder.create("main");
		builder.create("test");
	}

	@LanguageType
	void registerJavascript(LanguageTypeBuilder<ClosureJavaScriptSourceSet> builder) {
		builder.setLanguageName("closureJavaScript");
		builder.defaultImplementation(DefaultClosureJavaScriptSourceSet.class);
	}
	@LanguageType
	void registerExterns(LanguageTypeBuilder<ExternsSourceSet> builder) {
		builder.setLanguageName("externs");
		builder.defaultImplementation(DefaultExternsSourceSet.class);
	}

	@Mutate
	void createJavascriptSourceSets(ModelMap<SilksmithWebAppSpec> components) {
		components.beforeEach { SilksmithWebAppSpec compSpec ->

			compSpec.sources.create "externs", ExternsSourceSet, { ExternsSourceSet externsSourceSet ->
				externsSourceSet.source.srcDir("src/$compSpec.name/externs");
				externsSourceSet.source.include("**/*.js");
			}
			compSpec.sources.create "closureJavaScript", ClosureJavaScriptSourceSet, { ClosureJavaScriptSourceSet javaScriptSourceSet ->
				javaScriptSourceSet.source.srcDir("src/$compSpec.name/js");
				javaScriptSourceSet.source.include("**/*.js");
			}
		};
	}

	@BinaryType
	void registerApplication(BinaryTypeBuilder<SilksmithWebAppBinarySpec> builder) {
		builder.defaultImplementation(DefaultSilksmithWebAppBinarySpec.class);
		
	}
	
	@ComponentBinaries
	void createBinaries(ModelMap<SilksmithWebAppBinarySpec> binaries, final SilksmithWebAppSpec componentSpec, @Path("buildDir") final File buildDir) {
		
		binaries.create("${componentSpec.name}Binary"){}
	}

	@BinaryTasks
	void createJavaScriptTasks(ModelMap<Task> tasks, final SilksmithWebAppBinarySpec binary, ServiceRegistry serviceRegistry, @Path("buildDir") final File buildDir, @Path("closureJavaScript") ClosureJavaScriptSourceSet s) {
		println "craeting binary task with source set $s"
		for (ClosureJavaScriptSourceSet javaScriptSourceSet : binary.getInputs().withType(ClosureJavaScriptSourceSet.class)) {
			if (((LanguageSourceSetInternal) javaScriptSourceSet).getMayHaveSources()) {
				createJavaScriptMinifyTask(tasks, javaScriptSourceSet, binary, buildDir);
			}
		}
	}
	void createJavaScriptMinifyTask(ModelMap<Task> tasks, final ClosureJavaScriptSourceSet javaScriptSourceSet, final SilksmithWebAppBinarySpec binary, @Path("buildDir") final File buildDir) {
		final String minifyTaskName = "closureCompile" + capitalize(binary.getName()) + capitalize(javaScriptSourceSet.getName());
		final File minifyOutputDirectory = new File(buildDir, "$binary.name/src/$minifyTaskName");
		tasks.create(minifyTaskName, ClosureCompileTask, { ClosureCompileTask javaScriptMinify ->

			javaScriptMinify.setDescription("Minifies javascript for the '" + javaScriptSourceSet.getName() +"' source set.");
			javaScriptMinify.source javaScriptSourceSet.source

			javaScriptMinify.dest(minifyOutputDirectory);


			binary.getAssets().builtBy(javaScriptMinify);
			binary.getAssets().addAssetDir(minifyOutputDirectory);

			javaScriptMinify.dependsOn(javaScriptSourceSet.getBuildDependencies());
		});
	}
}