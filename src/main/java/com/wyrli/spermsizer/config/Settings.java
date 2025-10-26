package com.wyrli.spermsizer.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.info.Error;
import com.wyrli.spermsizer.info.Notification;

import javafx.scene.paint.Color;

public class Settings {
	private static final String FILENAME_CONFIG = "config.ini";
	private static final String PATH_CONFIG = Paths.get(FileUtil.getStorageFolder(), FILENAME_CONFIG).toString();

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

	/** Loads settings from a file. If the file does not exist, it will be created. */
	public static void init() {
		List<Exception> exceptions = new ArrayList<Exception>();

		// Attempt to load the configuration file.
		Exception eFirstLoad = loadConfigFile(PATH_CONFIG);
		if (eFirstLoad == null) {
			return;
		}
		exceptions.add(eFirstLoad);

		// Regenerate the configuration file.
		Exception eCreate = FileUtil.copyConfigResource(PATH_CONFIG, FILENAME_CONFIG);
		if (eCreate != null) {
			exceptions.add(eCreate);
		}

		// Attempt to load a second time.
		Exception eSecondLoad = loadConfigFile(PATH_CONFIG);
		if (eSecondLoad == null) {
			Notification.configRegenerated();
			return;
		}
		exceptions.add(eSecondLoad);

		// Display an error.
		Error.show(Error.Type.CONFIG, exceptions);
	}

	/** Import the configuration file via a FileChooser. */
	public static boolean importManually() {
		File file = SettingsImporter.askForFile();

		if (file == null) {
			return false;
		}

		Exception exception = Settings.loadConfigFile(file.getAbsolutePath());
		if (exception == null) {
			Notification.generic("Import Successful", "Your configuration file has been loaded.");
			return true;
		}

		if (exception instanceof IOException) {
			Notification.generic("Import Failed", Main.TITLE + " could not read the selected file.");
		} else {
			Notification.generic("Import Failed", "The selected file is not a valid configuration file.");
		}

		return false;
	}

	/** Loads properties from a configuration file. */
	private static Exception loadConfigFile(String filePath) {
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(filePath));
		} catch (IOException e) {
			return e;
		}

		if (lines == null || !hasAllKeys(lines)) {
			return new IllegalArgumentException("Configuration file has an incorrect format.");
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
				return new IllegalArgumentException("No labels have been provided.");
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

			return null;
		} catch (Exception e) {
			return e;
		}
	}

	/** Given the lines of a configuration file, returns whether all required keys are present. */
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
}