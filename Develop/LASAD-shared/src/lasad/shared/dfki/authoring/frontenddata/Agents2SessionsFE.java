package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * a list of agent-to-session assignments (to be used in the graphical
 * frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class Agents2SessionsFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8030258646671371385L;
	private Map<String, List<String>> sessionID2agentIDs = new HashMap<String, List<String>>();
	
	public Agents2SessionsFE(){
		
	}

	public void addMapping(String aID, String sID) {
		List<String> agents = sessionID2agentIDs.get(sID);
		if (agents == null) {
			agents = new Vector<String>();
			sessionID2agentIDs.put(sID, agents);
		}
		agents.add(aID);
	}

	public Map<String, List<String>> getMappings(){
		return sessionID2agentIDs;
	}

	@Override
	public String toString() {
		return "Agents2SessionsFE [sessionID2agentIDs=" + sessionID2agentIDs
				+ "]";
	}

}
