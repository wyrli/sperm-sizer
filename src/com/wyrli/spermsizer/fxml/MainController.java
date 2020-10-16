package com.wyrli.spermsizer.fxml;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.exports.Exporter;
import com.wyrli.spermsizer.images.ImageCanvas;
import com.wyrli.spermsizer.images.ImageSelector;
import com.wyrli.spermsizer.images.Indicator;
import com.wyrli.spermsizer.info.About;
import com.wyrli.spermsizer.info.Notification;
import com.wyrli.spermsizer.measurements.Marker;
import com.wyrli.spermsizer.measurements.Selector;
import com.wyrli.spermsizer.measurements.TraceHistory;
import com.wyrli.spermsizer.measurements.TraceResult;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

public class MainController {
	private Point2D.Double mousePos = null;
	private Indicator indicator = new Indicator();
	private Selector selector = new Selector();

	@FXML
	private ScrollPane scrollPane;
	@FXML
	private ImageCanvas canvas;
	@FXML
	private StackPane canvasPane;

	@FXML
	private Label description;
	@FXML
	private Label dimensions;
	@FXML
	private Label coordinates;

	@FXML
	private Label zoomPercentage;
	@FXML
	private Button recenter;
	@FXML
	private Button zoomIn;
	@FXML
	private Slider slider;
	@FXML
	private Button zoomOut;

	@FXML
	private ListView<Label> measurements;
	@FXML
	private Button export;

	@FXML
	private ListView<File> images;
	@FXML
	private MenuItem mImagePrev;
	@FXML
	private MenuItem mImageNext;

	@FXML
	private MenuItem mExport;
	@FXML
	private MenuItem mClearAll;

	@FXML
	private MenuItem mUndo;
	@FXML
	private MenuItem mRedo;

	@FXML
	private MenuItem mZoomIn;
	@FXML
	private MenuItem mZoomOut;
	@FXML
	private MenuItem mRecenter;

	@FXML
	private Label totalResults;
	@FXML
	private Label totalImages;

