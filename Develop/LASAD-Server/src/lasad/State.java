package lasad;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import lasad.entity.Map;
import lasad.entity.User;
import lasad.shared.communication.objects.ActionPackage;

public class State {
	
	public volatile ConcurrentHashMap<String, User> sessionToUser;
	public volatile ConcurrentHashMap<String, String> userIdToSession;
	public volatile ConcurrentHashMap<Integer, Vector<User>> mapUsers;
	public volatile ConcurrentHashMap<User, Vector<Integer>> userMaps;
	public volatile ConcurrentHashMap<Integer, Map> maps;	
	public volatile ConcurrentHashMap<String, Vector<ActionPackage>> actionPackagesForGWTClients;
	public volatile ConcurrentHashMap<String, Vector<ActionPackage>> actionPackagesForRMIClients;
	public volatile ConcurrentHashMap<String, Vector<ActionPackage>> session2Remove;
	
	public volatile int lastTopLevelElementID, secondLastTopLevelElementID; // Used to store the last top level element, i.e. box, relation, feedback-cluster to be able to replace LAST-ID as parent indicator of the child elements with the correct one (as done in Element.java)
	
	private static State myState = null;
	
	public static State getInstance() {
		if(myState == null) {
			myState = new State();
		}
		return myState;
	}
	
	private State() {
		sessionToUser = new ConcurrentHashMap<String, User>();
		userIdToSession = new ConcurrentHashMap<String, String>();
		mapUsers = new ConcurrentHashMap<Integer, Vector<User>>();
		userMaps = new ConcurrentHashMap<User, Vector<Integer>>();
		maps = new ConcurrentHashMap<Integer, Map>();
				
		actionPackagesForGWTClients = new ConcurrentHashMap<String, Vector<ActionPackage>>();
		actionPackagesForRMIClients = new ConcurrentHashMap<String, Vector<ActionPackage>>();
		session2Remove = new ConcurrentHashMap<String, Vector<ActionPackage>>();
	}
	
}
