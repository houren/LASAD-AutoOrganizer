package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingInfoDialogue {
	
	private MessageBox loadingScreen = null;
	private static LoadingInfoDialogue instance = null;
	
	private LoadingInfoDialogue() { }
	
	public static LoadingInfoDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingInfoDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Loading information, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}

}
