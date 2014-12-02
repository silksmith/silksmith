package io.silksmith.development.server.files

import io.silksmith.plugin.SilkSmithExtension
import io.silksmith.source.WebSourceSet

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.ivy.core.cache.CacheUtil
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet

class FilesHandler extends AbstractHandler{

	Project project



	def pathPatternProject = ~/\/FILES\/project\/([^\/\s]*)\/(\w+)\/(\w+)\/(\d+)\/(.*)/
	def pathPatternModule = ~/\/FILES\/module\/([^\/\s]*)\/([^\/\s]*)\/([^\/\s]*)\/(\w+)\/(.*)/
	def path = "/FILES/project/:my-project/main/js/0/path"
	def path2 = "/FILES/module/my-groupd/my-name/my-version/js/path"
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {

		def matcherProject = target =~pathPatternProject
		if(matcherProject.matches()) {
			def projectPath = matcherProject.group(1)
			def sourceSetName =matcherProject.group(2)
			def sourceType = matcherProject.group(3)
			def dirNr = matcherProject.group(4)
			def path = matcherProject.group(5)


			Project p = project.findProject(projectPath)
			WebSourceSet ws = p.extensions.getByType(SilkSmithExtension).source[sourceSetName]
			SourceDirectorySet sds = ws[sourceType]

			def srcDirList = sds.srcDirs.unique().sort()


			def sourceDir = srcDirList[dirNr.toInteger()]
			def file = project.file("$sourceDir/$path")
			if(file.path.endsWith(".js")) {
				response.contentType = "application/javascript"
			}

			file.withInputStream { response.outputStream << it }


			baseRequest.handled = true
			return
		}
		def matcherModule = target =~pathPatternModule
		if(matcherModule.matches()) {
			def group = matcherModule.group(1)
			def name =matcherModule.group(2)
			def version = matcherModule.group(3)
			def sourceType = matcherModule.group(4)
			def path = matcherModule.group(5)

			def pathInCache = CacheUtil.pathInCache(group, name, version)

			//XXX
			//TODO: to much knowledge about cache structure
			def file = project.file("$pathInCache/$sourceType/$path")

			if(file.path.endsWith(".js")) {
				response.contentType = "application/javascript"
			}
			file.withInputStream { response.outputStream << it }

			baseRequest.handled = true
		}
	}
}
