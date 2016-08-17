package lasad.gwt.client.ui.workspace.tableview;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;

public class ElementEditDialog extends Window {
	
	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private AbstractTableCell tableCell;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public ElementEditDialog(AbstractTableCell tableCell) {
		this.tableCell = tableCell;
		tableCell.restoreView(true);
		tableCell.setEditMode(true);
		
		setHeading("Element Information");
		setBorders(false);
		setBodyBorder(false);
		setPlain(true);
		setModal(true);
		setClosable(false);
		
		
		setLayout(new FitLayout());
		add(tableCell, new FitData(2));
		
		
//		Button saveButton = new Button("Save");
//		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//
//				TableCell tableCell = getTableCell();
//				
//				ArgumentMapTable table = tableCell.getTableCellInfo().getMap();
//				
//				table.getFocusHandler().releaseFocus(tableCell);
//			}
//		});
//		
//		addButton(saveButton);
		
		
		
		Button closeButton = new Button("Close");
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				AbstractTableCell tableCell = getTableCell();
				TableCellInfo cellInfo = tableCell.getTableCellInfo();
				FlexTable flexTable = cellInfo.getFlexTable();
				
				tableCell.restoreView(false);
				
				flexTable.setWidget(cellInfo.getRowNumber(), cellInfo.getColNumber(), tableCell);
				hide();
				tableCell.setEditMode(false);
				tableCell.setupToolButtons();
				
				//TextElement auto save
				cellInfo.getMap().getFocusHandler().releaseFocus(tableCell);
			}
		});
		
		addButton(closeButton);
		
	}

	
	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
	public AbstractTableCell getTableCell() {
		return tableCell;
	}

	
	public void setTableCell(AbstractTableCell tableCell) {
		this.tableCell = tableCell;
	}
	
	
}
