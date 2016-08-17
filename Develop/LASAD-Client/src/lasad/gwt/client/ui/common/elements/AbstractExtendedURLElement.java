package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractExtendedURLElement extends AbstractExtendedElement {

	public AbstractExtendedURLElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
		this.elementVars.add(ParameterTypes.Link);
	}
	
	public AbstractExtendedURLElement(ExtendedElementContainerInterface container, ElementInfo config, boolean readOnly, boolean dummy) {
		super(container, config, readOnly);
		this.elementVars.add(ParameterTypes.Link);

		if(dummy) {
			buildElement();
			this.setVarValue(ParameterTypes.Link, "http://lasad.dfki.de", LASAD_Client.getInstance().getUsername());
		}
	}

	Element elementContent = null;

	Element logoDiv = null;
	Element editButtonDiv = null;

	Element textField = null;
	Element urlField = null;
	Element textFrameDiv = null;

	protected void buildElement() {
		if (elementContent != null) {
			// Already builded
			return;
		}

		EventListener listener = new EventListener() {
			public void onBrowserEvent(Event be) {
				int code = be.getTypeInt();
				if (code == Events.OnClick.getEventCode() && be.getCurrentEventTarget().cast() != urlField) {
					if (AbstractExtendedURLElement.this.isActiveViewMode()) {
						// Ask for Focus
						AbstractExtendedURLElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedURLElement.this);
					}
				} else if (code == Events.OnFocus.getEventCode()) {
					if (AbstractExtendedURLElement.this.isActiveViewMode()) {
						// Ask for Focus by TABULATOR
						AbstractExtendedURLElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedURLElement.this);
					}
				} else if (code == Events.OnBlur.getEventCode()) {
					if (AbstractExtendedURLElement.this.isActiveViewMode()) {
						// Focus was lost by TABULATOR
						AbstractExtendedURLElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedURLElement.this);
					}
				}
				be.stopPropagation();
			}
		};

		elementContent = DOM.createDiv();
		elementContent.setClassName("extendedURLElement");

		if (isModificationAllowed() && !this.readOnly) {
			DOM.sinkEvents(this.elementContent, Events.OnClick.getEventCode());
			DOM.setEventListener(this.elementContent, listener);
		}

		// LOGO
		logoDiv = DOM.createDiv();
		logoDiv.setClassName("extendedURLElement-Logo");
		DOM.appendChild(elementContent, logoDiv);

		// EDIT MODE

		// TEXTFIELD
		textFrameDiv = DOM.createDiv();
		DOM.appendChild(elementContent, textFrameDiv);

		// OneRowTextfield
		textField = DOM.createInputText();
		textField.setClassName("extendedURLElement-TextField-EditMode");

		DOM.sinkEvents(textField, Events.Focus.getEventCode());
		DOM.setEventListener(textField, listener);

		// VIEW MODE

		// URL FIELD
		urlField = DOM.createDiv();
		urlField.setClassName("extendedURLElement-TextField-ViewMode");

		// Add an EventListener to the URL Element to prevent a switch to the
		// editmode by clicking on the url
		DOM.sinkEvents(urlField, Events.OnClick.getEventCode());
		DOM.setEventListener(urlField, listener);

		// Edit Button
		editButtonDiv = DOM.createDiv();
		editButtonDiv.setClassName("extendedURLElement-EditButton");

		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	protected void setElementSize(Size size) {
		int balanceWidth = 4, balanceHeight = 4; // 1px distance to the
													// frameborder, 1 px padding
													// in each direction

		if (textFrameDiv != null) {
			DOM.setStyleAttribute(textFrameDiv, "width", Math.max(0, size.width - balanceWidth) + "px");
			DOM.setStyleAttribute(textFrameDiv, "height", Math.max(0, size.height - balanceHeight) + "px");
		}
		if (textField != null) {
			DOM.setStyleAttribute(textField, "width", Math.max(0, size.width - balanceWidth - logoDiv.getOffsetWidth()) + "px");
		}
		if (urlField != null) {
			DOM.setStyleAttribute(urlField, "width", Math.max(0, size.width - balanceWidth - logoDiv.getOffsetWidth()) + "px");
		}
	}

	protected void switchToEditMode(Element contentFrame) {
		buildElement();
		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
		if (urlField != null && DOM.isOrHasChild(textFrameDiv, urlField)) {
			DOM.removeChild(textFrameDiv, urlField);
		}
		if (textField != null && !DOM.isOrHasChild(textFrameDiv, textField)) {
			DOM.appendChild(textFrameDiv, textField);
		}
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();
		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
		if (urlField != null && !DOM.isOrHasChild(textFrameDiv, urlField)) {
			DOM.appendChild(textFrameDiv, urlField);
		}
		if (textField != null && DOM.isOrHasChild(textFrameDiv, textField)) {
			DOM.removeChild(textFrameDiv, textField);
		}
	}

	protected String getVarValue(ParameterTypes name) {
		if (name == ParameterTypes.Link) {
			if (textField != null) {
				return DOM.getElementProperty(this.textField, "value");
			}
		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		this.checkForHighlight(username);

		if (name == ParameterTypes.Link) {
			if (value.startsWith("http://") || value.startsWith("HTTP://")) {
				if (textField != null) {
					DOM.setElementProperty(this.textField, "value", value);
				}
				if (urlField != null) {
					urlField.setInnerHTML("<a href=\"" + value + "\" target=\"_blank\">" + value + "</a>");
				}
			} else {
				if (textField != null) {
					DOM.setElementProperty(this.textField, "value", value);
				}
				if (urlField != null) {
					urlField.setInnerHTML("<a href=\"http://" + value + "\" target=\"_blank\">" + value + "</a>");
				}
			}
		}
	}

	@Override
	protected void setElementHighlight(boolean highlight) {
	}

	@Override
	protected void onEstablishModelConnection() {
	}

	@Override
	protected void onRemoveModelConnection() {
	}
}