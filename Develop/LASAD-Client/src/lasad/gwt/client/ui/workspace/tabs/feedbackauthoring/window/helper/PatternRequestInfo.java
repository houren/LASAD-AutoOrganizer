package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid.PatternsGrid;

/**
 * Data structure to help handling the request of a new pattern Id.
 * @author Anahuac
 *
 */
public class PatternRequestInfo extends RequestInfo {
	private String agentId;
	private String patternName;
	private String patternType;
	private PatternsGrid patternsGrid;

	public PatternRequestInfo(int id, String agentId, String patternName, String patternType, PatternsGrid patternsGrid) {
		super(id);
		this.agentId = agentId;
		this.patternName = patternName;
		this.patternType = patternType;
		this.patternsGrid = patternsGrid;
	}

	public String getAgentId() {
		return agentId;
	}
	
	public String getPatternName() {
		return patternName;
	}

	public String getPatternType() {
		return patternType;
	}

	public PatternsGrid getPatternsGrid() {
		return patternsGrid;
	}
	
	

}
