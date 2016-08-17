package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Map;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.session.AddAgentToSessionEvent;

/**
 * Add agent to session dialog
 * @author Anahuac
 *
 */
public class AddAgent2Session extends SelectAgentWindow {

	public AddAgent2Session(Map<String,String> agentMap, String sessionId, String sessionName, FeedbackAuthoringTabContent td) {
		super(agentMap, sessionId, sessionName, td);
	}

	@Override
	public void doAction(String agentId, String agentName, String helper) {
		//getFATabRef().handleAddAgentToSessionEvent(agentName, helper);
		getFATabRef().handleUserActionEvent(new AddAgentToSessionEvent(helper, agentName, agentId));
	}

}
