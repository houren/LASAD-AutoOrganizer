package lasad.gwt.client.ui.box.argument;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.argument.elements.BoxHeaderElementArgument;
import lasad.gwt.client.ui.box.elements.AbstractBoxHeaderElement;
import lasad.gwt.client.ui.box.helper.BoxConnectorElement;
import lasad.gwt.client.ui.workspace.argumentmap.elements.CreateLinkDialogArgument;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.ActionPackage;

/**
 * Where is the documentation?
 */

public class BoxArgument extends AbstractBox {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	private MVController myController;
	
	public BoxArgument(GraphMap map, ElementInfo info,
			SelectionDetailsPanel sdp, boolean isR) {
		super(map, info, sdp, isR);
	}

	@Override
	protected AbstractBoxHeaderElement createBoxHeaderElement(
			AbstractBox correspondingBox, String title, boolean isReplay) {
		return new BoxHeaderElementArgument(correspondingBox, title, isReplay);
	}

	@Override
	protected void sendUpdateTranscriptLinkToServer(String mapID, int elementID,
			TranscriptLinkData tData) {
		communicator.sendActionPackage(actionBuilder.updateTranscriptLink(mapID, elementID, tData));
	}

	@Override
	protected void sendCreateTranscriptLinkToServer(String mapID, String boxID,
			TranscriptLinkData tData) {
		communicator.sendActionPackage(new ActionPackage().addAction(actionBuilder.createTranscriptLink(mapID, boxID, tData)));
	}

	@Override
	protected AbstractCreateLinkDialog createLinkDialog( GraphMap graphMap, AbstractBox b1, AbstractBox b2, int x, int y) {
		return new CreateLinkDialogArgument(graphMap, b1, b2,x, y);
	}

	@Override
	public MVController getMyController() {
		return myController;
	}

	@Override
	protected BoxConnectorElement createBoxConnectorElement(
			AbstractBox abstractBox, String style) {
		return new BoxConnectorElement(this, style);
	}

	@Override
	protected void onClickSendUpdateBoxSizeToServer(String mapID, int boxID, int width, int height) {
		communicator.sendActionPackage(actionBuilder.updateBoxSize(mapID, boxID, width, height));
	}

	@Override
	public void setMyController(AbstractMVController myController) {
		this.myController = (MVController)myController;
	}

	@Override
	public void setMyControllerFromLASADClient() {
		myController = LASAD_Client.getMVCController(myMap.getID());
	}

	@Override
	protected SelectionDetailsPanel createSelectionDetailsPanel(
			GraphMap mymap) {
		return new SelectionDetailsPanel(myMap);
	}

	@Override
	protected void onClickSendUpdatePositionToServer(String mapID, int boxID,
			int x, int y) {
		communicator.sendActionPackage(actionBuilder.updateBoxPosition(mapID, boxID, x, y));
	}

}
