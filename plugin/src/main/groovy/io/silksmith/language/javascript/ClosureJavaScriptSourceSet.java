package io.silksmith.language.javascript;

import org.gradle.language.base.LanguageSourceSet;

public interface ClosureJavaScriptSourceSet extends LanguageSourceSet{

	public ExternsSourceSet getExternsSourceSet();
}
