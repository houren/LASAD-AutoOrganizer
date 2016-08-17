package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialogListener;

/**
 * Creates the delete confirmation dialog of a box or link
 * @author Anahuac
 *
 */
public class DeleteDialogPattern extends AbstractDeleteDialog {

	public DeleteDialogPattern(GraphMap targetMap, AbstractBox targetBox,
			int left, int top) {
		super(targetMap, targetBox, left, top);
	}

	public DeleteDialogPattern(GraphMap targetMap,
			AbstractLinkPanel targetLink, int left, int top) {
		super(targetMap, targetLink, left, top);
	}

	@Override
	protected AbstractDeleteDialogListener createDeleteDialogListener(
			GraphMap myMap, AbstractDeleteDialog dialog,
			AbstractBox boxTarget) {
		return new DeleteDialogListenerPattern(myMap, dialog, boxTarget);
	}

	@Override
	protected AbstractDeleteDialogListener createDeleteDialogListener(
			GraphMap myMap, AbstractDeleteDialog dialog,
			AbstractLinkPanel linkTarget) {
		return new DeleteDialogListenerPattern(myMap, dialog, linkTarget);
	}

}
