package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class ExportScreenShotDialogue {
	private MessageBox loadingScreen = null;
	private static ExportScreenShotDialogue instance = null;

	private ExportScreenShotDialogue() {
	}

	public static ExportScreenShotDialogue getInstance() {
		if (instance == null) {
			instance = new ExportScreenShotDialogue();
		}
		return instance;
	}

	public void showLoadingScreen() {
		loadingScreen = MessageBox.progress("Progress", "Creating screenshot, please wait...", "Loading...");
	}

	public void updateProgress(float i) {
		loadingScreen.updateProgress(i, Math.round(100 * i) + "% completed");
	}

	public void closeLoadingScreen() {
		if (loadingScreen.isVisible()) {
			loadingScreen.close();
		}
	}

}
