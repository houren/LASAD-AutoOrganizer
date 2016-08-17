package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingLoginDialogue {

	private MessageBox loadingScreen = null;
	private static LoadingLoginDialogue instance = null;
	
	private LoadingLoginDialogue() { }
	
	public static LoadingLoginDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingLoginDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Logging in, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}