package com.wyrli.spermsizer.measurements;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.config.Settings;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class TracePath {
	private static final DecimalFormat LENGTH_FORMAT = new DecimalFormat("#.##");

	private int index;
	private List<Point> path;
	private PathLabel label;

	public TracePath(int index) {
		this.index = index;
		path = new ArrayList<Point>();
	}

	public void update(List<Point> path, double canvasWidth) {
		if (path.size() < 2) {
			throw new IllegalArgumentException("Path must contain at least 2 points.");
		}
		this.path = path;

		// Update the label.
		Point midpoint = path.get(path.size() / 2);
		String text = Settings.labels[index] + " = " + LENGTH_FORMAT.format(getLength());
		Font font = new Font(Settings.labelSize);
		label = new PathLabel(midpoint, text, font, canvasWidth);
	}

	public double getLength() {
		double length = 0;

		for (int i = 1; i < path.size(); i++) {
			Point prev = path.get(i - 1);
			Point cur = path.get(i);

			double dx = cur.x - prev.x;
			double dy = cur.y - prev.y;
			length += Math.sqrt(dx * dx + dy * dy);
		}

		return length;
	}

	public void drawLine(GraphicsContext gc) {
		gc.setStroke(Settings.lineColors[index]);
		gc.setLineWidth(Settings.lineThickness);

		for (int i = 1; i < path.size(); i++) {
			Point prev = path.get(i - 1);
			Point cur = path.get(i);

			gc.strokeLine(prev.x, prev.y, cur.x, cur.y);
		}
	}

	public void drawLabel(GraphicsContext gc) {
		if (path.isEmpty()) {
			return;
		}

		gc.setFill(Settings.labelColor);
		gc.setTextBaseline(VPos.CENTER);

		gc.setFont(label.getFont());
		gc.setTextAlign(label.getAlignment());
		gc.fillText(label.getText(), label.getLocation().x, label.getLocation().y);
	}

	public PathLabel getLabel() {
		return label;
	}

	public List<Point> getPoints() {
		return path;
	}
}