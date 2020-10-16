package com.wyrli.spermsizer.info;

import java.io.IOException;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.fxml.AboutController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class About {
	public static final String VERSION_INFO = "Version 1.6 (16 October 2020)";
	public static final String COPYRIGHT = "Copyright " + '\u00A9' + " 2020 Roger Li. All rights reserved.";
	public static final String GITHUB_LINK = "https://github.com/wyrli/sperm-sizer";

	public static void show() {
		Stage window = new Stage();
		window.setTitle("About " + Main.TITLE);
		window.setResizable(false);
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(Main.getPrimaryStage());

		FXMLLoader loader = new FXMLLoader(AboutController.class.getResource("About.fxml"));
		Scene scene;
		try {
			scene = new Scene(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		window.setScene(scene);

		AboutController controller = (AboutController) loader.getController();
		controller.setStage(window);

		window.show();
	}
}