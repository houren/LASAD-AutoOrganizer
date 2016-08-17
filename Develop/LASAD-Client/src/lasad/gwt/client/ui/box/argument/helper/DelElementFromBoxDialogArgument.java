package lasad.gwt.client.ui.box.argument.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class DelElementFromBoxDialogArgument extends
		AbstractDelElementFromBoxDialog {

	public DelElementFromBoxDialogArgument(GraphMap map, AbstractBox box,
			int posX, int posY) {
		super(map, box, posX, posY);
	}

	@Override
	protected AbstractDelElementFromBoxDialogListener createDelElementFromBoxDialogListener(
			GraphMap map, AbstractBox box,
			AbstractDelElementFromBoxDialog DelElementfromBoxDialog) {
		return new DelElementFromBoxDialogListenerArgument(map, box, DelElementfromBoxDialog);
	}


}
