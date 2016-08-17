package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.session;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ActionUserEventProperty;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.UserActionEventImpl;

public class SessionUserActionEventImpl extends UserActionEventImpl {
	
	public SessionUserActionEventImpl(String sessionId, String agentName, String agentId) {
		addProperty(ActionUserEventProperty.session, sessionId);
		addProperty(ActionUserEventProperty.agentName, agentName);
		addProperty(ActionUserEventProperty.agentId, agentId);
	}

}
