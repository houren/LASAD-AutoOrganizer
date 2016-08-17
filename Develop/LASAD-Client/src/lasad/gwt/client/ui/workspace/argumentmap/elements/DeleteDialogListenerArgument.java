package lasad.gwt.client.ui.workspace.argumentmap.elements;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialogListener;

public class DeleteDialogListenerArgument extends AbstractDeleteDialogListener {
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public DeleteDialogListenerArgument(GraphMap myMap,
			AbstractDeleteDialog dialog, AbstractBox boxTarget) {
		super(myMap, dialog, boxTarget);
	}

	public DeleteDialogListenerArgument(GraphMap myMap,
			AbstractDeleteDialog dialog, AbstractLinkPanel linkTarget) {
		super(myMap, dialog, linkTarget);
	}

	@Override
	protected void onClickSendUpdateToServer(String mapId, int elementId) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapId, elementId));
	}

}
