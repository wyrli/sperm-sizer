package com.wyrli.spermsizer.exports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.fxml.ExportController;
import com.wyrli.spermsizer.info.Error;
import com.wyrli.spermsizer.info.Notification;
import com.wyrli.spermsizer.measurements.TraceHistory;
import com.wyrli.spermsizer.measurements.TracePath;
import com.wyrli.spermsizer.measurements.TraceResult;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ExportTask implements Runnable {
	private boolean cancelled = false;

	private String folder;
	private ExportController controller;

	private Canvas canvas = new Canvas();
	private GraphicsContext gc = canvas.getGraphicsContext2D();

	public ExportTask(String folder, ExportController controller) {
		this.folder = folder;
		this.controller = controller;
	}

	public void cancel() {
		cancelled = true;
	}

	@Override
	public void run() {
		DataWriter writer = null;

		try {
			// Create the directory.
			String timestamp = new SimpleDateFormat("'Results' (yyyy-MM-dd HH-mm-ss)").format(new Date());
			String resultsDir = folder + File.separatorChar + timestamp;
			new File(resultsDir).mkdirs();

			// Record the settings used.
			writer = new DataWriter(resultsDir, "settings.txt");
			writer.write("LineSmoothing=" + Settings.lineSmoothing);
			writer.write("SnapEnabled=" + Settings.snapEnabled);
			writer.write("TrimRadius=" + Settings.trimRadius);
			writer.close();

			// Create the CSV file.
			writer = new DataWriter(resultsDir, "results.csv");
			writer.write(getHeaders());

			int processed = 0;
			int cropCount = 1;
			int total = getTotalResults();

			main: for (String filePath : TraceHistory.ALL.keySet()) {
				ImagePlus ip = IJ.openImage(filePath);
				if (ip == null) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Notification.invalidImage(filePath);
						}
					});
					continue;
				}

				BufferedImage bufferedImage = ip.getBufferedImage();
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);

				canvas.setWidth(image.getWidth());
				canvas.setHeight(image.getHeight());

				String fileName = getFileName(ip);

				for (TraceResult result : TraceHistory.ALL.get(filePath).getResults()) {
					processed++;

					if (!result.isSuccessful()) {
						continue;
					}

					drawMeasurement(gc, image, result);

					String savedFile = fileName + " (" + cropCount + ").png";
					saveImage(canvas, gc, result, resultsDir + File.separatorChar + savedFile);
					writer.write("\"" + savedFile + "\"," + result.getLengths());

					cropCount++;

					final double progress = processed / (double) total;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller.setProgress(progress);
						}
					});

					if (cancelled) {
						break main;
					}
				}
			}
			writer.close();

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller.setComplete();
				}
			});
		} catch (Exception e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Exporter.stop();
					Error.show(Error.Type.EXPORT, e);
				}
			});
			return;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static int getTotalResults() {
		int total = 0;
		for (TraceHistory history : TraceHistory.ALL.values()) {
			total += history.getResults().size();
		}
		return total;
	}

	private static String getHeaders() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < Settings.labels.length; i++) {
			sb.append(i == 0 ? "File Name," : ",");
			sb.append("\"" + Settings.labels[i] + "\"");
		}

		return sb.toString();
	}

	private static String getFileName(ImagePlus ip) {
		String fileName = ip.getTitle();
		int dot = fileName.lastIndexOf(".");
		if (dot != -1) {
			fileName = fileName.substring(0, dot);
		}
		return fileName;
	}

	private static void drawMeasurement(GraphicsContext gc, Image image, TraceResult result) {
		gc.drawImage(image, 0, 0);

		for (TracePath path : result.getPaths()) {
			path.drawLine(gc);
		}

		for (TracePath path : result.getPaths()) {
			path.drawLabel(gc);
		}
	}

	private static void saveImage(Canvas canvas, GraphicsContext gc, TraceResult result, String path) {
		Rectangle2D viewport = result.getBoundingBox(Settings.cropPadding);
		viewport = clampViewportToCanvas(canvas, viewport);

		WritableImage wi = new WritableImage((int) viewport.getWidth(), (int) viewport.getHeight());
		SnapshotParameters sp = new SnapshotParameters();
		sp.setViewport(viewport);

		// Use 32-bit ARGB by default.
		BufferedImage buffered = SwingFXUtils.fromFXImage(takeSnapshot(canvas, sp, wi), null);
		BufferedImage rgb = new BufferedImage(buffered.getWidth(), buffered.getHeight(), BufferedImage.TYPE_INT_ARGB);
		rgb.getGraphics().drawImage(buffered, 0, 0, null);

		ImagePlus crop = new ImagePlus("", rgb);

		// Convert to 8-bit color to reduce file size.
		if (Settings.compressCroppedImages) {
			IJ.run(crop, "8-bit Color", "number=256");
		}

		new FileSaver(crop).saveAsPng(path);
	}

	private static Rectangle2D clampViewportToCanvas(Canvas canvas, Rectangle2D viewport) {
		double width = viewport.getWidth();
		double height = viewport.getHeight();

		double minX = viewport.getMinX();
		if (minX < 0) {
			width -= Math.abs(minX);
			minX = 0;
		}

		double minY = viewport.getMinY();
		if (minY < 0) {
			height -= Math.abs(minY);
			minY = 0;
		}

		if (viewport.getMinX() + width > canvas.getWidth()) {
			width = canvas.getWidth() - viewport.getMinX();
		}

		if (viewport.getMinY() + height > canvas.getHeight()) {
			height = canvas.getHeight() - viewport.getMinY();
		}

		return new Rectangle2D(minX, minY, width, height);
	}

	// Takes a snapshot on the FX application thread.
	private static WritableImage takeSnapshot(Canvas canvas, SnapshotParameters sp, WritableImage empty) {
		final CountDownLatch latch = new CountDownLatch(1);
		final ObjectProperty<WritableImage> output = new SimpleObjectProperty<WritableImage>();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				output.setValue(canvas.snapshot(sp, empty));
				latch.countDown();
			}
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return output.getValue();
	}
}