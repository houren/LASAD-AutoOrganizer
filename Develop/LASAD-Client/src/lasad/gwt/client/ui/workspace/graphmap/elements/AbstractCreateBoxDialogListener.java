package lasad.gwt.client.ui.workspace.graphmap.elements;

//import lasad.gwt.client.communication.LASADActionSender;
import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractCreateBoxDialogListener implements EventListener {

//    private final LASADActionSender communicator = LASADActionSender.getInstance();
//    private final ActionFactory actionBuilder = ActionFactory.getInstance();

    protected GraphMap myMap;
    private AbstractCreateBoxDialog myDialog;
    private TranscriptLinkData myTData = null;

    public AbstractCreateBoxDialogListener(GraphMap map, AbstractCreateBoxDialog dialog) {
	this.myMap = map;
	this.myDialog = dialog;
    }

    public AbstractCreateBoxDialogListener(GraphMap map, AbstractCreateBoxDialog dialog, TranscriptLinkData tData) {
	this.myMap = map;
	this.myDialog = dialog;
	this.myTData = tData;
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
//		for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("box").values()) {
    	for (ElementInfo info : getElementsByType("box")) {
		    if (((Element) be.getEventTarget().cast()).getInnerText().equals(info.getElementOption(ParameterTypes.Heading))) {
	
//			Action transcriptLinkAction = null;
			boolean flag = false;
			for (ElementInfo childInfo : info.getChildElements().values()) {
			    if (childInfo.getElementType().equals("transcript-link")) {
					// TranscriptLink for this element allowed
					if (myTData != null) {
						flag = true;
					    //transcriptLinkAction = actionBuilder.createTranscriptLink(myMap.getID(), "LAST-ID", myTData);
					}
			    }
			}
//			// Send Action --> Server
//			ActionPackage actionPackage = actionBuilder.createBoxWithElements(info, myMap.getID(), myDialog.getPosition(true).x, myDialog.getPosition(true).y);
//			actionPackage.addAction(transcriptLinkAction);
//			communicator.sendActionPackage(actionPackage);
			onClickSendCreateBoxUpdateToServer(myTData, flag, info, myMap.getID(), myDialog.getPosition(true).x, myDialog.getPosition(true).y);
		    }
		}
		myDialog.removeFromParent();
    }
    protected abstract void onClickSendCreateBoxUpdateToServer(TranscriptLinkData myTData, boolean createTranscriptLink, ElementInfo info, String mapID, int posX, int posY);
    protected abstract Collection<ElementInfo> getElementsByType(String type);

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