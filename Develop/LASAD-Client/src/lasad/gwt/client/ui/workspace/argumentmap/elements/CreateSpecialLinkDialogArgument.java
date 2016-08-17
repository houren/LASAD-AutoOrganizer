package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.TreeMap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateSpecialLinkDialog;

public class CreateSpecialLinkDialogArgument extends AbstractCreateSpecialLinkDialog {
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	private MVController myController;

	public CreateSpecialLinkDialogArgument(ElementInfo config, String mapId,
			TreeMap<String, AbstractBox> boxes, TreeMap<String, AbstractLink> links) {
		super(config, mapId, boxes, links);
	}

	@Override
	protected void onClickSendCreateLinkWithElementsToServer(ElementInfo info,
			String mapID, String startElementID, String endElementID) {
		communicator.sendActionPackage(actionBuilder.createLinkWithElements(info, mapID, startElementID, endElementID));
	}

	@Override
	protected MVController getMyController() {
		return myController;
	}

	@Override
	protected void setMyController() {
		myController = LASAD_Client.getMVCController(correspondingMapId);
	}

}
