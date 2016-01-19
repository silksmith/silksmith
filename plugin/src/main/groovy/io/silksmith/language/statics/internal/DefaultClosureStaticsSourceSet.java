package io.silksmith.language.statics.internal;

import org.gradle.language.base.sources.BaseLanguageSourceSet;

import io.silksmith.language.statics.StaticsSourceSet;

public class DefaultClosureStaticsSourceSet extends BaseLanguageSourceSet implements StaticsSourceSet {

	@Override
    protected String getTypeName() {
        return "Statics source";
    }

}
