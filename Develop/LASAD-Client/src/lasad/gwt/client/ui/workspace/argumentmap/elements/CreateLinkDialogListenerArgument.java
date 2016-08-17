package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialogListener;

public class CreateLinkDialogListenerArgument extends
		AbstractCreateLinkDialogListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public CreateLinkDialogListenerArgument(GraphMap map,
			AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractBox b2) {
		super(map, dialogue, b1, b2);
	}
	public CreateLinkDialogListenerArgument(GraphMap map,
			AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractLink l2) {
		super(map, dialogue, b1, l2);
	}
	
	@Override
	protected void onClickSendUpdateToServer(ElementInfo info, String mapId,
			String firstElemId, String secondElemId) {
		communicator.sendActionPackage(actionBuilder.createLinkWithElements(info, mapId, firstElemId, secondElemId));
	}
	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}

}
