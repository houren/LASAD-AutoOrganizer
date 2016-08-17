package lasad.gwt.client.ui.link.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractLinkHeaderButtonListener;
import lasad.gwt.client.ui.workspace.argumentmap.elements.DeleteDialogArgument;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;

public class LinkHeaderButtonListenerArgument extends
		AbstractLinkHeaderButtonListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public LinkHeaderButtonListenerArgument(AbstractLinkPanel linkPanelReference) {
		super(linkPanelReference);
	}

	@Override
	protected AbstractDeleteDialog createDeleteDialog(GraphMap targetMap,
			AbstractLinkPanel targetBox, int left, int top) {
		return new DeleteDialogArgument(targetMap, targetBox, left, top);
	}

	@Override
	protected AbstractAddElementToLinkDialog createAddElementToLinkDialog(
			GraphMap map, AbstractLinkPanel linkPanelReference, int posX,
			int posY) {
		return new AddElementToLinkDialogArgument(map, linkPanelReference, posX, posY);
	}

	@Override
	protected AbstractDelElementFromLinkDialog createDelElementFromLinkDialog(
			GraphMap map, AbstractLinkPanel linkPanelReference, int posX,
			int posY) {
		return new DelElementFromLinkDialogArgument(map, linkPanelReference, posX, posY);
	}

	@Override
	protected void onClickSendRemoveUpdate2Sever(String mapId, int linkId) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapId, linkId));
	}

	@Override
	protected void onClickSendLinKDirUpdate2Sever(String mapId, int linkId,
			String start, String end) {
		communicator.sendActionPackage(actionBuilder.setLinkDirection(mapId, linkId, start, end));
	}

	@Override
	protected AbstractConfigWindow createConfigLinkWindow(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		// This is not required on this side
		return null;
	}

}
