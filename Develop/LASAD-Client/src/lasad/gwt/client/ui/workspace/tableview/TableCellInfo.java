package lasad.gwt.client.ui.workspace.tableview;

import com.google.gwt.user.client.ui.FlexTable;

public class TableCellInfo {

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private AbstractMapTable map;
	private FlexTable flexTable;
	
	private int rowNumber;
	private int colNumber;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************

	public TableCellInfo(AbstractMapTable map, FlexTable flexTable) {
		this.map = map;
		this.flexTable = flexTable;
	}


	public TableCellInfo(AbstractMapTable abstractMapTable, FlexTable flexTable, int rowNumber, int colNumber) {
		this.map = abstractMapTable;
		this.flexTable = flexTable;
		this.rowNumber = rowNumber;
		this.colNumber = colNumber;
	}

	
	//*************************************************************************************
	// getter & setter
	//*************************************************************************************
	
	public AbstractMapTable getMap() {
		return map;
	}

	public void setMap(AbstractMapTable map) {
		this.map = map;
	}

	public FlexTable getFlexTable() {
		return flexTable;
	}

	public void setFlexTable(FlexTable flexTable) {
		this.flexTable = flexTable;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public int getColNumber() {
		return colNumber;
	}

	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}
	
}
