package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class ImportingMapDialogue {
	private MessageBox loadingScreen = null;
	private static ImportingMapDialogue instance = null;
	
	private ImportingMapDialogue() { }
	
	public static ImportingMapDialogue getInstance() {
		if(instance == null) {
			instance = new ImportingMapDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Importing map, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}
