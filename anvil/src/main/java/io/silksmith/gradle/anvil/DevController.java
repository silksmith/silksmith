package io.silksmith.gradle.anvil;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.silksmith.gradle.tooling.model.javascript.JavaScriptModel;

@Controller

public class DevController {

	private JavaScriptModel javaScriptModel;

	@Autowired
	public DevController(JavaScriptModel javaScriptModel) {
		this.javaScriptModel = javaScriptModel;

	}

	@ResponseBody
	@RequestMapping("/app.js")
	public FileSystemResource appJS() {

		String jsPath = javaScriptModel.getJSPath();
		File file = new File(jsPath);
		return new FileSystemResource(file);
	}
}
