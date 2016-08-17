package lasad.gwt.client.ui.workspace.loaddialogues;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.ui.workspace.tabs.map.MapLoginTab;

import com.extjs.gxt.ui.client.widget.MessageBox;

public class ReloadingMapsDialogue {

	
	private int mapCount = 0;
	private MessageBox loadingScreen = null;
	private static ReloadingMapsDialogue instance = null;
	
	private ReloadingMapsDialogue() { }
	
	public static ReloadingMapsDialogue getInstance() {
		if(instance == null) {
			instance = new ReloadingMapsDialogue();
		}
		return instance;
	}
	
	public void showLoadingScreen() {
		LASAD_Client.getInstance().disable(false);
		MapLoginTab.getInstance().setUpdateAllowed(false);
		loadingScreen = MessageBox.wait("Progress", "There was an error with your connection, reloading maps...", "Loading...");
	}
	
	public void closeLoadingScreen() {
		if(loadingScreen != null) {
			loadingScreen.close();
			MapLoginTab.getInstance().setUpdateAllowed(true);
			LASAD_Client.getInstance().enable();
		}
	}
	
	/**
	 * Defines the number of maps that needs to be reloaded
	 */
	public void setMapCount(int numberOfMaps) {
		mapCount = numberOfMaps;
	}
	
	public void decreaseMapCount() {
		if(mapCount > 0) {
			mapCount--;
		}
		
		// If all maps have been reloaded...
		if(mapCount == 0) {
			closeLoadingScreen();
		}
	}
}