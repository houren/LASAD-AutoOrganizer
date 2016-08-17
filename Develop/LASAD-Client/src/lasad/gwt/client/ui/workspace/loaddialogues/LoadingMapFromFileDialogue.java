package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingMapFromFileDialogue {

	private MessageBox loadingScreen = null;
	private static LoadingMapFromFileDialogue instance = null;
	
	private LoadingMapFromFileDialogue() { }
	
	public static LoadingMapFromFileDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingMapFromFileDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Loading map from file, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}