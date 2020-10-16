package com.wyrli.spermsizer.images;

import java.awt.image.BufferedImage;
import java.util.List;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.measurements.Marker;
import com.wyrli.spermsizer.measurements.TraceHistory;
import com.wyrli.spermsizer.measurements.TracePath;
import com.wyrli.spermsizer.measurements.TraceResult;
import com.wyrli.spermsizer.measurements.TraceTask;

import ij.IJ;
import ij.ImagePlus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;

public class ImageCanvas extends Canvas {
	private static final double ZOOM_FACTOR = 0.12;

	private TraceHistory history;
	private GraphicsContext gc;
	private ObjectProperty<ImagePlus> imagePlusProperty = new SimpleObjectProperty<ImagePlus>();
	private Image image;

	private Point2D dragPosPrev;

	public ImageCanvas() {
		super();
		gc = getGraphicsContext2D();

		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
	}

	public boolean setImage(String path) {
		ImagePlus ip = IJ.openImage(path);
		if (ip == null) {
			return false;
		}

		// Flatten if not single image (e.g. animated GIF).
		if (ip.getStackSize() > 1) {
			ip = ip.flatten();
		}

		imagePlusProperty.setValue(ip);
		BufferedImage bufferedImage = imagePlusProperty.getValue().getBufferedImage();
		image = SwingFXUtils.toFXImage(bufferedImage, null);

		setWidth(image.getWidth());
		setHeight(image.getHeight());

		history = TraceHistory.get(path);
		draw();

		return true;
	}

	public void close() {
		imagePlusProperty.set(null);
		history = new TraceHistory();
		setWidth(0);
		setHeight(0);
	}

	public void pan(double x, double y) {
		Point2D current = new Point2D(x, y);

		if (dragPosPrev == null) {
			dragPosPrev = current;
			return;
		}

		Point2D delta = dragPosPrev.subtract(current);
		setTranslateX(getTranslateX() - delta.getX());
		setTranslateY(getTranslateY() - delta.getY());

		dragPosPrev = current;
	}

	public void endPan() {
		dragPosPrev = null;
	}

	public void addMarker(Marker marker) {
		List<Marker> markers = history.addMarker(marker);

		if (markers != null) {
			new TraceTask(this, markers).start();
		}

		draw();
	}

	public void zoomReposition(double x, double y, int oldValue, int newValue) {
		double scaleChange = getZoomScale(newValue) / getZoomScale(oldValue);
		Bounds boundsInScreen = localToScreen(getBoundsInLocal());
		double imageCenterX = boundsInScreen.getMinX() + boundsInScreen.getWidth() / 2;
		double imageCenterY = boundsInScreen.getMinY() + boundsInScreen.getHeight() / 2;

		double deltaX = (x - imageCenterX) * (scaleChange - 1);
		double deltaY = (y - imageCenterY) * (scaleChange - 1);

		setTranslateX(getTranslateX() - deltaX);
		setTranslateY(getTranslateY() - deltaY);
	}

	public int getPreferredZoom(int prefWidth, int prefHeight) {
		double prefZoomX = Math.log(prefWidth / getWidth()) / ZOOM_FACTOR;
		double prefZoomY = Math.log(prefHeight / getHeight()) / ZOOM_FACTOR;
		int prefZoom = (int) Math.floor(prefZoomX < prefZoomY ? prefZoomX : prefZoomY);

		if (prefZoom > 0) {
			prefZoom = 0;
		}

		return prefZoom;
	}

	public void setZoom(int value) {
		double scale = getZoomScale(value);

		setScaleX(scale);
		setScaleY(scale);
	}

	public void center() {
		setTranslateX(0);
		setTranslateY(0);
	}

	public static double getZoomScale(int value) {
		return Math.exp(ZOOM_FACTOR * value);
	}

	public void draw() {
		gc.drawImage(image, 0, 0);

		// Draw visible lines.
		for (TraceResult result : history.getResults()) {
			for (TracePath path : result.getPaths()) {
				path.drawLine(gc);
			}
		}

		double size = Settings.markerSize;
		gc.setFill(Settings.markerColor);

		// Draw visible markers.
		for (Marker marker : history.getMarkers()) {
			gc.fillOval(marker.getStart().x - size / 2, marker.getStart().y - size / 2, size, size);

			if (marker.isLine()) {
				gc.fillOval(marker.getEnd().x - size / 2, marker.getEnd().y - size / 2, size, size);
			}
		}

		// Draw visible labels.
		for (TraceResult result : history.getResults()) {
			for (TracePath path : result.getPaths()) {
				path.drawLabel(gc);
			}
		}

		// Notify controller.
		Main.getController().imageDrawn();
	}

	public ObjectProperty<ImagePlus> imagePlusProperty() {
		return imagePlusProperty;
	}

	public ImagePlus getImagePlus() {
		return imagePlusProperty.getValue();
	}

	public TraceHistory getHistory() {
		return history;
	}
}