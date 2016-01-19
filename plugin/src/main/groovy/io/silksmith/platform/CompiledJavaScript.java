package io.silksmith.platform;

import java.io.File;

import org.gradle.api.BuildableModelElement;



public interface CompiledJavaScript extends BuildableModelElement {
   
	
    void setJavaScript(File js);
    File getJavaScript();
    
    void setSourceMap(File sourceMap);
    File getSourceMap();
}
