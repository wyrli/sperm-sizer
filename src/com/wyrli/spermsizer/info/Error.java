package com.wyrli.spermsizer.info;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.wyrli.spermsizer.fxml.ErrorController;

public class Error {
	public static final String GITHUB_ISSUE_LINK = "https://github.com/wyrli/sperm-sizer/issues";

	public static void show(Exception e) {
		Modal<ErrorController> modal = Modal.fromFxml("Error Report", "Error.fxml");

		// Convert the stack trace to a string by printing it to a string writer.
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			e.printStackTrace(pw);
		}

		modal.getController().setErrorDetails(sw.toString());
		modal.getStage().show();
	}
}