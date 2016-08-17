package lasad.gwt.client.ui.workspace.transcript;

import java.util.Vector;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;

public class TranscriptLinkData {

	private Transcript transcript = null;
	private int elementID = 0;
	private AbstractUnspecifiedElementModel model = null;

	private String style = "";
	private String bgColor = "";
	private String borderColor = "";

	private boolean active = false;
	private boolean selected = false;

	private TranscriptRow startRow;
	private TranscriptRow endRow;
	private TextPassage startPassage;
	private TextPassage endPassage;

	private Vector<TextPassage> textPassages = new Vector<TextPassage>();

	public TranscriptLinkData(TranscriptLinkData tData) {
		this(tData.getTranscript(), tData.getStartRow().getLineNumber(), tData.getStartRow().getTextPassages().indexOf(tData.getStartPassage()), tData.getEndRow().getLineNumber(), tData.getEndRow().getTextPassages().indexOf(tData.getEndPassage()));

		// Clone Values
		this.setActive(tData.isActive());
		this.setColor(tData.getBgColor());
		this.setSelected(tData.isSelected());
		this.setStyle(tData.getStyle());
		this.setElementID(tData.getElementID());
		this.setModel(tData.getModel());

	}

	public TranscriptLinkData(Transcript trans, int startRow, int startTextPassageIndex, int endRow, int endTextPassageIndex) {

		// Handle end-to-beginning text selection
		if (startRow == endRow && startTextPassageIndex > endTextPassageIndex) {
			int temp = startTextPassageIndex;
			startTextPassageIndex = endTextPassageIndex;
			endTextPassageIndex = temp;
		}

		if (trans != null) {
			this.startRow = trans.getRowDataModel().get(startRow);
			this.endRow = trans.getRowDataModel().get(endRow);
			this.startPassage = trans.getRowDataModel().get(startRow).getTextPassages().get(startTextPassageIndex);
			this.endPassage = trans.getRowDataModel().get(endRow).getTextPassages().get(endTextPassageIndex);
			this.transcript = trans;
		}

		// Required for authoring tool
		else {
			this.startRow = new TranscriptRow(null, 1, "");
			this.endRow = new TranscriptRow(null, 1, "");
			this.startPassage = new TextPassage(new TranscriptRow(null, 1, ""), "", 0);
			this.endPassage = new TextPassage(new TranscriptRow(null, 1, ""), "", 0);
			this.transcript = null;
		}

	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public Vector<TextPassage> getTextPassages() {
		return textPassages;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getBgColor() {
		return bgColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setColor(String color) {
		this.bgColor = color;
		this.borderColor = color;
	}

	public int getElementID() {
		return elementID;
	}

	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

	public Transcript getTranscript() {
		return transcript;
	}

	public AbstractUnspecifiedElementModel getModel() {
		return model;
	}

	public void setModel(AbstractUnspecifiedElementModel model) {
		this.model = model;
	}

	public TranscriptRow getStartRow() {
		return startRow;
	}

	public void setStartRow(TranscriptRow startRow) {
		this.startRow = startRow;
	}

	public TranscriptRow getEndRow() {
		return endRow;
	}

	public void setEndRow(TranscriptRow endRow) {
		this.endRow = endRow;
	}

	public TextPassage getStartPassage() {
		return startPassage;
	}

	public void setStartPassage(TextPassage startPassage) {
		this.startPassage = startPassage;
	}

	public TextPassage getEndPassage() {
		return endPassage;
	}

	public void setEndPassage(TextPassage endPassage) {
		this.endPassage = endPassage;
	}
}
