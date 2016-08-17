package lasad.gwt.client.ui.link.helper;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractAddElementToLinkDialog extends BoxComponent implements FocusableInterface {

	private AbstractLinkPanel linkPanelReference;
	private GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;
	
	private EventListener linkDialogListener;
	
	public AbstractAddElementToLinkDialog(GraphMap map, AbstractLinkPanel linkPanelReference, int posX, int posY) {	
		this.myMap = map;
		this.linkPanelReference = linkPanelReference;
		
		//linkDialogListener = new AddElementToLinkDialogListener(myMap, linkPanelReference, this);
		linkDialogListener = createAddElementToLinkDialogListener(myMap, linkPanelReference, this);
		
		actions = new Vector<Element>();
		
		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");
		
		initHeading();
		
		initMenu();
		for(int i=0; i<actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		
		this.addToMap(myMap, posX, posY);
		// Get Focus
		myMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractAddElementToLinkDialogListener createAddElementToLinkDialogListener(GraphMap map, AbstractLinkPanel linkPanelReference, AbstractAddElementToLinkDialog dialog);
	
	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Please choose an element to add...");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}
	

	
	private void initMenu(){
		for(ElementInfo childElement : linkPanelReference.getElementInfo().getChildElements().values()){			
			// Calculate current element Count
			int elementCount = MVCHelper.getChildModelsByElementID(linkPanelReference.getMyLink().getConnectedModel(), childElement.getElementID()).size();
		
			
			
			if((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()){
				// All ok, more elements could be created
				Element tmpElement = DOM.createDiv();
				tmpElement.setClassName("dialog-text");
				
				// Set elementname
				if(childElement.getElementOption(ParameterTypes.LongLabel)!= null){
					tmpElement.setInnerText("Add "+childElement.getElementOption(ParameterTypes.LongLabel));
				}
				else if(childElement.getElementOption(ParameterTypes.Label)!= null){
					tmpElement.setInnerText("Add "+childElement.getElementOption(ParameterTypes.Label));
				}
				else{
					tmpElement.setInnerText("Add "+childElement.getElementID());
				}
				// Set invisible Element Type DIV
				Element elementTypeElement = DOM.createDiv();
				elementTypeElement.setInnerText(childElement.getElementType());
				DOM.setStyleAttribute(elementTypeElement, "visibility", "hidden");
				DOM.setStyleAttribute(elementTypeElement, "display", "none");
				DOM.appendChild(tmpElement, elementTypeElement);
				// Set invisible ElementID DIV
				Element elementIDElement = DOM.createDiv();
				elementIDElement.setInnerText(childElement.getElementID());
				DOM.setStyleAttribute(elementIDElement, "visibility", "hidden");
				DOM.setStyleAttribute(elementIDElement, "display", "none");
				DOM.appendChild(tmpElement, elementIDElement);
				
				// Set Listener stuff
				this.actions.add(tmpElement);
				DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT| Event.ONMOUSEOVER | Event.ONCLICK);
				DOM.setEventListener(tmpElement, linkDialogListener);

			}
		
		}
		// Add Cancel Element
		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");		
		cancelElement.setInnerText("Cancel");		
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT| Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(cancelElement, linkDialogListener);
	}
	
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}
	
	private void addToMap(GraphMap targetMap, int left, int top) {
		targetMap.add(this);
		setPosition(left, top);
		targetMap.layout();
	}
	
	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractAddElementToLinkDialog.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if(!focus){
			// Focus got lost, delete Element
			removeFromParent();
		}
	}

}
