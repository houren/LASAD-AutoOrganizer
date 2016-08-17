package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ontology;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.ActionUserEventProperty;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event.UserActionEventImpl;

public abstract class OntologyUserActionEventImpl extends UserActionEventImpl {

	public OntologyUserActionEventImpl(String ontology, String agentName, String agentId) {
		addProperty(ActionUserEventProperty.ontology, ontology);
		addProperty(ActionUserEventProperty.agentName, agentName);
		addProperty(ActionUserEventProperty.agentId, agentId);
	}

}

