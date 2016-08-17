package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingMapDialogue {

	private MessageBox loadingScreen = null;
	private static LoadingMapDialogue instance = null;
	
	private LoadingMapDialogue() { }
	
	public static LoadingMapDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingMapDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Loading map, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}