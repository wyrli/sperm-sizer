package com.wyrli.spermsizer;

import java.io.IOException;

import com.wyrli.spermsizer.config.FolderHistory;
import com.wyrli.spermsizer.config.Settings;
import com.wyrli.spermsizer.fxml.MainController;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String TITLE = "Sperm Sizer";
	private static final int MIN_WIDTH = 800;
	private static final int MIN_HEIGHT = 600;

	private static Stage primaryStage;
	private static HostServices hostServices;
	private static MainController controller;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		Settings.init();
		FolderHistory.init();

		primaryStage = stage;
		hostServices = getHostServices();

		setWindowTitle(null);
		stage.setMinWidth(MIN_WIDTH);
		stage.setMinHeight(MIN_HEIGHT);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
		BorderPane root = loader.load();
		controller = loader.getController();

		Scene scene = new Scene(root, MIN_WIDTH / 2, MIN_HEIGHT / 2);
		stage.setScene(scene);
		stage.show();

		// If the window is closed, terminate the program.
		stage.setOnCloseRequest(e -> System.exit(0));
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static MainController getController() {
		return controller;
	}

	public static void showDocument(String uri) {
		hostServices.showDocument(uri);
	}

	public static void setWindowTitle(String title) {
		if (title == null || title.isEmpty()) {
			primaryStage.setTitle(TITLE);
		} else {
			primaryStage.setTitle(TITLE + " - " + title);
		}
	}
}