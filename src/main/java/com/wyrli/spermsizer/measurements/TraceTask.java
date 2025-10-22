package com.wyrli.spermsizer.measurements;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.images.ImageCanvas;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import javafx.application.Platform;

public class TraceTask implements Runnable {
	private boolean started = false;
	private boolean cancelled = false;
	private BinaryProcessor bp = null;

	private ImageCanvas imageCanvas;
	private ImagePlus imagePlus;

	private List<Marker> markers;
	private TraceResult result;

	public TraceTask(ImageCanvas imageCanvas, List<Marker> markers) {
		this.imageCanvas = imageCanvas;
		this.imagePlus = imageCanvas.getImagePlus();
		this.markers = new ArrayList<Marker>(markers);
		result = new TraceResult(this);

		imageCanvas.getHistory().addResult(result);
	}

	public void start() {
		if (started) {
			throw new IllegalStateException("Task has already started.");
		}
		started = true;
		new Thread(this).start();
	}

	@Override
	public void run() {
		boolean success = trace();

		if (cancelled) {
			return;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				result.setSuccess(success);
				imageCanvas.draw();
			}
		});
	}

	/** Stops the task at its earliest convenience. */
	public void cancel() {
		cancelled = true;

		if (bp != null) {
			bp.setTimeout(0);
		}
	}

	/** Returns a List view of all successfully-traced line segments. */
	public boolean trace() {
		// Create a list of points to represent each marker.
		List<Point> points = new ArrayList<Point>();
		for (int i = 0; i < markers.size(); i++) {
			points.add(i == 0 ? markers.get(i).getEnd() : markers.get(i).getStart());
		}

		ImagePlus image = toBinaryBridge(imagePlus, points);
		if (image == null) {
			return false;
		}

		// Apply line smoothing. "Select None" is called to ensure that blurring is applied across the
		// entire image and not just the current selection (a.k.a. region of interest).
		if (Settings.lineSmoothing >= 0) {
			IJ.run(image, "Select None", "");
			IJ.run(image, "Gaussian Blur...", "sigma=" + Settings.lineSmoothing);

			image = toBinaryBridge(image, points);
			if (image == null) {
				return false;
			}
		}

		// Produce a skeleton.
		bp = new BinaryProcessor(((ByteProcessor) image.getProcessor()), Settings.timeout);
		boolean success = bp.skeletonizeInterruptible();
		if (!success) {
			return false;
		}

		// Represent the skeleton as a set of points.
		Set<Point> skeleton = new HashSet<Point>();
		for (Point p : image.getRoi()) {
			if (isBlackPixel(image, p)) {
				skeleton.add(p);
			}
		}

		// If there are no pixels, something went wrong.
		if (skeleton.isEmpty()) {
			return false;
		}

		int last = points.size() - 1;
		Point[] closest = new Point[points.size()];

		// Get the closest point on the skeleton to the first marker.
		closest[0] = getClosestPoint(skeleton, points.get(0));

		// Get the closest point on the skeleton to the last marker.
		closest[last] = getClosestPoint(skeleton, points.get(last));

		// Find the shortest path between those two points.
		List<Point> path = BreadthFirstSearch.getShortestPath(skeleton, closest[0], closest[last]);

		// Get the closest point on the skeleton to all other markers.
		for (int j = 1; j < last; j++) {
			closest[j] = getClosestPoint(path, points.get(j));
		}

		// Segment the path.
		for (int i = 0; i < last; i++) {
			List<Point> subPath = BreadthFirstSearch.getShortestPath(path, closest[i], closest[i + 1]);

			if (Settings.snapEnabled) {
				if (i == 0) {
					// Make sure the first path starts with the first marker.
					subPath = snap(subPath, points.get(0), Settings.trimRadius);

				}

				if (i == last - 1) {
					// Make sure the last path ends with the last marker.
					Collections.reverse(subPath);
					subPath = snap(subPath, points.get(last), Settings.trimRadius);
					Collections.reverse(subPath);
				}
			}

			if (i == 0) {
				Marker m = markers.get(0);

				// If the first marker is a line, include it in the path.
				if (m.isLine() && !m.getStart().equals(subPath.get(0))) {
					subPath.add(0, m.getStart());
				}
			}

			if (i == last - 1) {
				Marker m = markers.get(last);

				// If the last marker is a line, include it in the path.
				if (m.isLine() && !m.getEnd().equals(subPath.get(subPath.size() - 1))) {
					subPath.add(m.getEnd());
				}
			}

			result.getPaths().get(i).update(subPath, imageCanvas.getWidth());
		}

		return true;
	}

	/**
	 * Converts an image into a binary image by locking the upper threshold at 255 and choosing a lower
	 * threshold such that a bridge is formed across the given points. Returns the bridge as a mask (no
	 * holes), or null if no solution could be found.
	 */
	private static ImagePlus toBinaryBridge(ImagePlus image, Iterable<Point> points) {
		ImagePlus binImg = null;
		Roi roi = null;

		int left = 0;
		int right = 255;

		// Perform thresholding until the exact moment all markers are connected.
		// A binary search is used to perform no more than 8 iterations.
		while (left <= right) {
			int mid = (left + right) / 2;

			// Create a binary image via thresholding.
			ImagePlus binImgTest = image.duplicate();
			toBinary(binImgTest, mid, 255);

			// Check if the markers are connected. If not, continue.
			Roi roiTest = getConnectedRoi(binImgTest, points);
			if (roiTest == null) {
				left = mid + 1;
				continue;
			}

			// Store the last successful binary image and selection.
			binImg = binImgTest;
			roi = roiTest;

			// Continue until there are no more values to try.
			right = mid - 1;
		}

		// Stop if no solution was found.
		if (binImg == null) {
			return null;
		}

		// Override the current ROI with the accepted one.
		binImg.setRoi(roi);

		// Convert the ROI to a mask.
		roiToMask(binImg);

		return binImg;
	}

	/** Converts an image into a binary image using a lower and upper threshold. */
	private static void toBinary(ImagePlus image, int lower, int upper) {
		IJ.run(image, "8-bit", "");
		IJ.setThreshold(image, lower, upper);
		IJ.run("Options...", "iterations=1 count=1");
		IJ.run(image, "Convert to Mask", "");
		IJ.run(image, "Invert LUT", "");
	}

	/**
	 * Returns a ROI that contains all of the given points. The ROI is obtained by applying an
	 * 8-connected wand selection at each point. Returns null if no such ROI could be found.
	 */
	private static Roi getConnectedRoi(ImagePlus image, Iterable<Point> points) {
		for (Point p : points) {
			if (!isBlackPixel(image, p)) {
				continue;
			}

			// Perform a wand selection on the point (to check if all points are connected).
			Roi roi = getWandSelection(image, p);
			if (roiContainsPoints(roi, points)) {
				return roi;
			}
		}
		return null;
	}

	/** Returns the ROI obtained by applying an 8-connected wand selection at a given point. */
	private static Roi getWandSelection(ImagePlus image, Point point) {
		IJ.doWand(image, point.x, point.y, 0, "8-connected");
		return image.getRoi();
	}

	/** Indicates whether all given points are contained within a ROI. */
	private static boolean roiContainsPoints(Roi roi, Iterable<Point> points) {
		for (Point p : points) {
			if (!roi.contains(p.x, p.y)) {
				return false;
			}
		}
		return true;
	}

	/** Converts an image into a mask using its current ROI. */
	private static void roiToMask(ImagePlus image) {
		if (image.getRoi() == null) {
			return;
		}

		IJ.setBackgroundColor(0, 0, 0);
		IJ.run(image, "Clear", "slice");

		IJ.setBackgroundColor(255, 255, 255);
		IJ.run(image, "Clear Outside", "");
	}

	/** Returns the point (from a list of points) that is closest to a given point. */
	private static Point getClosestPoint(Iterable<Point> points, Point point) {
		Point closest = null;
		double minDistSqr = Double.MAX_VALUE;

		for (Point p : points) {
			double distSqr = point.distanceSq(p);
			if (distSqr < minDistSqr) {
				minDistSqr = distSqr;
				closest = p;
			}
		}

		return closest;
	}

	/**
	 * Returns a view of the given list between the snap index (inclusive) and the last index of the
	 * list (inclusive). The snap index is the index of the point on the list that is both closest to
	 * the end of the list (in terms of index number) and within the given radius (in pixels) of the
	 * given point. The returned list will always begin with the given point.
	 */
	private static List<Point> snap(List<Point> path, Point point, double radius) {
		if (path.size() < 2) {
			throw new IllegalArgumentException("Path must contain at least 2 points.");
		}

		double radiusSqr = radius * radius;

		// Start at 'size - 2' to avoid producing a list with fewer than 2 points.
		for (int i = path.size() - 2; i >= 0; i--) {
			double dSqr = path.get(i).distanceSq(point);

			if (dSqr <= radiusSqr) {
				path = path.subList(i, path.size());
				break;
			}
		}

		if (!path.get(0).equals(point)) {
			path.add(0, point);
		}

		return path;
	}

	/** Indicates whether a pixel on a binary image is black. */
	private static boolean isBlackPixel(ImagePlus binImg, Point point) {
		return binImg.getPixel(point.x, point.y)[0] == 0;
	}
}