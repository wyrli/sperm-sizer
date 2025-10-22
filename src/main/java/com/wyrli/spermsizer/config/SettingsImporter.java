package com.wyrli.spermsizer.config;

import java.io.File;

import com.wyrli.spermsizer.Main;

import javafx.stage.FileChooser;

public class SettingsImporter {
	private static final FileChooser CHOOSER;
	private static String lastDir = "";

	static {
		CHOOSER = new FileChooser();
		CHOOSER.setTitle("Import Configuration");
		CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration Files (*.ini)", "*.ini"));
	}

	public static File askForFile() {
		// Start from the last-visited directory.
		File last = new File(lastDir);
		if (last.isDirectory()) {
			CHOOSER.setInitialDirectory(last);
		} else {
			CHOOSER.setInitialDirectory(null);
		}

		File file = CHOOSER.showOpenDialog(Main.getPrimaryStage());
		if (file == null) {
			return null;
		}

		// Remember the last-visited directory.
		File parent = file.getParentFile();
		if (parent != null) {
			lastDir = parent.getAbsolutePath();
		}

		return file;
	}
}