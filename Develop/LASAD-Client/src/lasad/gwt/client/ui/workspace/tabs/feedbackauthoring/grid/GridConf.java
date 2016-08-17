package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;

/**
 * This class is used to configure a {@link CustomizedGrid}
 * @author Anahuac
 *
 */
public class GridConf {
	
	private String Header; //header label
	private boolean markReadyButtonFlag; //enables mark ready button
	private int markReadyStatus;
	private boolean viewButtonFlag; //enables view button
	private boolean editButtonFlag; //enables edit button
	private boolean deleteButtonFlag;//enables delete button
	private boolean duplicateButtonFlag;//enables duplicate button
	private boolean addButtonFlag; //if == true, then create a tool bar with an add button having as label addButtonLabel 
	private String commonLabel; //label to be displayed in the tooltip of the buttons
	private boolean enableSelection; //enable the selection of a grid's element.
	private String autoExpandColumn = GridElementLabel.NAME; //column to be expanded to cover all the remaining space size
	private boolean hideHeaders = false; //hides the column headers
	private int width = 0; //width of the grid
	private int height = 0; //height of the grid
	
	private Vector<ColumnConf> colConfigList = new Vector<ColumnConf>();
	
	public String getHeader() {
		return Header;
	}
	/**
	 * grid header label
	 * @param header
	 */
	public void setHeader(String header) {
		Header = header;
	}
	public boolean isMarkReadyButtonFlag() {
		return markReadyButtonFlag;
	}
	/**
	 * enables mark ready button
	 * @param markReadyButtonFlag
	 */
	public void setMarkReadyButtonFlag(boolean markReadyButtonFlag) {
		this.markReadyButtonFlag = markReadyButtonFlag;
	}
	public int getMarkReadyStatus() {
		return markReadyStatus;
	}
	public void setMarkReadyStatus(int markReadyStatus) {
		this.markReadyStatus = markReadyStatus;
	}
	public boolean isViewButtonFlag() {
		return viewButtonFlag;
	}
	/**
	 * enables view button
	 * @param viewButtonFlag
	 */
	public void setViewButtonFlag(boolean viewButtonFlag) {
		this.viewButtonFlag = viewButtonFlag;
	}
	public boolean isEditButtonFlag() {
		return editButtonFlag;
	}
	/**
	 * enables edit button
	 * @param editButtonFlag
	 */
	public void setEditButtonFlag(boolean editButtonFlag) {
		this.editButtonFlag = editButtonFlag;
	}
	public boolean isDeleteButtonFlag() {
		return deleteButtonFlag;
	}
	/**
	 * enables delete button
	 * @param deleteButtonFlag
	 */
	public void setDeleteButtonFlag(boolean deleteButtonFlag) {
		this.deleteButtonFlag = deleteButtonFlag;
	}
	public boolean isDuplicateButtonFlag() {
		return duplicateButtonFlag;
	}
	/**
	 * enables duplicate button
	 * @param duplicateButtonFlag
	 */
	public void setDuplicateButtonFlag(boolean duplicateButtonFlag) {
		this.duplicateButtonFlag = duplicateButtonFlag;
	}
	public boolean isAddButtonFlag() {
		return addButtonFlag;
	}
	/**
	 * if == true, then create a tool bar with an add button having as label addButtonLabel
	 * @param addButtonFlag
	 */
	public void setAddButtonFlag(boolean addButtonFlag) {
		this.addButtonFlag = addButtonFlag;
	}
	public String getCommonLabel() {
		return commonLabel;
	}
	/**
	 * label to be displayed in the tooltip of the buttons
	 * @param commonLabel
	 */
	public void setCommonLabel(String commonLabel) {
		this.commonLabel = commonLabel;
	}
	
	public Vector<ColumnConf> getColConfigList() {
		return colConfigList;
	}
	public void setColConfigList(Vector<ColumnConf> colConfigList) {
		this.colConfigList = colConfigList;
	}
	public void addColConfig(ColumnConf colConfig) {
		if(colConfig != null)
			this.colConfigList.add(colConfig);
	}
	public boolean isEnableSelection() {
		return enableSelection;
	}
	/**
	 * enable the selection of a grid's element.
	 * @param enableSelection
	 */
	public void setEnableSelection(boolean enableSelection) {
		this.enableSelection = enableSelection;
	}
	public String getAutoExpandColumn() {
		return autoExpandColumn;
	}
	/**
	 * column to be expanded to cover all the remaining space size
	 * @param autoExpandColumn
	 */
	public void setAutoExpandColumn(String autoExpandColumn) {
		this.autoExpandColumn = autoExpandColumn;
	}
	public boolean isHideHeaders() {
		return hideHeaders;
	}
	/**
	 * hides the column headers
	 * @param hideHeaders
	 */
	public void setHideHeaders(boolean hideHeaders) {
		this.hideHeaders = hideHeaders;
	}
	public int getWidth() {
		return width;
	}
	/**
	 * sets width of the grid
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	/**
	 * sets height of the grid
	 * @param height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	

}
