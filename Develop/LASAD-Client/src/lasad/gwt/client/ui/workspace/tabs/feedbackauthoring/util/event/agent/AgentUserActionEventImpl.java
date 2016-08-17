package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.agent;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ActionUserEventProperty;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.UserActionEventImpl;

public class AgentUserActionEventImpl extends UserActionEventImpl {
	
	public AgentUserActionEventImpl(String agentId) {
		addProperty(ActionUserEventProperty.agentId, agentId);
	}

}
