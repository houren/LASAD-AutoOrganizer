package lasad.gwt.client.ui.workspace.transcript;

import java.util.Vector;

import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class TranscriptRow extends BoxComponent {

    private Transcript transcript;

    private int lineNumber;
    private String text;

    // DOM Elements
    private Element textPassageContainer;
    private Element transcriptRowContainer;

    // Selection related Stuff
//    private int selectionStart = -1;
//    private int selectionEnd = -1;
    private TextPassage selectionStartTextPassage;
    private TextPassage selectionEndTextPassage;

    // TextPassages
    private Vector<TextPassage> textPassages = new Vector<TextPassage>();

    public TranscriptRow(Transcript transcript, int lineNumber, String text) {
	super();

	this.transcript = transcript;

	// Set some TranscriptRow related Stuff
	this.setLineNumber(lineNumber);
	this.setText(text);

	// Set some BoxComponent related Stuff
	this.setAutoHeight(true);
	this.setWidth("100%");

	// Create first fully TextPassage
	textPassages.add(new TextPassage(this, this.getText(), 0));
//	String[] array = this.getText().split(" ");
//	for (int i = 0; i < array.length; i++) {
//	    textPassages.add(new TextPassage(this, array[i] + " ", 0));
//	}

    }

    /**
     * Create the DOM-structure of the row element
     */
    @Override
    public void onRender(Element target, int index) {
	super.onRender(target, index);

	// Build tabletree
	Element lineGrid = DOM.createTable();
	this.transcriptRowContainer = lineGrid;

	Element lineTBody = DOM.createTBody();
	lineTBody.setClassName("trans-grid-tBody");
	DOM.appendChild(lineGrid, lineTBody);

	Element lineRow = DOM.createTR();
	lineRow.setClassName("trans-grid-lineRow");
	DOM.appendChild(lineTBody, lineRow);

	Element lineCell = DOM.createTD();
	lineCell.setClassName("trans-grid-lineCell");
	lineCell.setInnerText(String.valueOf(this.getLineNumber()));
	DOM.appendChild(lineRow, lineCell);

	Element textCell = DOM.createTD();
	textCell.setClassName("trans-grid-textCell");
	this.textPassageContainer = textCell;
	DOM.appendChild(lineRow, textCell);

	// Register some Events for the Elements
	DOM.sinkEvents(lineCell, Events.OnMouseDown.getEventCode() | Events.OnMouseUp.getEventCode());
	transcript.registerEventElement(lineCell, this);

	DOM.sinkEvents(textCell, Events.OnMouseDown.getEventCode() | Events.OnMouseUp.getEventCode());
	transcript.registerEventElement(textCell, this);

	DOM.sinkEvents(lineGrid, Events.OnMouseOut.getEventCode() | Events.OnMouseUp.getEventCode());
	transcript.registerEventElement(lineGrid, this);

	this.setElement(lineGrid, target, index);
    }
    
    @Override
    public void afterRender() {
	super.afterRender();

	// Add switching Row Style
	if (DOM.getChildIndex((Element) this.getElement().getParentElement(), this.getElement()) % 2 == 1) {
	    this.getElement().setClassName("trans-grid-1");
	} else {
	    this.getElement().setClassName("trans-grid-0");
	}

	// Render existing TextPassages
	renderTextPassages();
    }

    /**
     * Render all unrendered TextPassages
     */
    private void renderTextPassages() {
	int index = 0;
	for (TextPassage tp : textPassages) {
	    if (!tp.isRendered()) {
		tp.render(textPassageContainer, index);
	    }
	    index = DOM.getChildIndex(textPassageContainer, tp.getElement()) + 1;
	}
    }


    /**
     * Add a TranscriptLink to the Row
     * 
     * @param start
     *            StartPoint
     * @param end
     *            EndPoint
     * @param tData
     *            Related TranscriptLink
     */
    public void addTranscriptLink(TranscriptLinkData tData) {

	//Only one TextPassage because only Whole-Line-Selection is possible
	textPassages.get(0).addTranscriptLink(tData);
	
/**
 * The following block was needed in word-for-word text selection, but buggy	
 */
//	/**
//	 * StartPassage set, EndPassage set => Only one row
//	 * StartPassage set, EndPassage null => First row
//	 * StartPassage null, EndPassage set => Last row
//	 * StartPassage null, EndPassage null => Mid row (initial values)
//	 */
//	int startIndex = 0;
//	int endIndex = textPassages.size() - 1;
//	
//	if (tData.getStartRow().getLineNumber() == tData.getEndRow().getLineNumber()) {
//	    startIndex = textPassages.indexOf(tData.getStartPassage());
//	    endIndex = textPassages.indexOf(tData.getEndPassage());
//	}
//	else if (this.getLineNumber() == tData.getStartRow().getLineNumber()) {
//	    startIndex = textPassages.indexOf(tData.getStartPassage());
//	    endIndex = textPassages.size() - 1;
//	}
//	else if (this.getLineNumber() == tData.getEndRow().getLineNumber()) {
//	    startIndex = 0;
//	    endIndex = textPassages.indexOf(tData.getEndPassage());
//	}
//	
//	for (int i = startIndex; i <= endIndex; i++) {
////	    textPassages.get(i).addTranscriptLink(tData);
//	    textPassages.get(i).activateTranscriptLink(tData);
//	}
    }

    /*
     * GETTER & SETTER STUFF
     */

    
    
    /**
     * Returns the Row LineNumber
     * 
     * @return the LineNumber
     */
    public int getLineNumber() {
	return lineNumber;
    }


    /**
     * Set the Row LineNumber
     * 
     * @param lineNumber
     *            the LineNumber
     */
    public void setLineNumber(int lineNumber) {
	this.lineNumber = lineNumber;
    }

    public Transcript getTranscript() {
	return transcript;
    }
    
    public void setTranscript(Transcript transcript) {
	this.transcript = transcript;
    }

    /**
     * Return the SelectionStartTextPassage
     * 
     * @return
     */
    public TextPassage getSelectionStartTextPassage() {
	return selectionStartTextPassage;
    }

    /**
     * Set the SelectionStartTextPassage
     * 
     * @param selectionStartTextPassage
     */
    public void setSelectionStartTextPassage(TextPassage selectionStartTextPassage) {
	this.selectionStartTextPassage = selectionStartTextPassage;
    }

    /**
     * Return the Row Text
     * 
     * @return the Row Text
     */
    public String getText() {
	return text;
    }

    /**
     * Set the Row Text
     * 
     * @param text
     *            the Row Text
     */
    public void setText(String text) {
	this.text = text;
    }

    public Element getTextPassageContainer() {
	return textPassageContainer;
    }
    
    public void setTextPassageContainer(Element textPassageContainer) {
	this.textPassageContainer = textPassageContainer;
    }
    
    public Element getTranscriptRowContainer() {
	return transcriptRowContainer;
    }
    
    public void setTranscriptRowContainer(Element transcriptRowContainer) {
	this.transcriptRowContainer = transcriptRowContainer;
    }
    
    public TextPassage getSelectionEndTextPassage() {
	return selectionEndTextPassage;
    }
    
    public void setSelectionEndTextPassage(TextPassage selectionEndTextPassage) {
	this.selectionEndTextPassage = selectionEndTextPassage;
    }
    
    public Vector<TextPassage> getTextPassages() {
	return textPassages;
    }
    
    public void setTextPassages(Vector<TextPassage> textPassages) {
	this.textPassages = textPassages;
    }

    /*
     * EVENTS
     */


    public void handleEvent(DomEvent be) {
	if (be.getType() == Events.OnMouseDown) {
	    handleMouseDown(be);

	} else if (be.getType() == Events.OnMouseUp) {
	    handleMouseUp(be);

	} 
    }

    private void handleMouseDown(DomEvent be) {
	this.getTranscript().setSelectionMode(true);
	this.getTranscript().setSelectionStartRow(this);
	this.setSelectionStartTextPassage(this.getTextPassages().get(0));
    }

    private void handleMouseUp(DomEvent be) {
	if (this.getTranscript().isSelectionMode()) {
	    this.getTranscript().setSelectionMode(false);
	    if (this.getTranscript().getSelectionStartRow().getTextPassages().indexOf(this.getTranscript().getSelectionStartRow().getSelectionStartTextPassage()) < 0) {
		this.getTranscript().getSelectionStartRow().setSelectionStartTextPassage(this.getTranscript().getSelectionStartRow().getTextPassages().get(0));
	    }

	    if (this.getTranscript().getSelectionEndRow() == null) {
		this.getTranscript().setSelectionEndRow(this);
	    }

	    if (this.getTranscript().getSelectionEndRow().getTextPassages().indexOf(this.getTranscript().getSelectionEndRow().getSelectionEndTextPassage()) < 0) {
		this.getTranscript().getSelectionEndRow().setSelectionEndTextPassage(this.getTranscript().getSelectionEndRow().getTextPassages().get(this.getTranscript().getSelectionEndRow().getTextPassages().size() - 1));
	    }

	    this.getTranscript().onSelectArea();
	}
    }
}
