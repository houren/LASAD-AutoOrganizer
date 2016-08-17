package lasad.gwt.client.ui.common.elements;

import java.util.Date;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;


public abstract class AbstractExtendedAwarenessElement extends AbstractExtendedElement {

	private String startText = null;

	public AbstractExtendedAwarenessElement(
			ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);
	}

	public AbstractExtendedAwarenessElement(
			ExtendedElementContainerInterface recipient,
			ElementInfo elementInfo, String value) {
		super(recipient, elementInfo);
		this.startText = value;
	}

	private Element elementContent = null;
	private Element viewModeDiv = null;

	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

		// INIT Awareness Info

		// Build Content Div
		elementContent = DOM.createDiv();
		elementContent.setClassName("box-ee-awareness-div");

		// Icon
		Element icon = DOM.createDiv();
		icon.setClassName("box-ee-awareness-icondiv");
		DOM.setStyleAttribute(icon, "height", "16px");
		DOM.setStyleAttribute(icon, "width", "16px");
		DOM.appendChild(elementContent, icon);

		// Build View Mode
		viewModeDiv = DOM.createDiv();
		viewModeDiv.setClassName("box-ee-awareness-viewModeDiv");
		
		
		DateTimeFormat df = DateTimeFormat.getFormat("HH:mm:ss");
		Date dateT = new Date(System.currentTimeMillis());

		viewModeDiv.setInnerHTML("Username, "+df.format(dateT));
		DOM.appendChild(elementContent, viewModeDiv);

		setElementSize(new Size(getActualViewModeWidth(),
				getActualViewModeHeight()));

	}

	protected void setElementSize(Size size) {
	}

	protected void switchToEditMode(Element contentFrame) {

	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();

		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}

	}

	protected String getVarValue(ParameterTypes name) {
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
	}

	@Override
	protected void setElementHighlight(boolean highlight) { }

	protected void onEstablishModelConnection() {
		String initText = null;
		if (startText != null) {
			initText = startText;
		} else {
			initText = this.getConnectedModel().getAuthor();
		}
		Logger.log("ExtendedAwarenessElement: onEstablishModelConnection() author: initText", Logger.DEBUG);
		if (initText != null) {
			viewModeDiv.setInnerHTML("&nbsp;" + initText);
		}
	}

	@Override
	protected void onRemoveModelConnection() { }
}