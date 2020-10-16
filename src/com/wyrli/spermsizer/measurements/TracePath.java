package com.wyrli.spermsizer.measurements;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.config.Settings;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public class TracePath {
	private static final DecimalFormat LENGTH_FORMAT = new DecimalFormat("#.##");

	private int index;
	private List<Point> path;

	public TracePath(int index) {
		this.index = index;
		path = new ArrayList<Point>();
	}

	public void setPath(List<Point> path) {
		if (path.size() < 2) {
			throw new IllegalArgumentException("Path must contain at least 2 points.");
		}
		this.path = path;
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
		gc.setFont(new Font(Settings.labelSize));

		Point mid = path.get(path.size() / 2);
		String length = LENGTH_FORMAT.format(getLength());

		gc.fillText(Settings.labels[index] + " = " + length, mid.x, mid.y);
	}

	public List<Point> getPoints() {
		return path;
	}
}