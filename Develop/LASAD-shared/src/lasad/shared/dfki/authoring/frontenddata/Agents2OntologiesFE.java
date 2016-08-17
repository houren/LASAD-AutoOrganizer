package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * a list of agent-to-ontology assignments (to be used in the graphical
 * frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class Agents2OntologiesFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = -674028659970378682L;
	private Map<String, List<String>> ontologyID2agentIDs = new HashMap<String, List<String>>();
	
	public Agents2OntologiesFE(){
		
	}

	public void addMapping(String aID, String oID) {
		List<String> agents = ontologyID2agentIDs.get(oID);
		if (agents == null) {
			agents = new Vector<String>();
			ontologyID2agentIDs.put(oID, agents);
		}
		agents.add(aID);
	}
	
	public Map<String, List<String>> getMappings(){
		return ontologyID2agentIDs;
	}

	@Override
	public String toString() {
		return "Agents2OntologiesFE [ontologyID2agentIDs="
				+ ontologyID2agentIDs + "]";
	}

}
