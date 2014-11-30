package io.silksmith.content;

import java.io.File;
import java.util.Set;

/**
 * 
 * @author bruchmann ProjectComponentIdentifier ModuleComponentIdentifier
 *
 *         ComponentIdentifier
 */
public interface WebPackContent {

	public Set<File> getJSDirectory();

	public Set<File> getStaticsDirectory();

	public Set<File> getScssDirectory();

}
