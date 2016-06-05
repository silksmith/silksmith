package io.silksmith.gradle.anvil;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/tasks")
@Controller
public class TasksController {

	private ProjectConnection projectConnection;

	@Autowired
	public TasksController(ProjectConnection projectConnection) {
		this.projectConnection = projectConnection;
	}

	@ResponseBody
	@RequestMapping("/")
	public String index() {

		// Configure the build
		BuildLauncher launcher = projectConnection.newBuild();
		launcher.forTasks("mainCompiled");
		launcher.withArguments("--continuous");
		launcher.setStandardOutput(System.out);
		launcher.setStandardError(System.err);

		// Run the build
		launcher.run();
		return "OK";

	}
}