	@FXML
	private void initialize() {
		// Use a node to highlight markers when undoing/redoing.
		indicator.addToPane(canvasPane);

		// Use a node to mark circular areas.
		selector.addToPane(canvasPane);

		// Lock the scrollbar to prevent unwanted interactions with the scroll wheel.
		scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
			scrollPane.setVvalue(0.5);
		});
		scrollPane.hvalueProperty().addListener((observable, oldValue, newValue) -> {
			scrollPane.setHvalue(0.5);
		});

		// Control image magnification using the slider.
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			canvas.setZoom(newValue.intValue());

			if (mousePos != null) {
				canvas.zoomReposition(mousePos.x, mousePos.y, oldValue.intValue(), newValue.intValue());
			}

			String perc = (int) (ImageCanvas.getZoomScale(newValue.intValue()) * 100) + "%";
			zoomPercentage.setText(perc);
		});

		// Enable clearing if there are images open.
		mClearAll.disableProperty().bind(Bindings.isEmpty(images.getItems()));

		// Enable exporting if there are measurements to export.
		export.disableProperty().bind(Bindings.isEmpty(measurements.getItems()));
		mExport.disableProperty().bind(export.disableProperty());

		// Disable dimensions label if no image has been loaded.
		// Have other footer elements follow suit.
		dimensions.disableProperty().bind(Bindings.isNull(canvas.imagePlusProperty()));
		coordinates.disableProperty().bind(dimensions.disableProperty());
		zoomPercentage.disableProperty().bind(dimensions.disableProperty());
		recenter.disableProperty().bind(dimensions.disableProperty());
		zoomIn.disableProperty().bind(dimensions.disableProperty());
		slider.disableProperty().bind(dimensions.disableProperty());
		zoomOut.disableProperty().bind(dimensions.disableProperty());

		// Sync disable property of menu items with buttons.
		mZoomIn.disableProperty().bind(zoomIn.disableProperty());
		mZoomOut.disableProperty().bind(zoomOut.disableProperty());
		mRecenter.disableProperty().bind(recenter.disableProperty());

		// Disable image navigation depending on the selected item.
		ReadOnlyIntegerProperty indexProp = images.getSelectionModel().selectedIndexProperty();
		mImagePrev.disableProperty().bind(Bindings.lessThanOrEqual(indexProp, 0));
		mImageNext.disableProperty().bind(indexProp.greaterThanOrEqualTo(Bindings.size(images.getItems()).subtract(1)));

		// Configure the dimensions label based on the dimensions image.
		dimensions.textProperty().bind(Bindings.createStringBinding(
				() -> (int) canvas.widthProperty().get() + " " + '\u00D7' + " " + (int) canvas.heightProperty().get(),
				canvas.widthProperty(), canvas.heightProperty()));

		// Measurements counter.
		totalResults.textProperty().bind(Bindings.size(measurements.getItems()).asString());

		// Images counter.
		totalImages.textProperty().bind(Bindings.size(images.getItems()).asString());

		// Initialize description.
		updateDescription();

		// Display whatever image is selected in the ListView.
		images.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String path = newValue.getAbsolutePath();
				if (canvas.setImage(path)) {
					Main.setWindowTitle(newValue.getName());
				} else {
					Notification.invalidImage(path);
				}

				slideCenter();
			}
		});

		// When using File for ListView, display the file name.
		images.setCellFactory(param -> new ListCell<File>() {
			@Override
			protected void updateItem(File file, boolean empty) {
				super.updateItem(file, empty);

				if (empty || file == null) {
					setText(null);
				} else {
					setText(file.getName());
				}
			}
		});
	}

	@FXML
	private void paneScrolled(ScrollEvent e) {
		if (slider.isDisable()) {
			return;
		}

		int sign = (int) Math.signum(e.getDeltaY());
		int oldValue = (int) slider.getValue();
		int newValue = oldValue + sign;
		slider.setValue(newValue);
	}

	@FXML
	private void paneDragged(MouseEvent e) {
		updateMousePosition(e);

		if (e.getButton() != MouseButton.SECONDARY && e.getButton() != MouseButton.MIDDLE) {
			return;
		}

		canvasPane.setCursor(Cursor.CLOSED_HAND);
		canvas.pan(e.getScreenX(), e.getScreenY());
	}

	@FXML
	private void paneReleased(MouseEvent e) {
		canvasPane.setCursor(Cursor.DEFAULT);
		canvas.endPan();
	}

	@FXML
	private void canvasPressed(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) {
			return;
		}

		int marker = canvas.getHistory().getMarkerNumber();
		boolean first = marker == 0;
		boolean last = marker == Settings.markers - 1;

		if (first || last) {
			selector.press(canvas, Settings.markerColor, e.getX(), e.getY());
		} else {
			canvas.addMarker(new Marker(new Point((int) e.getX(), (int) e.getY())));
		}
	}

	@FXML
	private void canvasDragged(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) {
			return;
		}

		selector.drag(canvas, e.getX(), e.getY());
	}

	@FXML
	private void canvasReleased(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY) {
			return;
		}

		Marker marker = selector.release();
		if (marker != null) {
			canvas.addMarker(marker);
		}
	}

	@FXML
	private void updateCoordinates(MouseEvent e) {
		coordinates.setText((int) e.getX() + ", " + (int) e.getY());
	}

	@FXML
	private void clearCoordinates() {
		coordinates.setText(null);
	}

	@FXML
	private void updateMousePosition(MouseEvent e) {
		mousePos = new Point2D.Double(e.getScreenX(), e.getScreenY());
	}

	@FXML
	private void clearMousePosition(MouseEvent e) {
		mousePos = null;
	}

	@FXML
	private void slideCenter() {
		int width = (int) scrollPane.getViewportBounds().getWidth();
		int height = (int) scrollPane.getViewportBounds().getHeight();
		int prefZoom = canvas.getPreferredZoom(width, height);

		slider.setValue(prefZoom);
		canvas.center();
	}

	@FXML
	private void slideLeft() {
		slider.setValue(slider.getValue() - 1);
	}

	@FXML
	private void slideRight() {
		slider.setValue(slider.getValue() + 1);
	}

	@FXML
	public void addImage(ActionEvent e) {
		List<File> files = ImageSelector.selectFiles(Main.getPrimaryStage());

		if (files == null || files.isEmpty()) {
			return;
		}

		for (File file : files) {
			images.getItems().add(file);
		}

		images.getSelectionModel().select(files.get(0));
	}

	@FXML
	private void undo() {
		Marker undid = canvas.getHistory().undo();
		indicator.indicate(canvas, undid.getStart(), Settings.markerColor);
		canvas.draw();
	}

	@FXML
	private void redo() {
		Marker redid = canvas.getHistory().redo();
		indicator.indicate(canvas, redid.getStart(), Settings.markerColor);
		canvas.draw();
	}

	@FXML
	private void nextImage() {
		int next = images.getSelectionModel().getSelectedIndex() + 1;
		images.getSelectionModel().select(next);
	}

	@FXML
	private void previousImage() {
		int prev = images.getSelectionModel().getSelectedIndex() - 1;
		images.getSelectionModel().select(prev);
	}

	@FXML
	private void clearAll() {
		if (!Notification.confirmClearAll()) {
			return;
		}

		TraceHistory.ALL.clear();
		ImageSelector.clear();

		measurements.getItems().clear();
		images.getItems().clear();

		Main.setWindowTitle(null);
		canvas.close();
		imageDrawn();

		slider.setValue(0);
	}

	@FXML
	private void export() {
		DirectoryChooser chooser = new DirectoryChooser();

		// Start from the last-visited directory.
		File last = new File(Settings.folderLastOutput);
		if (last.isDirectory()) {
			chooser.setInitialDirectory(last);
		}

		File destination = chooser.showDialog(Main.getPrimaryStage());
		if (destination == null) {
			return;
		}

		// Remember the last-visited directory.
		Settings.setFolder(false, destination.getAbsolutePath());

		Exporter.export(destination.getAbsolutePath());
	}

	@FXML
	private void documentation() {
		Main.showDocument(About.GITHUB_LINK);
	}

	@FXML
	private void about() {
		About.show();
	}

	@FXML
	private void closeApplication() {
		System.exit(0);
	}

	public void imageDrawn() {
		updateUndoRedo();
		updateMeasurements();
		updateDescription();
	}

	private void updateUndoRedo() {
		mUndo.setDisable(!canvas.getHistory().canUndo());
		mRedo.setDisable(!canvas.getHistory().canRedo());
	}

	private void updateMeasurements() {
		// Rebuild the list of measurements.
		measurements.getItems().clear();
		for (TraceHistory history : TraceHistory.ALL.values()) {
			for (TraceResult result : history.getResults()) {
				measurements.getItems().add(result.getListViewItem());
			}
		}

		// Scroll to and select the last result belonging to the current image, if possible.
		List<TraceResult> results = canvas.getHistory().getResults();
		if (!results.isEmpty()) {
			Label last = results.get(results.size() - 1).getListViewItem();
			measurements.getSelectionModel().select(last);
			measurements.scrollTo(last);
		}
	}

	private void updateDescription() {
		if (canvas.getImagePlus() == null) {
			description.setText("Add at least one image.");
			description.setGraphic(new ImageView(Icon.NO_IMAGE));
			return;
		}

		int marker = canvas.getHistory().getMarkerNumber();
		description.setText("Left click to select point " + (marker + 1) + ".");

		if (marker == 0) {
			description.setGraphic(new ImageView(Icon.POINT_FIRST));
		} else if (marker == Settings.markers - 1) {
			description.setGraphic(new ImageView(Icon.POINT_LAST));
		} else {
			description.setGraphic(new ImageView(Icon.POINT_MID));
		}
	}
}