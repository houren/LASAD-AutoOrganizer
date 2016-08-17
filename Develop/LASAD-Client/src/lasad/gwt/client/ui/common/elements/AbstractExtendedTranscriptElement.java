package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.transcript.Transcript;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractExtendedTranscriptElement extends AbstractExtendedElement {

	public AbstractExtendedTranscriptElement(ExtendedElementContainerInterface container, ElementInfo config, TranscriptLinkData tData) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
		this.elementVars.add(ParameterTypes.StartRow);
		this.elementVars.add(ParameterTypes.StartPoint);
		this.elementVars.add(ParameterTypes.EndRow);
		this.elementVars.add(ParameterTypes.EndPoint);

		updateValue(tData);
	}

	Element elementContent = null;

	private Element viewModeDiv = null;

	private Element editModeDiv = null;
	private Element editModeStartRow = null;
	private Element editModeEndRow = null;

	private TranscriptLinkData tData;

	protected void buildElement() {
		if (elementContent != null) {
			// Already builded
			return;
		}

		EventListener listener = new EventListener() {
			public void onBrowserEvent(Event be) {
				if (be.getTypeInt() == Events.OnClick.getEventCode()) {
					// Highlight transcriptlink if it exists
					if (tData != null) {
						MVCViewSession viewSession = (MVCViewSession) AbstractExtendedTranscriptElement.this.getContainer().getMVCViewSession();
						String mapid = viewSession.getController().getMapID();
//						int mapid = ExtendedTranscriptElement.this.getContainer().getMVCViewSession().getController().getMapID();
						ArgumentMapSpace space = LASAD_Client.getMapTab(mapid).getMyMapSpace();
						Transcript transcript = space.getTranscript();
						if (transcript != null) {
							transcript.highlightElementsTranscriptLink(tData.getElementID());
						}
					}
				}
				be.stopPropagation();
			}
		};

		// Build Content Div
		elementContent = DOM.createDiv();
		elementContent.setClassName("box-ee-transcript-div");

		// Icon
		Element icon = DOM.createDiv();
		icon.setClassName("box-ee-transcript-icondiv");
		DOM.setStyleAttribute(icon, "height", "16px");
		DOM.setStyleAttribute(icon, "width", "16px");
		DOM.appendChild(elementContent, icon);

		DOM.sinkEvents(icon, Events.OnClick.getEventCode());
		DOM.setEventListener(icon, listener);

		// Build Edit Mode
		editModeDiv = DOM.createDiv();
		editModeDiv.setClassName("box-ee-transcript-editModeDiv");

		Element dummy = DOM.createDiv();

		dummy.setClassName("box-ee-transcript-editMode-text");
		dummy.setInnerText("From Line:");
		DOM.appendChild(this.editModeDiv, dummy);

		this.editModeStartRow = DOM.createInputText();
		this.editModeStartRow.setClassName("box-ee-transcript-editMode-input");
		DOM.appendChild(this.editModeDiv, this.editModeStartRow);

		dummy = DOM.createDiv();
		dummy.setClassName("box-ee-transcript-editMode-text");
		dummy.setInnerText("to Line:");
		DOM.appendChild(this.editModeDiv, dummy);

		this.editModeEndRow = DOM.createInputText();
		this.editModeEndRow.setClassName("box-ee-transcript-editMode-input");

		DOM.appendChild(this.editModeDiv, this.editModeEndRow);

		Element clearDiv = DOM.createDiv();
		DOM.setStyleAttribute(clearDiv, "clear", "both");
		DOM.appendChild(this.editModeDiv, clearDiv);

		// Build View Mode

		viewModeDiv = DOM.createDiv();
		viewModeDiv.setClassName("box-ee-transcript-viewModeDiv");
		DOM.appendChild(elementContent, viewModeDiv);

		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
		updateValue();
	}

	public void updateValue(TranscriptLinkData tData) {
		this.tData = tData;
	}

	private void updateValue() {
		if (tData == null)
			return;

		if (tData.getStartRow() != tData.getEndRow()) {
			viewModeDiv.setInnerHTML("&nbsp;Text: Line " + tData.getStartRow().getLineNumber() + " - " + tData.getEndRow().getLineNumber());
			DOM.setElementAttribute(this.editModeStartRow, "value", String.valueOf(tData.getStartRow().getLineNumber()));
			DOM.setElementAttribute(this.editModeEndRow, "value", String.valueOf(tData.getEndRow().getLineNumber()));
		} else {
			viewModeDiv.setInnerHTML("&nbsp;Text: Line " + tData.getEndRow().getLineNumber());
			DOM.setElementAttribute(this.editModeStartRow, "value", String.valueOf(tData.getStartRow().getLineNumber()));
			DOM.setElementAttribute(this.editModeEndRow, "value", String.valueOf(tData.getEndRow().getLineNumber()));
		}
	}

	protected void setElementSize(Size size) {
	}

	protected void switchToEditMode(Element contentFrame) {
		buildElement();
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();

		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
	}

	protected String getVarValue(ParameterTypes name) {
		if (name == ParameterTypes.Text) {
		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		this.checkForHighlight(username);

		if (name == ParameterTypes.StartRow) {
			this.tData.setStartRow(tData.getTranscript().getRowDataModel().get(Integer.parseInt(value)));
		} else if (name == ParameterTypes.StartPoint) {
			this.tData.setStartPassage(tData.getStartRow().getSelectionStartTextPassage());
		} else if (name == ParameterTypes.EndRow) {
			this.tData.setEndRow(tData.getTranscript().getRowDataModel().get(Integer.parseInt(value)));
		} else if (name == ParameterTypes.EndPoint) {
			this.tData.setEndPassage(tData.getEndRow().getSelectionEndTextPassage());
		}
		updateValue();
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