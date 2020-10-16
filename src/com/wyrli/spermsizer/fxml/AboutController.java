package com.wyrli.spermsizer.fxml;

import com.wyrli.spermsizer.Main;
import com.wyrli.spermsizer.info.About;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.stage.Stage;

public class AboutController {
	private Stage stage;

	@FXML
	private Label version;
	@FXML
	private Label copyright;
	@FXML
	private Hyperlink gitHubLink;

	@FXML
	private void initialize() {
		version.setText(About.VERSION_INFO);
		copyright.setText(About.COPYRIGHT);

		gitHubLink.setText(About.GITHUB_LINK);
		gitHubLink.setBorder(Border.EMPTY);
		gitHubLink.setPadding(Insets.EMPTY);
	}

	@FXML
	private void close() {
		if (stage != null) {
			stage.close();
		}
	}

	@FXML
	private void openGitHubLink() {
		Main.showDocument(About.GITHUB_LINK);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}