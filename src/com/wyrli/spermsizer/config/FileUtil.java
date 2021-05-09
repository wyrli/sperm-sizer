package com.wyrli.spermsizer.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.Main;

public class FileUtil {

	/** Copies a resource in the 'config' package to a certain location. */
	public static Exception copyConfigResource(String filePath, String resourceName) {
		try {
			Files.copy(Settings.class.getResourceAsStream(resourceName), Paths.get(filePath),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			return e;
		}
		return null;
	}

	/** Returns all lines from a file, or an empty list if the file could not be read. */
	public static List<String> readAllLines(String filePath) {
		try {
			return Files.readAllLines(Paths.get(filePath));
		} catch (IOException e) {
			return new ArrayList<String>();
		}
	}

	/** Returns the JAR folder path. If not found, returns the current working directory. */
	public static String getJarFolder() {
		File jar;

		try {
			jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (SecurityException | URISyntaxException e) {
			return "";
		}

		File folder = jar.getParentFile();
		return folder == null ? "" : folder.getAbsolutePath();
	}
}