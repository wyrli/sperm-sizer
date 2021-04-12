package com.wyrli.spermsizer.info;

import com.wyrli.spermsizer.Main;

public class About {
	public static final String VERSION_INFO = "Version 1.6.1 (12 April 2021)";
	public static final String COPYRIGHT = "Copyright " + '\u00A9' + " 2020-2021 Roger Li. All rights reserved.";
	public static final String GITHUB_LINK = "https://github.com/wyrli/sperm-sizer";

	public static void show() {
		Modal.fromFxml("About " + Main.TITLE, "About.fxml").getStage().show();
	}
}