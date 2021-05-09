package com.wyrli.spermsizer.images;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.wyrli.spermsizer.config.FolderHistory;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageSelector {
	private static final Set<File> SELECTED = new LinkedHashSet<File>();
	private static final FileChooser CHOOSER;

	static {
		CHOOSER = new FileChooser();
		CHOOSER.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Supported Images", "*.bmp", "*.gif",
				"*.jpg", "*.jpeg", "*.jpe", "*.jfif", "*.png", "*.tif", "*.tiff"));
	}

	public static List<File> selectFiles(Stage stage) {
		// Start from the last-visited directory.
		File last = new File(FolderHistory.getInputFolder());
		if (last.isDirectory()) {
			CHOOSER.setInitialDirectory(last);
		} else {
			CHOOSER.setInitialDirectory(null);
		}

		// Prompt the user to select files.
		List<File> files = CHOOSER.showOpenMultipleDialog(stage);

		if (files != null && !files.isEmpty()) {
			// Remember the last-visited directory.
			File parent = files.get(0).getParentFile();
			if (parent != null) {
				FolderHistory.setInputFolder(parent.getAbsolutePath());
			}

			// Remove duplicates.
			files = new ArrayList<File>(files);
			files.removeAll(SELECTED);

			// Add new files.
			SELECTED.addAll(files);
		}

		return files;
	}

	public static void clear() {
		SELECTED.clear();
	}
}