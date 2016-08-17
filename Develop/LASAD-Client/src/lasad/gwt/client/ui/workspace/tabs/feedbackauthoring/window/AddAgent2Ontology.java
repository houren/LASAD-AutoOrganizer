package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.Map;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ontology.AddAgentToOntologyEvent;

/**
 * Add agent to ontology dialog
 * @author Anahuac
 *
 */
public class AddAgent2Ontology extends SelectAgentWindow {

	public AddAgent2Ontology(Map<String,String> agentMap, String ontologyId, String ontologyName, FeedbackAuthoringTabContent td) {
		super(agentMap, ontologyId, ontologyName, td);
	}

	@Override
	public void doAction(String agentId, String agentName, String ontology) {
		//getFATabRef().handleAddAgentToOntologyEvent(agentName, helper);
		getFATabRef().handleUserActionEvent(new AddAgentToOntologyEvent(ontology, agentName, agentId));
	}

}
