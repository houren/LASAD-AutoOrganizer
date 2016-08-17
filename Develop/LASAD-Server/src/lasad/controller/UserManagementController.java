/**
 * 
 */
package lasad.controller;

import lasad.Server;
import lasad.entity.Map;
import lasad.entity.Revision;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * @author SN
 */
public class UserManagementController {

	public static String userToBeRemoved = null;
	public static String userFailedToLogin = null;

	/**
	 * Processes a user joining a map
	 * 
	 * @param a The join action
	 * @param u The user joining the map
	 * @author FL
	 */
	public static void processJoin(Action a, User u) {

		int mapID = ActionProcessor.getMapIDFromAction(a);

		// Check if map exists
		if (Map.isExisting(mapID)) {
			synchronized (ActionProcessor.DeleteUpdateJoinActionLock) {

				ManagementController.addUserIntoMapList(mapID, u);

				// Generate new revision for the map
				Revision r = new Revision(mapID, "User join", u.getUserID());
				r.saveToDatabase();

				// Add to users action queue
				ActionPackage ap = ActionPackageFactory.joinMap(mapID, Server.getInstance().currentState);
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, u.getSessionID());

				// Notify all users on map that a new user joined
				ActionPackage ua = ActionPackageFactory.userEventUserJoin(mapID, u.getNickname() + " (" + u.getRole() + ")");
				Logger.doCFLogging(ua);
				ManagementController.addToAllUsersOnMapActionQueue(ua, mapID);

				// Send list of all users already active on the map to the new
				// client
				ActionPackage ul = ActionPackageFactory.userEventUserList(mapID, Server.getInstance().currentState);
				Logger.doCFLogging(ul);
				ManagementController.addToUsersActionQueue(ul, u.getSessionID());

				ManagementController.handleHistoryWithDifferentClients(a, mapID, u);

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
	 * Removes a certain user from all lists (whose id is stored in a global variable?)
	 * 
	 * @author FL
	 */
	public static void removeUserWhoLoggedOff() {
		Server myServer = Server.getInstance();
		if (userToBeRemoved != null) {

			if (myServer.currentState.sessionToUser.get(userToBeRemoved) != null) {
				synchronized (ActionProcessor.userMapsLock) {
					Logger.debugLog("Enable lock userMapsLock from removeUserWhoLoggedOff");
					myServer.currentState.userMaps.remove(myServer.currentState.sessionToUser.get(userToBeRemoved));
					Logger.debugLog("Disable lock userMapsLock from removeUserWhoLoggedOff");
				}
			}
			User loggedOutUser = myServer.currentState.sessionToUser.remove(userToBeRemoved);

			synchronized (ActionProcessor.userIdToSessionLock) {
				Logger.debugLog("Enable lock userIdToSessionLock from processLogin");
				if (myServer.currentState.userIdToSession.containsKey(loggedOutUser.getNickname())) {
					myServer.currentState.userIdToSession.remove(loggedOutUser.getNickname());
				}
				Logger.debugLog("Disable lock userIdToSessionLock from processLogin");
			}

			synchronized (ActionProcessor.addToRMIUserQueueAndSendActionsLock) {
				Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from removeUserWhoLoggedOff");
				myServer.currentState.actionPackagesForRMIClients.remove(userToBeRemoved);
				Logger.debugLog("Disable lock addToRMIUserQueueAndSendActionsLock from removeUserWhoLoggedOff");
			}

			synchronized (ActionProcessor.addToGWTUserQueueAndSendActionsLock) {
				Logger.debugLog("Enable lock addToGWTUserQueueAndSendActionsLock from removeUserWhoLoggedOff");
				myServer.currentState.actionPackagesForGWTClients.remove(userToBeRemoved);
				Logger.debugLog("Disable lock addToGWTUserQueueAndSendActionsLock from removeUserWhoLoggedOff");
			}

			userToBeRemoved = null;
		} else {
			Logger.debugLog("No user logout detected.");
		}
	}

	/**
	 * To send an error message to the client (which says that either user name or password was wrong), we needed his sessionID
	 * stored. However, we no longer need it after sending out the error message. In fact, it could even cause problems later.
	 * Thus, we drop all sessionIDs from users that did not login correctly
	 * 
	 * @author FL
	 */
	public static void removeUserThatDidNotLoginCorrectly() {
		if (userFailedToLogin != null) {
			synchronized (ActionProcessor.sessionToUserLock) {
				Logger.debugLog("Enable lock sessionToUserLock from removeUserThatDidNotLoginCorrectly");
				Server.getInstance().currentState.sessionToUser.remove(userFailedToLogin);
				Logger.debugLog("Disable lock sessionToUserLock from removeUserThatDidNotLoginCorrectly");
			}
			userFailedToLogin = null;
		}
	}

}
