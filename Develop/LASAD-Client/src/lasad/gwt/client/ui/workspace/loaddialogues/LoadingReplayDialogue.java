package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingReplayDialogue {

	private MessageBox loadingScreen = null;
	private static LoadingReplayDialogue instance = null;
	
	private LoadingReplayDialogue() { }
	
	public static LoadingReplayDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingReplayDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Loading replay, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}