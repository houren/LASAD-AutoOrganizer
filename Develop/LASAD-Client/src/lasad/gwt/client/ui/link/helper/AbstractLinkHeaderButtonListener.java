package lasad.gwt.client.ui.link.helper;

import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractLinkHeaderButtonListener implements EventListener {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	protected AbstractLinkPanel linkPanelReference;

	public AbstractLinkHeaderButtonListener(AbstractLinkPanel linkPanelReference) {
		this.linkPanelReference = linkPanelReference;
	}

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnClick.getEventCode()) {
			handleOnClick(be);
			be.stopPropagation();
		} else if (be.getTypeInt() == Events.OnMouseOver.getEventCode()) {
			handleOnMouseOver(be);
		} else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()) {
			handleOnMouseOut(be);
		}
	}

	private void handleOnClick(Event be) {
		if (((Element) be.getEventTarget().cast()).getClassName().equals("link-close-button-highlighted")) {
			if (!linkPanelReference.getMyLink().getMap().isDeleteElementsWithoutConfirmation()) {
				//Position it at close button
				//new DeleteDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference,  linkPanelReference.getPosition(true).x + linkPanelReference.getWidth(true) - linkPanelReference.getGeneralOffsetWidth(), linkPanelReference.getPosition(true).y + linkPanelReference.getGeneralOffsetHeight());
				createDeleteDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference,  linkPanelReference.getPosition(true).x + linkPanelReference.getWidth(true) - linkPanelReference.getGeneralOffsetWidth(), linkPanelReference.getPosition(true).y + linkPanelReference.getGeneralOffsetHeight());
			} else {
				// Build ActionSet with all needed Updates
				linkPanelReference.getMyLink().getMap().getFocusHandler().releaseAllFocus();
				//communicator.sendActionPackage(actionBuilder.removeElement(linkPanelReference.getMyLink().getMap().getID(), linkPanelReference.getMyLink().getConnectedModel().getId()));
				onClickSendRemoveUpdate2Sever(linkPanelReference.getMyLink().getMap().getID(), linkPanelReference.getMyLink().getConnectedModel().getId());
			}
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("add-button-highlighted")) {
			//new AddElementToLinkDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference, linkPanelReference.getPosition(true).x, linkPanelReference.getPosition(true).y);
			createAddElementToLinkDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference, linkPanelReference.getPosition(true).x, linkPanelReference.getPosition(true).y);
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("del-button-highlighted")) {
			//new DelElementFromLinkDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference, linkPanelReference.getPosition(true).x, linkPanelReference.getPosition(true).y);
			createDelElementFromLinkDialog(linkPanelReference.getMyLink().getMap(), linkPanelReference, linkPanelReference.getPosition(true).x, linkPanelReference.getPosition(true).y);
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("switch-direction-button-highlighted")) {
			String start;
			String end;
			
			//In case the direction value has not been set until now
			if (linkPanelReference.getMyLink().getConnectedModel().getValue(ParameterTypes.Direction) == null){
				start = Integer.toString(linkPanelReference.getMyLink().getConnectedModel().getParents().get(1).getId());
				end = Integer.toString(linkPanelReference.getMyLink().getConnectedModel().getParents().get(0).getId());
			} 
			else {
				start = linkPanelReference.getMyLink().getConnectedModel().getValue(ParameterTypes.Direction).split(",")[1];
				end = linkPanelReference.getMyLink().getConnectedModel().getValue(ParameterTypes.Direction).split(",")[0];
			}
			//communicator.sendActionPackage(actionBuilder.setLinkDirection(linkPanelReference.getMyLink().getMap().getID(), linkPanelReference.getMyLink().getConnectedModel().getId(), start, end));
			onClickSendLinKDirUpdate2Sever(linkPanelReference.getMyLink().getMap().getID(), linkPanelReference.getMyLink().getConnectedModel().getId(), start, end);
		}
		else if (((Element) be.getEventTarget().cast()).getClassName().equals("link-config-button-highlighted")) {
			AbstractConfigWindow w = createConfigLinkWindow(linkPanelReference.getMyLink().getMap(), linkPanelReference, linkPanelReference.getPosition(true).x, linkPanelReference.getPosition(true).y);
			w.show();
		}
	}
	
	
	protected abstract AbstractDeleteDialog createDeleteDialog(GraphMap targetMap, AbstractLinkPanel targetBox, int left, int top);
	protected abstract AbstractAddElementToLinkDialog createAddElementToLinkDialog(GraphMap map, AbstractLinkPanel linkPanelReference, int posX, int posY);
	protected abstract AbstractDelElementFromLinkDialog createDelElementFromLinkDialog(GraphMap map, AbstractLinkPanel linkPanelReference, int posX, int posY);
	protected abstract AbstractConfigWindow createConfigLinkWindow(GraphMap map, AbstractLinkPanel linkPanelReference, int posX, int posY);
	
	
	protected abstract void onClickSendRemoveUpdate2Sever(String mapId, int linkId);
	protected abstract void onClickSendLinKDirUpdate2Sever(String mapId, int linkId, String start, String end);
	

	private void handleOnMouseOver(Event be) {
		if (((Element) be.getEventTarget().cast()).getClassName().equals("link-close-button")) {
			((Element) be.getEventTarget().cast()).setClassName("link-close-button-highlighted");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("add-button")) {
			((Element) be.getEventTarget().cast()).setClassName("add-button-highlighted");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("del-button")) {
			((Element) be.getEventTarget().cast()).setClassName("del-button-highlighted");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("switch-direction-button")) {
			((Element) be.getEventTarget().cast()).setClassName("switch-direction-button-highlighted");
		}
		if (((Element) be.getEventTarget().cast()).getClassName().equals("link-config-button")) {
			((Element) be.getEventTarget().cast()).setClassName("link-config-button-highlighted");
		}
	}

	private void handleOnMouseOut(Event be) {
		if (((Element) be.getEventTarget().cast()).getClassName().equals("link-close-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("link-close-button");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("add-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("add-button");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("del-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("del-button");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("switch-direction-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("switch-direction-button");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("link-config-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("link-config-button");
		}
	}
}