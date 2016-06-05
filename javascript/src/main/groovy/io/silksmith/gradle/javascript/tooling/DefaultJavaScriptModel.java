package io.silksmith.gradle.javascript.tooling;

import java.io.Serializable;

public class DefaultJavaScriptModel implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String jsPath;
    private final String sourceMapPath;

    public DefaultJavaScriptModel(String jsPath, String sourceMapPath) {

        this.jsPath = jsPath;
        this.sourceMapPath = sourceMapPath;
    }

    public String getName() {
        return "javascript";
    }

    public String getJSPath() {
        return jsPath;
    }

    public String getSourceMapPath() {
        return sourceMapPath;
    }
}
