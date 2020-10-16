package com.wyrli.spermsizer.images;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.wyrli.spermsizer.config.Settings;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageSelector {
	private static Set<File> selected = new LinkedHashSet<File>();
	private static FileChooser chooser = null;

	static {
		chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Supported Images", "*.bmp", "*.gif",
				"*.jpg", "*.jpeg", "*.jpe", "*.jfif", "*.png", "*.tif", "*.tiff"));
	}

	public static List<File> selectFiles(Stage stage) {
		// Start from the last-visited directory.
		File last = new File(Settings.folderLastInput);
		if (last.isDirectory()) {
			chooser.setInitialDirectory(last);
		}

		// Prompt the user to select files.
		List<File> files = chooser.showOpenMultipleDialog(stage);

		if (files != null && !files.isEmpty()) {
			// Remember the last-visited directory.
			Settings.setFolder(true, files.get(0).getParentFile().getAbsolutePath());

			// Remove duplicates.
			files = new ArrayList<File>(files);
			files.removeAll(selected);

			// Add new files.
			selected.addAll(files);
		}

		return files;
	}

	public static void clear() {
		selected.clear();
	}
}