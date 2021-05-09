package com.wyrli.spermsizer.info;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Notification {
	public static void invalidImage(String filePath) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Invalid Image");
		alert.setHeaderText("Image could not be loaded.");
		alert.setContentText("The image at '" + filePath + "' may be missing or invalid.");
		alert.showAndWait();
	}

	public static void generic(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setGraphic(null);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void configRegenerated() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Invalid Configuration");
		alert.setHeaderText("config.ini missing or invalid.");
		alert.setContentText("The file has been regenerated.");
		alert.showAndWait();
	}

	public static boolean confirmClearAll() {
		Alert alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.CANCEL);
		alert.setTitle("Clear All");
		alert.setHeaderText("Remove all images and measurements?");
		alert.setContentText("This cannot be undone.");
		alert.showAndWait();

		return alert.getResult() == ButtonType.YES;
	}
}