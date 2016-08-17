package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Collection;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * This class creates the dialog for adding a link between two existing, non-connected boxes when the user drags between them.
 * If using the drop down argument map menu to add a link, see AbstractCreateSpecialLinkDialog.
 * If creating a box and link simulatenously, see AbstractCreateBoxLinkDialog.
 * Documentation added by Kevin Loughlin, 16 June 2015
 * @author Unknown
 */

public abstract class AbstractCreateLinkDialog extends BoxComponent implements FocusableInterface {

	protected GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;

	private EventListener linkDialogListener;

	//Constructor not even called? 
	public AbstractCreateLinkDialog(GraphMap map, AbstractBox start, AbstractBox end) {
		this.myMap = map;

		//linkDialogListener = new CreateLinkDialogListener(myMap, this, start, end);
		linkDialogListener = createCreateLinkDialogListener(myMap, this, start, end);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		int posX = Math.abs(start.getPosition(true).x + end.getPosition(true).x + end.getWidth()) / 2 - this.getWidth() / 2;
		int posY = Math.abs(start.getPosition(true).y + end.getPosition(true).y + end.getHeight()) / 2;

		setPosition(posX, posY);
		
		//Get Focus
		myMap.getFocusHandler().releaseAllFocus();
		myMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractCreateLinkDialogListener createCreateLinkDialogListener(GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractBox b2);
	protected abstract AbstractCreateLinkDialogListener createCreateLinkDialogListener(GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1, AbstractLink l2);

	public AbstractCreateLinkDialog(GraphMap map, AbstractBox start, AbstractBox end, int posX, int posY) {
		this.myMap = map;

		//linkDialogListener = new CreateLinkDialogListener(myMap, this, start, end);
		linkDialogListener = createCreateLinkDialogListener(myMap, this, start, end);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}


		setPosition(Math.abs(posX), Math.abs(posY));

		//Get Focus
		myMap.getFocusHandler().releaseAllFocus();
		myMap.getFocusHandler().setFocus(this);
	}

	public AbstractCreateLinkDialog(GraphMap map, AbstractBox start, AbstractLink end, int posX, int posY) {
		this.myMap = map;

		//linkDialogListener = new CreateLinkDialogListener(myMap, this, start, end);
		linkDialogListener = createCreateLinkDialogListener(myMap, this, start, end);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		setPosition(Math.abs(posX), Math.abs(posY));
		myMap.getFocusHandler().releaseAllFocus();
		myMap.getFocusHandler().setFocus(this);
	}


	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Please choose a Relation...");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	protected abstract Collection<ElementInfo> getElementsByType(String type);
	
	public void initMenu() {
//		for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("relation").values()) {
		for (ElementInfo info : getElementsByType("relation")) {

			Element tmpElement = DOM.createDiv();
			tmpElement.setClassName("dialog-text");
			tmpElement.setInnerText(info.getElementOption(ParameterTypes.Heading));
			this.actions.add(tmpElement);

			DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
			DOM.setEventListener(tmpElement, linkDialogListener);
		}

		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");
		cancelElement.setInnerText("Cancel");
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(cancelElement, linkDialogListener);
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractCreateLinkDialog.this.el().updateZIndex(1);
	}

	public FocusableInterface getFocusParent() {
		return myMap;
	}

	public void setElementFocus(boolean focus) {
		if (!focus) {
			// focus got lost
			removeFromParent();
		}
	}
}