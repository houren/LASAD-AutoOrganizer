package lasad.gwt.client.ui.workspace.transcript;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import rocket.selection.client.Selection;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;



public class Transcript extends ContentPanel implements MVCViewRecipient {

	AbstractGraphMap myMap;

	// i18N
	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	// Model
	Map<Integer, TranscriptRow> rowDataModel = new HashMap<Integer, TranscriptRow>();

	Map<Integer, TranscriptLinkData> transcriptLinkDataModel = new HashMap<Integer, TranscriptLinkData>();

	public Map<Element, Component> componentDataModel = new HashMap<Element, Component>();

	// Selection related Stuff
	private boolean selectionMode = false;

	private TranscriptRow selectionStartRow;
	private TranscriptRow selectionEndRow;

	// Active Selected Element
	private TranscriptLinkData activePart = null;

	// Marked Area
	private TranscriptLinkData markedPart = null;

	private Listener<DomEvent> domEventListener = null;

	public Transcript(AbstractGraphMap map) {
		this.myMap = map;

		this.setBodyBorder(false);
		this.setScrollMode(Scroll.AUTOY);
		this.setHeading(myConstants.Transcript());
		this.setLayout(new FlowLayout());

		createEventListener();

		registerEventElement(this.getElement(), this);
	}

	public void addTranscriptRow(int rowID, String text) {
		this.add(new TranscriptRow(this, rowID, text));
	}

	public void setTranscriptRows(LinkedHashMap<Integer, String> lines) {
		for (int number : lines.keySet()) {
			addTranscriptRow(number, lines.get(number));
		}
		layout();
	}

	private void add(TranscriptRow row) {
		super.add(row); // Add the row to the ContentPanel and shows them up

		rowDataModel.put(row.getLineNumber(), row); // Append the new Transcript-Line to the intern rowDataModel
	}

	/**
	 * Fires up, if an Textarea was selected
	 */
	public void onSelectArea() {

		// Unselect active part
		setActiveTranscriptLink(null);

		resetSelection();

		// Handle end-to-beginning text selection over multiple TranscriptRows
		if (this.getSelectionStartRow().getLineNumber() > this.getSelectionEndRow().getLineNumber()) {
			TextPassage tempPassage = this.getSelectionStartRow().getSelectionStartTextPassage();
			this.getSelectionStartRow().setSelectionStartTextPassage(this.getSelectionStartRow().getSelectionEndTextPassage());
			this.getSelectionStartRow().setSelectionEndTextPassage(tempPassage);

			tempPassage = this.getSelectionEndRow().getSelectionStartTextPassage();
			this.getSelectionEndRow().setSelectionStartTextPassage(this.getSelectionEndRow().getSelectionEndTextPassage());
			this.getSelectionEndRow().setSelectionEndTextPassage(tempPassage);
			TranscriptRow tempRow = this.getSelectionStartRow();
			this.setSelectionStartRow(this.getSelectionEndRow());
			this.setSelectionEndRow(tempRow);
		}

		// Create new LinkData
		TranscriptLinkData tData = new TranscriptLinkData(this, this.getSelectionStartRow().getLineNumber(), this.getSelectionStartRow().getTextPassages().indexOf(this.getSelectionStartRow().getSelectionStartTextPassage()), this.getSelectionEndRow().getLineNumber(), this.getSelectionEndRow().getTextPassages().indexOf(this.getSelectionEndRow().getSelectionEndTextPassage()));

		tData.setStyle("trans-highlight-selection"); // Set the style information
		tData.setSelected(true);

		this.addTranscriptLink(tData); // Adds the new Area to the Transcript

		this.markedPart = tData; // Set new marked Area

		this.resetNativeSelection();

	}

	/**
	 * Reset some Selection related stuff
	 */
	public void resetNativeSelection() {

		// Reset some Selection related stuff
		Selection.clearAnySelectedText(); // Removes the native text selection area
		if (this.selectionStartRow != null) {
			this.selectionStartRow.setSelectionStartTextPassage(null);
		}
		if (this.selectionEndRow != null) {
			this.selectionEndRow.setSelectionEndTextPassage(null);
		}
		this.selectionStartRow = null;
		this.selectionEndRow = null;
	}

	public void resetSelection() {
		// Reset selected Area
		if (markedPart != null) {
			removeTranscriptLink(this.markedPart);
		}
	}

	/**
	 * Removes an existing Transcript Link
	 * 
	 * @param tData
	 *            the TranscriptLink to remove
	 */
	public void removeTranscriptLink(TranscriptLinkData tData) {
		if (tData != null) {
			TextPassage tp = null;
			do {
				if (!tData.getTextPassages().isEmpty()) {
					tp = tData.getTextPassages().firstElement();
				} else {
					break;
				}
				tp.removeTranscriptLink(tData);
			} while (true);

			transcriptLinkDataModel.remove(tData.getElementID());

			tData = null;
		}
	}

