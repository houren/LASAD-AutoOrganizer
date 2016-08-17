package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class ExportSessionDialogue {
	private MessageBox loadingScreen = null;
	private static ExportSessionDialogue instance = null;
	
	private ExportSessionDialogue() { }
	
	public static ExportSessionDialogue getInstance() {
		if(instance == null) {
			instance = new ExportSessionDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Exporting map, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen.isVisible()) {
			loadingScreen.close();
		}
	}
}
