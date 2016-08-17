package lasad.gwt.client.ui.workspace.userlist;

import java.util.HashMap;
import java.util.Vector;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.events.LASADEventListenerInterface;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;



/**
 * Provides a list of active users on a map
 * 
 * TODO Modify the appearance to make it fit to the other interface elements like the chat, etc.
 *
 */
public class UserListPanel extends ContentPanel implements LASADEventListenerInterface {

	private final lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	private AbstractGraphMap myMap = null;
	
	private ArgumentMapSpace myMSpace = null;
		
	private ListStore<ModelData> store;
	
	public UserListPanel(AbstractGraphMap map,ArgumentMapSpace mspace) {
		this.myMap = map;
		this.myMSpace=mspace;

		// Some GUI Settings
		this.setHeading(myConstants.ListOfUsersHeading());
		this.setLayout(new FitLayout());
		this.setBodyBorder(false);
		this.setBorders(false);
				
		// build gui elements
		buildGUIElements();

		// Register LasadEvents
		MVCViewSession viewSession = (MVCViewSession) myMap.getMyViewSession();
		viewSession.addLasadEventListener("USER_" + Commands.UserJoin, this);
		viewSession.addLasadEventListener("USER_" + Commands.UserLeave, this);
		viewSession.addLasadEventListener("USER_" + Commands.UserList, this);
	}

	private void buildGUIElements() {
		//ListView<ModelData> list = new ListView<ModelData>();
		ListView<ModelData> list = new ListView<ModelData>();
		list.setDisplayProperty("username");
		store = new ListStore<ModelData>();
		store.setStoreSorter(new StoreSorter<ModelData>());
		list.setStore(store);
		list.disableEvents(true);
		add(list);
	}

	private void clearList() {
		store.removeAll();
	}

	private void addUser(String username, String client) {
		ModelData model = store.findModel("username", username);
		if (model == null) {
			ModelData m = new BaseModelData();
			m.set("username", username);

			store.add(m);
			if (!username.contains(client)) LASADInfo.display("Userevent", username + " joined the diagram.");
		}
	}

	private void removeUser(String username) {
		ModelData model = store.findModel("username", username);
		if (model != null) {
			store.remove(model);
			LASADInfo.display("Userevent", username + " left the diagram.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fireLasadEvent(LasadEvent event) {
//		if (event.getType().equals("USER_USERJOIN")) {
		if (event.getType().equals("USER_" + Commands.UserJoin)) {
			// A new User added the map
			HashMap<String, String> data = (HashMap<String, String>) event.getData();
			String user = data.get(ParameterTypes.UserName);
			String client = data.get(ParameterTypes.Client);
			addUser(user, client);
//		} else if (event.getType().equals("USER_USERLEAVE")) {
		} else if (event.getType().equals("USER_" + Commands.UserLeave)) {
			// A User left the map
			removeUser((String) event.getData());
//		} else if (event.getType().equals("USER_USERLIST")) {
		} else if (event.getType().equals("USER_" + Commands.UserList)) {
			// A new Userlist arrived
			clearList();
			HashMap<String, Vector<String>> data = (HashMap<String, Vector<String>>) event.getData();
			String client = data.get(ParameterTypes.Client).firstElement();
			Vector<String> userList = data.get(ParameterTypes.UserList);
			for (String username : userList) {
				addUser(username, client);
			}
		}
		
		this.myMSpace.updateEastPanel(false);
	}
	
	public int getNumOfItems()
	{
		return store.getCount();
	}
}