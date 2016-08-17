package lasad.processors.specific;

import java.sql.SQLException;
import java.util.Vector;

import lasad.controller.ClientCommunicationController;
import lasad.controller.ManagementController;
import lasad.controller.UserManagementController;
import lasad.entity.User;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * this class handles all actions while an User login
 * 
 * @author ? Refactored by TUC
 */
public class LoginActionProcessor extends AbstractActionObserver implements ActionObserver {

	/**
	 * Constructor
	 */
	public LoginActionProcessor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lasad.processors.ActionObserver#processAction(lasad.gwt.client.communication.objects.Action, lasad.entity.User,
	 * java.lang.String)
	 */
	@Override
	public boolean processAction(Action a, User u, String sessionID) {
		boolean returnValue = false;
		if (a.getCategory().equals(Categories.Management)) {
			if (a.getCmd().equals(Commands.Login)) { // Check
				try {
					processLogin(a, sessionID);
					returnValue = true;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return returnValue;
	}

	/**
	 * check if the username and pwd are right or not and if they are used or not if the username or pwd is wrong,feed back to the
	 * user if they are in use, feed back to user too otherwise log in successfully
	 * 
	 * @param a a specific LASAD action
	 * @param sessionID the Session ID
	 * @author ?
	 */
	private void processLogin(Action a, String sessionID) throws SQLException {
		synchronized (ActionProcessor.mapUsersLock) {
			Logger.debugLog("Enable lock mapUsersLock from processLogin Part");

			// Returns in any case (login successful or not) a new User.
			// However, if
			// login failed, then the User will not have a nickname and a
			// password.
			Boolean isPasswordEncrypted = ActionProcessor.isPasswordEncrypted(a);
			User u = User.login(a.getParameterValue(ParameterTypes.UserName), a.getParameterValue(ParameterTypes.Password),
					isPasswordEncrypted);
			if (u.getNickname() != null) {

				// Check if session ID is already assigned to a user --> Inactive
				// the old user
				// User oldUser = null;

				/*
				 * If auto-logout-login = true -> allows a user to kick out a user already logged in with the same ID/PWD
				 */
				String autoLogoutOnLoginStr = myServer.conf.parameters.get("Auto-Logout-On-Login");

				if (autoLogoutOnLoginStr != null) {
					boolean autoLogoutOnLogin = Boolean.parseBoolean(autoLogoutOnLoginStr);
					if (autoLogoutOnLogin) {
						replaceUser(a, sessionID);
						removeOldUser(sessionID);

					}
				}

				handleActionPackageWithDifferentClients(a, u, sessionID);

				// This is required to send the answer later
				myServer.currentState.sessionToUser.put(sessionID, u);
				myServer.currentState.userIdToSession.put(a.getParameterValue(ParameterTypes.UserName), sessionID);

				// Correct username and password combination

				// Check if username is already used by another client --> Send
				// error
				for (User loggedInUser : myServer.currentState.userMaps.keySet()) {
					if (loggedInUser.getNickname().equalsIgnoreCase(u.getNickname())) {
						// Username already in use...
						ActionPackage ap = ActionPackageFactory.failedLogin("Login failed. Username already in use");
						Logger.doCFLogging(ap);
						ManagementController.addToUsersActionQueue(ap, sessionID);
						UserManagementController.userFailedToLogin = sessionID;
						return;
					}
				}

				// Username not in use, everything ok
				u.setSessionID(sessionID);

				ActionPackage ap = ActionPackageFactory.confirmLogin(u.getNickname(), u.getRole());
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, sessionID);
				Logger.log("Login successful. Username: " + u.getNickname() + ", Role: " + u.getRole());

				// Add mapping to get all maps of a user later
				myServer.currentState.userMaps.put(u, new Vector<Integer>());
			}

			// Login failed
			else {
				handleActionPackageWithDifferentClients(a, u, sessionID);
				u.setNickname(a.getParameterValue(ParameterTypes.UserName) + "_LOGIN_FAILED");
				myServer.currentState.sessionToUser.put(sessionID, u);
				myServer.currentState.userIdToSession.put(u.getNickname(), sessionID);

				String error = "Login failed. Username (" + a.getParameterValue(ParameterTypes.UserName) + ") or password ("
						+ a.getParameterValue(ParameterTypes.Password) + ") wrong";
				Logger.log(error);

				ActionPackage ap = ActionPackageFactory.failedLogin(error);
				Logger.doCFLogging(ap);
				ManagementController.addToUsersActionQueue(ap, sessionID);
				UserManagementController.userFailedToLogin = sessionID;
				myServer.currentState.userIdToSession.remove(u.getNickname(), sessionID);
				myServer.currentState.sessionToUser.remove(sessionID, u);

			}
			Logger.debugLog("Disable lock mapUsersLock from processLogin");
		}
	}

	private void removeOldUser(String sessionID) {
		User oldUser;
		synchronized (ActionProcessor.sessionToUserLock) {
			Logger.debugLog("Enable lock sessionToUserLock from processLogin");
			oldUser = myServer.currentState.sessionToUser.get(sessionID);
			Logger.debugLog("Disable lock sessionToUserLock from processLogin");
		}

		if (oldUser != null) {
			removeOldUserLock(oldUser, sessionID);
		}
	}

	private void replaceUser(Action a, String sessionID) {
		synchronized (ActionProcessor.userIdToSessionLock) {
			Logger.debugLog("Enable lock userIdToSessionLock from processLogin");
			synchronized (ActionProcessor.sessionToUserLock) {
				Logger.debugLog("Enable lock sessionToUserLock from processLogin");

				if (myServer.currentState.userIdToSession.containsKey(a.getParameterValue(ParameterTypes.UserName))) {
					String oldSessionId = myServer.currentState.userIdToSession.remove(a.getParameterValue(ParameterTypes.UserName));
					myServer.currentState.userIdToSession.put(a.getParameterValue(ParameterTypes.UserName), sessionID);

					removeoldSessionId(sessionID, oldSessionId);

					ClientCommunicationController.processDuplicateUserLogin(oldSessionId, sessionID);

					workonAddToRMIUserQueueAndSendActionsLock(oldSessionId, sessionID);
				}
				Logger.debugLog("Disable lock sessionToUserLock from processLogin");
			}
			Logger.debugLog("Disable lock userIdToSessionLock from processLogin");
		}
	}

	/**
	 * remove the old session id and add new session id to the temp user
	 * 
	 * @param sessionID
	 * @param oldSessionId
	 * @author unknown
	 */
	private void removeoldSessionId(String sessionID, String oldSessionId) {
		if (myServer.currentState.sessionToUser.containsKey(oldSessionId)) {
			User tmpUser = myServer.currentState.sessionToUser.remove(oldSessionId);
			myServer.currentState.sessionToUser.put(sessionID, tmpUser);

		} else {
			Logger.debugLog("sessionToUser does not contain key:" + oldSessionId);
		}
	}

	/**
	 * updates the sessionID in the ActionPackages for RMI Clients
	 * 
	 * @param oldSessionID String
	 * @param sessionID String
	 */
	private void workonAddToRMIUserQueueAndSendActionsLock(String oldSessionId, String sessionID) {
		synchronized (ActionProcessor.addToRMIUserQueueAndSendActionsLock) {
			Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from processLogin");
			if (myServer.currentState.actionPackagesForRMIClients.containsKey(oldSessionId)) {
				Vector<ActionPackage> tmp = myServer.currentState.actionPackagesForRMIClients.remove(oldSessionId);
				myServer.currentState.actionPackagesForRMIClients.put(sessionID, tmp);
			}
			Logger.debugLog("Disable lock addToRMIUserQueueAndSendActionsLock from processLogin");
		}

	}

	/**
	 * removes the old user's session from the state removes the old user's Awareness Cursor and lock
	 * 
	 * @param oldUser User
	 * @param sessionID String
	 */
	private void removeOldUserLock(User oldUser, String sessionID) {
		synchronized (ActionProcessor.userMapsLock) {
			Logger.debugLog("Enable lock userMapsLock from processLogin");
			for (Integer mapID : myServer.currentState.userMaps.get(oldUser)) {
				ActionPackage ap = ActionPackageFactory.userEventUserLeave(mapID, oldUser.getNickname());
				Logger.doCFLogging(ap);
				ManagementController.addToAllUsersOnMapActionQueue(ap, mapID);

				myServer.currentState.mapUsers.get(mapID).remove(oldUser);

				synchronized (ActionProcessor.addToRMIUserQueueAndSendActionsLock) {
					Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from processLogin");
					myServer.currentState.actionPackagesForRMIClients.remove(sessionID);
					Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from processLogin");
				}

				synchronized (ActionProcessor.addToGWTUserQueueAndSendActionsLock) {
					Logger.debugLog("Enable lock addToGWTUserQueueAndSendActionsLock from processLogin");
					myServer.currentState.actionPackagesForGWTClients.remove(sessionID);
					Logger.debugLog("Disable lock addToGWTUserQueueAndSendActionsLock from processLogin");
				}

				// Remove Awareness Cursor
				ManagementController.removeAwarenessCursor(mapID, oldUser);

				// Remove Locks
				aproc.removeLocks(mapID, oldUser);
			}
			myServer.currentState.userMaps.remove(oldUser);

			Logger.debugLog("Disable lock userMapsLock from processLogin");
		}
	}

	/**
	 * differentiates between GWT, RMI and WS clients
	 * 
	 * @param a Action
	 * @param u User
	 * @param sessionID String
	 */
	private void handleActionPackageWithDifferentClients(Action a, User u, String sessionID) {
		Vector<ActionPackage> v = new Vector<ActionPackage>();

		if (("RMI").equals(a.getParameterValue(ParameterTypes.Method))) {
			u.setUsingWebservice(true);
			myServer.currentState.actionPackagesForRMIClients.put(sessionID, v);
		} else {
			myServer.currentState.actionPackagesForGWTClients.put(sessionID, v);
		}
		// TODO Implement WS client handling if required.
	}

}
