package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent;

import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
/**
 * Event to handle Add or Update of an Agent
 * @author Anahuac
 *
 */
public class AddUpdateAgentEvent extends AgentUserActionEventImpl {
	private AgentDescriptionFE agentDesc;

	public AddUpdateAgentEvent(String agentId, AgentDescriptionFE agentDesc) {
		super(agentId);
		this.agentDesc = agentDesc;
	}

	public AgentDescriptionFE getAgentDesc() {
		return agentDesc;
	}

}
