package lasad.gwt.client.ui.box.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractBoxHeaderButtonListener;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialog;
import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.workspace.argumentmap.elements.DeleteDialogArgument;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;

public class BoxHeaderButtonListenerArgument extends
		AbstractBoxHeaderButtonListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public BoxHeaderButtonListenerArgument(AbstractBox boxReference) {
		super(boxReference);
	}

	@Override
	protected AbstractDeleteDialog createDeleteDialog(GraphMap targetMap,
			AbstractBox targetBox, int left, int top) {
		return new DeleteDialogArgument(targetMap, targetBox, left, top);
	}

	@Override
	protected AbstractAddElementToBoxDialog createAddElementToBoxDialog(
			GraphMap map, AbstractBox box, int posX, int posY) {
		return new AddElementToBoxDialogArgument(map, box, posX, posY);
	}

	@Override
	protected AbstractDelElementFromBoxDialog createDelElementFromBoxDialog(
			GraphMap map, AbstractBox box, int posX, int posY) {
		return new DelElementFromBoxDialogArgument(map, box, posX, posY);
	}

	@Override
	protected void onClickServerUpdate(String mapID, int boxId) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, boxId));
	}

	@Override
	protected AbstractConfigWindow createConfigBoxWindow(GraphMap map,
			AbstractBox box, int posX, int posY) {
		// This is not required on this side
		return null;
	}

}
