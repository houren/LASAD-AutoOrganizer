package lasad.controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import lasad.Server;
import lasad.helper.ActionPackageFactory;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.ClientInterface;
import lasad.shared.communication.objects.ActionPackage;

public class ClientCommunicationController {

	/**
	 * Server push to inform the servlet that there are new actions for the clients. The servlet will then forward the actions to
	 * the connected clients.
	 * 
	 * @author FL
	 */
	public static void sendActionsToConnectedClients() {

		// TODO Filter out empty user queues to reduce network load
		Logger.debugLog("@sendActionsToConnectedClients");

		sendSessions2RemoveToGWTClients();

		sendActionPackagesToGWTClients();

		// Send actions to RMI users
		// Check to avoid unnecessary calls
		sendActionPackagesToRMICLients();
	}

	/**
	 * Sending sesions2Remove to Clients
	 * 
	 * @author FL
	 */
	private static void sendSessions2RemoveToGWTClients() {
		Server myServer = Server.getInstance();
		synchronized (ActionProcessor.session2RemoveLock) {
			Logger.debugLog("Disable lock session2RemoveLock from sendActionsToConnectedClients");
			int toRemoveActions = 0;
			for (String s : myServer.currentState.session2Remove.keySet()) {
				toRemoveActions += myServer.currentState.session2Remove.get(s).size();
			}

			Logger.debugLog("sendActionsToConnectedClients actionsNum:" + toRemoveActions);

			if (toRemoveActions > 0) {
				if (sendToGWTClients(myServer.currentState.session2Remove)) {
					cleanSession2RemoveActionsSent();
				}
			}
			Logger.debugLog("Disable lock session2RemoveLock from sendActionsToConnectedClients");
		}
	}

	/**
	 * Sending ActionPackagesForGWTClients to clients
	 * 
	 * @author FL
	 */
	private static void sendActionPackagesToGWTClients() {
		Server myServer = Server.getInstance();
		synchronized (ActionProcessor.addToGWTUserQueueAndSendActionsLock) {
			Logger.debugLog("Enable lock addToGWTUserQueueAndSendActionsLock from sendActionsToConnectedClients");

			// Check to avoid unnecessary calls
			int gwtActions = 0;
			for (String s : myServer.currentState.actionPackagesForGWTClients.keySet()) {
				gwtActions += myServer.currentState.actionPackagesForGWTClients.get(s).size();
			}

			Logger.debugLog("@sendActionsToConnectedClients actionsNum:" + gwtActions);

			if (gwtActions > 0) {
				if (sendToGWTClients(myServer.currentState.actionPackagesForGWTClients)) {
					cleanGWTActionsSent();
				}
			}
			Logger.debugLog("Disable lock addToGWTUserQueueAndSendActionsLock from sendActionsToConnectedClients");
		}
	}

	/**
	 * Opens a HTTpURLConnection to GWT clients to send some content.
	 * 
	 * @param content ConcurrentHashMap that will be transferred to clients
	 * @return true if the content has been successfully sent - false otherwise.
	 * @author SN
	 */
	private static boolean sendToGWTClients(ConcurrentHashMap<String, Vector<ActionPackage>> content) {
		Server myServer = Server.getInstance();
		// Send actions to GWT users
		URL gwtServlet = null;
		try {
			gwtServlet = new URL(myServer.conf.parameters.get("Servlet URL"));
			HttpURLConnection servletConnection = (HttpURLConnection) gwtServlet.openConnection();
			servletConnection.setRequestMethod("POST");
			servletConnection.setDoOutput(true);

			ObjectOutputStream objOut = new ObjectOutputStream(servletConnection.getOutputStream());

			objOut.writeObject(content);
			objOut.flush();
			objOut.close();

			// Important! Even though, the answer is not needed, it is
			// required to actually execute the call.
			int result = servletConnection.getResponseCode();
			if (result != 200) {
				Logger.logError("PushServlet call failed, error code: " + result);
				return false;
			} else {
				// 200 = successful
				return true;
			}

		} catch (MalformedURLException e) {
			Logger.logError(e.toString());
			return false;
		} catch (IOException e) {
			Logger.logError(e.toString());
			return false;
		}
	}

