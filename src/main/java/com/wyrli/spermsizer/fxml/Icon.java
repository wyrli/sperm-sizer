package com.wyrli.spermsizer.fxml;

import javafx.scene.image.Image;

public class Icon {
	public static final Image LOADING = new Image(getUrl("ui-progress-bar-indeterminate.gif"));
	public static final Image SUCCESS = new Image(getUrl("tick.png"));
	public static final Image FAILURE = new Image(getUrl("cross.png"));

	public static final Image NO_IMAGE = new Image(getUrl("picture--exclamation.png"));
	public static final Image POINT_FIRST = new Image(getUrl("layer-select-point-first.png"));
	public static final Image POINT_MID = new Image(getUrl("layer-select-point.png"));
	public static final Image POINT_LAST = new Image(getUrl("layer-select-point-last.png"));

	private static String getUrl(String fileName) {
		return Icon.class.getResource("icons/" + fileName).toString();
	}
}