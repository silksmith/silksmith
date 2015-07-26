package io.silksmith.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class DownloadFiles extends DefaultTask{

	@Input
	def url

	@OutputDirectory
	def downloadDir = project.file("$project.buildDir/download/$name")
	@TaskAction
	def get() {
		ant.get(src: url, dest: downloadDir)
	}
}
