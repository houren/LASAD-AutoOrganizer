package lasad.gwt.client.ui.workspace.argumentmap.elements;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialogListener;

public class DeleteDialogArgument extends AbstractDeleteDialog {

	public DeleteDialogArgument(GraphMap targetMap, AbstractBox targetBox,
			int left, int top) {
		super(targetMap, targetBox, left, top);
	}

	public DeleteDialogArgument(GraphMap targetMap,
			AbstractLinkPanel targetLink, int left, int top) {
		super(targetMap, targetLink, left, top);
	}

	@Override
	protected AbstractDeleteDialogListener createDeleteDialogListener(
			GraphMap myMap, AbstractDeleteDialog dialog,
			AbstractBox boxTarget) {
		return new DeleteDialogListenerArgument(myMap, dialog, boxTarget);
	}

	@Override
	protected AbstractDeleteDialogListener createDeleteDialogListener(
			GraphMap myMap, AbstractDeleteDialog dialog,
			AbstractLinkPanel linkTarget) {
		return new DeleteDialogListenerArgument(myMap, dialog, linkTarget);
	}

}
