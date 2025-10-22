package com.wyrli.spermsizer.measurements;

import java.awt.Point;

import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.images.ImageCanvas;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Selector {
	private boolean active;
	private Line line;

	public Selector() {
		line = new Line();
		line.setMouseTransparent(true);
		line.setVisible(false);
		line.setStrokeWidth(Settings.markerSize);
		line.setStrokeLineCap(StrokeLineCap.ROUND);
	}

	public void press(ImageCanvas canvas, Color color, double x, double y) {
		active = true;

		line.setStartX(x);
		line.setStartY(y);
		line.setEndX(x);
		line.setEndY(y);

		line.setStroke(color);
		line.setVisible(true);
		line.setOpacity(0.5);

		line.translateXProperty()
				.bind(canvas.translateXProperty().add(canvas.scaleXProperty().multiply(canvas.widthProperty().divide(-2)
						.add(x).add(line.endXProperty().subtract(line.startXProperty()).divide(2)))));
		line.translateYProperty()
				.bind(canvas.translateYProperty().add(canvas.scaleYProperty().multiply(canvas.heightProperty()
						.divide(-2).add(y).add(line.endYProperty().subtract(line.startYProperty()).divide(2)))));

		line.scaleXProperty().bind(canvas.scaleXProperty());
		line.scaleYProperty().bind(canvas.scaleYProperty());
	}

	public void drag(ImageCanvas canvas, double x, double y) {
		if (!active) {
			return;
		}

		line.setEndX(x);
		line.setEndY(y);
	}

	public Marker release() {
		if (!active) {
			return null;
		}

		line.setVisible(false);
		active = false;

		return new Marker(getPoint(true), getPoint(false));
	}

	private Point getPoint(boolean start) {
		int x = (int) Math.round((start ? line.getStartX() : line.getEndX()));
		int y = (int) Math.round((start ? line.getStartY() : line.getEndY()));
		return new Point(x, y);
	}

	public void addToPane(Pane pane) {
		pane.getChildren().add(line);
	}
}