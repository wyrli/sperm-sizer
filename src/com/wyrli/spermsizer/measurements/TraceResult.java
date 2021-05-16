package com.wyrli.spermsizer.measurements;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.fxml.Icon;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class TraceResult {
	private static final String MSG_LOADING = "Processing...";
	private static final String MSG_FAILURE = "Not Detected";

	private WeakReference<TraceTask> taskRef;
	private boolean success = false;

	private Label label;
	private List<TracePath> paths = new ArrayList<TracePath>();

	public TraceResult(TraceTask task) {
		this.taskRef = new WeakReference<TraceTask>(task);

		label = new Label(MSG_LOADING, new ImageView(Icon.LOADING));

		for (int i = 0; i < Settings.markers - 1; i++) {
			paths.add(new TracePath(i));
		}
	}

	public void cancelTask() {
		TraceTask task = taskRef.get();
		if (task != null) {
			task.cancel();
		}
	}

	public Label getListViewItem() {
		return label;
	}

	public List<TracePath> getPaths() {
		return paths;
	}

	public void setSuccess(boolean success) {
		this.success = success;

		if (success) {
			label.setText(getLengthsFormatted());
			label.setGraphic(new ImageView(Icon.SUCCESS));
		} else {
			label.setText(MSG_FAILURE);
			label.setGraphic(new ImageView(Icon.FAILURE));
		}
	}

	public boolean isSuccessful() {
		return success;
	}

	/** Returns the lengths (as integers) of all segments as a string, separated by ", ". */
	private String getLengthsFormatted() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < paths.size(); i++) {
			sb.append((int) paths.get(i).getLength());

			if (i != paths.size() - 1) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}

	/** Returns the lengths of all segments as a string, separated by commas. */
	public String getLengths() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < paths.size(); i++) {
			sb.append(paths.get(i).getLength());

			if (i != paths.size() - 1) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public Rectangle2D getBoundingBox(int padding) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (TracePath path : paths) {
			for (Point p : path.getPoints()) {
				if (p.x < minX) {
					minX = p.x;
				}

				if (p.y < minY) {
					minY = p.y;
				}

				if (p.x > maxX) {
					maxX = p.x;
				}

				if (p.y > maxY) {
					maxY = p.y;
				}
			}

			Rectangle2D label = path.getLabel().getBoundingBox();

			if (label.getMinX() < minX) {
				minX = (int) label.getMinX();
			}

			if (label.getMinY() < minY) {
				minY = (int) label.getMinY();
			}

			if (label.getMaxX() > maxX) {
				maxX = (int) label.getMaxX();
			}

			if (label.getMaxY() > maxY) {
				maxY = (int) label.getMaxY();
			}
		}

		int width = maxX - minX;
		int height = maxY - minY;

		minX -= padding;
		minY -= padding;
		width += padding * 2;
		height += padding * 2;

		return new Rectangle2D(minX, minY, width, height);
	}
}