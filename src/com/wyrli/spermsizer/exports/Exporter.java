package com.wyrli.spermsizer.exports;

import com.wyrli.spermsizer.fxml.ExportController;
import com.wyrli.spermsizer.info.Modal;

public class Exporter {
	private static Modal<ExportController> modal;
	private static ExportTask task;

	public static void export(String folder) {
		if (task != null) {
			task.cancel();
		}

		modal = Modal.fromFxml("Export", "Export.fxml");
		modal.getStage().show();

		task = new ExportTask(folder, modal.getController());
		new Thread(task).start();
	}

	public static void stop() {
		modal.getStage().close();
		task.cancel();
	}
}