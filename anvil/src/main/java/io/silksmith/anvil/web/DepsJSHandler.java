package io.silksmith.anvil.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DepsJSHandler {

	

	@RequestMapping("/deps.js")
	public void depsjs(HttpServletResponse response) {

		
		Path path = Paths.get(".");
		try {
			PrintWriter writer = response.getWriter();
			SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					
					writer.println(file.toUri());
					return FileVisitResult.CONTINUE;
				}

			};
			Files.walkFileTree(path, fileVisitor);
		} catch (Exception e) {
			log.error("Could not parse", e);
		}

	}

}
