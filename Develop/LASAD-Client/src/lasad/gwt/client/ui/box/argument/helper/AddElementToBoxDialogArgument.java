package lasad.gwt.client.ui.box.argument.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToBoxDialogArgument extends
		AbstractAddElementToBoxDialog{

	public AddElementToBoxDialogArgument(GraphMap map, AbstractBox box,
			int posX, int posY) {
		super(map, box, posX, posY);
	}

	@Override
	protected AbstractAddElementToBoxDialogListener createtAddElementToBoxDialogListener(
			GraphMap map, AbstractBox box, AbstractAddElementToBoxDialog dialog) {
		return new AddElementToBoxDialogListenerArgument(map, box, this);
	}



}
