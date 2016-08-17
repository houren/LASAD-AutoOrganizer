package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Vector;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractDeleteDialog extends BoxComponent implements FocusableInterface {

	private Element rootElement, headerElement, dontAskAgainElement;
	private GraphMap myMap;

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);
	
	public Element getDontAskAgainElement() {
		return dontAskAgainElement;
	}

	private Vector<Element> actions = null;

	private EventListener deleteDialogListener;

	public AbstractDeleteDialog(GraphMap targetMap, AbstractBox targetBox, int left, int top) {
		//deleteDialogListener = new DeleteDialogListener(targetMap, this, targetBox);
		deleteDialogListener = createDeleteDialogListener(targetMap, this, targetBox);
		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		addToMap(targetMap, left, top);

		// Get Focus
		myMap = targetMap;
		targetMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractDeleteDialogListener createDeleteDialogListener(GraphMap myMap, AbstractDeleteDialog dialog, AbstractBox boxTarget);
	protected abstract AbstractDeleteDialogListener createDeleteDialogListener(GraphMap myMap, AbstractDeleteDialog dialog, AbstractLinkPanel linkTarget);

	public AbstractDeleteDialog(GraphMap targetMap, AbstractLinkPanel targetLink, int left, int top) {
		//deleteDialogListener = new DeleteDialogListener(targetMap, this, targetLink);
		deleteDialogListener = createDeleteDialogListener(targetMap, this, targetLink);
		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		// DOM.setIntStyleAttribute(this.getElement(), "zIndex",
		// XDOM.getTopZIndex());
		addToMap(targetMap, left, top);

		// Get Focus
		myMap = targetMap;
		targetMap.getFocusHandler().setFocus(this);

	}

	private void addToMap(GraphMap targetMap, int left, int top) {
		targetMap.add(this);
		// DOM.setIntStyleAttribute(this.getElement(), "zIndex",
		// XDOM.getTopZIndex());
		setPosition(left, top);
		targetMap.layout();
	}

	private void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText(myConstants.DeleteDialogueTitle());
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	private void initMenu() {
		Element yesElement = DOM.createDiv();
		yesElement.setClassName("dialog-text");
		yesElement.setInnerText(myConstants.DeleteDialogueYes());
		this.actions.add(yesElement);

		DOM.sinkEvents(yesElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(yesElement, deleteDialogListener);

		Element noElement = DOM.createDiv();
		noElement.setClassName("dialog-text");
		noElement.setInnerText(myConstants.DeleteDialogueNo());
		this.actions.add(noElement);

		DOM.sinkEvents(noElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(noElement, deleteDialogListener);

		Element checkBoxDiv = DOM.createDiv();
		checkBoxDiv.setClassName("delete-dialog-root");

		this.dontAskAgainElement = DOM.createInputCheck();
		this.dontAskAgainElement.setClassName("delete-dialog-checkbox");
		checkBoxDiv.appendChild(this.dontAskAgainElement);
//		DOM.setStyleAttribute(dontAskAgainElement, "float", "left !important");

		DOM.sinkEvents(checkBoxDiv, Event.ONCLICK);
		DOM.setEventListener(checkBoxDiv, deleteDialogListener);

		Element checkBoxLabel = DOM.createDiv();
		checkBoxLabel.setInnerText(myConstants.DeleteDialogueAskAgainCheckbox());
		checkBoxLabel.setClassName("dialog-text-nohighlighting delete-dialog-checkbox-label");

		checkBoxDiv.appendChild(checkBoxLabel);

		this.actions.add(checkBoxDiv);
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractDeleteDialog.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if (!focus) {
			// Focus got lost
			removeFromParent();
		}
	}
}