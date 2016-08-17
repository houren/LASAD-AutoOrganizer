package lasad.gwt.client.ui.link.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToLinkDialogListenerArgument extends
		AbstractAddElementToLinkDialogListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public AddElementToLinkDialogListenerArgument(GraphMap map,
			AbstractLinkPanel linkPanelReference,
			AbstractAddElementToLinkDialog dialog) {
		super(map, linkPanelReference, dialog);
	}

	@Override
	protected void onClickSendUpdateToServer(String mapId, int linkId,
			String elementType, String elementId) {
		communicator.sendActionPackage(actionBuilder.addElement(mapId, linkId, elementType, elementId));
	}

}
