package com.wyrli.spermsizer.fxml;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.info.Error;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorController {
	@FXML
	private TextArea errorDetails;
	@FXML
	private Button createIssue;
	@FXML
	private Button close;

	public void setErrorDetails(String value) {
		errorDetails.setText(value);
	}

	@FXML
	private void close() {
		((Stage) close.getScene().getWindow()).close();
	}
	
	@FXML
	private void createIssue() {
		Main.showDocument(Error.GITHUB_ISSUE_LINK);
	}
}