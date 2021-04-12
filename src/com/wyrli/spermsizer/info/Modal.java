package com.wyrli.spermsizer.info;

import java.io.IOException;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.fxml.MainController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Modal<T> {
	private Stage stage;
	private T controller;

	private Modal(Stage stage, T controller) {
		this.stage = stage;
		this.controller = controller;
	}

	public Stage getStage() {
		return stage;
	}

	public T getController() {
		return controller;
	}

	/** Returns a Modal created from an FXML file in the fxml package. */
	public static <T> Modal<T> fromFxml(String title, String fxml) {
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(Main.getPrimaryStage());

		FXMLLoader loader = new FXMLLoader(MainController.class.getResource(fxml));
		Scene scene;
		try {
			scene = new Scene(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		stage.setScene(scene);

		return new Modal<T>(stage, loader.getController());
	}
}