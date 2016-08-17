package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * a list of session-ontology assignments (to be used in the graphical
 * frontend).
 * 
 * @author anahuacvalero
 * 
 */
public class SessionsFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6904171355421749597L;
	private Map<String, String> id2OntMap = new HashMap<String, String>(); //id-ontology
	private Map<String, String> id2NameMap = new HashMap<String, String>(); //id-name
	
	public SessionsFE(){
		
	}
	
	public void add(String sessionId, String sessionName, String ontology) {
		if(!id2OntMap.containsKey(sessionId)){
			id2OntMap.put(sessionId, ontology);
			id2NameMap.put(sessionId, sessionName);
		}
	}
	
	public Map<String, String> getId2OntMap(){
		return id2OntMap;
	}
	
	public Map<String, String> getId2NameMap(){
		return id2NameMap;
	}

	@Override
	public String toString() {
		return "SessionsFE [id2OntMap=" + id2OntMap + ", id2NameMap="
				+ id2NameMap + "]";
	}
	
}
