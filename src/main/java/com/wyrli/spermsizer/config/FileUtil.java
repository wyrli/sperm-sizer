package com.wyrli.spermsizer.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
		try (InputStream input = Settings.class.getResourceAsStream("/config/" + resourceName)) {
			if (input == null) {
				return new FileNotFoundException("Resource missing: " + resourceName);
			}
			Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
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
		try {
			URI location = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();

			// Not a normal file (e.g. "jrt:/" or "module:/").
			if (!location.getScheme().equalsIgnoreCase("file")) {
				return System.getProperty("user.dir");
			}

			File file = new File(location);
			File folder = file.getParentFile();

			// Windows jpackage ("/app/"). Go one level up to "MyApp/".
			if (folder != null && folder.getName().equalsIgnoreCase("app")) {
				folder = folder.getParentFile();
			}

			// macOS jpackage ("/MyApp.app/Contents/app/"). Go up three levels.
			if (folder != null && folder.getPath().contains(".app/Contents/app")) {
				folder = folder.getParentFile().getParentFile().getParentFile();
			}

			return folder != null ? folder.getAbsolutePath() : System.getProperty("user.dir");
		} catch (Exception e) {
			return System.getProperty("user.dir");
		}
	}
}