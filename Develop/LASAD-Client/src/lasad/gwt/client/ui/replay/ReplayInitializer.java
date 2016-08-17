package lasad.gwt.client.ui.replay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.model.argument.MapInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapMVCViewSession;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingReplayDialogue;
import lasad.gwt.client.ui.workspace.tabs.ReplayTab;
import lasad.gwt.client.ui.workspace.tabs.map.MapLoginTab;
import lasad.gwt.client.xml.OntologyReader;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.Component;



public class ReplayInitializer {
	
	private TreeMap<Integer, ActionPackage> treeForwReplay;
	private TreeMap<String, List<Integer>> treeUserReplay; 
	private List<Integer> elementReplay;
	private int chatActionSize = 0;
	
	
	public ReplayInitializer(TreeMap<Integer, ActionPackage> treeForwReplay, TreeMap<String, List<Integer>> treeUserReplay, List<Integer> elementReplay) {
		this.treeForwReplay = treeForwReplay;
		this.treeUserReplay = treeUserReplay;
		this.elementReplay = elementReplay;
	}
	
	public void createAllReplayActions (Action a) {
		// CREATE/UPDATE/DELETE-ELEMENT
		// ///////////////////////////////
		// Initialize
		Integer time = new Integer(a.getParameterValue(ParameterTypes.ReplayTime));
		String username = a.getParameterValue(ParameterTypes.UserName);
		List<Integer> userActions;
		ActionPackage p;
		
		// TODO hier stand vorher "contains" statt equals / equalsIgnoreCase...
		if(a.getCmd().equals(Commands.ChatMsg))
		{
			a.setCategory(Categories.Communication);
			chatActionSize++;
		}
		else
		{
			Integer elementID = new Integer(a.getParameterValue(ParameterTypes.Id));

			// Prepare a common map-action
			// a.removeParameter("REPLAY-TIME");
			a.setCategory(Categories.Map);

			// Check if elementID already exists in elementReplay
			if (!elementReplay.contains(elementID)) {
				elementReplay.add(elementID);
			}
		}

		// Ignore status actions
		if (a.getParameterValue(ParameterTypes.Status) == null) {

			// Is user name already in map treeUserReplay?
			if (treeUserReplay.containsKey(username)) {
				// Get saved action-times of user
				userActions = treeUserReplay.get(username);
			} else {
				// If not create new list for user name
				userActions = new ArrayList<Integer>();
			}
			// New time?
			if (!userActions.contains(time)) {
				// Add current time
				userActions.add(time);
			}
			// Add the list for user
			treeUserReplay.put(username, userActions);

//			// Check if elementID already exists in elementReplay
//			if (!elementReplay.contains(elementID)) {
//				elementReplay.add(elementID);
//			}

			// Check if there is already an action at this time
			// If not create new ActionPackage at this time
			if (treeForwReplay.get(time) != null) {
				p = treeForwReplay.get(time);
			} else {
				p = new ActionPackage();
			}
			p.addAction(a);

			// Save new ActionPackage in HashMap
			treeForwReplay.put(time, p);
		}
		
	}
	
