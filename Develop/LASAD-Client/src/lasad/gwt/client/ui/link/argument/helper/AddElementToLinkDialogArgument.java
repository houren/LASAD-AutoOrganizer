package lasad.gwt.client.ui.link.argument.helper;

import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToLinkDialogArgument extends
		AbstractAddElementToLinkDialog {

	public AddElementToLinkDialogArgument(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		super(map, linkPanelReference, posX, posY);
	}

	@Override
	protected AbstractAddElementToLinkDialogListener createAddElementToLinkDialogListener(
			GraphMap map, AbstractLinkPanel linkPanelReference,
			AbstractAddElementToLinkDialog dialog) {
		return new AddElementToLinkDialogListenerArgument(map, linkPanelReference, dialog);
	}
}
