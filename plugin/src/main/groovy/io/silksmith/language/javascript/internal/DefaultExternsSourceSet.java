package io.silksmith.language.javascript.internal;

import org.gradle.language.base.sources.BaseLanguageSourceSet;

import io.silksmith.language.javascript.ClosureJavaScriptSourceSet;
import io.silksmith.language.javascript.ExternsSourceSet;

public class DefaultExternsSourceSet extends BaseLanguageSourceSet implements ExternsSourceSet {

	@Override
    protected String getTypeName() {
        return "Externs source";
    }

}
