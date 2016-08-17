package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialogListener;

public class CreateBoxLinkDialogListenerArgument extends AbstractCreateBoxLinkDialogListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public CreateBoxLinkDialogListenerArgument(GraphMap map, AbstractCreateBoxLinkDialog dialog) {
		super(map, dialog);
	}

	@Override
	protected AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(
			GraphMap map, AbstractBox start, int posX, int posY, int step,
			ElementInfo boxConfig) {
		return new CreateBoxLinkDialogArgument(map, start, posX, posY, step, boxConfig);
	}

	@Override
	protected void onClickSendUpdateToServer(ElementInfo boxInfo,
			ElementInfo linkInfo, String mapID, int x, int y, String start,
			String end) {
		communicator.sendActionPackage(actionBuilder.createBoxAndLink(boxInfo, linkInfo, mapID, x, y, start, end));
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}
}