package io.silksmith.closure.depsparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

public class DepsParser {

	public static final String PROVIDE_PATTERN = "(?m)^\\s*goog\\.provide\\(\\s*['\"](.+)['\"]\\s*\\)";
	public static final String MODULE_PATTERN = "(?m)^\\s*goog\\.module\\(\\s*['\"](.+)['\"]\\s*\\)";
	public static final String REQUIRE_PATTERN = "(?m)^\\s*(?:(?:var|let|const)\\s+[a-zA-Z\\_\\$][a-zA-Z0-9\\$\\_]*\\s*=\\s*)?goog\\.require\\(\\s*[\\'\"](.+)[\\'\"]\\s*\\)";

	public static final Pattern PROVIDE = Pattern.compile(PROVIDE_PATTERN);
	public static final Pattern MODULE = Pattern.compile(MODULE_PATTERN);
	public static final Pattern REQUIRE = Pattern.compile(REQUIRE_PATTERN);

	@Data
	@AllArgsConstructor
	private static class DepInfo {
		private String provide;
		private String require;
		private String module;
	}

	public void parse(Path path) {

		try {
			Files.lines(path).map((String line) -> {

				String provide = PROVIDE.matcher(line).group(1);
				String module = MODULE.matcher(line).group(1);
				String require = REQUIRE.matcher(line).group(1);
				
				return new DepInfo(provide, require, module);
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Pattern.compile(PROVIDE_PATTERN).matcher("").
	}
}
