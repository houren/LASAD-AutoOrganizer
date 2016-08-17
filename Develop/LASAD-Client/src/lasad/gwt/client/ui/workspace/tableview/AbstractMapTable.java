package lasad.gwt.client.ui.workspace.tableview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackCluster;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractMapTable extends AbstractGraphMap{
	
//	protected LASADActionSender communicator = LASADActionSender.getInstance();
//	protected ActionFactory actionBuilder = ActionFactory.getInstance();
	
	protected MVCViewSession myViewSession;

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private FlexTable flexTable;
	
	public TableZoomEnum preferredZoom;
	public Size preferredSize;
	public static final Map<TableZoomEnum, Size> cellSize;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	static {
		
		int width = 215;
		int height = 200;
		
		cellSize = new HashMap<TableZoomEnum, Size>();
		TableZoomEnum[] zoomSizes = TableZoomEnum.values();
		for (int i = 0; i < zoomSizes.length; i++) {
			
			int w = width * (i + 1) / zoomSizes.length ;
			int h = height * (i + 1) / zoomSizes.length ;
			
			cellSize.put(zoomSizes[i], new Size(w, h));
		}
		
	}
	
	
	public AbstractMapTable(ArgumentMapSpace parentElement) {
		
		super(parentElement);
		
		setHeaderVisible(false);
		setBodyBorder(false);
		setBorders(false);
		setScrollMode(Scroll.AUTO);
		
		setLayout(new AbsoluteLayout());
		
		initMapDimensions();
		
		
		// init Table
		flexTable = new FlexTable();
		add(flexTable, new AbsoluteData(3, 3));
		
		sinkEvents(Event.ONDBLCLICK);
		addListener(Events.OnDoubleClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				getFocusHandler().releaseAllFocus();
			}
		});
		
		preferredZoom = TableZoomEnum.SIZE100;
		preferredSize = cellSize.get(preferredZoom);
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	private void createCorner() {
		
		LayoutContainer leftCornerCell = new LayoutContainer();
		leftCornerCell.setBorders(true);
		leftCornerCell.setSize(preferredSize.width, preferredSize.height);
		changeCornerStyle(leftCornerCell, preferredZoom);
		flexTable.setWidget(0, 0, leftCornerCell);
	}
	
	protected abstract AbstractTableCell getTableCell(ElementInfo info, TableCellTypeEnum type);
	
	public List<MVCViewRecipient> addElement(ElementInfo info) {
		
		List<MVCViewRecipient> cells = new ArrayList<MVCViewRecipient>();
		
		int rowCount = flexTable.getRowCount();
		int colCount = 0;
		
//		TableCell rowCell = new TableCell(info, TableCellTypeEnum.BOX);
//		TableCell colCell = new TableCell(info, TableCellTypeEnum.BOX);
		AbstractTableCell rowCell = getTableCell(info, TableCellTypeEnum.BOX);
		AbstractTableCell colCell = getTableCell(info, TableCellTypeEnum.BOX);
		rowCell.setDefaultSize(preferredZoom);
		colCell.setDefaultSize(preferredZoom);
		
		if (rowCount == 0) {

			createCorner();
			
			flexTable.setWidget(0, 1, rowCell);
			flexTable.setWidget(1, 0, colCell);
			
			rowCell.setTableCellInfo(new TableCellInfo(this, flexTable, 0, 1));
			colCell.setTableCellInfo(new TableCellInfo(this, flexTable, 1, 0));
			
		} else {
			
			colCount = flexTable.getCellCount(0);
			
			flexTable.setWidget(0, colCount, rowCell);
			flexTable.setWidget(colCount, 0, colCell);
			
			rowCell.setTableCellInfo(new TableCellInfo(this, flexTable, 0, colCount));
			colCell.setTableCellInfo(new TableCellInfo(this, flexTable, colCount, 0));
		}
		
		
		fillTableCell();
		layout();
		
		cells.add(rowCell);
		cells.add(colCell);
		
		return cells;
		
	}
	
	
	public void removeElement(AbstractTableCell tableTell) {
		
		int row = tableTell.getTableCellInfo().getRowNumber();
		int column = tableTell.getTableCellInfo().getColNumber();
		
		int maxIndex = flexTable.getRowCount() - 1;
		
		if ((row == maxIndex && column == 0) || (column == maxIndex && row == 0)) {
			
			flexTable.removeRow(maxIndex);
			for (int i = 0; i < maxIndex; i++) {
			
				flexTable.removeCell(i, maxIndex);
			}
			
		} else {
			
			int rowAndCol = row != 0? row: column;
			
			flexTable.removeRow(rowAndCol);
			for (int i = 0; i < flexTable.getRowCount(); i++) {
			
				flexTable.removeCell(i, rowAndCol);
			}
			
			
			for (int i = 0; i < flexTable.getRowCount(); i++) {
				
				for (int j = 0; j < flexTable.getRowCount(); j++) {
					
					Widget widget = flexTable.getWidget(i, j);
						
					if (widget instanceof CellContainer) {
						
						CellContainer cellContainer = (CellContainer)widget;
						TableCellInfo cellInfo = cellContainer.getTableCellInfo();
						
						int rowNumber = cellInfo.getRowNumber();
						int colNumber = cellInfo.getColNumber();
						
						if (rowNumber > rowAndCol) {
							rowNumber = rowNumber - 1;
						}
						
						if (colNumber > rowAndCol) {
							colNumber = colNumber - 1;
						}
						
						cellContainer.setTableCellInfo(new TableCellInfo(this, flexTable, rowNumber, colNumber));
						
					}
				}
			}
			
		}
		
		//clear table
		if (flexTable.getRowCount() == 1) {
			
			if (flexTable.getCellCount(0) == 1) {
			
				flexTable.removeRow(0);
			}
		}
		
	}
	
	
	public List<MVCViewRecipient> addRelation(ElementInfo info, List<TableCellInfo> cellInfos) {
		
		List<MVCViewRecipient> cells = new ArrayList<MVCViewRecipient>();
		
		int row = cellInfos.get(0).getRowNumber();
		int col = cellInfos.get(1).getColNumber();
		
		/*
		 * with direction man should set up only one Cell, otherweise should set up two Cells symmetric
		 */
		String direction = info.getElementOption(ParameterTypes.Endings);
		
		if (direction.equalsIgnoreCase("true")) {
			
//			TableCell tableCell = new TableCell(info, TableCellTypeEnum.RELEATION);
			AbstractTableCell tableCell = getTableCell(info, TableCellTypeEnum.RELEATION);
			
			tableCell.setDefaultSize(preferredZoom);
			tableCell.setTableCellInfo(new TableCellInfo(this, flexTable, row, col));
			flexTable.setWidget(row, col, tableCell);
			cells.add(tableCell);
			
			tableCell.setSession(myViewSession);
			
		} else {
			
//			TableCell tableCell = new TableCell(info, TableCellTypeEnum.RELEATION);
			AbstractTableCell tableCell = getTableCell(info, TableCellTypeEnum.RELEATION);
			tableCell.setDefaultSize(preferredZoom);
			tableCell.setTableCellInfo(new TableCellInfo(this, flexTable, row, col));
			flexTable.setWidget(row, col, tableCell);
			cells.add(tableCell);
			
//			TableCell tableCell2 = new TableCell(info, TableCellTypeEnum.RELEATION);
			AbstractTableCell tableCell2 = getTableCell(info, TableCellTypeEnum.RELEATION);
			tableCell2.setDefaultSize(preferredZoom);
			tableCell2.setTableCellInfo(new TableCellInfo(this, flexTable, col, row));
			flexTable.setWidget(col, row, tableCell2);
			cells.add(tableCell2);
			
			tableCell.setSession(myViewSession);
			tableCell2.setSession(myViewSession);
			
		}
		
		return cells;
	}
	
	
	
	public void removeRelation(AbstractTableCell relation) {
		
		int row = relation.getTableCellInfo().getRowNumber();
		int col = relation.getTableCellInfo().getColNumber();
		
		flexTable.remove(relation);
		
		BlankTableCell blankTableCell = new BlankTableCell(TableCellTypeEnum.BLANK);
		blankTableCell.setSize(preferredSize.width, preferredSize.height);
		blankTableCell.setTableCellInfo(new TableCellInfo(this, flexTable, row, col));
		flexTable.setWidget(row, col, blankTableCell);
		
		
	}
	
	
	public List<MVCViewRecipient> addRelatedRelation(ElementInfo info) {
		
		List<MVCViewRecipient> cells = new ArrayList<MVCViewRecipient>();
		
//		RelatedRelation relatedRelation = new RelatedRelation(info, TableCellTypeEnum.RELEATION);
		AbstractRelatedRelation relatedRelation =  getRelatedRelation(info, TableCellTypeEnum.RELEATION);
		relatedRelation.setTableCellInfo(new TableCellInfo(this, flexTable));
		relatedRelation.setDefaultSize(preferredZoom);
		cells.add(relatedRelation);
		
		relatedRelation.setSession(myViewSession);
		
		return cells;
	}
	
	protected abstract AbstractRelatedRelation getRelatedRelation(ElementInfo info, TableCellTypeEnum type);
	
	public void fillTableCell() {
		
		int rowCount = flexTable.getRowCount();
		int maxIndex = rowCount -1 ;
		
		for (int i = 1; i < rowCount -1 ; i ++) {
			
			BlankTableCell blankTableCell = new BlankTableCell(TableCellTypeEnum.BLANK);
			blankTableCell.setTableCellInfo(new TableCellInfo(this, flexTable, i, maxIndex));
			blankTableCell.setSize(preferredSize.width, preferredSize.height);
			flexTable.setWidget(i, maxIndex, blankTableCell);
			
			
			blankTableCell = new BlankTableCell(TableCellTypeEnum.BLANK);
			blankTableCell.setTableCellInfo(new TableCellInfo(this, flexTable, maxIndex, i));
			blankTableCell.setSize(preferredSize.width, preferredSize.height);
			flexTable.setWidget(maxIndex, i, blankTableCell);
			
		}
		
		if (maxIndex > 0) {
			
			BlankTableCell blankTableCell = new BlankTableCell(TableCellTypeEnum.BLANK);
			blankTableCell.setTableCellInfo(new TableCellInfo(this, flexTable, maxIndex, maxIndex));
			blankTableCell.setSize(preferredSize.width, preferredSize.height);
			flexTable.setWidget(maxIndex, maxIndex, blankTableCell);
			
		}
		
	}
	
	
	@Override
	public Size getMapDimensionSize(){
		return new Size(mapDimensions.get(Direction.LEFT)+mapDimensions.get(Direction.RIGHT),mapDimensions.get(Direction.UP)+mapDimensions.get(Direction.DOWN));
	}

	
	private void initMapDimensions(){
		mapDimensions.put(Direction.UP, 2500);
		mapDimensions.put(Direction.RIGHT, 2500);
		mapDimensions.put(Direction.DOWN, 2500);
		mapDimensions.put(Direction.LEFT, 2500);
//		scrollPanel.setSize(getMapDimensionSize().width,getMapDimensionSize().height);		
	}
	
	
	public void resize(TableZoomEnum zoom) {
		
		preferredZoom = zoom;
		preferredSize = cellSize.get(zoom);
		
		
		for (int i = 0 ; i < flexTable.getRowCount(); i++) {
			
			for (int j = 0; j < flexTable.getCellCount(i); j++) {
				Widget widget = flexTable.getWidget(i, j);
				
				if (widget instanceof CellContainer) {
					
					CellContainer cellContainer = (CellContainer)widget;
					cellContainer.changeView(zoom);
					
				} else {
				
					changeCornerStyle(widget, zoom);
				}
				
			}
			
			//force to refresh the size of every Cell
			for (int j = 0; j < flexTable.getCellCount(i); j++) {
				Widget widget = flexTable.getWidget(i, j);
				
				widget.setWidth(String.valueOf(preferredSize.width));
				widget.setHeight(String.valueOf(preferredSize.height));
			}
		}
		
	}
	
	
	private void changeCornerStyle(Widget widget, TableZoomEnum zoom) {
		
		if (zoom == TableZoomEnum.SIZE100) {
			
			widget.setStyleName("tableCorner-zoom100-img");
		} 
		
		if (zoom == TableZoomEnum.SIZE75) {
			
			widget.setStyleName("tableCorner-zoom75-img");
		} 
		
		if (zoom == TableZoomEnum.SIZE50) {
			
			widget.setStyleName("tableCorner-zoom50-img");
		} 
		
		if (zoom == TableZoomEnum.SIZE25) {
			
			widget.setStyleName("tableCorner-zoom25-img");
		} 
	}
	
	
	public void enableCursorTracking() {
		LASADInfo.display("enableCursorTracking", "enableCursorTracking");
		
		Listener<ComponentEvent> listener = new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				// This seams to be buggy. Even if one does not move the mouse there is a OnMouseMove event. Thus we need to calculate of there is a position change...
					
				if(myAwarenessCursorID != -1 && LASAD_Client.getInstance().isAuthed()) {
						
//					int newMouseX = (be.getClientX() - getAbsoluteLeft() + getHScrollPosition());
//					int newMouseY = (be.getClientY() - getAbsoluteTop() + getVScrollPosition());
					
					int newMouseX = (be.getClientX() - getAbsoluteLeft());
					int newMouseY = (be.getClientY() - getAbsoluteTop());
					
					if (preferredZoom == TableZoomEnum.SIZE75) {
						
						newMouseX = newMouseX * 4 / 3;
						newMouseY = newMouseY *4 / 3;
					}
					
					if (preferredZoom == TableZoomEnum.SIZE50) {
						
						newMouseX = newMouseX * 2;
						newMouseY = newMouseY * 2;
					}
					
					if (preferredZoom == TableZoomEnum.SIZE25) {
						
						newMouseX = newMouseX * 4 ;
						newMouseY = newMouseY *4 ;
					}
					
					int totalChange = Math.abs((oldMouseX-newMouseX)) + Math.abs((oldMouseY-newMouseY));
						
					if(refreshMyCursor && !disableNormalCursorUpdates) {
					
						// If the number is too low there will be to many messages.
						if (totalChange > 20) {
//							communicator.sendActionPackage(actionBuilder.updateMyCursorPositionNonPersistent(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY));
							sendUpdateMyCursorPositionNonPersistentToServer(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY);
							oldMouseX = newMouseX;
							oldMouseY = newMouseY;
							refreshMyCursor = false;
						}
						
					} else if(totalChange > 20 && refreshMyCursorPersistent) {			
//							communicator.sendActionPackage(actionBuilder.updateMyCursorPositionPersistent(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY));
							sendUpdateMyCursorPositionPersistentToServer(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY);
							oldMouseX = newMouseX;
							oldMouseY = newMouseY;
							refreshMyCursorPersistent = false;
							numberOfCursorRecords++;
					}
				}

			}
		};
		
		
		sinkEvents(Event.ONMOUSEMOVE);
		addListener(Events.OnMouseMove, listener);
			
		// Timer to allow cursor update
		Timer t = new Timer() {
			public void run() {
				refreshMyCursor = true;
			}
		};
		
		t.scheduleRepeating(750);
	}

	protected abstract void sendUpdateMyCursorPositionPersistentToServer(String mapID, int cursorID, String username, int x, int y);
	protected abstract void sendUpdateMyCursorPositionNonPersistentToServer(String mapID, int cursorID, String username, int x, int y);
	protected abstract void sendRemoveElementToServer(String mapID, int elementID);
	
	//*************************************************************************************
	// getter & setter
	//*************************************************************************************
	
	public FlexTable getFlexTable() {
		return flexTable;
	}


	public void setFlexTable(FlexTable flexTable) {
		this.flexTable = flexTable;
	}


	/**
	 * @return the preferredZoom
	 */
	public TableZoomEnum getPreferredZoom() {
		return preferredZoom;
	}


	/**
	 * @param preferredZoom the preferredZoom to set
	 */
	public void setPreferredZoom(TableZoomEnum preferredZoom) {
		this.preferredZoom = preferredZoom;
	}


	@Override
	protected void init() {
		this.myViewSession = (MVCViewSession)myArgumentMapSpace.getSession();
		ID = myViewSession.getController().getMapInfo().getMapID();
	}


	@Override
	public void deleteAllFeedbackClusters() {
		MVController controller = LASAD_Client.getMVCController(this.ID);
		if (controller != null) {
			List<Component> mapComponents = this.getItems();

			for (Component c : mapComponents) {
				if (c instanceof AbstractFeedbackCluster) {
					AbstractFeedbackCluster fc = (AbstractFeedbackCluster) c;

//					communicator.sendActionPackage(actionBuilder.removeElement(this.ID, fc.getID()));
					sendRemoveElementToServer(this.ID, fc.getID());
				}
			}
		}
	}


	@Override
	public AbstractMVCViewSession getMyViewSession() {
		return myViewSession;
	}


	@Override
	public void setMyViewSession(AbstractMVCViewSession myViewSession) {
		this.myViewSession = (MVCViewSession) myViewSession;
	}
	
}