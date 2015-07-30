package io.silksmith.task

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.TagOpt
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GitFiles extends DefaultTask{

	@Input
	def url
	@Input
	def checkout
	@OutputDirectory
	def workingCopyDir = project.file("$project.buildDir/git/$name")
	@TaskAction
	def get() {
		Git git
		try {
			git = Git.open(workingCopyDir)
		}catch(Exception e) {
			git = Git.cloneRepository().setDirectory(workingCopyDir).setURI(url).call()
		}

		git.fetch().setTagOpt(TagOpt.FETCH_TAGS).call()
		git.checkout().setName(checkout).call()
		
	}
}
