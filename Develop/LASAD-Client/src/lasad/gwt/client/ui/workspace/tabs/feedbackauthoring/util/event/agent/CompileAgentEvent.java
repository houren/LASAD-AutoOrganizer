package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent;

import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;

/**
 * Event to handle Compilation of an Agent
 * @author Anahuac
 *
 */
public class CompileAgentEvent extends AgentUserActionEventImpl {
	private AgentDescriptionFE agentDesc;

	public CompileAgentEvent(String agentId, AgentDescriptionFE agentDesc) {
		super(agentId);
		this.agentDesc = agentDesc;
	}
	
	public AgentDescriptionFE getAgentDesc() {
		return agentDesc;
	}
}
