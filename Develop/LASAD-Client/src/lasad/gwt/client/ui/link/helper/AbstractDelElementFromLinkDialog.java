package lasad.gwt.client.ui.link.helper;

import java.util.Vector;

import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractDelElementFromLinkDialog extends BoxComponent implements
FocusableInterface {

	private AbstractLinkPanel linkPanelReference;
	private GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;

	private EventListener linkDialogListener;

	public AbstractDelElementFromLinkDialog(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		this.myMap = map;
		this.linkPanelReference = linkPanelReference;

		//linkDialogListener = new DelElementFromLinkDialogListener(myMap, linkPanelReference, this);
		linkDialogListener = createDelElementFromLinkDialogListener(myMap, linkPanelReference, this);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		this.addToMap(myMap, posX, posY);
		// Get Focus
		myMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractDelElementFromLinkDialogListener createDelElementFromLinkDialogListener(GraphMap map, 
			AbstractLinkPanel linkPanelReference , AbstractDelElementFromLinkDialog DelElementfromLinkDialog);

	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement
		.setInnerText("Please choose an element to delete...");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	private void initMenu() {
		for (AbstractExtendedElement linkElement : linkPanelReference
				.getExtendedElements()) {
			Element tmpElement = DOM.createDiv();
			tmpElement.setClassName("dialog-text");
			String tmpString;
			int elementCount = MVCHelper.getChildModelsByElementID(
					linkPanelReference.getMyLink().getConnectedModel(),
					linkElement.getConfig().getElementID()).size();

			if (elementCount > linkElement.getConfig().getMinQuantity()) {
				if ((linkElement.getConnectedModel().getValue(ParameterTypes.Text)) != null) {
					String dummy = linkElement.getConnectedModel().getValue(ParameterTypes.Text);
					tmpString = " :" + cropString(dummy);
				} else {
					tmpString = "";
				}
				if (linkElement.getConfig().getElementOption(ParameterTypes.LongLabel) != null) {
					tmpElement.setInnerText("Del "
							+ linkElement.getConfig().getElementOption(
									ParameterTypes.LongLabel) + tmpString);
				} else if (linkElement.getConfig().getElementOption(ParameterTypes.Label) != null) {
					tmpElement.setInnerText("Del "
							+ linkElement.getConfig().getElementOption(ParameterTypes.Label)
							+ tmpString);
				} else {
					tmpElement.setInnerText("Del "
							+ linkElement.getConfig().getElementID()
							+ tmpString);
				}

			}
			// Set invisible Element Type DIV
			Element elementTypeElement = DOM.createDiv();
			elementTypeElement.setInnerText(linkElement.getConfig().getElementType());
			DOM.setStyleAttribute(elementTypeElement, "visibility", "hidden");
			DOM.setStyleAttribute(elementTypeElement, "display", "none");
			DOM.appendChild(tmpElement, elementTypeElement);

			// Set invisible ElementID DIV
			Element elementIDElement = DOM.createDiv();
			elementIDElement.setInnerText(""
					+ linkElement.getConnectedModel().getId());
			DOM.setStyleAttribute(elementIDElement, "visibility", "hidden");
			DOM.setStyleAttribute(elementIDElement, "display", "none");
			DOM.appendChild(tmpElement, elementIDElement);

			// Set Listener stuff
			this.actions.add(tmpElement);
			DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER
					| Event.ONCLICK);
			DOM.setEventListener(tmpElement, linkDialogListener);
		}

		// Add Cancel Element
		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");
		cancelElement.setInnerText("Cancel");
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER
				| Event.ONCLICK);
		DOM.setEventListener(cancelElement, linkDialogListener);
	}

	private String cropString(String string) {
		if (string.length() > 5) {
			return string.substring(0, 5) + "...";
		} else {
			return string;
		}
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
		AbstractDelElementFromLinkDialog.this.el().updateZIndex(1);
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
