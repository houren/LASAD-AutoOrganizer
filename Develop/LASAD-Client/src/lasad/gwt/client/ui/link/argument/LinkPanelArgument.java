package lasad.gwt.client.ui.link.argument;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.argument.helper.LinkHeaderButtonListenerArgument;
import lasad.gwt.client.ui.link.helper.AbstractLinkHeaderButtonListener;
import lasad.gwt.client.ui.workspace.argumentmap.elements.CreateLinkDialogArgument;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;

public class LinkPanelArgument extends AbstractLinkPanel {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	MVController myController;

	public LinkPanelArgument(AbstractLink myLink, String details, boolean isR) {
		super(myLink, details, isR);
	}

	@Override
	protected AbstractLinkHeaderButtonListener createLinkHeaderButtonListener(
			AbstractLinkPanel linkPanel) {
		return new LinkHeaderButtonListenerArgument(linkPanel);
	}

	@Override
	protected AbstractCreateLinkDialog createCreateLinkDialog(GraphMap map,
			AbstractBox start, AbstractLink end, int posX, int posY) {
		return new CreateLinkDialogArgument(map, start, end, posX, posY);
	}

	@Override
	protected void sendUpdateLinkSize(String mapID, int linkID, int height) {
		communicator.sendActionPackage(actionBuilder.updateLinkSize(mapID, linkID, height));
	}
	@Override
	protected void sendUpdateLinkPosition(String mapID, int linkID, float per) {
		communicator.sendActionPackage(actionBuilder.updateLinkPosition(mapID, linkID, per));
	}

	@Override
	protected AbstractMVController getController() {
		return myController;
	}

	@Override
	protected void setController(GraphMap myMap) {
		myController = LASAD_Client.getMVCController(myMap.getID());
	}

}
