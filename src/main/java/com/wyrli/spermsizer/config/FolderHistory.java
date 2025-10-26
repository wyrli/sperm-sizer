package com.wyrli.spermsizer.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class FolderHistory {
	private static final String FILENAME_HISTORY = "history.cache";
	private static final String PATH_HISTORY = Paths.get(FileUtil.getStorageFolder(), FILENAME_HISTORY).toString();

	private static final String KEY_FOLDER_INPUT = "LastInput";
	private static final String KEY_FOLDER_OUTPUT = "LastOutput";

	private static String folderLastInput = "";
	private static String folderLastOutput = "";

	/** Loads folder paths from a cache file. If the cache file does not exist, it will be created. */
	public static void init() {
		createCacheFile();
		loadCacheFile();
	}

	/** Creates the cache file if it does not already exist. */
	private static void createCacheFile() {
		if (!new File(PATH_HISTORY).isFile()) {
			FileUtil.copyConfigResource(PATH_HISTORY, FILENAME_HISTORY);
		}
	}

	/** Loads previously-visited folders from a cache file. */
	private static void loadCacheFile() {
		// File not designed to be human-editable, reducing need for validation.
		try {
			for (String line : FileUtil.readAllLines(PATH_HISTORY)) {
				String[] entry = line.split("=");
				String key = entry[0];
				String value = entry[1];

				if (key.equals(KEY_FOLDER_INPUT)) {
					folderLastInput = value;
				} else if (key.equals(KEY_FOLDER_OUTPUT)) {
					folderLastOutput = value;
				}
			}
		} catch (Exception e) {
			// Let file be recreated the next time a folder is selected.
		}
	}

	/** Caches the input folder for image imports. */
	public static void setInputFolder(String path) {
		folderLastInput = path;
		updateCacheFile();
	}

	/** Caches the output folder for measurement exports. */
	public static void setOutputFolder(String path) {
		folderLastOutput = path;
		updateCacheFile();
	}

	/** Returns the last folder used to import an image. */
	public static String getInputFolder() {
		return folderLastInput;
	}

	/** Returns the last folder used to export measurements. */
	public static String getOutputFolder() {
		return folderLastOutput;
	}

	/** Attempts to write the current folder paths to a cache file. */
	private static void updateCacheFile() {
		try {
			FileWriter writer = new FileWriter(PATH_HISTORY);
			writer.write(KEY_FOLDER_INPUT + "=" + folderLastInput);
			writer.write(System.lineSeparator());
			writer.write(KEY_FOLDER_OUTPUT + "=" + folderLastOutput);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}