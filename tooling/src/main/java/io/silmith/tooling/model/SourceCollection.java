package io.silmith.tooling.model;

import java.io.Serializable;
import java.nio.file.Path;

public interface SourceCollection extends Serializable{

	public Iterable<Path> getFiles();
}