	/**
	 * Adds a new TranscriptLink to the Transcript
	 * 
	 * @param tData
	 *            the TranscriptLink to add
	 */
	public void addTranscriptLink(TranscriptLinkData tData) {
		// Only one Link per Element
		if (transcriptLinkDataModel.containsKey(tData.getElementID())) {
			removeTranscriptLink(transcriptLinkDataModel.get(tData.getElementID()));
		}

		// Add TranscriptLinkData to the TextPassages
		for (int i = tData.getStartRow().getLineNumber(); i <= tData.getEndRow().getLineNumber(); i++) {
			this.getRowDataModel().get(i).addTranscriptLink(tData);
		}
		transcriptLinkDataModel.put(tData.getElementID(), tData);

		// And activates the Link
		setActiveTranscriptLink(tData);
	}

	/**
	 * Activate a TranscriptLink
	 * 
	 * @param tData
	 *            the TranscriptLink to activate
	 */
	public void setActiveTranscriptLink(TranscriptLinkData tData) {
		// Unselect active Element
		if (activePart != null) {
			activePart.setActive(false);

			for (TextPassage tp : activePart.getTextPassages()) {
				tp.updateActiveTranscriptStyle();
			}
		}
		// Select new active Element
		activePart = tData;
		if (activePart != null) {
			activePart.setActive(true);

			for (TextPassage tp : activePart.getTextPassages()) {
				tp.activateTranscriptLink(activePart);
			}
		}
	}

	/**
	 * Returns the Text of an TextArea within the Transcript
	 * 
	 * @param sData
	 *            the Area
	 * @return the corresponding Text
	 */
	public String getTextPart(SelectionData sData) {
		String text = "";

		for (int i = sData.getStartRow(); i <= sData.getEndRow(); i++) {
			boolean endRow = false;

			int start = 0;
			int end = rowDataModel.get(i).getText().length();

			if (i == sData.getStartRow()) {
				start = sData.getStartPoint();
			}
			if (i == sData.getEndRow()) {
				end = sData.getEndPoint();
				endRow = true;
			}

			text = text + rowDataModel.get(i).getText().substring(start, end);

			if (!endRow) {
				text = text + "\n";
			}
		}

		return text;
	}

	/**
	 * Returns the Text of an TextArea within the Transcript
	 * 
	 * @param sR
	 *            StartRow
	 * @param sP
	 *            StartPoint
	 * @param eR
	 *            EndRow
	 * @param eP
	 *            EndPoint
	 * @return the corresponding Text
	 */
	public String getTextPart(int sR, int sP, int eR, int eP) {
		return getTextPart(new SelectionData(sR, sP, eR, eP));
	}

	/**
	 * Fires up, when the user clicks on a Textpassage
	 * 
	 * @param source
	 * @param be
	 */
	protected void onTextPassageClick(TextPassage source, DomEvent be) {
		if (source.getActiveTData() == markedPart) {
			// Click on Marked Area

		} else {
			// Click on another area

			removeTranscriptLink(Transcript.this.markedPart); // Remove possible
			// selected Area
			// Selection.clearAnySelectedText(); // Remove possible Native Text
			// Selection
			this.resetNativeSelection();

			// Activate Area and/or switch the active TranscriptLink
			TranscriptLinkData activeLink = source.getActiveTData();
			if (source.getActiveTData().isActive()) {
				activeLink = source.switchToNextTLD(); // Switch active
				// TranscriptLink
			}
			setActiveTranscriptLink(activeLink);
		}
	}

	public void highlightElementsTranscriptLink(int elementID) {
		if (transcriptLinkDataModel.containsKey(elementID)) {

			// TranscriptLink exists
			setActiveTranscriptLink(transcriptLinkDataModel.get(elementID));
		}

		// Scroll Link into View
		if (activePart != null)
			scrollIntoView((Widget) rowDataModel.get(activePart.getStartRow().getLineNumber()));

	}

	public void removeElemensTranscriptLink(int elementID) {
		if (transcriptLinkDataModel.containsKey(elementID)) {
			// TranscriptLink exists
			removeTranscriptLink(transcriptLinkDataModel.get(elementID));
		}

	}

	public TranscriptLinkData getElemensTranscriptLink(int elementID) {
		if (transcriptLinkDataModel.containsKey(elementID)) {
			// TranscriptLink exists
			return transcriptLinkDataModel.get(elementID);
		} else {
			return null;
		}
	}

	protected void onResize(int width, int height) {
		super.onResize(width, height);

		for (TranscriptRow row : rowDataModel.values()) { // A little hack to
			// prevent the
			// TranscriptRows
			// from hiding by
			// the scrollbars
			row.setWidth(width - 17);
		}
	}

