package com.wyrli.spermsizer.exports;

import java.io.IOException;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.fxml.ExportController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Exporter {
	private static Stage window;
	private static ExportTask task;

	public static void export(String folder) {
		if (task != null) {
			task.cancel();
		}

		ExportController controller;
		try {
			controller = createWindow();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		window.show();

		task = new ExportTask(folder, controller);
		new Thread(task).start();
	}

	public static void stop() {
		window.close();
		task.cancel();
	}

	private static ExportController createWindow() throws IOException {
		window = new Stage();
		window.setTitle("Export");
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(Main.getPrimaryStage());

		FXMLLoader loader = new FXMLLoader(ExportController.class.getResource("Export.fxml"));
		Scene scene = new Scene(loader.load());
		window.setScene(scene);

		return loader.getController();
	}
}