package lasad.gwt.client.ui.link.pattern.helper;

import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToLinkDialogPattern extends
		AbstractAddElementToLinkDialog {

	public AddElementToLinkDialogPattern(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		super(map, linkPanelReference, posX, posY);
	}

	@Override
	protected AbstractAddElementToLinkDialogListener createAddElementToLinkDialogListener(
			GraphMap map, AbstractLinkPanel linkPanelReference,
			AbstractAddElementToLinkDialog dialog) {
		return new AddElementToLinkDialogListenerPattern(map, linkPanelReference, dialog);
	}

}
