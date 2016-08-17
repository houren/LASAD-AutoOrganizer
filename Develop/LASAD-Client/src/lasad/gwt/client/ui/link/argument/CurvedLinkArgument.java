package lasad.gwt.client.ui.link.argument;

import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.link.AbstractCurvedLink;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class CurvedLinkArgument extends AbstractCurvedLink {

	public CurvedLinkArgument(GraphMap myMap, ElementInfo config,
			Connector cn1, Connector cn2, boolean isReplay) {
		super(myMap, config, cn1, cn2, isReplay);
	}
	
	@Override
	protected AbstractLinkPanel createAbstractLinkPanel(AbstractLink myLink,
			String details, boolean isR) {
		return new LinkPanelArgument(this, details, isR);
	}

}
