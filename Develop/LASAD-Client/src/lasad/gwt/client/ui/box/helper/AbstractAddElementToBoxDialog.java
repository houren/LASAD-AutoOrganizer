package lasad.gwt.client.ui.box.helper;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;



/**
 * Menu to add childs to a top-level box element
 * 
 * @author Frank Loll
 * @author anahuacv refactoring
 * 
 */
public abstract class AbstractAddElementToBoxDialog extends BoxComponent implements FocusableInterface {

	private AbstractBox myBox;
	private GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;

	private EventListener boxDialogListener;

	public AbstractAddElementToBoxDialog(GraphMap map, AbstractBox box, int posX, int posY) {
		this.myMap = map;
		this.myBox = box;

		//boxDialogListener = new AddElementToBoxDialogListener(myMap, myBox, this);
		boxDialogListener = createtAddElementToBoxDialogListener(myMap, myBox, this);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		// this.setPosition(posX, posY);

		this.addToMap(myMap, posX, posY);
		// Get Focus
		myMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractAddElementToBoxDialogListener createtAddElementToBoxDialogListener(GraphMap map, AbstractBox box, AbstractAddElementToBoxDialog dialog);

	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Please choose an element to add...");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	private void initMenu() {
		for (ElementInfo childElement : myBox.getElementInfo().getChildElements().values()) {
			// Calculate current element Count
			int elementCount = MVCHelper.getChildModelsByElementID(myBox.getConnectedModel(), childElement.getElementID()).size();

			if ((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()) {
				// All ok, more elements could be created
				Element tmpElement = DOM.createDiv();
				tmpElement.setClassName("dialog-text");

				// Set elementname
				if (childElement.getElementOption(ParameterTypes.LongLabel) != null) {
					tmpElement.setInnerText("Add " + childElement.getElementOption(ParameterTypes.LongLabel));
				} else if (childElement.getElementOption(ParameterTypes.Label) != null) {
					tmpElement.setInnerText("Add " + childElement.getElementOption(ParameterTypes.Label));
				} else {
					tmpElement.setInnerText("Add " + childElement.getElementID());
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
				DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
				DOM.setEventListener(tmpElement, boxDialogListener);

			}

		}
		// Add Cancel Element
		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");
		cancelElement.setInnerText("Cancel");
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(cancelElement, boxDialogListener);
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	private void addToMap(GraphMap targetMap, int left, int top) {
		targetMap.add(this);
		// DOM.setIntStyleAttribute(this.getElement(), "zIndex",
		// XDOM.getTopZIndex());
		setPosition(left, top);
		targetMap.layout();
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractAddElementToBoxDialog.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if (!focus) {
			// Focus got lost, delete Element
			removeFromParent();
		}
	}
}