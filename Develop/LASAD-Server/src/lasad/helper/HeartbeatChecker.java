package lasad.helper;

import lasad.Server;
import lasad.controller.ClientCommunicationController;
import lasad.controller.ManagementController;
import lasad.entity.User;
import lasad.logging.Logger;
import lasad.processors.ActionProcessor;
import lasad.shared.communication.objects.ActionPackage;

public class HeartbeatChecker extends Thread {

	private Server myServer;
	private ActionProcessor ap;

	private boolean requestNeeded = false;

	public HeartbeatChecker() {
		this.myServer = Server.getInstance();
		this.ap = ActionProcessor.getInstance();
	}

	public void run() {

		try {
			while (true) {
				requestNeeded = false;
				sleep(Long.parseLong(myServer.conf.parameters.get("Heartbeat-Checker-Timer")));

				long currentTime = System.currentTimeMillis();

				synchronized (ActionProcessor.sessionToUserLock) {
					Logger.debugLog("Enable lock sessionToUserLock from HeartbeatChecker.run()");

					for (String sessionID : myServer.currentState.sessionToUser.keySet()) {
						User u = myServer.currentState.sessionToUser.get(sessionID);

						Logger.log("Checking heartbeat of client: " + sessionID + " [" + (currentTime - u.getLastHeartbeat()) + "] Max: "
								+ Integer.parseInt(myServer.conf.parameters.get("Timeout-Limit")));

						if (currentTime - u.getLastHeartbeat() > 2 * Integer.parseInt(myServer.conf.parameters.get("Timeout-Limit"))) {
							// Heartbeat request was not answered -> logout user
							Logger.log("Logging out user: " + sessionID);
							ap.processActionPackage(ActionPackageFactory.getLogOutActionPackage(u.getSessionID(), u.getNickname()));
							continue;
						} else if (currentTime - u.getLastHeartbeat() > Integer.parseInt(myServer.conf.parameters.get("Timeout-Limit"))) {
							Logger.log("Sending heartbeat request for user: " + sessionID);
							ActionPackage ap1 = ActionPackageFactory.heartbeatRequest();
							Logger.doCFLogging(ap1);
							ManagementController.addToUsersActionQueue(ap1, u.getSessionID());
							requestNeeded = true;
						}
					}
					Logger.debugLog("Disable lock sessionToUserLock from HeartbeatChecker.run()");
				}
				if (requestNeeded) {
					ClientCommunicationController.sendActionsToConnectedClients();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