	private void createEventListener() {

		domEventListener = new Listener<DomEvent>() {

			public void handleEvent(DomEvent be) {
				Element target = DOM.eventGetTarget(be.getEvent());
				Component source = getEventComponent(target);

				if (source instanceof TextPassage) {
					((TextPassage) source).handleEvent(be);
				} else if (source instanceof TranscriptRow) {
					((TranscriptRow) source).handleEvent(be);

				} else if (source instanceof Transcript) {
					// ((TextPassage) source).handleEvent(be);
				}

			}
		};

		// Register all possible Events, also Events which are fired from the
		// TranscriptRow and TextPassage
		this.addListener(Events.OnMouseDown, domEventListener);
		this.addListener(Events.OnMouseUp, domEventListener);
		this.addListener(Events.OnMouseOut, domEventListener);
		this.addListener(Events.OnMouseOver, domEventListener);
		this.addListener(Events.OnMouseMove, domEventListener);
		this.addListener(Events.OnContextMenu, domEventListener);

		this.sinkEvents(Events.OnMouseDown.getEventCode());
		this.sinkEvents(Events.OnMouseUp.getEventCode());
		this.sinkEvents(Events.OnMouseOut.getEventCode());
		this.sinkEvents(Events.OnMouseOver.getEventCode());
		this.sinkEvents(Events.OnMouseMove.getEventCode());
		this.sinkEvents(Events.OnContextMenu.getEventCode());

	}

	/**
	 * Register an Element to calculate later the ComponentSource of an event
	 * 
	 * @param el
	 *            the DOM Element
	 * @param source
	 *            the SourceComponent
	 */
	protected void registerEventElement(Element el, Component source) {
		componentDataModel.put(el, source);
	}

	/**
	 * Return the Source Component of an element
	 * 
	 * @param el
	 *            the element
	 * @return
	 */
	private Component getEventComponent(Element el) {
		return componentDataModel.get(el);
	}

	TranscriptLinkData changedTData;

	// Needed for performance issues
	private boolean startRowSet, endRowSet  = false;
	

	//Note: 
	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {
		// Updates all corresponding TranscriptLink / parent Elements
		for (AbstractUnspecifiedElementModel parent : model.getParents()) {
			changedTData = getElemensTranscriptLink(parent.getId());
			if (changedTData != null) {
				if (vname.equals("STARTROW")) {
					changedTData.setStartRow(rowDataModel.get(Integer.parseInt(model.getValue(ParameterTypes.StartRow))));
					startRowSet = true;
				} else if (vname.equals("ENDROW")) {
					changedTData.setEndRow(rowDataModel.get(Integer.parseInt(model.getValue(ParameterTypes.EndRow))));
					endRowSet = true;
				} else if (vname.equals("COLOR")) {
					changedTData.setColor(model.getValue(ParameterTypes.Color));
					addTranscriptLink(changedTData);
				}
				// And activates the Link
				if ((startRowSet && endRowSet) || (vname == ParameterTypes.UserActionId)) {
					addTranscriptLink(changedTData);
					startRowSet = endRowSet = false;
				}
			}
		}
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		// Delete the corresponding TranscriptLink
		this.removeTranscriptLink(this.getElemensTranscriptLink(model2ElementIDMapping.get(model)));
		model2ElementIDMapping.remove(model);
	}

	private Map<AbstractUnspecifiedElementModel, Integer> model2ElementIDMapping = new HashMap<AbstractUnspecifiedElementModel, Integer>();

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		// Create Transcript Link for Parents with this modeldata
		model2ElementIDMapping.put(model, model.getParents().firstElement().getId());
		return true;
	}

	@Override
	public AbstractUnspecifiedElementModel getConnectedModel() {
		return null;
	}

	public boolean isSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(boolean selectionMode) {
		this.selectionMode = selectionMode;
	}

	public Map<Integer, TranscriptRow> getRowDataModel() {
		return rowDataModel;
	}

	public void setRowDataModel(Map<Integer, TranscriptRow> rowDataModel) {
		this.rowDataModel = rowDataModel;
	}

	public Map<Integer, TranscriptLinkData> getTranscriptLinkDataModel() {
		return transcriptLinkDataModel;
	}

	public void setTranscriptLinkDataModel(Map<Integer, TranscriptLinkData> transcriptLinkDataModel) {
		this.transcriptLinkDataModel = transcriptLinkDataModel;
	}

	public TranscriptRow getSelectionStartRow() {
		return selectionStartRow;
	}

	public void setSelectionStartRow(TranscriptRow selectionStartRow) {
		this.selectionStartRow = selectionStartRow;
	}

	public TranscriptRow getSelectionEndRow() {
		return selectionEndRow;
	}

	public void setSelectionEndRow(TranscriptRow selectionEndRow) {
		this.selectionEndRow = selectionEndRow;
	}

	public TranscriptLinkData getActivePart() {
		return activePart;
	}

	public void setActivePart(TranscriptLinkData activePart) {
		this.activePart = activePart;
	}

	public TranscriptLinkData getMarkedPart() {
		return markedPart;
	}

	public void setMarkedPart(TranscriptLinkData markedPart) {
		this.markedPart = markedPart;
	}

	public Map<AbstractUnspecifiedElementModel, Integer> getModel2ElementIDMapping() {
		return model2ElementIDMapping;
	}

	public void setModel2ElementIDMapping(Map<AbstractUnspecifiedElementModel, Integer> model2ElementIDMapping) {
		this.model2ElementIDMapping = model2ElementIDMapping;
	}
}