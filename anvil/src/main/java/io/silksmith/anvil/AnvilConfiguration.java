package io.silksmith.anvil;

import java.io.File;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.silmith.tooling.model.SilksmithModel;

@Configuration
public class AnvilConfiguration {

	@Bean
	public ProjectConnection connection() {
		GradleConnector connector = GradleConnector.newConnector();
		connector.forProjectDirectory(new File("/Users/bruchmann/git/silksmith/anvil"));
		
		ProjectConnection connection = connector.connect();
		return connection;
	}
	@Bean
	public SilksmithModel model(ProjectConnection connection) {
		
		ModelBuilder<SilksmithModel> customModelBuilder = connection.model(SilksmithModel.class);
		customModelBuilder.withArguments("--init-script", "foo_init.gradle");
		SilksmithModel model = customModelBuilder.get();
		return model;
	}
}