	/**
	 * checks if the server contains oldSessionId for a user willing to login. If yes, the old SessionID is removed and replaced
	 * by the new one
	 * 
	 * @param oldSessionId String containing the old SessionID the user had been logged in
	 * @param sessionID String containing the new SessionID the user actual wants to log in
	 * @author FL
	 */
	public static void processDuplicateUserLogin(String oldSessionId, String sessionID) {
		Server myServer = Server.getInstance();
		synchronized (ActionProcessor.addToGWTUserQueueAndSendActionsLock) {
			Logger.debugLog("Enable lock addToGWTUserQueueAndSendActionsLock from processLogin");
			if (myServer.currentState.actionPackagesForGWTClients.containsKey(oldSessionId)) {
				Vector<ActionPackage> tmp = myServer.currentState.actionPackagesForGWTClients.remove(oldSessionId);
				myServer.currentState.actionPackagesForGWTClients.put(sessionID, tmp);

				Vector<ActionPackage> v = new Vector<ActionPackage>();
				ActionPackage apKickOut = ActionPackageFactory.getKickOutAction();
				Logger.doCFLogging(apKickOut);
				v.add(apKickOut);
				myServer.currentState.session2Remove.put(oldSessionId, v);

			}
			Logger.debugLog("Disable lock addToGWTUserQueueAndSendActionsLock from processLogin");
		}
	}

	/**
	 * Sends content to RMI clients
	 * 
	 * @author FL
	 */
	private static void sendActionPackagesToRMICLients() {
		// Send actions to RMI users
		// Check to avoid unnecessary calls
		Server myServer = Server.getInstance();
		synchronized (ActionProcessor.addToRMIUserQueueAndSendActionsLock) {
			Logger.debugLog("Enable lock addToRMIUserQueueAndSendActionsLock from sendActionsToConnectedClients");
			int rmiActions = 0;
			for (String s : myServer.currentState.actionPackagesForRMIClients.keySet()) {
				rmiActions += myServer.currentState.actionPackagesForRMIClients.get(s).size();
			}

			if (rmiActions > 0) {
				// Send actions to RMI users
				for (String s : myServer.currentState.actionPackagesForRMIClients.keySet()) {
					if (myServer.currentState.actionPackagesForRMIClients.get(s).size() > 0) {

						// Add meta information (number of update, create
						// actions + one generic id for actions that belong together)
						// to each package. This is required for the current feedback implementation
						// for(ActionPackage p : myServer.currentState.actionPackagesForRMIClients.get(s))
						// {
						// addMetaInformation(p, s);
						// }
						sendToRMIClient(s, myServer.currentState.actionPackagesForRMIClients.get(s));
					}
				}

				cleanRMIActionsSent();
			}
			Logger.debugLog("Disable lock addToRMIUserQueueAndSendActionsLock from sendActionsToConnectedClients");
		}
	}

	/**
	 * Sends action packages to user that are connected via RMI connection
	 * 
	 * @param user The user
	 * @param vp The action packages
	 * @author FL
	 */
	static void sendToRMIClient(String user, Vector<ActionPackage> vp) {
		try {
			ClientInterface client = (ClientInterface) Naming.lookup("rmi://localhost:"
					+ Server.getInstance().conf.parameters.get("RMI-Registry Port") + "/" + user);
			client.doActionPackagesOnClient(vp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * cleans session2Remove from server state
	 * 
	 * @author FL
	 */
	private static void cleanSession2RemoveActionsSent() {
		Server myServer = Server.getInstance();
		for (String session : myServer.currentState.session2Remove.keySet()) {
			myServer.currentState.session2Remove.get(session).clear();
		}
		myServer.currentState.session2Remove.clear();
	}

	/**
	 * cleans ActionPackages sent to GWT Clients
	 * 
	 * @author FL
	 */
	private static void cleanGWTActionsSent() {
		Server myServer = Server.getInstance();
		for (String session : myServer.currentState.actionPackagesForGWTClients.keySet()) {
			myServer.currentState.actionPackagesForGWTClients.get(session).clear();
		}
	}

	/**
	 * cleans ActionPackages sent to RMI Clients
	 * 
	 * @author FL
	 */
	static void cleanRMIActionsSent() {
		Server myServer = Server.getInstance();
		for (String session : myServer.currentState.actionPackagesForRMIClients.keySet()) {
			myServer.currentState.actionPackagesForRMIClients.get(session).clear();
		}
	}

}
