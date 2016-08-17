package lasad.gwt.client.ui.box.pattern.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class AddElementToBoxDialogPattern extends AbstractAddElementToBoxDialog {

	public AddElementToBoxDialogPattern(GraphMap map, AbstractBox box,
			int posX, int posY) {
		super(map, box, posX, posY);
	}

	@Override
	protected AbstractAddElementToBoxDialogListener createtAddElementToBoxDialogListener(
			GraphMap map, AbstractBox box,
			AbstractAddElementToBoxDialog dialog) {
		return new AddElementToBoxDialogListenerPattern(map, box, this);
	}

}
