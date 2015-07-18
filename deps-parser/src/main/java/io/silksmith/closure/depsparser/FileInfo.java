package io.silksmith.closure.depsparser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {

	private Path file;
	private List<String> provides = new ArrayList<>();
	private List<String> requires = new ArrayList<>();
	private boolean module = false;

}
