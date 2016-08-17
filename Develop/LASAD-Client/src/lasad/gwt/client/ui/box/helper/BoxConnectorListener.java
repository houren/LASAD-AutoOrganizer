package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.ui.workspace.LASADInfo;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class BoxConnectorListener implements EventListener {

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnMouseOut.getEventCode()) {
			handleMouseOut(be);

		} else if (be.getTypeInt() == Events.OnMouseOver.getEventCode()) {
			handleMouseOver(be);
		}
//		be.stopPropagation();
	}

	private void handleMouseOut(Event be) {
		// End hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("north-connector-dark")) {
			((Element) be.getEventTarget().cast()).setClassName("north-connector");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("east-connector-dark")) {
			((Element) be.getEventTarget().cast()).setClassName("east-connector");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("south-connector-dark")) {
			((Element) be.getEventTarget().cast()).setClassName("south-connector");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("west-connector-dark")) {
			((Element) be.getEventTarget().cast()).setClassName("west-connector");
		}
	}

	private void handleMouseOver(Event be) {
		LASADInfo.display("Over", ((Element) be.getEventTarget().cast()).getClassName());
		// Start hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("north-connector")) {
			((Element) be.getEventTarget().cast()).setClassName("north-connector-dark");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("east-connector")) {
			((Element) be.getEventTarget().cast()).setClassName("east-connector-dark");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("south-connector")) {
			((Element) be.getEventTarget().cast()).setClassName("south-connector-dark");
		} else if (((Element) be.getEventTarget().cast()).getClassName().equals("west-connector")) {
			((Element) be.getEventTarget().cast()).setClassName("west-connector-dark");
		}
	}
}