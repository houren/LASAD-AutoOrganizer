package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.sessions;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ActionUserEventProperty;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.UserActionEventImpl;

public class SessionsUserActionEventImpl extends UserActionEventImpl {
	
	public SessionsUserActionEventImpl(String session, String agentName, String agentId) {
		addProperty(ActionUserEventProperty.session, session);
		addProperty(ActionUserEventProperty.agentName, agentName);
		addProperty(ActionUserEventProperty.agentId, agentId);
	}
}
