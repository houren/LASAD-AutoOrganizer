package lasad.gwt.client.ui.workspace.tableview.elements;

import lasad.gwt.client.logger.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBox;

public class ExtendedTextBox extends TextBox{
	public ExtendedTextBox(){
		super();
		sinkEvents(Event.ONPASTE);
	}
	
	public ExtendedTextBox(Element element){
		super(element);
		sinkEvents(Event.ONPASTE);
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (event.getTypeInt()) {
			case Event.ONPASTE: {
				Logger.log("onpaste detected!", Logger.DEBUG);
				break;
			}
		}
	}
}