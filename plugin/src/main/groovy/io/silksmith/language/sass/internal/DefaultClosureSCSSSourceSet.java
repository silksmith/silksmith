package io.silksmith.language.sass.internal;

import org.gradle.language.base.sources.BaseLanguageSourceSet;

import io.silksmith.language.sass.SCSSSourceSet;

public class DefaultClosureSCSSSourceSet extends BaseLanguageSourceSet implements SCSSSourceSet {

	@Override
    protected String getTypeName() {
        return "SCSS source";
    }

}
