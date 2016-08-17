package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.logger.Logger;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class BoxEventListener implements EventListener {

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnClick.getEventCode()){
			handleOnClick(be);
				
		}
		else if (be.getTypeInt() == Events.OnMouseDown.getEventCode()){
			handleMouseDown(be);
			
		}
		else if (be.getTypeInt() == Events.OnMouseUp.getEventCode()){
			handleMouseUp(be);
			
		}
		else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()){
			handleMouseOut(be);
			
		}
		else if (be.getTypeInt() == Events.OnMouseOver.getEventCode()){
			handleMouseOver(be);
			
		}
		else if (be.getTypeInt() == Events.OnMouseMove.getEventCode()){
			handleMouseMove(be);
			
		}
		
	}		
	
	private void handleMouseDown(Event be){ }
		
	private void handleMouseUp(Event be) { }
		
	private void handleMouseOut(Event be) { 
		Logger.log("MouseOut", Logger.DEBUG_DETAILS);
	}
		
	private void handleMouseOver(Event be) { 
		Logger.log("MouseOver", Logger.DEBUG_DETAILS);
	}
	
	private void handleMouseMove(Event be) { }
	
	private void handleOnClick(Event be) {
		if (((Element) be.getEventTarget().cast()).getClassName().equals("box-heading-text")) {
			Logger.log("Bubble false", Logger.DEBUG_DETAILS);
			be.stopPropagation();
		}
		else {
			be.stopPropagation();
			Logger.log("Bubble true", Logger.DEBUG_DETAILS);
		}
	}
}