package io.silksmith.development.server.js.test

import groovy.json.JsonBuilder
import io.silksmith.development.server.files.FilePathBuilder
import io.silksmith.plugin.SilkSmithExtension;
import io.silksmith.source.WebSourceSet

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

class MochaHandler extends AbstractHandler{

	def mochaVersion = "2.0.1"

	def cssPath = "/META-INF/resources/webjars/mocha/${mochaVersion}/mocha.css"
	def jsPath = "/META-INF/resources/webjars/mocha/${mochaVersion}/mocha.js"

	Project project
	def sourceSetName = SourceSet.TEST_SOURCE_SET_NAME

	def globals = ["foo"]
	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {



		if(target == "/TEST/MOCHA") {

			WebSourceSet testSourceSet = project.extensions.getByType(SilkSmithExtension).source[sourceSetName]
			FilePathBuilder filePathBuilder = new FilePathBuilder([project:project])

			def paths = []

			testSourceSet.js.srcDirs.unique().sort().eachWithIndex { File srcDir,int index ->
				paths += project.fileTree(srcDir).collect({ File f ->
					if(testSourceSet.js.contains(f)) {
						return filePathBuilder.jsPathFor(project.path, testSourceSet, srcDir, index, f)
					}
				}).grep()
			}

			JsonBuilder globalsJSON = new JsonBuilder(globals)


			response.writer << """
<html>
  <head>
    <title>Mocha</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<script src="${server.URI}DEVELOPMENT?statics.exclude=CSS"></script>
    <link rel="stylesheet" href="/TEST/MOCHA/mocha.css" />
    <script src="/TEST/MOCHA/mocha.js"></script>
    <script>mocha.setup('bdd')</script>
    
    <script>
      onload = function(){
        mocha.checkLeaks();
        mocha.globals($globalsJSON);
        var runner = mocha.run();
      };
    </script>
		${out -> paths.each{ out << """<script src="$it"></script>"""
				}
			}
  </head>
  <body>
    <div id="mocha"></div>
  </body>
</html>
"""

	baseRequest.handled = true
}else if(target == "/TEST/MOCHA/mocha.css") {

	response.outputStream << getClass().getResourceAsStream(cssPath)

	baseRequest.handled = true
}else if(target == "/TEST/MOCHA/mocha.js") {

	response.outputStream << getClass().getResourceAsStream(jsPath)

	baseRequest.handled = true
}
}
}
