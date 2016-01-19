package io.silksmith.language.javascript.internal;

import org.gradle.language.base.sources.BaseLanguageSourceSet;

import io.silksmith.language.javascript.ClosureJavaScriptSourceSet;
import io.silksmith.language.javascript.ExternsSourceSet;

public class DefaultClosureJavaScriptSourceSet extends BaseLanguageSourceSet implements ClosureJavaScriptSourceSet {

	@Override
    protected String getTypeName() {
        return "Closure JavaScript source";
    }
	private ExternsSourceSet externsSourceSet;

	@Override
	public ExternsSourceSet getExternsSourceSet() {
		return externsSourceSet;
	}
	

}
