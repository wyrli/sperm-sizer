package com.wyrli.spermsizer.info;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.wyrli.spermsizer.fxml.ErrorController;

public class Error {
	public static final String GITHUB_ISSUE_LINK = "https://github.com/wyrli/sperm-sizer/issues";

	public enum Type {
		CONFIG("ErrorConfig.fxml"), EXPORT("ErrorExport.fxml");

		private final String fileName;

		private Type(String fileName) {
			this.fileName = fileName;
		}
	}

	private static boolean show(Type type, String errorDetails) {
		Modal<ErrorController> modal = Modal.fromFxml("Error Report", type.fileName);

		modal.getController().setErrorDetails(errorDetails);
		modal.getStage().showAndWait();

		return modal.getController().IsAcknowledged();
	}

	public static boolean show(Type type, Exception e) {
		return show(type, stackTraceToString(e));
	}

	public static boolean show(Type type, List<Exception> exceptions) {
		StringBuilder errorDetails = new StringBuilder();

		boolean first = true;
		for (Exception e : exceptions) {
			if (first) {
				first = false;
			} else {
				errorDetails.append(System.lineSeparator());
			}
			errorDetails.append(stackTraceToString(e));
		}

		return show(type, errorDetails.toString());
	}

	private static String stackTraceToString(Exception e) {
		StringWriter sw = new StringWriter();

		// Convert the stack trace to a string by printing it to a string writer.
		try (PrintWriter pw = new PrintWriter(sw)) {
			e.printStackTrace(pw);
		}

		return sw.toString();
	}
}