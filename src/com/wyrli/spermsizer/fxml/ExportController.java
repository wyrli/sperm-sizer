package com.wyrli.spermsizer.fxml;

import com.wyrli.spermsizer.exports.Exporter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ExportController {
	@FXML
	private Label label;
	@FXML
	private ProgressBar progress;
	@FXML
	private Button close;

	@FXML
	private void close() {
		Exporter.stop();
	}

	public void setProgress(double value) {
		progress.setProgress(value);
	}

	public void setComplete() {
		progress.setProgress(1);
		label.setText("All measurements exported.");
		close.setText("Done");
	}
}