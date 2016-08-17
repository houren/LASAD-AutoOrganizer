package lasad.gwt.client.ui.workspace.graphmap.elements;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractDeleteDialogListener implements EventListener {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	protected GraphMap myMap;
	private AbstractDeleteDialog myDialogue;
	private AbstractBox boxTarget = null;
	private AbstractLinkPanel linkTarget = null;
	
	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	public AbstractDeleteDialogListener(GraphMap myMap, AbstractDeleteDialog dialog, AbstractBox boxTarget) {
		this.myMap = myMap;
		this.myDialogue = dialog;
		this.boxTarget = boxTarget;
	}

	public AbstractDeleteDialogListener(GraphMap myMap, AbstractDeleteDialog dialog, AbstractLinkPanel linkTarget) {
		this.myMap = myMap;
		this.myDialogue = dialog;
		this.linkTarget = linkTarget;
	}

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnMouseOver.getEventCode()) {
			handleMouseOver(be);
		} else if (be.getTypeInt() == Events.OnClick.getEventCode()) {
			handleOnClick(be);
		} else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()) {
			handleMouseOut(be);
		}
		be.stopPropagation();
	}

	private void handleOnClick(Event be) {
		if (((Element) be.getEventTarget().cast()).getInnerText().equals(myConstants.DeleteDialogueYes())) {
			if (boxTarget != null) {
				myMap.getFocusHandler().releaseAllFocus();
				//communicator.sendActionPackage(actionBuilder.removeElement(myMap.getID(), boxTarget.getConnectedModel().getId()));
				onClickSendUpdateToServer(myMap.getID(), boxTarget.getConnectedModel().getId());
			} else if (linkTarget != null) {
				myMap.getFocusHandler().releaseAllFocus();
				//communicator.sendActionPackage(actionBuilder.removeElement(myMap.getID(), linkTarget.getMyLink().getConnectedModel().getId()));
				onClickSendUpdateToServer(myMap.getID(), linkTarget.getMyLink().getConnectedModel().getId());
			}
			myDialogue.removeFromParent();
		}

		if (this.myDialogue.getDontAskAgainElement().getPropertyString("checked").equals("true")) {
			this.myMap.setDeleteElementsWithoutConfirmation(true);
		}

		if (((Element) be.getEventTarget().cast()).getInnerText().equals(myConstants.DeleteDialogueNo())) {
			myMap.getFocusHandler().releaseAllFocus();
			myDialogue.removeFromParent();
		}
	}
	protected abstract void onClickSendUpdateToServer(String mapId, int elementId);

	private void handleMouseOut(Event be) {
		// End hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text");
		}
	}

	private void handleMouseOver(Event be) {
		// Start hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text-highlighted");
		}
	}
}