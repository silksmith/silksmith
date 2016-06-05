package io.silksmith.gradle.tooling.model.javascript;

import org.gradle.tooling.model.Model;

public interface JavaScriptModel extends Model {

	String getName();

	String getJSPath();
	String getSourceMapPath();


}
