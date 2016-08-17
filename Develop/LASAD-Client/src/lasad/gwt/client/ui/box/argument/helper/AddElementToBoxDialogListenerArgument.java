package lasad.gwt.client.ui.box.argument.helper;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToBoxDialogListenerArgument extends
		AbstractAddElementToBoxDialogListener {
	
	public AddElementToBoxDialogListenerArgument(GraphMap map,
			AbstractBox box, AbstractAddElementToBoxDialog dialog) {
		super(map, box, dialog);
	}

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	@Override
	protected void onclickSendUpdate2Sever(String mapID, int boxID,
			String elementType, String elementID) {
		communicator.sendActionPackage(actionBuilder.addElement(mapID, boxID, elementType, elementID));
	}

}
