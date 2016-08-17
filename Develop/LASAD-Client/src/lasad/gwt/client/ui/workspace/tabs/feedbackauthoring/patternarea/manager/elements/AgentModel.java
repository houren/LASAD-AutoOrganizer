package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.elements;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents an agent, it stores all the patterns defined for the agent.
 * 
 * @author anahuacv
 * 
 */
public class AgentModel {
	private String id;
	private Map<String, PatternModel> patternMap = new LinkedHashMap<String, PatternModel>();
	
	public AgentModel(String id) {
		super();
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, PatternModel> getPatternMap() {
		return patternMap;
	}
	public void setPatternMap(Map<String, PatternModel> patternMap) {
		this.patternMap = patternMap;
	}
	
	public boolean addPattern(String patternId) {
		boolean retVal= false;
		if(!patternMap.containsKey(patternId)){
			patternMap.put(patternId, new PatternModel(patternId));
			retVal= true;
		}
		return retVal;
	}
	
	public PatternModel deletePattern(String patternId) {
		return patternMap.remove(patternId);
	}
	public PatternModel getPattern(String patternId) {
		return patternMap.get(patternId);
	}
	
	
}
