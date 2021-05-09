package com.wyrli.spermsizer.fxml;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.info.Error;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorController {
	private boolean acknowledged = false;

	@FXML
	private TextArea errorDetails;

	public void setErrorDetails(String value) {
		errorDetails.setText(value);
	}

	private Stage getStage() {
		return (Stage) errorDetails.getScene().getWindow();
	}

	@FXML
	private void close() {
		acknowledged = true;
		getStage().close();
	}

	@FXML
	private void createIssue() {
		Main.showDocument(Error.GITHUB_ISSUE_LINK);
	}

	@FXML
	private void importConfig() {
		if (Settings.importManually()) {
			close();
		}
	}

	// An error is considered "acknowledged" if it was closed via a button (as opposed to Alt + F4).
	// Pressing the "X" button on the title bar does not count as an acknowledgement.
	public boolean IsAcknowledged() {
		return acknowledged;
	}
}