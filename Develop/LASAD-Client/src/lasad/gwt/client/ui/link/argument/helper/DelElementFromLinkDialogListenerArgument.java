package lasad.gwt.client.ui.link.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class DelElementFromLinkDialogListenerArgument extends
		AbstractDelElementFromLinkDialogListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public DelElementFromLinkDialogListenerArgument(GraphMap map,
			AbstractLinkPanel linkPanelReference,
			AbstractDelElementFromLinkDialog DelElementfromLinkDialog) {
		super(map, linkPanelReference, DelElementfromLinkDialog);
	}

	@Override
	protected void onClickSendUpdate2Sever(String mapId, int elementId) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapId, elementId));
	}

}
