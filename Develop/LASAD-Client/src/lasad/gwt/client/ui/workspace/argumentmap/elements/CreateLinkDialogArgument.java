package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialogListener;

public class CreateLinkDialogArgument extends AbstractCreateLinkDialog {

	public CreateLinkDialogArgument(GraphMap map, AbstractBox start,
			AbstractBox end, int posX, int posY) {
		super(map, start, end, posX, posY);
	}

	public CreateLinkDialogArgument(GraphMap map, AbstractBox start,
			AbstractBox end) {
		super(map, start, end);
	}

	public CreateLinkDialogArgument(GraphMap map, AbstractBox start,
			AbstractLink end, int posX, int posY) {
		super(map, start, end, posX, posY);
	}

	@Override
	protected AbstractCreateLinkDialogListener createCreateLinkDialogListener(
			GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1,
			AbstractBox b2) {
		return new CreateLinkDialogListenerArgument(map, dialogue, b1, b2);
	}

	@Override
	protected AbstractCreateLinkDialogListener createCreateLinkDialogListener(
			GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1,
			AbstractLink l2) {
		return new CreateLinkDialogListenerArgument(map, dialogue, b1, l2);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}


}
