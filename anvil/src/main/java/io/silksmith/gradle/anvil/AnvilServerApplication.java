package io.silksmith.gradle.anvil;

import java.io.File;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.silksmith.gradle.tooling.model.javascript.JavaScriptModel;

@SpringBootApplication
public class AnvilServerApplication {

	@Value("${anvil.projectDir:/Users/bruchmann/Documents/cookbook/sample-demo}")
	public String projectDir;

	public static void main(String[] args) {
		SpringApplication.run(AnvilServerApplication.class, args);
	}

	@Bean(destroyMethod = "close")
	public ProjectConnection projectConnection() {
		GradleConnector connector = GradleConnector.newConnector();

		connector.forProjectDirectory(new File(projectDir));
		ProjectConnection connection = connector.connect();
		return connection;
	}

	@Bean
	public JavaScriptModel javaScriptModel(ProjectConnection connection) {
		return connection.getModel(JavaScriptModel.class);
	}
}
