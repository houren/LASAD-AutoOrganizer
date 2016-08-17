package lasad.gwt.server;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lasad.gwt.client.logger.Logger;
import lasad.shared.ServerInterface;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import de.novanic.eventservice.service.EventExecutorService;
import de.novanic.eventservice.service.EventExecutorServiceFactory;
import de.novanic.eventservice.service.UserTimeoutListener;
import de.novanic.eventservice.service.registry.user.UserInfo;

public class LASADGWTServiceBroker implements UserTimeoutListener {

	public final Map<String, EventExecutorService> clientsSession2Event = new HashMap<String, EventExecutorService>();
	public final Map<String, HttpServletRequest> clientsSessions2Sessions = new HashMap<String, HttpServletRequest>();

	private ServerInterface server = null;

	private static LASADGWTServiceBroker instance = null;

	public static LASADGWTServiceBroker getInstance() {
		if (instance == null) {
			instance = new LASADGWTServiceBroker();
		}
		return instance;
	}

	private LASADGWTServiceBroker() {
	}

	// public void workOnClientActionContainer(HttpSession session, ActionPackage p) {
	//
	// // Handle Client and EventService on ServletSide
	// EventExecutorService eventHandler = null;
	// String sessionId= session.getId();
	//
	// // Otherwise, the client is known
	// if (instance.clientsSession2Event.containsKey(session)) {
	// eventHandler = instance.clientsSession2Event.get(session);
	// }
	//
	// // If the client is unknown...
	// else {
	// eventHandler = EventExecutorServiceFactory.getInstance().getEventExecutorService(sessionId);
	// instance.clientsSession2Event.put(session, eventHandler);
	// instance.clientsSessions2Sessions.put(sessionId, session);
	// }
	//
	// p.addParameter("SESSION-ID", sessionId);
	//
	// sendToRMIServer(p);
	// }

	public void workOnClientActionContainer(HttpServletRequest request, ActionPackage p) {

		// Handle Client and EventService on ServletSide
		EventExecutorService eventHandler = null;

		if (p == null) {
			Logger.log("No actionPackage to get SessionId from, no action taken for request - " + request, Logger.DEBUG_ERRORS);
		}
		String sessionId = new String(p.getParameterValue(ParameterTypes.SessionId));// "CLIENT-ID"
		request.setAttribute("id", sessionId);

		// Otherwise, the client is known
		if (instance.clientsSession2Event.containsKey(request)) {
			eventHandler = instance.clientsSession2Event.get(request);
		}

		// If the client is unknown...
		else {
			eventHandler = EventExecutorServiceFactory.getInstance().getEventExecutorService(sessionId);
			instance.clientsSession2Event.put(sessionId, eventHandler);
			instance.clientsSessions2Sessions.put(sessionId, request);
		}

		// p.addParameter("SESSION-ID", sessionId);

		sendToRMIServer(p);
	}

	public void sendToRMIServer(ActionPackage p) {
		try {
			if (server == null) {
				// Be sure format of URL is localhost:[RMI port]/serverName.  Port and servername might need to be updated.
				// Make sure they match what you set in Deploy/lasad-server/server.cfg
				server = (ServerInterface) Naming.lookup("rmi://localhost:1899/LASAD-8099");
			}
			server.doActionOnServer(p);
		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	public void onTimeout(UserInfo user) {
		Logger.log("GWT Event Service: Client timeout detected: " + user.getUserId(), Logger.DEBUG);
	}
}