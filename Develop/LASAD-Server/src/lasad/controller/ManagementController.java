package lasad.controller;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import lasad.Server;
import lasad.entity.Element;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ManagementController {

	public static HashMap<Integer, User> usersToBeRemovedFromMaps = new HashMap<Integer, User>();

	/**
	 * Creates an ActionPackage to remove a certain awareness cursor
	 * 
	 * @param mapID The id of the map which this awareness cursor is supposed to be removed from
	 * @param u The user whose awareness cursor is supposed to be removed
	 * @author FL
	 */
	public static void removeAwarenessCursor(int mapID, User u) {
		int elementID = Element.getAwarenessCursorElementID(mapID, u.getUserID());
		// 0 means that there is no awareness cursor of this user on the map
		if (elementID != 0) {
			// Create new revision of the map
			Revision r = new Revision(mapID, u.getUserID());
			r.saveToDatabase();

			// Set elements.end_revision_id to new Revision for the main element
			Element.updateEndRevisionID(elementID, r.getId());

			ActionPackage ap = ActionPackageFactory.deleteElement(mapID, elementID, u.getNickname());
			Logger.doCFLogging(ap);
			addToAllUsersButMySelfOnMapActionQueue(ap, mapID, u);
		}
	}

	/**
	 * Adds a certain ActionPackage to the action queues of all users an a map except to that of the user who send this
	 * ActionPackage to the server
	 * 
	 * @param p The ActionPackage
	 * @param mapID The map
	 * @param me The user that send the ActionPackage
	 * @author FL
	 */
	public static void addToAllUsersButMySelfOnMapActionQueue(ActionPackage p, int mapID, User me) {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from addToAllUsersButMySelfOnMapActionQueue");

			for (User u : Server.getInstance().currentState.mapUsers.get(mapID)) {
				if (!u.equals(me)) {
					addToUsersActionQueue(p, u.getSessionID());
					Logger.debugLog("Added to user's queue: ");
					Logger.debugLog(p.toString());
				}
			}
			Logger.debugLog("Disable lock mapUsersLock from addToAllUsersButMySelfOnMapActionQueue");
		}
	}

	/**
	 * Adds a certain ActionPackage to a user's action queue.
	 * 
	 * @param p The ActionPackage
	 * @param sessionID The session id of this user
	 * @author FL
	 */
	public static void addToUsersActionQueue(ActionPackage p, String sessionID) {

		synchronized (ActionProcessor.sessionToUserLock) {
			Logger.debugLog("Enable lock sessionToUserLock from addToUsersActionQueue");

			ActionProcessor.getInstance().addMetaInformation(p, sessionID);

			if (Server.getInstance().currentState.sessionToUser.get(sessionID) != null) {
				if (!Server.getInstance().currentState.sessionToUser.get(sessionID).isUsingWebservice()) {
					synchronized (ActionProcessor.addToGWTUserQueueAndSendActionsLock) {
						Logger.debugLog("Enable lock addToGWTUserQueueAndSendActionsLock from addToUsersActionQueue");
						Vector<ActionPackage> packages = Server.getInstance().currentState.actionPackagesForGWTClients.get(sessionID);
						if (packages != null) {
							packages.add(p);
						}
						Logger.debugLog("Disable lock addToGWTUserQueueAndSendActionsLock from addToUsersActionQueue");
					}
				} else {
					synchronized (ActionProcessor.addToRMIUserQueueAndSendActionsLock) {
						Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from addToUsersActionQueue");
						Vector<ActionPackage> packages = Server.getInstance().currentState.actionPackagesForRMIClients.get(sessionID);
						if (packages != null) {
							packages.add(p);
						}
						Logger.debugLog("Disable lock addToRMIUserQueueAndSendActionsLock from addToUsersActionQueue");
					}
				}
			} else {
				// TODO Remove the non-existing user from all lists
			}
			Logger.debugLog("Disable lock sessionToUserLock from addToUsersActionQueue");
		}
	}

	/**
	 * Puts a certain user into the user-list of a map and also puts this map into the list of active maps of this user
	 * 
	 * @param mapID The map
	 * @param u The user
	 * @author FL
	 */
	static void addUserIntoMapList(int mapID, User u) {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from processJoin");
			if (Server.getInstance().currentState.mapUsers.get(mapID) == null) {
				Server.getInstance().currentState.mapUsers.put(mapID, new Vector<User>());
			}
			// Add user to the active users on the map
			Server.getInstance().currentState.mapUsers.get(mapID).add(u);

			// Add the map to the user's maplist
			synchronized (ActionProcessor.userMapsLock) {
				Logger.debugLog("Enable lock userMapsLock from processJoin");
				Server.getInstance().currentState.userMaps.get(u).add(mapID);
				Logger.debugLog("Disable lock userMapsLock from processJoin");
			}
			Logger.debugLog("Disable lock mapUsersLock from processJoin");
		}
	}

	/**
	 * Adds a certain ActionPackage to the action queues of all users on a map
	 * 
	 * @param p The ActionPackage
	 * @param mapID The map
	 * @author FL
	 */
	public static void addToAllUsersOnMapActionQueue(ActionPackage p, int mapID) {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from addToAllUsersOnMapActionQueue");

			for (User u : Server.getInstance().currentState.mapUsers.get(mapID)) {
				addToUsersActionQueue(p, u.getSessionID());
				Logger.debugLog("Added to user's queue: ");
				Logger.debugLog(p.toString());
			}
			Logger.debugLog("Disable lock mapUsersLock from addToAllUsersOnMapActionQueue");
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
	static void handleHistoryWithDifferentClients(Action a, int mapID, User u) {
		// Load existing elements from database
		ActionPackage completeActionsHistory = getAllActionsHappenedBefore(mapID, u);

		Action joinComplete = new Action(Commands.JoinComplete, Categories.Info);
		joinComplete.addParameter(ParameterTypes.MapId, a.getParameterValue(ParameterTypes.MapId));

		// GWT clients do not need the complete history
		if ("TRUE".equalsIgnoreCase(a.getParameterValue(ParameterTypes.Summary))) {
			ActionPackage filteredActions = filterForJoin(completeActionsHistory);
			filteredActions.addAction(joinComplete);

			Logger.doCFLogging(filteredActions);
			addToUsersActionQueue(filteredActions, u.getSessionID());
		}
		// AI clients need it...
		else {
			completeActionsHistory.addAction(joinComplete);

			Logger.doCFLogging(completeActionsHistory);
			addToUsersActionQueue(completeActionsHistory, u.getSessionID());
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
	private static ActionPackage getAllActionsHappenedBefore(int mapID, User u) {
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
	private static ActionPackage filterForJoin(ActionPackage p) {
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
	private static void workonUpdateElementForJoin(Action action, TreeMap<Integer, Action> updateActions) {
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

	/**
	 * Adds an ActionPackage to all logged in user's action queue, regardless on which map they are working on. (?)
	 * 
	 * @param p The ActionPackage
	 * @author FL
	 */
	public static void addToAllUsersActionQueue(ActionPackage p) {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from addToAllUsersActionQueue");

			for (User u : Server.getInstance().currentState.userMaps.keySet()) {
				addToUsersActionQueue(p, u.getSessionID());
				Logger.debugLog("Added to user's queue: ");
				Logger.debugLog(p.toString());
			}
			Logger.debugLog("Disable lock mapUsersLock from addToAllUsersActionQueue");
		}
	}

	/**
	 * This method is used to remove a user from all relevant HashMaps AFTER sending him/her the actions left in his/her queue
	 * 
	 * @author FL
	 */
	public static void removeUsersWhoLeftAMap() {
		for (Integer mapID : usersToBeRemovedFromMaps.keySet()) {
			User u = usersToBeRemovedFromMaps.get(mapID);

			synchronized (ActionProcessor.sessionToUserLock) {
				Logger.debugLog("Enable lock sessionToUserLock from removeUsersWhoLeftAMap");
				Logger.debugLog("Tried to remove user " + u.getSessionID() + ": "
						+ Server.getInstance().currentState.mapUsers.get(mapID).remove(u));
				Logger.debugLog("Disable lock sessionToUserLock from removeUsersWhoLeftAMap");
			}

			synchronized (ActionProcessor.userMapsLock) {
				Logger.debugLog("Enable lock userMapsLock from removeUsersWhoLeftAMap");
				Server.getInstance().currentState.userMaps.get(u).remove(mapID);
				Logger.debugLog("Disable lock userMapsLock from removeUsersWhoLeftAMap");
			}
		}
		usersToBeRemovedFromMaps.clear();
	}

}
