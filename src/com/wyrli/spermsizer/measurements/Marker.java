package com.wyrli.spermsizer.measurements;

import java.awt.Point;

public class Marker {
	private Point start;
	private Point end;
	private double distance;

	public Marker(Point point) {
		this(point, point);
	}

	public Marker(Point start, Point end) {
		this.start = start;
		this.end = end;
		distance = start.distance(end);
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public double getDistance() {
		return distance;
	}

	public boolean isLine() {
		return distance > 0;
	}
}