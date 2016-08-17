package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractCreateLinkDialogListener implements EventListener {

//    private final LASADActionSender communicator = LASADActionSender.getInstance();
//    private final ActionFactory actionBuilder = ActionFactory.getInstance();

    protected GraphMap myMap;
    private AbstractCreateLinkDialog myDialogue;
    private AbstractBox b1, b2;
    private AbstractLink l2;

    public AbstractCreateLinkDialogListener(GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractBox b2) {
	this.myMap = map;
	this.myDialogue = dialogue;
	this.b1 = b1;
	this.b2 = b2;
    }

    public AbstractCreateLinkDialogListener(GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractLink l2) {
	this.myMap = map;
	this.myDialogue = dialogue;
	this.b1 = b1;
	this.l2 = l2;
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
//		for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("relation").values()) {
		for (ElementInfo info : getElementsByType("relation")) {
		    if (((Element) be.getEventTarget().cast()).getInnerText().equals(info.getElementOption(ParameterTypes.Heading))) {
				// Send Action --> Server
				if (b1 != null && b2 != null) {
				    //communicator.sendActionPackage(actionBuilder.createLinkWithElements(info, myMap.getID(), b1.getConnectedModel().getId() + "", b2.getConnectedModel().getId() + ""));
					onClickSendUpdateToServer(info, myMap.getID(), b1.getConnectedModel().getId() + "", b2.getConnectedModel().getId() + "");
				} else if (b1 != null && l2 != null) {
				    //communicator.sendActionPackage(actionBuilder.createLinkWithElements(info, myMap.getID(), b1.getConnectedModel().getId() + "", l2.getConnectedModel().getId() + ""));
					onClickSendUpdateToServer(info, myMap.getID(), b1.getConnectedModel().getId() + "", l2.getConnectedModel().getId() + "");
				}
		    }
		}
		myDialogue.removeFromParent();
    }
    protected abstract Collection<ElementInfo> getElementsByType(String type);
    protected abstract void onClickSendUpdateToServer(ElementInfo info, String mapId, String firstElemId, String secondElemId);

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