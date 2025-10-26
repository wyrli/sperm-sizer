package com.wyrli.spermsizer.info;

import com.wyrli.spermsizer.Main;

public class About {
	public static final String VERSION_INFO = "Version 1.7 (26 October 2025)";
	public static final String COPYRIGHT = "Copyright " + '\u00A9' + " 2020-2025 Roger Li. All rights reserved.";
	public static final String GITHUB_LINK = "https://github.com/wyrli/sperm-sizer";

	public static void show() {
		Modal.fromFxml("About " + Main.TITLE, "About.fxml").getStage().show();
	}
}