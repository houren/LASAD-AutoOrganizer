package lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui;

import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainer;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.ExtendedElementContainerMasterInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.helper.TextSelection;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.link.argument.helper.LinkHeaderButtonListenerArgument;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 *	I THINK SimpleLinkPanel is the representation of the basic link panel created for previews while using the authoring tool, but since the author
 *	of this file was kind enough to not provide a file description, this is unconfirmed.
 *	@author ???
 */

public class SimpleLinkPanel extends BoxComponent implements ExtendedElementContainerMasterInterface, ExtendedElementContainerInterface {

	private SimpleLink myLink;
	private boolean details = true; // If true, then the normal link panel will be drawn, i.e. with all elements, title text, etc.
									// If false, there will be only a button to delete the link

	private int height = 10;
	private int width = 10;

	int modelHeight, modelWidth;

	private int generalOffsetHeight = 2; // 2 Pixel for the Border
	private int generalOffsetWidth = 2; // 2 Pixel for the Border
	private int generalOwnHeight = 14; // Header + Header BottomBorder

	private Element root;
	private ExtendedElementContainer extendedElementContainer = null;

	private LinkHeaderButtonListenerArgument myButtonListener;

	public SimpleLinkPanel(SimpleLink myLink, String details) {
		this.myLink = myLink;
		this.modelHeight = Integer.parseInt(myLink.getElementInfo().getUiOption(ParameterTypes.Height)==null?myLink.getElementInfo().getUiOption(ParameterTypes.Height):myLink.getElementInfo().getUiOption(ParameterTypes.Height));
		this.modelWidth = Integer.parseInt(myLink.getElementInfo().getUiOption(ParameterTypes.Width)==null?myLink.getElementInfo().getUiOption(ParameterTypes.Width):myLink.getElementInfo().getUiOption(ParameterTypes.Width));

		if (details.equalsIgnoreCase("true")) {
			this.details = true;
		} else {
			this.details = false;
		}

		this.root = DOM.createDiv();

		initHeader();

		if (this.details) {
			this.root.setClassName("link-content");
			initExtendedElementContainer();
			this.setLocalSize(modelWidth, modelHeight);
		} else {
			this.root.setClassName("link-content-nondetails");
		}
		
		setRootElementID("3");
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.root, target, index);
		this.el().setZIndex(XDOM.getTopZIndex());

	}

	private Element header = null;
	private Element headerText = null;
	private Element delButton;
	private Element addButton;
	private Element closeButton;
	private Element switchDirectionButton;

	private void initHeader() {

		// Set HEADER

		header = DOM.createDiv();
		DOM.appendChild(root, header);
		DOM.setStyleAttribute(header, "height", "13px");

		if (details) {
			header.setClassName("link-heading");
			DOM.setStyleAttribute(header, "background", myLink.getElementInfo().getUiOption(ParameterTypes.BackgroundColor));
		} else {
			header.setClassName("link-heading-nondetails");
		}

		if (details) {
			headerText = DOM.createDiv();
			headerText.setInnerHTML(myLink.getElementInfo().getElementOption(ParameterTypes.Heading));
			headerText.setClassName("link-heading-text");
			DOM.appendChild(header, headerText);

			TextSelection.disableTextSelection(headerText);
		}

		closeButton = DOM.createDiv();
		closeButton.setClassName("link-close-button");
		DOM.appendChild(header, closeButton);

		if (details) {

			switchDirectionButton = DOM.createDiv();
			switchDirectionButton.setClassName("switch-direction-button");

			if (myLink.getElementInfo().getElementOption(ParameterTypes.Endings).equalsIgnoreCase("TRUE")) {
				DOM.appendChild(header, switchDirectionButton);

				DOM.sinkEvents(switchDirectionButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
				DOM.setEventListener(switchDirectionButton, myButtonListener);
			}

			addButton = DOM.createDiv();
			DOM.appendChild(header, addButton);

			delButton = DOM.createDiv();
			DOM.appendChild(header, delButton);

			addButton.setClassName("add-button");
			delButton.setClassName("del-button");

			DOM.setStyleAttribute(this.addButton, "visibility", "visible");
			DOM.setStyleAttribute(this.delButton, "visibility", "visible");
			DOM.setStyleAttribute(this.switchDirectionButton, "visibility", "visible");
		}
	}

	private void initExtendedElementContainer() {
		this.extendedElementContainer = new ExtendedElementContainer(this);
		this.extendedElementContainer.setElementSpacing(1);
		this.extendedElementContainer.setPaddingTop(1);
		this.extendedElementContainer.setPaddingBottom(1);
		this.extendedElementContainer.setPaddingLeft(1);
		this.extendedElementContainer.setPaddingRight(1);
		// add Container Element to DIV
		DOM.appendChild(root, extendedElementContainer.getContainerElement());
	}

	private void setLocalSize(int width, int height) {
		if (width != -1 && width != this.width && height == -1) {
			// Set the Root Width
			this.width = width;
			super.setSize(width, -1);
			extendedElementContainer.setWidth(width);
		} else if (height != -1 && height != this.height && width == -1) {
			// Set the Root Height
			this.height = height;
			super.setSize(-1, height + generalOffsetHeight);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		} else if (height != -1 && width != -1) {
			this.width = width;
			this.height = height;
			super.setSize(width, height + generalOffsetHeight);
			extendedElementContainer.setWidth(this.width);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		}

		Logger.log("[LinkPanel][setLocalSize]: width: " + this.width + " height: " + this.height, Logger.DEBUG);
		myLink.updateLinkPanelPosition();
	}

	public void setSize(int width, int height) {
		if (width != -1 && width - generalOffsetWidth != this.width && height == -1) {
			// Set the Root Width
			this.width = width - generalOffsetWidth;
			super.setSize(width, -1);
			extendedElementContainer.setWidth(width);
		} else if (height != -1 && height - generalOffsetHeight != this.height && width == -1) {
			// Set the Root Height
			this.height = height - generalOffsetHeight;
			super.setSize(-1, height);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		} else if (height != -1 && width != -1) {
			this.width = width - generalOffsetWidth;
			this.height = height - generalOffsetHeight;
			super.setSize(width, height);
			extendedElementContainer.setWidth(this.width);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		}
		Logger.log("[LinkPanel][setSize]: width: " + this.width + " height: " + this.height, Logger.DEBUG);
		myLink.updateLinkPanelPosition();
	}

	public void setRootElementID(String rootElementID) {
		if (details) {
			headerText.setInnerHTML(rootElementID + "&nbsp;&middot;&nbsp;<b>" + myLink.getElementInfo().getElementOption(ParameterTypes.Heading) + "</b>");
		}
	}

	public SimpleLink getMyLink() {
		return myLink;
	}

	public GenericFocusHandler getFocusHandler() {
		return myLink.getFocusHandler();
	}

	public void extendedElementContainerCallNewHeight(int height) {
		setLocalSize(-1, height + generalOwnHeight);
	}

	public void addExtendedElement(AbstractExtendedElement element) {
		// Redirect call to the Container
		extendedElementContainer.addExtendedElement(element);
	}

	public ElementInfo getElementInfo() {
		return myLink.getElementInfo();
	}

	public MVCViewSession getMVCViewSession() {
		return myLink.getMVCViewSession();
	}

	public void removeExtendedElement(AbstractExtendedElement element) {
		// Redirect call to the Container
		extendedElementContainer.removeExtendedElement(element);
	}

	public FocusableInterface getFocusParent() {
		// Redirect call to the Link itself
		return myLink.getFocusParent();
	}

	public void setElementFocus(boolean focus) {
	// In Future Panel Highlight
	}

	public void extendedElementContainerPublishedNewMaxHeight(int height) {
	}

	public void extendedElementContainerPublishedNewMinHeight(int height) {
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
//		if (element.getConfig().getElementType().equalsIgnoreCase("text")) {
//			extendedElementContainer.addExtendedElement(element, true);
//		} else {
			extendedElementContainer.addExtendedElement(element, pos);
//		}
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return extendedElementContainer.getExtendedElements();
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return null;
	}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		return null;
	}

	@Override
	public void setHighlight(boolean highlight) { }

	@Override
	public void textAreaCallNewHeightgrow(int height) {
		// TODO Auto-generated method stub
		
	}

}