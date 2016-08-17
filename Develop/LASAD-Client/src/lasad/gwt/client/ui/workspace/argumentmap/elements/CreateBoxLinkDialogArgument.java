package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialogListener;

public class CreateBoxLinkDialogArgument extends AbstractCreateBoxLinkDialog{

	public CreateBoxLinkDialogArgument(GraphMap map, AbstractBox start,
			int posX, int posY, int step) {
		super(map, start, posX, posY, step);
	}
	
	public CreateBoxLinkDialogArgument(GraphMap map, AbstractBox start,
			int posX, int posY, int step, ElementInfo boxConfig) {
		super(map, start, posX, posY, step, boxConfig);
	}

	@Override
	protected AbstractCreateBoxLinkDialogListener createAbstractCreateBoxLinkDialogListener(
			GraphMap map, AbstractCreateBoxLinkDialog dialog) {
		return new CreateBoxLinkDialogListenerArgument(map, dialog);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}

}
