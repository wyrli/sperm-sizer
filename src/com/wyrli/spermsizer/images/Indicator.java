package com.wyrli.spermsizer.images;

import java.awt.Point;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Indicator {
	private static final double RADIUS = 10;
	private static final Duration DURATION = Duration.millis(200);

	private Circle indicator;
	private FadeTransition ft;
	private ScaleTransition st;

	public Indicator() {
		indicator = new Circle(RADIUS);
		indicator.setMouseTransparent(true);
		indicator.setOpacity(0);

		ft = new FadeTransition(DURATION, indicator);
		ft.setFromValue(1);
		ft.setToValue(0);

		st = new ScaleTransition(DURATION, indicator);
		st.setFromX(0.5);
		st.setFromY(0.5);
		st.setFromZ(0.5);
		st.setToX(1);
		st.setToY(1);
		st.setToZ(1);
	}

	public void indicate(ImageCanvas canvas, Point point, Color color) {
		if (point == null) {
			return;
		}

		// Make sure the indicator follows the image as it moves.
		indicator.translateXProperty().bind(canvas.translateXProperty()
				.add(canvas.scaleXProperty().multiply(canvas.widthProperty().divide(-2).add(point.x))));
		indicator.translateYProperty().bind(canvas.translateYProperty()
				.add(canvas.scaleYProperty().multiply(canvas.heightProperty().divide(-2).add(point.y))));

		indicator.setFill(color);
		ft.playFromStart();
		st.playFromStart();
	}

	public void addToPane(Pane pane) {
		pane.getChildren().add(indicator);
	}
}