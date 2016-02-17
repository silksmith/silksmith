package io.silksmith.model

import org.gradle.model.RuleSource
import org.gradle.platform.base.LanguageType;
import org.gradle.platform.base.LanguageTypeBuilder;

class SilkSmithModelPlugin extends RuleSource{

	@LanguageType
	void registerMarkdown(LanguageTypeBuilder<JavaScriptSourceSet> builder) {
		builder.setLanguageName("JavaScript")
		
	}
	
	
	
	
}
