package com.wyrli.spermsizer.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.info.Notification;

import javafx.scene.paint.Color;

public class Settings {
	private static final String CONFIG = "config.ini";
	private static final String HISTORY = "history.cache";

	private static final String JAR_FOLDER = getJarFolder();
	private static final String PATH_CONFIG = Paths.get(JAR_FOLDER, CONFIG).toString();
	private static final String PATH_HISTORY = Paths.get(JAR_FOLDER, HISTORY).toString();

	private static final String KEY_FOLDER_INPUT = "LastInput";
	private static final String KEY_FOLDER_OUTPUT = "LastOutput";

	public static int timeout = 3000;

	public static int markers = 4;
	public static Color markerColor = Color.RED;
	public static double markerSize = 3;

	public static Color[] lineColors = { Color.RED, Color.ORANGE, Color.YELLOW };
	public static double lineThickness = 2;

	public static String[] labels = { "Head", "Body", "Tail" };
	public static Color labelColor = Color.BLACK;
	public static double labelSize = 12;

	public static int lineSmoothing = 4;

	public static boolean snapEnabled = true;
	public static int trimRadius = 8;

	public static int cropPadding = 20;
	public static boolean compressCroppedImages = true;

	public static String folderLastInput = "";
	public static String folderLastOutput = "";

	public static void init() {
		loadHistory();

		if (!loadConfig()) {
			// Regenerate the configuration file.
			create(PATH_CONFIG, CONFIG);
			Notification.configRegenerated();

			// Attempt to load once more.
			if (!loadConfig()) {
				Notification.invalidConfig();
				System.exit(0);
			}
		}
	}

	public static void setFolder(boolean input, String path) {
		if (input) {
			folderLastInput = path;
		} else {
			folderLastOutput = path;
		}

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

	private static void create(String filePath, String resourceName) {
		try {
			Files.copy(Settings.class.getResourceAsStream(resourceName), Paths.get(filePath),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<String> readAllLines(String filePath) {
		try {
			return Files.readAllLines(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}

	private static void loadHistory() {
		// Create if missing.
		if (!new File(PATH_HISTORY).isFile()) {
			create(PATH_HISTORY, HISTORY);
		}

		List<String> lines = readAllLines(PATH_HISTORY);
		if (lines == null) {
			return;
		}

		// File not designed to be human-editable, reducing need for validation.
		try {
			for (String line : lines) {
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
			// Let file be recreated.
			return;
		}
	}

	private static boolean loadConfig() {
		List<String> lines = readAllLines(PATH_CONFIG);
		if (lines == null || !hasAllKeys(lines)) {
			return false;
		}

		try {
			for (String line : lines) {
				if (!line.contains("=")) {
					continue;
				}

				String[] entry = line.split("=", 2);
				String key = entry[0].trim();
				String value = entry[1].trim();

				switch (key) {
				case "Timeout":
					timeout = Integer.parseInt(value);
					break;

				case "MarkerColor":
					markerColor = Color.web(value);
					break;

				case "MarkerSize":
					markerSize = Double.parseDouble(value);
					break;

				case "LineColors":
					String[] strColors = value.split(",");
					lineColors = new Color[strColors.length];
					for (int i = 0; i < strColors.length; i++) {
						lineColors[i] = Color.web(strColors[i]);
					}
					break;

				case "LineThickness":
					lineThickness = Double.parseDouble(value);
					break;

				case "Labels":
					labels = value.split(",");
					for (int i = 0; i < labels.length; i++) {
						// Remove quotes; they are used to escape commas in CSV outputs.
						labels[i] = labels[i].replace("\"", "");
					}
					break;

				case "LabelColor":
					labelColor = Color.web(value);
					break;

				case "LabelSize":
					labelSize = Double.parseDouble(value);
					break;

				case "LineSmoothing":
					lineSmoothing = Integer.parseInt(value);
					break;

				case "SnapEnabled":
					snapEnabled = Boolean.parseBoolean(value);
					break;

				case "TrimRadius":
					trimRadius = Integer.parseInt(value);
					break;

				case "CropPadding":
					cropPadding = Integer.parseInt(value);
					break;

				case "CompressCroppedImages":
					compressCroppedImages = Boolean.parseBoolean(value);
					break;

				default:
					break;
				}
			}

			// There needs to be at least one label, or there'd be nothing to measure.
			if (labels.length <= 0) {
				return false;
			}

			// e.g. if you have 3 labels, then you'll need to place 4 markers.
			markers = labels.length + 1;

			// If there are more labels than line colors, use red for the rest.
			if (labels.length > lineColors.length) {
				Color[] correction = new Color[labels.length];

				for (int i = 0; i < labels.length; i++) {
					if (i < lineColors.length) {
						correction[i] = lineColors[i];
					} else {
						correction[i] = Color.RED;
					}
				}

				lineColors = correction;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean hasAllKeys(List<String> lines) {
		List<String> keys = new ArrayList<String>();
		keys.add("Timeout");
		keys.add("MarkerColor");
		keys.add("MarkerSize");
		keys.add("LineColors");
		keys.add("LineThickness");
		keys.add("Labels");
		keys.add("LabelColor");
		keys.add("LabelSize");
		keys.add("LineSmoothing");
		keys.add("SnapEnabled");
		keys.add("TrimRadius");
		keys.add("CropPadding");
		keys.add("CompressCroppedImages");

		for (String line : lines) {
			String[] array = line.split("=");

			if (array.length < 2) {
				continue;
			}

			String key = array[0].trim();
			keys.remove(key);
		}
		return keys.isEmpty();
	}

	/**
	 * Returns the path to the folder in which this JAR is located. If one could not be found, returns
	 * an empty string, which represents the current working directory.
	 */
	private static String getJarFolder() {
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