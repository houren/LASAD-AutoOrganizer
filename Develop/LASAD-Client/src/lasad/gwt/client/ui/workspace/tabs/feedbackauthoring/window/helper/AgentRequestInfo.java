package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.helper;

/**
 * Data structure to help handling the request of a new agent Id.
 * @author Anahuac
 *
 */
public class AgentRequestInfo extends RequestInfo {
	private String agentName, ontology, oldAgentId;

	public AgentRequestInfo(int id, String agentName, String ontology) {
		super(id);
		this.agentName = agentName;
		this.ontology = ontology;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getOntology() {
		return ontology;
	}
	
	public String getOldAgentId() {
		return oldAgentId;
	}

	public void setOldAgentId(String oldAgentId) {
		this.oldAgentId = oldAgentId;
	}

}
