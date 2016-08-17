package lasad.gwt.client.ui.link.helper;

import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractAddElementToLinkDialogListener implements EventListener {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	protected GraphMap myMap;
	private AbstractLinkPanel linkPanelReference;
	private AbstractAddElementToLinkDialog myDialog;
	
	public AbstractAddElementToLinkDialogListener(GraphMap map, AbstractLinkPanel linkPanelReference, AbstractAddElementToLinkDialog dialog) {
		this.myMap = map;
		this.linkPanelReference = linkPanelReference;
		this.myDialog = dialog;
	}
	
	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnMouseOver.getEventCode()){
			handleMouseOver(be);		
		}
		else if (be.getTypeInt() == Events.OnClick.getEventCode()) {
			handleOnClick(be);
		}
		else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()){
			handleMouseOut(be);
			
		}
		be.stopPropagation();
	}		

	private void handleOnClick(Event be) {	
		// Get childelement with ElementIDs
		Element clickedElement = (Element) be.getEventTarget().cast();		
		Element childTypeElement = clickedElement.getFirstChildElement();
		Element childIDElement = null;
		
		if(childTypeElement != null){
			childIDElement = childTypeElement.getNextSiblingElement();
		}
		
		if(childTypeElement != null && childIDElement != null){
			// Extract type
			String elementType = childTypeElement.getInnerText();
			// Extract elementID
			String elementID = childIDElement.getInnerText();
		
			// Updating server model
			//communicator.sendActionPackage(actionBuilder.addElement(myMap.getID(), linkPanelReference.getMyLink().getConnectedModel().getId(), elementType, elementID));
			onClickSendUpdateToServer(myMap.getID(), linkPanelReference.getMyLink().getConnectedModel().getId(), elementType, elementID);
		}
		// else Cancel Element
		
		myDialog.removeFromParent();
	}
	protected abstract void onClickSendUpdateToServer(String mapId, int linkId, String elementType, String elementId);
	
	private void handleMouseOut(Event be) {			
		// End hover effect
		if(((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text");
		}
	}
		
	private void handleMouseOver(Event be) {
		// Start hover effect
		if(((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text-highlighted");
		}
	}
}