	public void finishInitialization(Action a) {
		// Create replay tab and build needed maps...

		// Get total time
		// int totalSec = Integer.parseInt(a.getParameter("TOTAL-SEC"));
		int totalSec = treeForwReplay.lastKey();

		// Get all seconds with actions
		List<Integer> indexActionReplay = new ArrayList<Integer>(treeForwReplay.keySet());
		int actionSize = indexActionReplay.size();

		// No action?
		if (actionSize == 0) {
			// Error: There exists no action
			// Close wait-MessageBox
			// Show Error
			MapLoginTab.getInstance().infoReplayError(Commands.NoAction);
		}

		// INDEX-SECONDS-REPLAY
		// ///////////////////////////////
		// Initialize index for e.g. slider-skip (each second)
		List<Integer> indexSecondsReplay = new ArrayList<Integer>(totalSec);
		int actionIndex = 0;
		int currentTime = 0;
		int newTime = indexActionReplay.get(actionIndex);
		for (int second = 0; second <= totalSec; second++) {
			// Second equal to new action time?
			if (newTime == second) {
				// Save new action time
				currentTime = newTime;
				// Not at the end of actionIndex?
				if (actionIndex < actionSize - 1) {
					// Increase actionIndex
					actionIndex++;
					// Get new action time
					newTime = indexActionReplay.get(actionIndex);
				}
			}
			// Add current last action time
			indexSecondsReplay.add(currentTime);
		}

		// TREE-USER-REPLAY
		// ///////////////////////////////
		// Add "each user" with key: "" to treeUserReplay
		treeUserReplay.put("", new ArrayList<Integer>(indexActionReplay));

		// Prepare lists of the actions (second) of each user
		Iterator<String> it1 = treeUserReplay.keySet().iterator();
		while (it1.hasNext()) {
			// Get user name
			String userKey = it1.next();
			// Get the list of actions (second)
			List<Integer> userActions = treeUserReplay.get(userKey);
			// add 0 as an action of each user (start time)
			userActions.add(0);
			// Sort ascending
			Collections.sort(userActions);
			// Save edited list of actions (seconds)
			treeUserReplay.put(userKey, userActions);
		}

		// LAST-ACTION-REPLAY
		// ///////////////////////////////
		// Each second with an action has an entry for each user with his
		// own last action
		HashMap<Integer, List<Integer>> lastActionReplay = new HashMap<Integer, List<Integer>>();
		// List of user names
		List<String> userReplay = new ArrayList<String>(treeUserReplay.keySet());
		// History for last time of action for each user
		List<Integer> userHistory = new ArrayList<Integer>(treeUserReplay.size());

		// Initialize userHistory with 0 for each user
		for (int userNr = 0; userNr < userReplay.size(); userNr++) {
			userHistory.add(0);
		}
		// Save 0 as start time for lastActionReplay
		lastActionReplay.put(0, new ArrayList<Integer>(userHistory));

		// Sort is needed because possible parent-relations
		Collections.sort(elementReplay);

		// INDEX-ELEMENT-REPLAY
		// ///////////////////////////////
		// Each second with an action has an entry for each element status
		HashMap<Integer, List<Action>> indexElementReplay = new HashMap<Integer, List<Action>>();
		// INDEX-Chat-REPLAY
		// ///////////////////////////////
		// Each second with an action has an entry for each chat status
		HashMap<Integer, List<Action>> indexChatReplay = new HashMap<Integer, List<Action>>();
		// History for last state of each element
		List<Action> elementHistory = new ArrayList<Action>(elementReplay.size());
		// Special list with create action of each element
		List<Action> createActions = new ArrayList<Action>(elementReplay.size());
		// Special list with delete action of each element
		List<Action> deleteActions = new ArrayList<Action>(elementReplay.size());
		// Special list with chat action 
		List<Action> chatActions = new ArrayList<Action>(chatActionSize);

		// Generate delete action for each element
		// NOTE: Not every element has a real delete action,
		// but it will be necessary for back-skips.
		// So it has to be generated...
		for (int id : elementReplay) {
			Action delAction = new Action(Commands.DeleteElement, Categories.Map);
			delAction.addParameter(ParameterTypes.Id, id + "");
			// Maybe user name is not needed
			// It is added, just in case
			// NOTE: User name "replay" is just random - it can be replaced
			delAction.addParameter(ParameterTypes.UserName, "replay");
			delAction.addParameter(ParameterTypes.MapId, a.getParameterValue(ParameterTypes.MapId) + "");
			// Add delete Action of element
			deleteActions.add(delAction);

			// Initialize createActions for each element
			createActions.add(null);
			// Initialize element history
			elementHistory.add(delAction);
		}

		// Save create Actions at index -1
		indexElementReplay.put(-1, createActions);
		// Save delete Actions at index 0
		indexElementReplay.put(0, deleteActions);
		// Save chat Actions at index 1

		// LAST-ACTION-REPLAY
		// INDEX-ELEMENT-REPLAY
		// ///////////////////////////////
		// Each second (key) with an action
		for (int key : indexActionReplay) {
			// Get ActionPackage
			ActionPackage p = treeForwReplay.get(key);

			// Get each action of ActionPackage
			for (Action action : p.getActions()) {

				// Get user name of action
				String username = action.getParameterValue(ParameterTypes.UserName);
				// Get index of user
				int userIndex = userReplay.indexOf(username);
				// Set new time with action for user
				userHistory.set(userIndex, key);
				// Set new time with action for each user (index 0)
				userHistory.set(0, key);
				
				if(action.getCmd().equals(Commands.ChatMsg))
				{
					chatActions.add(action);
				}
				else
				{
					// Get elementID
					int elementID = new Integer(action.getParameterValue(ParameterTypes.Id));
					// Get index of element
					int elementIndex = elementReplay.indexOf(elementID);

				// CREATE-ELEMENT action
				if (action.getCmd().equals(Commands.CreateElement)) {
					// Get list of all saved createActions
					createActions = indexElementReplay.get(-1);
					// Set create action at element history list
					// (Original reference)
					elementHistory.set(elementIndex, action);
					// Set create action at create action list
					// (Original reference)
					createActions.set(elementIndex, action);
					// Put create action list at -1
					indexElementReplay.put(-1, createActions);


					}
					// UPDATE-ELEMENT action
					else if (action.getCmd().equals(Commands.UpdateElement)) {
						// Get original saved action
						Action origHistoryAction = elementHistory.get(elementIndex);

						// If there is already UPDATE-ELEMENT action
						if (origHistoryAction.getCmd().equals(Commands.UpdateElement)) {
							// Create new action with content of
							// origHistoryAction
							// (Do not use the original reference.)
							Action historyAction = new Action(origHistoryAction.getCmd(), origHistoryAction.getCategory());
							for (Parameter origParam : origHistoryAction.getParameters()) {
								historyAction.addParameter(origParam.getType(), origParam.getValue());
							}

							// Get each parameter of the action
							for (Parameter param : action.getParameters()) {
								// Get parameter name and value
								ParameterTypes pType = param.getType();
								String pValue = param.getValue();
								// Replace existing parameter or add new
								// parameter
								if (historyAction.getParameterValue(pType) != null) {
									historyAction.replaceParameter(pType, pValue);
								} else {
									historyAction.addParameter(pType, pValue);
								}
							}
							// Set update action at element history list
							// (No original reference)
							elementHistory.set(elementIndex, historyAction);
						} else {
							// Create new action with content of
							// origHistoryAction
							// (Do not use the original reference.)
							Action newUpdateAction = new Action(action.getCmd(), action.getCategory());
							for (Parameter origParam : action.getParameters()) {
								newUpdateAction.addParameter(origParam.getType(), origParam.getValue());
							}
							// Set update action at element history list
							// (No original reference)
							elementHistory.set(elementIndex, newUpdateAction);
						}
					}
					// DELETE-ELEMENT action
					else if (action.getCmd().equals(Commands.DeleteElement)) {
						// Get generated delete action
						Action deleteAction = indexElementReplay.get(0).get(elementIndex);
						// Set delete action at element history list
						// (Original reference)
						elementHistory.set(elementIndex, deleteAction);
					}
				}
			}
			// Put new user history and new element history
			// (No original reference)
			lastActionReplay.put(key, new ArrayList<Integer>(userHistory));
			indexElementReplay.put(key, new ArrayList<Action>(elementHistory));
			indexChatReplay.put(key, new ArrayList<Action>(chatActions));
		}

		// REPLAY-TAB
		// ///////////////////////////////
		// Build the ReplayTab
		// Create MapInfo
		MapInfo mapInfo = new MapInfo(a.getParameterValue(ParameterTypes.MapId));
		mapInfo.setTitle(a.getParameterValue(ParameterTypes.MapName));
		mapInfo.setOntologyName(a.getParameterValue(ParameterTypes.OntologyName));
		mapInfo.setTemplateName(a.getParameterValue(ParameterTypes.TemplateName));
		mapInfo.setTemplateTitle(a.getParameterValue(ParameterTypes.TemplateTitle));
		mapInfo = (MapInfo) OntologyReader.buildTemplateInfosFromXML(mapInfo, a.getParameterValue(ParameterTypes.Template));
		mapInfo = (MapInfo) OntologyReader.buildOntologyInfosFromXML(mapInfo, a.getParameterValue(ParameterTypes.Ontology));

		// Create the MVC Controller for the new map
		final MVController newController = LASAD_Client.registerMVCController(new MVController(mapInfo));

		// Transfer slower TreeMap in faster HashMap
		final HashMap<Integer, ActionPackage> indexForwReplay = new HashMap<Integer, ActionPackage>(treeForwReplay);

		// Create replay tab
		ReplayTab replayTab = LASAD_Client.getInstance().createReplayTab(newController, totalSec, indexSecondsReplay, indexForwReplay, indexElementReplay, lastActionReplay,indexChatReplay, treeUserReplay);
		
		// Add a new session
		ArgumentMapMVCViewSession mapSession = new ArgumentMapMVCViewSession(newController, replayTab);
		if(mapSession.getArgumentMapSpace().getChatPanel()!= null)
		{
			mapSession.getArgumentMapSpace().getChatPanel().disableTextField();
			mapSession.releaseArgumentMapListeners();
		}
		if(mapSession.getArgumentMapSpace().getExtendedChatPanel()!= null)
		{
			mapSession.getArgumentMapSpace().getExtendedChatPanel().disableTextField();
			mapSession.releaseArgumentMapListeners();
		}
		// ReplayViewSession mapSession = new
		// ReplayViewSession(newController, replayTab);
		newController.registerViewSession(mapSession);

		// DEBUG
		/*
		 * Iterator<Integer> it0 = treeForwReplay.keySet().iterator(); while
		 * (it0.hasNext()) { int key = it0.next(); Logger.log( key + " --> "
		 * + treeForwReplay.get(key) ); }
		 */

		// Reset maps and list
		this.treeForwReplay = null;
		this.treeUserReplay = null;
		this.elementReplay = null;

		// Close wait-MessageBox
		LoadingReplayDialogue.getInstance().closeLoadingScreen();
		this.centerMap(newController);
	}
	
