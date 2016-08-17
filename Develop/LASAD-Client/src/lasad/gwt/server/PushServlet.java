package lasad.gwt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lasad.gwt.client.communication.eventservice.events.ActionPackageEvent;
import lasad.gwt.client.logger.Logger;
import lasad.shared.communication.objects.ActionPackage;
import de.novanic.eventservice.service.EventExecutorService;

/**
 * The PushServlet is called from the Java (RMI) server. Once, an action is send to the server, the server will do the processing of the actions and then, after being finished, call the Push Servlet to inform connected clients about the new actions to stay synchronized.
 * 
 * @author Frank Loll
 *
 */
public class PushServlet extends HttpServlet { 
	
	private static final long serialVersionUID = -3182626693668230032L;
	
	private LASADGWTServiceBroker myBroker = LASADGWTServiceBroker.getInstance();
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.log("PushServlet.doPost", Logger.DEBUG);
		ObjectInputStream objIn = new ObjectInputStream(request.getInputStream());
		
		ConcurrentHashMap<String, Vector<ActionPackage>> packs = null;
		try {
			Object o = objIn.readObject();
			if(o instanceof ConcurrentHashMap<?, ?>) {
				packs = (ConcurrentHashMap<String, Vector<ActionPackage>>) o;
			}
			else {
				Logger.log("Object is not of expected type (ConcurrentHashMap<?, ?>)", Logger.DEBUG_ERRORS);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Send Packages to the specified client
		
		Logger.log("PushServlet.doPost.for", Logger.DEBUG);
		for(String sessionID : packs.keySet()) {
			//HttpSession session = myBroker.clientsSessions2Sessions.get(sessionID);
			HttpServletRequest session = myBroker.clientsSessions2Sessions.get(sessionID);
			Logger.log("PushServlet.doPost.for sessionID:" + sessionID, Logger.DEBUG);
			for(ActionPackage p : packs.get(sessionID)) {
				Logger.log("Received the following for user: "+sessionID, Logger.DEBUG);
				Logger.log(p.toString(), Logger.DEBUG);
				try {
					if(session != null) {
						EventExecutorService ees = myBroker.clientsSession2Event.get(sessionID);//session
						Logger.log("PushServlet.doPost.for2 ees= " + ees, Logger.DEBUG);
						if(ees != null) {
							if(ees.isUserRegistered()) {
								Logger.log("PushServlet.doPost.for2.isUserRegistered " + ees, Logger.DEBUG);
								ees.addEventUserSpecific(new ActionPackageEvent(p));
							}
							else {
								// TODO: Add queue if user has been disconnected and is currently in the reconnect phase
								Logger.log("PushServlet.doPost.for2 TODO " + ees, Logger.DEBUG);
							}
						}
						else {
							Logger.log("PushServlet.doPost.for2 " + "EventExecutorService for session "+session+" is null", Logger.DEBUG_ERRORS);
						}
					}
					else {
						Logger.log("PushServlet.doPost.for2 " + "ESession is null (--> Client disconnected)", Logger.DEBUG_ERRORS);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
