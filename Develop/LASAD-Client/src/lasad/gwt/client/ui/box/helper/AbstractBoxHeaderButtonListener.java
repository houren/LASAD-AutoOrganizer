package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractBoxHeaderButtonListener implements EventListener {
	
	protected AbstractBox boxReference;
	
//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	public AbstractBoxHeaderButtonListener(AbstractBox boxReference) {
		this.boxReference = boxReference;
	}
	
	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnClick.getEventCode()){
			handleOnClick(be);
				
		}
		else if (be.getTypeInt() == Events.OnMouseOver.getEventCode()){
			handleOnMouseOver(be);
			
			
		}
		else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()){
			handleOnMouseOut(be);
			
		}
		be.stopPropagation();
	}		
	
	private void handleOnClick(Event be) {
		
		if (!boxReference.isModificationAllowed()) {
			// Say smth to the user and exit
			LASADInfo.display("Error", "Only author can modify boxes.");
			return;
		}
		
		if(((Element) be.getEventTarget().cast()).getClassName().equals("close-button-highlighted")) {
			if (!boxReference.getMap().isDeleteElementsWithoutConfirmation()) {
				//position it at the close-button
				//new DeleteDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x + boxReference.getWidth(true) - boxReference.getBORDER_WIDTH() - boxReference.getCONNECTOR_WIDTH(), boxReference.getPosition(true).y + boxReference.getCONNECTOR_HEIGHT() + boxReference.getBORDER_HEIGHT());
				createDeleteDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x + boxReference.getWidth(true) - boxReference.getBORDER_WIDTH() - boxReference.getCONNECTOR_WIDTH(), boxReference.getPosition(true).y + boxReference.getCONNECTOR_HEIGHT() + boxReference.getBORDER_HEIGHT());
			}
			else {		
				// Build ActionSet with all needed Updates
				boxReference.getMap().getFocusHandler().releaseAllFocus();
				onClickServerUpdate(boxReference.getMap().getID(), boxReference.getConnectedModel().getId());
				//communicator.sendActionPackage(actionBuilder.removeElement(boxReference.getMap().getID(), boxReference.getConnectedModel().getId()));
			}
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("add-button-highlighted")) {
			//new AddElementToBoxDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x, boxReference.getPosition(true).y);
			createAddElementToBoxDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x, boxReference.getPosition(true).y);
		}else if(((Element) be.getEventTarget().cast()).getClassName().equals("del-button-highlighted")) {
			//new DelElementFromBoxDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x, boxReference.getPosition(true).y);
			createDelElementFromBoxDialog(boxReference.getMap(), boxReference, boxReference.getPosition(true).x, boxReference.getPosition(true).y);
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("config-button-highlighted")) {
			AbstractConfigWindow w = createConfigBoxWindow(boxReference.getMap(), boxReference, boxReference.getPosition(true).x, boxReference.getPosition(true).y);
			if(w != null){
				w.show();
			}
		}
	}
	protected abstract AbstractDeleteDialog createDeleteDialog(GraphMap targetMap, AbstractBox targetBox, int left, int top);
	protected abstract AbstractAddElementToBoxDialog createAddElementToBoxDialog(GraphMap map, AbstractBox box, int posX, int posY);
	protected abstract AbstractDelElementFromBoxDialog createDelElementFromBoxDialog(GraphMap map, AbstractBox box, int posX, int posY);
	protected abstract AbstractConfigWindow createConfigBoxWindow(GraphMap map, AbstractBox box, int posX, int posY);
	
	protected abstract void onClickServerUpdate(String mapID, int boxId);
	
	private void handleOnMouseOver(Event be) {
		if (!boxReference.isModificationAllowed()) {
			// Say smth to the user and exit
			return;
		}
		if(((Element) be.getEventTarget().cast()).getClassName().equals("close-button")) {
			((Element) be.getEventTarget().cast()).setClassName("close-button-highlighted");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("add-button")) {
			((Element) be.getEventTarget().cast()).setClassName("add-button-highlighted");		
			//((Element) be.getEventTarget().cast()).setAttribute("visibility", "visible");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("del-button")) {
			((Element) be.getEventTarget().cast()).setClassName("del-button-highlighted");
			//((Element) be.getEventTarget().cast()).setAttribute("visibility", "visible");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("config-button")) {
			((Element) be.getEventTarget().cast()).setClassName("config-button-highlighted");
			//((Element) be.getEventTarget().cast()).setAttribute("visibility", "visible");
		}
	}
	
	private void handleOnMouseOut(Event be) {
		if (!boxReference.isModificationAllowed()) {
			// Say smth to the user and exit
			return;
		}
		if(((Element) be.getEventTarget().cast()).getClassName().equals("close-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("close-button");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("add-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("add-button");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("del-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("del-button");
		}
		else if(((Element) be.getEventTarget().cast()).getClassName().equals("config-button-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("config-button");
		}
	}
}
