package lasad.processors;

import java.sql.SQLException;

import lasad.Server;
import lasad.controller.ClientCommunicationController;
import lasad.controller.ManagementController;
import lasad.controller.MapController;
import lasad.controller.UserManagementController;
import lasad.entity.Element;
import lasad.entity.User;
import lasad.helper.HeartbeatChecker;
import lasad.logging.Logger;
import lasad.logging.xml.ActionPackageXmlConverter;
import lasad.processors.specific.AuthoringActionProcessor;
import lasad.processors.specific.CommunicationActionProcessor;
import lasad.processors.specific.FeedbackActionProcessor;
import lasad.processors.specific.FeedbackAuthoringActionProcessor;
import lasad.processors.specific.FileActionProcessor;
import lasad.processors.specific.LoginActionProcessor;
import lasad.processors.specific.ManagementActionProcessor;
import lasad.processors.specific.MapActionProcessor;
import lasad.processors.specific.QuestionnaireActionProcessor;
import lasad.processors.specific.ReplayActionProcessor;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ActionProcessor extends ActionObservable {

	public static final Object addToGWTUserQueueAndSendActionsLock = new Object();
	public static final Object addToRMIUserQueueAndSendActionsLock = new Object();
	public static final Object session2RemoveLock = new Object();
	public static final Object userMapsLock = new Object();
	private static final Object createElementLock = new Object();
	public static final Object DeleteUpdateJoinActionLock = new Object();
	public static final Object mapUsersLock = new Object();
	public static final Object sessionToUserLock = new Object();
	public static final Object userIdToSessionLock = new Object();

	private static Server myServer;
	private static ActionProcessor singletonInstance = null;

	/*private AuthoringActionProcessor authoringActionProc;
	private MapActionProcessor mapActionProc;
	private ManagementActionProcessor mgmtActionProc;
	private CommunicationActionProcessor commActionProc;
	private FeedbackActionProcessor feedbackActionProc;
	private QuestionnaireActionProcessor questActionProc;
	private ReplayActionProcessor replayActionProc;
	private FileActionProcessor fileActionProc;
	private LoginActionProcessor authActionProc;
	private FeedbackAuthoringActionProcessor feedAuthActionProc;*/

	/**
	 * Constructor
	 */
	private ActionProcessor() {
		super();
		singletonInstance = this;
		myServer = Server.getInstance();
		/*this.authoringActionProc = */new AuthoringActionProcessor();
		HeartbeatChecker hbc = new HeartbeatChecker();
		hbc.start();
		/*this.mapActionProc = */new MapActionProcessor();
		/*this.mgmtActionProc = */new ManagementActionProcessor();
		/*this.commActionProc = */new CommunicationActionProcessor();
		/*this.feedbackActionProc = */new FeedbackActionProcessor();
		/*this.questActionProc = */new QuestionnaireActionProcessor();
		/*this.replayActionProc = */new ReplayActionProcessor();
		/*this.fileActionProc = */new FileActionProcessor();
		/*this.authActionProc = */new LoginActionProcessor();
		/*this.feedAuthActionProc = */new FeedbackAuthoringActionProcessor();
	}

	/**
	 * Method to get Instance of ActionProcessor
	 * 
	 * @return singletonInstance of ActionProcessor
	 * @author SN
	 */
	public static ActionProcessor getInstance() {
		if (singletonInstance == null) {
			new ActionProcessor();
		}
		return singletonInstance;
	}

	/**
	 * Fetches and distributes an incoming ActionPackage. Cleans up afterwards.
	 * 
	 * @param p The incoming ActionPackage
	 * @author FL
	 */
	public void processActionPackage(ActionPackage p) {
		Logger.log("Action Package arrived : ");
		Logger.logPackage(p);

		String sessionID = p.getParameterValue(ParameterTypes.SessionId);
		
		try {
			Logger.debugLog("processActionPackage -\n" + ActionPackageXmlConverter.toXml(p));
			System.out.println("processActionPackage -\n" + ActionPackageXmlConverter.toXml(p));
		}
		catch(Exception e){
			Logger.debugLog("Error in printing log message xml: " + e);
		}
		
		// Update timeout status for the current user
		updateTimeOut(sessionID);

		// Process actual package
		addMetaInformation(p, sessionID);

		if (checkIfPackageContainsMapCreateActions(p)) {
			// Lock required to make sure the LAST-ID is used correctly.
			// The lock is shared by the creation of pre-defined elements
			synchronized (createElementLock) {
				Logger.debugLog("Enable lock createElementLock from processActionPackage");
				distributeActions(p, sessionID);
				Logger.debugLog("Disable lock createElementLock from processActionPackage");
			}
		} else {
			distributeActions(p, sessionID);
		}

		ClientCommunicationController.sendActionsToConnectedClients();
		UserManagementController.removeUserThatDidNotLoginCorrectly();
		ManagementController.removeUsersWhoLeftAMap();
		UserManagementController.removeUserWhoLoggedOff();
	}

	/**
	 * Extracts the Actions out of an ActionPackage and distributes them by their category
	 * 
	 * @param p The ActionPackage
	 * @param sessionID The session id of that ActionPackage (can also be found as parameter of that AP)
	 * @author FL
	 */
	private void distributeActions(ActionPackage p, String sessionID) {
		for (Action a : p.getActions()) {
			try {
				distributeByActionCategory(a, sessionID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Iterates over Actions of an ActionPackage and checks for map creation actions
	 * 
	 * @param p The ActionPackage
	 * @return True, if the ActionPackage contains a map creation action
	 * @author FL
	 */
	private boolean checkIfPackageContainsMapCreateActions(ActionPackage p) {
		for (Action a : p.getActions()) {
			if (a.getCmd().equals(Commands.CreateElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the last heart beat time of a certain user. This time is used for timeout issues
	 * 
	 * @param sessionID The session id of a certain user
	 * @author FL
	 */
	private void updateTimeOut(String sessionID) {
		User u = null;

		synchronized (sessionToUserLock) {
			Logger.debugLog("Enable lock sessionToUserLock from updateTimeOut");
			u = myServer.currentState.sessionToUser.get(sessionID);
			Logger.debugLog("Disable lock sessionToUserLock from updateTimeOut");
		}

		if (u != null) {
			u.setLastHeartbeat(System.currentTimeMillis());
		}
	}

	/**
	 * Distributes an action by its category, notifying all observing processors
	 * 
	 * @param a The action to be distributed
	 * @param sessionID The session id of the ActionPackage that contained this action
	 * @throws SQLException
	 * @author FL/SN
	 */
	private void distributeByActionCategory(Action a, String sessionID) throws SQLException {

		User u = null;

		synchronized (sessionToUserLock) {
			Logger.debugLog("Enable lock sessionToUserLock from distributeByActionCategory");
			u = myServer.currentState.sessionToUser.get(sessionID);
			Logger.debugLog("Disable lock sessionToUserLock from distributeByActionCategory");
		}
		notifyObservers(a, u, sessionID);
	}

	/**
	 * Releases all locks a certain user had on any element of a certain map
	 * 
	 * @param mapID The map
	 * @param u The user whose locks are to be removed
	 * @author FL
	 */
	public void removeLocks(int mapID, User u) {
		Action a = Element.removeLastActiveLockOfUser(u, mapID);

		// If there is an active lock of this user
		if (a != null) {
			MapController.processUpdateElement(a, u);
		}
	}

	/**
	 * Extracts the map id of an action
	 * 
	 * @param a The Action
	 * @return The map id
	 * @author Zge
	 */
	public static int getMapIDFromAction(Action a) {
		int mapID = 0;

		try {
			mapID = Integer.parseInt(a.getParameterValue(ParameterTypes.MapId));
		} catch (Exception e) {
			Logger.debugLog("ERROR: No valid map-id.");
		}
		return mapID;
	}

	/**
	 * Packages are expanded: To Update- Create- and DeleteActions will be added the number of these actions as well as SessionID
	 * and the current time.
	 * 
	 * @param p ActionPackage, from which the Actions are taken
	 * @param sessionID String, which contains the sessionID
	 */
	public void addMetaInformation(ActionPackage p, String sessionID) {
		int numActions = 0;

		// to avoid adding meta-information twice, for example during a replay.
		for (Action a : p.getActions()) {
			if (a.getParameterValue(ParameterTypes.UserActionId) != null) {
				return;
			}
			switch (a.getCmd()) {
			case UpdateElement:
			case CreateElement:
			case DeleteElement:
				numActions++;
				break;
			default:
				break;
			}
		}
		// Nothing to add?
		if (numActions == 0) {
			return;
		}

		long time = System.currentTimeMillis();

		for (Action a : p.getActions()) {
			switch (a.getCmd()) {
			case UpdateElement:
			case CreateElement:
			case DeleteElement:
				a.addParameter(ParameterTypes.UserActionId, sessionID + time);
				a.addParameter(ParameterTypes.NumActions, "" + numActions);
				break;
			default:
				break;
			}
		}
	}
	
	public static Boolean isPasswordEncrypted(Action a) {
		String alreadyConvertedPasswordString = a.getParameterValue(ParameterTypes.passwordEncrypted);
		boolean alreadyConvertedPassword = false;
		if (alreadyConvertedPasswordString != null){
			try {
				alreadyConvertedPassword = Boolean.valueOf(alreadyConvertedPasswordString);
				return alreadyConvertedPassword;
			}
			catch (Exception e){
				Logger.debugLog("[isPasswordConverted] bad format of boolean value for action : " + a);
			}
		}
		return false;
	}


}
