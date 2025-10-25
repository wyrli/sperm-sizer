package com.wyrli.spermsizer.fxml;

import javafx.scene.image.Image;

public class Icon {
	public static final Image LOADING = loadImage("ui-progress-bar-indeterminate.gif");
	public static final Image SUCCESS = loadImage("tick.png");
	public static final Image FAILURE = loadImage("cross.png");

	public static final Image NO_IMAGE = loadImage("picture--exclamation.png");
	public static final Image POINT_FIRST = loadImage("layer-select-point-first.png");
	public static final Image POINT_MID = loadImage("layer-select-point.png");
	public static final Image POINT_LAST = loadImage("layer-select-point-last.png");

	private static Image loadImage(String fileName) {
		var url = Icon.class.getResource("/fxml/icons/" + fileName);
		if (url == null) {
			throw new IllegalArgumentException("Resource missing: " + fileName);
		}
		return new Image(url.toExternalForm());
	}
}