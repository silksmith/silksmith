package io.silksmith.development.server.closure

import io.silksmith.SilkModuleCacheUtil
import io.silksmith.development.server.files.FilePathBuilder
import io.silksmith.js.closure.DepsParser
import io.silksmith.js.closure.FileInfo
import io.silksmith.source.WebSourceSet

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.slf4j.LoggerFactory

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet

class DepsJSHandler extends AbstractHandler {

	static final String DEPS_JS = "/deps.js"


	Configuration configuration

	Project project

	def logger = LoggerFactory.getLogger(DepsJSHandler)

	DepsParser depsParser = new DepsParser()

	def sourceSetName = SourceSet.MAIN_SOURCE_SET_NAME

	FilePathBuilder filePathBuilder = new FilePathBuilder([project:project])
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		if(DEPS_JS == target) {

			def moduleComponents = configuration.incoming.resolutionResult.allComponents.grep({ResolvedComponentResult it ->it.id instanceof ModuleComponentIdentifier })
			response.setContentType('application/javascript')
			moduleComponents.each( {
				ModuleComponentIdentifier mcid = it.id

				def pathInRepo = SilkModuleCacheUtil.jsPathInCache(mcid)

				def fileInfos = depsParser.parse(project.fileTree(pathInRepo).files)

				fileInfos.each { FileInfo fileInfo ->

					def provides =fileInfo.provides.collect({"'$it'"}).join(",")
					def requires =fileInfo.requires.collect({"'$it'"}).join(",")

					def depsPath = filePathBuilder.jsPathFor(mcid,fileInfo.file)

					response.writer << """goog.addDependency('$depsPath', [$provides], [$requires]);
"""
				}
			})

			def projectComponents = configuration.incoming.resolutionResult.allComponents.grep({ResolvedComponentResult it ->it.id instanceof ProjectComponentIdentifier })
			projectComponents.each( {


				ProjectComponentIdentifier projectComponentId = it.id

				Project otherProject =  project.findProject(projectComponentId.projectPath)
				WebSourceSet wss = otherProject.extensions.webcrafttools.source[sourceSetName]
				SourceDirectorySet dirSet = wss.js

				dirSet.srcDirs.unique().sort().eachWithIndex { srcDir,index ->

					println "$index, $srcDir"
					def fileInfos = depsParser.parse(project.fileTree(srcDir).filter({dirSet.contains(it)}).files)
					fileInfos.each { FileInfo fileInfo ->


						def provides =fileInfo.provides.collect({"'$it'"}).join(",")
						def requires =fileInfo.requires.collect({"'$it'"}).join(",")

						def depsPath = filePathBuilder.jsPathFor(projectComponentId, wss,  srcDir, index, fileInfo.file) -"/"

						//ClosureJSPathUtil.projectJSDepsPath(otherProject, sourceSetName,srcDir, fileInfo )
						response.writer << """goog.addDependency('$depsPath', [$provides], [$requires]);
"""
					}
				}
			})

			baseRequest.handled = true
		}
	}
}