	/**
	 * Calculates the center of all existing boxes and scrolls the map to this position
	 * @param a the join-complete action with the map information
	 */
	private void centerMap( MVController newController) {
		//Scroll the map to the center of all boxes
		int xsum = 0;
		int ysum = 0;
		int numberOfObjects = 0;
		
		//Map-Id-Hack... normal tag would be filtered by ActionReceiver, that's why this action has its own mapid-attribute
		AbstractGraphMap map = LASAD_Client.getMapTab(newController.getMapID()).getMyMapSpace().getMyMap();
		List<Component> mapComponents = map.getItems();
		Iterator<Component> iter = mapComponents.iterator();
		while (iter.hasNext()) {
			Component component = iter.next();
			if (component instanceof AbstractBox ) {
				AbstractBox box = (AbstractBox) component;
				xsum += box.getPosition(true).x + box.getWidth() / 2;
				ysum += box.getPosition(true).y + box.getHeight() / 2;
				numberOfObjects++;
			}
		}
		
		if (numberOfObjects > 0) {
			map.getLayoutTarget().dom.setScrollLeft(xsum / numberOfObjects - map.getInnerWidth() / 2);
			map.getLayoutTarget().dom.setScrollTop(ysum / numberOfObjects - map.getInnerHeight() / 2);
		}
		else {
			map.getLayoutTarget().dom.setScrollLeft(map.getMapDimensionSize().width / 2 - map.getInnerWidth() / 2);
			map.getLayoutTarget().dom.setScrollTop(map.getMapDimensionSize().height / 2 - map.getInnerHeight() / 2);
		}
	}
}
