package lasad.processors.specific;

import java.util.TreeMap;
import java.util.Vector;

import lasad.controller.ManagementController;
import lasad.controller.UserManagementController;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.Template;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ManagementActionProcessor extends AbstractActionObserver implements ActionObserver {

	public ManagementActionProcessor() {
		super();
	}

	/**
	 * handling the action that a user joins a map return a current state of this map to the new user and inform the other users
	 * of this map
	 * 
	 * @param a a specific LASAD action
	 * @param u User,who wants to join this map
	 * @author ZGE
	 */
	public void processJoin(Action a, User u) {

		int mapID = ActionProcessor.getMapIDFromAction(a);

		// Check if map exists
		if (Map.isExisting(mapID)) {
			synchronized (ActionProcessor.DeleteUpdateJoinActionLock) {

				// TODO Zhenyu
				addUserIntoMapList(mapID, u);
				// If first user on the map

				// Generate new revision for the map
				Revision r = new Revision(mapID, "User join", u.getUserID());
				r.saveToDatabase();

				// Add to users action queue
				ActionPackage ap = ActionPackageFactory.joinMap(mapID, myServer.currentState);
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());

				// Notify all users on map that a new user joined
				ActionPackage ua = ActionPackageFactory.userEventUserJoin(mapID, u.getNickname() + " (" + u.getRole() + ")");
				Logger.doCFLogging(ua);
				ManagementController.addToAllUsersOnMapActionQueue(ua, mapID);

				// Send list of all users already active on the map to the new
				// client
				ActionPackage ul = ActionPackageFactory.userEventUserList(mapID, myServer.currentState);
				Logger.doCFLogging(ul);
				ManagementController.addToUsersActionQueue(ul, u.getSessionID());

				// TODO Zhenyu
				handleHistoryWithDifferentClients(a, mapID, u);

				// Needed when user imports a largo/argunaut file
				if (a.getParameterValue(ParameterTypes.XMLText) != null) {
					ActionPackage pack = new ActionPackage();
					Action action = new Action(Commands.Import, Categories.Map);
					// Action action = new Action("IMPORT", "MAP");
					action.addParameter(ParameterTypes.MapId, Integer.toString(mapID));
					action.addParameter(ParameterTypes.XMLText, a.getParameterValue(ParameterTypes.XMLText));
					pack.addAction(action);
					ManagementController.addToUsersActionQueue(pack, u.getSessionID());
				}
			}
		}
	}

	/**
	 * Gets a get-history-request from a client (e.g. when user join a map during a session) and, if needed (in case of
	 * GWT-Clients), modifies the history so that certain actions are merged into one single actions to reduce the size of the
	 * history
	 * 
	 * @param a The get-history-request action
	 * @param mapID The id of the map the user wants the history of
	 * @param u The user
	 * @author FL
	 */
	private void handleHistoryWithDifferentClients(Action a, int mapID, User u)
	// TODO The same procedure exists in ActionProcessor. Fix.
	{
		// Load existing elements from database
		ActionPackage completeActionsHistory = getAllActionsHappenedBefore(mapID, u);

		Action joinComplete = new Action(Commands.JoinComplete, Categories.Info);
		joinComplete.addParameter(ParameterTypes.MapId, a.getParameterValue(ParameterTypes.MapId));

		// GWT clients do not need the complete history
		if ("TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Summary))) {
			ActionPackage filteredActions = filterForJoin(completeActionsHistory);
			filteredActions.addAction(joinComplete);

			Logger.doCFLogging(filteredActions);
			ManagementController.addToUsersActionQueue(filteredActions, u.getSessionID());
		}
		// AI clients need it...
		else {
			completeActionsHistory.addAction(joinComplete);

			Logger.doCFLogging(completeActionsHistory);
			ManagementController.addToUsersActionQueue(completeActionsHistory, u.getSessionID());
		}

	}

	/**
	 * Puts a certain user into the user-list of a map and also puts this map into the list of active maps of this user
	 * 
	 * @param mapID The map
	 * @param u The user
	 * @author FL
	 */
	private void addUserIntoMapList(int mapID, User u) {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from processJoin");
			if (myServer.currentState.mapUsers.get(mapID) == null) {
				myServer.currentState.mapUsers.put(mapID, new Vector<User>());
			}
			// Add user to the active users on the map
			myServer.currentState.mapUsers.get(mapID).add(u);

			// Add the map to the user's maplist
			synchronized (ActionProcessor.userMapsLock) {
				Logger.debugLog("Enable lock userMapsLock from processJoin");
				myServer.currentState.userMaps.get(u).add(mapID);
				Logger.debugLog("Disable lock userMapsLock from processJoin");
			}
			Logger.debugLog("Disable lock mapUsersLock from processJoin");
		}
	}

	/**
	 * Get all actions that happened on this map before the user joined the map
	 * 
	 * @param mapID The map
	 * @param u The user joining this map
	 * @return An ActionPackage containing all actions
	 * @author FL
	 */
	private ActionPackage getAllActionsHappenedBefore(int mapID, User u) {
		ActionPackage p = Map.getCompleteElementInformation(mapID);

		return p;
	}

	/**
	 * Joining users usually don't need the complete history of actions so this procedure iterates over the history and generates
	 * a current-state-ActionPackage
	 * 
	 * @param p The ActionPackage with the whole history of actions
	 * @return Only necessary ActionPackages
	 * @author MB
	 */
	private ActionPackage filterForJoin(ActionPackage p) {
		ActionPackage newActionPackage = new ActionPackage();

		// Note: Integer is used as key for sorting purposes
		// Temporarily stores all parsed CREATE-Actions
		TreeMap<Integer, Action> createActions = new TreeMap<Integer, Action>();

		// Temporarily stores all parsed UPDATE-Actions. Every UPDATE-Action
		// for an element after the first one will update this first UPDATE-Action
		// (and won't be put into the Map)
		TreeMap<Integer, Action> updateActions = new TreeMap<Integer, Action>();

		for (Action action : p.getActions()) {
			switch (action.getCmd()) {
			case CreateElement:
				createActions.put(Integer.parseInt(action.getParameterValue(ParameterTypes.Id)), action);
				break;
			case DeleteElement:
				createActions.remove(Integer.parseInt(action.getParameterValue(ParameterTypes.Id)));
				break;
			case ChatMsg:
				newActionPackage.addAction(action);
				break;
			case UpdateElement:
				workonUpdateElementForJoin(action, updateActions);
				break;
			default:
				break;
			}
		}

		for (Action action : createActions.values()) {
			newActionPackage.addAction(action);
		}
		for (Integer key : updateActions.keySet()) {
			// Work-Around info: Filter UPDATE-ACTIONS that do not have an element
			// which could happen if an edited element was deleted later
			// Atm there's no other way to deal with this because the create und update actions
			// are not added to the new ActionPackage in the order they actually occurred
			if (createActions.containsKey(key)) {
				newActionPackage.addAction(updateActions.get(key));
			}
		}
		return newActionPackage;
	}

	/**
	 * Used for the filtering process when a gwt-client joins an active map. Merges actions packages related to a certain element
	 * into one single action package of possible
	 * 
	 * @param action The CreateElementAction of a certain element
	 * @param updateActions All UpdateActions for this element, which are to be merged into the createAction
	 * @author MB
	 */
	private void workonUpdateElementForJoin(Action action, TreeMap<Integer, Action> updateActions) {
		if (action.getParameterValue(ParameterTypes.Status) == null) {
			String elementId = action.getParameterValue(ParameterTypes.Id);
			// If there's already an UPDATE-ACTION for the element with elementId
			// then update all existing parameters and add the new ones
			// else just add a new UPDATE-ACTION
			if (updateActions.containsKey(Integer.parseInt(elementId))) {
				Vector<ParameterTypes> parameterTypes = updateActions.get(Integer.parseInt(elementId)).getParameterTypes();
				Action outdatedAction = updateActions.get(Integer.parseInt(elementId));
				for (Parameter parameter : action.getParameters()) {
					if (parameterTypes.contains(parameter.getType())) {
						// This DIRECTION condition is needed because of the special
						// mechanism of the starting/ending-Connectors of a link
						// The Problem is that atm we cannot navigate from a link
						// over the corresponding connector to the linked element's object
						//
						// TODO Another known problem is that if the UPDATE-ACTIONS for a
						// certain link element only consist of DIRECTION changes
						// then it may result in a "ghost"-UPDATE-Action which
						// appears in the UPDATE-Actions list but does not change anything,
						// which is the case when the last direction update switches the
						// link's direction to the one it had on creation
						if (parameter.getType().equals(ParameterTypes.Direction)) {
							outdatedAction = Action.removeParameter(outdatedAction, ParameterTypes.Direction);
						} else {
							outdatedAction.replaceParameter(parameter.getType(), parameter.getValue());
						}
					} else {
						outdatedAction.addParameter(parameter.getType(), parameter.getValue());
					}
				}
				updateActions.put(Integer.parseInt(elementId), outdatedAction);
			} else {
				updateActions.put(Integer.parseInt(elementId), action);
			}
		}
	}

	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (a.getCategory().equals(Categories.Management)) {
			if (u != null) {
				switch (a.getCmd()) {
				case Join:// Check
					processJoin(a, u);
					returnValue = true;
					break;
				case Leave:// Check
					processLeaveMap(a, u);
					returnValue = true;
					break;
				case Logout:// Check
					processLogout(a, u);
					returnValue = true;
					break;
				case List:// Check
					processList(a, u);
					returnValue = true;
					break;
				case MapDetails:// Check
					processMapDetails(a, sessionID);
					returnValue = true;
					break;
				case TemplateDetails:// Check
					processTemplateDetails(a, u);
					returnValue = true;
					break;
				case GetAllOntologiesAndTemplates:// Check
					processGetAllOntologiesAndTemplates(u);
					returnValue = true;
					break;
				case GetOntology:// Check
					processGetOntology(a, u);
					returnValue = true;
					break;
				case CreateAndJoin:// Check
					processCreateAndJoinMap(a, u);
					returnValue = true;
					break;
				case GetTemplates:// Check
					processAuthoringGetTemplates(u, false);
					returnValue = true;
					break;
				default:
					break;
				}
			} else {
				Logger.debugLog("User is null (maybe logged out before) --> Action ignored: " + a.toString());
			}
		}
		return returnValue;
	}

	/**
	 * Processes a get-template-details action
	 * 
	 * @param a The get-action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processTemplateDetails(Action a, User u) {
		ActionPackage ap = ActionPackageFactory.getTemplateDetails(a.getParameterValue(ParameterTypes.Template));
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}

	/**
	 * Creates a new map and then automatically joins the user that send the create-map-action
	 * 
	 * @param a The create-and-join-action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processCreateAndJoinMap(Action a, User u) {
		if (Map.getMapID(a.getParameterValue(ParameterTypes.MapName)) == -1) {
			Map m = new Map(a.getParameterValue(ParameterTypes.MapName), Template.getTemplateID(a
					.getParameterValue(ParameterTypes.Template)), u.getUserID(), null);
			m.saveToDatabase(aproc);
			if(a.getParameterValue(ParameterTypes.BackgroundImageURL)!= null )
			{
				Map.setBackgroundImage(m.getId(), a.getParameterValue(ParameterTypes.BackgroundImageURL));
			}
			
			myServer.currentState.maps.put(m.getId(), m);
			myServer.currentState.mapUsers.put(m.getId(), new Vector<User>());

			// m.generateMapConfigurationFile();
			ActionPackage ap = ActionPackageFactory.getNewMapAction(m);
			Logger.doCFLogging(ap);
			ManagementController.addToAllUsersActionQueue(ap);

			Action joinMap = ActionPackageFactory.joinMap(m.getId());
			if (a.getParameterValue(ParameterTypes.XMLText) != null) {
				joinMap.addParameter(ParameterTypes.XMLText, a.getParameterValue(ParameterTypes.XMLText));
			}
			if (a.getParameterValue(ParameterTypes.ChatLog) != null) {
				joinMap.addParameter(ParameterTypes.ChatLog, a.getParameterValue(ParameterTypes.ChatLog));
			}
			processJoin(joinMap, u);
		} else {
			ActionPackage ap = ActionPackageFactory.error("Session already exists! Please choose a different name");
			Logger.doCFLogging(ap);
			ManagementController.addToUsersActionQueue(ap, u.getSessionID());
		}

	}

	/**
	 * Processes a get-ontology action
	 * 
	 * @param a The request action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processGetOntology(Action a, User u) {
		int mapID = ActionProcessor.getMapIDFromAction(a);
		ActionPackage ap = ActionPackageFactory.getOntology(mapID);
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}

	/**
	 * Processes a get-all-ontologies-and-templates action
	 * 
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processGetAllOntologiesAndTemplates(User u) {
		// Returns a List of all Ontologies and Templates
		ActionPackage ap = ActionPackageFactory.getAllOntologiesAndTemplates();
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}

	/**
	 * Processes an action for a map details request
	 * 
	 * @param a The action
	 * @param sessionID The session id of the user the send this action
	 * @author FL
	 */
	private void processMapDetails(Action a, String sessionID) {
		ActionPackage ap = ActionPackageFactory.getMapDetails((Integer.parseInt(a.getParameterValue(ParameterTypes.MapId))),
				myServer.currentState);
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, sessionID);
	}

	/**
	 * Generate response to list all active maps on server
	 * 
	 * @param a The action that requested that response
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processList(Action a, User u) {
		Logger.debugLog("Processing request: List maps");

		ActionPackage ap = ActionPackageFactory.listMaps(myServer.currentState, u.getUserID());
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}

	/**
	 * Processes a leave-map action from a user
	 * 
	 * @param a The leave-map action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processLeaveMap(Action a, User u) {

		int mapID = ActionProcessor.getMapIDFromAction(a);

		ActionPackage ap1 = ActionPackageFactory.forcedClose();
		Logger.doCFLogging(ap1);
		ManagementController.addToUsersActionQueue(ap1, u.getSessionID());

		// Leave the map
		ActionPackage ap2 = ActionPackageFactory.leaveMap(mapID);
		Logger.doCFLogging(ap2);
		ManagementController.addToUsersActionQueue(ap2, u.getSessionID());

		ActionPackage ap = ActionPackageFactory.userEventUserLeave(mapID, u.getNickname() + " (" + u.getRole() + ")");
		Logger.doCFLogging(ap);
		ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);

		ManagementController.usersToBeRemovedFromMaps.put(mapID, u);

		// Remove Awareness Cursor
		ManagementController.removeAwarenessCursor(mapID, u);

		// Remove Locks
		aproc.removeLocks(mapID, u);
	}

	/**
	 * Processes a logout action from a user
	 * 
	 * @param a The logout action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processLogout(Action a, User u) {

		// Leave all maps, remove awareness-cursors and locks

		synchronized (ActionProcessor.userMapsLock) {
			Logger.debugLog("Enable lock userMapsLock from processLogout");

			for (Integer mapID : myServer.currentState.userMaps.get(u)) {
				processLeaveMap(ActionPackageFactory.getLeaveMapAction(mapID), u);
			}
			Logger.debugLog("Disable lock userMapsLock from processLogout");
		}

		// Send Logout
		ActionPackage ap = ActionPackageFactory.logOut();
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());

		// Remove user from push list
		UserManagementController.userToBeRemoved = u.getSessionID();
	}

	/**
	 * Processes a get-template action
	 * 
	 * @param a The request action
	 * @param u The user that send this action
	 * @author FL
	 */
	private void processAuthoringGetTemplates(User u, boolean authoring) {

		ActionPackage ap = ActionPackageFactory.getTemplateListActionPackage(authoring);
		Logger.doCFLogging(ap);
		ManagementController.addToUsersActionQueue(ap, u.getSessionID());
	}

}
