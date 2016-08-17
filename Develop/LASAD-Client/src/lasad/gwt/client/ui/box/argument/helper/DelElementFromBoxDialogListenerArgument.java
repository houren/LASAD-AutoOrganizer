package lasad.gwt.client.ui.box.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class DelElementFromBoxDialogListenerArgument extends
		AbstractDelElementFromBoxDialogListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public DelElementFromBoxDialogListenerArgument(GraphMap map,
			AbstractBox box,
			AbstractDelElementFromBoxDialog DelElementfromBoxDialog) {
		super(map, box, DelElementfromBoxDialog);
	}

	@Override
	protected void onClickSendUpdate2Sever(String mapId, int elementId) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapId, elementId));
	}

}
