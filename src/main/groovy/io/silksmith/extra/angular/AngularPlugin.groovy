package io.silksmith.extra.angular

import io.silksmith.extra.angular.source.AngularSourceSet
import io.silksmith.extra.angular.template.task.AngularTemplateToClosureJS
import io.silksmith.js.closure.ClosureCompilerPlugin
import io.silksmith.js.closure.task.ClosureCompileTask
import io.silksmith.plugin.SilkSmithBasePlugin
import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import javax.inject.Inject

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator

class AngularPlugin implements Plugin<Project>{

	private final Instantiator instantiator
	private final FileResolver fileResolver

	public static final String COMPILE_TEMPLATES ="compileNGTemplates"

	public static final String TEMPLATE_FOLDER_NAME = "ngTemplates"

	@Inject
	public AngularPlugin(Instantiator instantiator, FileResolver fileResolver) {


		this.instantiator = instantiator
		this.fileResolver = fileResolver
	}
	@Override
	public void apply(Project project) {

		project.apply  plugin:SilkSmithBasePlugin
		project.apply  plugin:ClosureCompilerPlugin

		AngularExtension ngExt = project.extensions.create(AngularExtension.NAME, AngularExtension,project, instantiator, fileResolver)

		SilkSmithExtension silkSmithExtension = project.extensions.getByType(SilkSmithExtension)

		WebSourceSet mainWebSourceSet = silkSmithExtension.source.findByName(SourceSet.MAIN_SOURCE_SET_NAME)
		if(mainWebSourceSet) {
			ngExt.source.create(mainWebSourceSet.name)
		}


		ngExt.source.all { AngularSourceSet ass ->

			WebSourceSet wss = silkSmithExtension.source.getByName(ass.name)
			ass.templates.srcDir "$SilkSmithBasePlugin.SRC_FOLDER_NAME/$ass.name/$TEMPLATE_FOLDER_NAME"

			AngularTemplateToClosureJS template2JSTask = project.task(SilkSmithBasePlugin.getSourceSetNamedTask(wss, COMPILE_TEMPLATES), type: AngularTemplateToClosureJS ){
				dest = project.file("$project.buildDir/ngTemplates/$ass.name")
				srcDirs = ass.templates.srcDirs
			}

			wss.js.srcDir template2JSTask.dest
		}
		project.tasks.withType(ClosureCompileTask){

			args << "--angular_pass" // generate $inject array for @ngInject

			args <<	"--extra_annotation_name"
			args <<"ngdoc"

			args <<	"--extra_annotation_name"
			args <<"restrict"

			args <<	"--extra_annotation_name"
			args <<"scope"

			args <<"--extra_annotation_name"
			args <<"element"


		}
	}
}
