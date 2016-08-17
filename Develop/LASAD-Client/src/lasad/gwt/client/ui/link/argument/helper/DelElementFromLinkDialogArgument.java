package lasad.gwt.client.ui.link.argument.helper;

import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class DelElementFromLinkDialogArgument extends
		AbstractDelElementFromLinkDialog {

	public DelElementFromLinkDialogArgument(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		super(map, linkPanelReference, posX, posY);
	}

	@Override
	protected AbstractDelElementFromLinkDialogListener createDelElementFromLinkDialogListener(
			GraphMap map, AbstractLinkPanel linkPanelReference,
			AbstractDelElementFromLinkDialog DelElementfromLinkDialog) {
		return new DelElementFromLinkDialogListenerArgument(map, linkPanelReference, DelElementfromLinkDialog);
	}

}
