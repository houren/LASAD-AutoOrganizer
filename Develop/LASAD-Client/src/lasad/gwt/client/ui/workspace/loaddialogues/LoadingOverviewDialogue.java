package lasad.gwt.client.ui.workspace.loaddialogues;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class LoadingOverviewDialogue {

	private MessageBox loadingScreen = null;
	private static LoadingOverviewDialogue instance = null;
	
	private LoadingOverviewDialogue() { }
	
	public static LoadingOverviewDialogue getInstance() {
		if(instance == null) {
			instance = new LoadingOverviewDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		loadingScreen = MessageBox.wait("Progress", "Loading overview, please wait...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
		}
	}
}