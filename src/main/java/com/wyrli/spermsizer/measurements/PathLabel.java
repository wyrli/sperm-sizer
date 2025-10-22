package com.wyrli.spermsizer.measurements;

import java.awt.Point;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PathLabel {
	private final Point location;
	private final String text;
	private final Font font;
	private final TextAlignment alignment;
	private final Rectangle2D boundingBox;

	public PathLabel(Point location, String text, Font font, double canvasWidth) {
		this.location = location;
		this.text = text;
		this.font = font;

		Text label = new Text(text);
		label.setFont(font);

		// Determine the size of the label.
		Bounds layoutBounds = label.getLayoutBounds();
		double w = layoutBounds.getWidth();
		double h = layoutBounds.getHeight();

		TextAlignment[] alignments = { TextAlignment.CENTER, TextAlignment.LEFT, TextAlignment.RIGHT };
		AlignmentOption bestOption = null;
		double minClipped = Double.MAX_VALUE;

		// Choose a text alignment that minimizes the distance clipped. This is a measurement of
		// how much the label is "clipped" (i.e. lying outside of the canvas boundary).
		for (TextAlignment alignment : alignments) {
			AlignmentOption option = new AlignmentOption(alignment, location.x, w, canvasWidth);
			if (option.distanceClipped < minClipped) {
				minClipped = option.distanceClipped;
				bestOption = option;
			}
		}

		alignment = bestOption.alignment;
		boundingBox = new Rectangle2D(bestOption.xMin, location.y - h / 2, w, h);
	}

	public Point getLocation() {
		return location;
	}

	public String getText() {
		return text;
	}

	public Font getFont() {
		return font;
	}

	public TextAlignment getAlignment() {
		return alignment;
	}

	public Rectangle2D getBoundingBox() {
		return boundingBox;
	}

	private class AlignmentOption {
		private TextAlignment alignment;
		private double xMin;
		private double distanceClipped;

		private AlignmentOption(TextAlignment alignment, double x, double width, double xLimit) {
			this.alignment = alignment;

			if (alignment == TextAlignment.LEFT) {
				xMin = x;
			} else if (alignment == TextAlignment.RIGHT) {
				xMin = x - width;
			} else {
				xMin = x - width / 2;
			}

			distanceClipped = 0;
			if (xMin < 0) {
				distanceClipped += Math.abs(xMin);
			}

			double xMax = xMin + width;
			if (xMax > xLimit) {
				distanceClipped += xMax - xLimit;
			}
		}
	}
}