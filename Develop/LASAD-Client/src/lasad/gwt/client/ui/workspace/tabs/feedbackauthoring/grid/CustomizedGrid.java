package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.grid;

import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.ElementModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.data.GridElementLabel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATConstants;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

/**
 * Creates a customized grid based on the {@link GridConf} provided
 * @author Anahuac
 *
 */
public abstract class CustomizedGrid extends ContentPanel{
	
	public static final int INVALID_TYPE = 0;
	public static final int AGENT_TYPE = 1;
	public static final int PATTERN_TYPE = 2;
	public static final int MESSAGE_TYPE = 3;
	public static final int STRATEGIE_TYPE = 4;
	
	private GridConf gridConf;
	public int status = 0;
	
	private ListStore<ElementModel> gridStore = new ListStore<ElementModel>();
	Grid<ElementModel> elementsGrid;
	
	public CustomizedGrid(GridConf gridConf) {
//		super(new FitLayout());
		this.setHeaderVisible(false);
	    this.setBodyBorder(false);
	    if(gridConf.getWidth() > 0){
			setWidth(gridConf.getWidth());
	    }else {
	    	this.setLayout(new FitLayout());
	    }
		this.gridConf = gridConf;
	}
	
	@Override  
	  protected void onRender(Element parent, int index) {  
	    super.onRender(parent, index);
//	    this.setHeaderVisible(false);
//	    this.setBodyBorder(false);
		
	    add(createGridPanel());
		layout();
	    
	}
	
	/**
	 * method called on markReady action
	 * @param id
	 */
	abstract void markReadyElement(String id);
	/**
	 * method called on view action
	 * @param id
	 */
	abstract void viewElement(String id);
	/**
	 * method called on edit action
	 * @param id
	 */
	abstract void editElement(String id);
	/**
	 * method called on delete action
	 * @param id
	 */
	abstract void deleteElement(String id, String name);
	/**
	 * method called on duplicate action
	 * @param id
	 */
	abstract void duplicateElement(String id);
	/**
	 * method called on add action
	 * @param id
	 */
	abstract void addElement();
	/**
	 * Used to define a hardcoded elements in the grid for testing purposes
	 */
	abstract void populateGridForTesting();
	/**
	 * Used to set the initial elements in the grid 
	 */
	abstract void populateGrid();
	/**
	 * Method to handle selection of the row elements
	 * @param selectedRow
	 */
	abstract void selectedRowEvent(ElementModel selectedRow);
	/**
	 * Removes all the elements from the grid
	 */
	public void resetGrid(){
		gridStore.removeAll();
	}
	/**
	 * Gets a reference to the Grid store
	 * @return
	 */
	public ListStore<ElementModel> getGridStore(){
		return gridStore;
	}
	/**
	 * Calls the GridView().refresh() method
	 */
	public void refreshView(){
		if(elementsGrid.isRendered())
			elementsGrid.getView().refresh(false);
	}
	
	/**
	 * Builds the grid 
	 * @return {@link ContentPanel}
	 */
	private ContentPanel createGridPanel(){
		ContentPanel panel = new ContentPanel();
		panel.setHeading(gridConf.getHeader());
		panel.setScrollMode(Scroll.AUTOY);
		panel.setStyleAttribute("color", "#000000");
		if(gridConf.getWidth() > 0){
			panel.setWidth(gridConf.getWidth());
	    }
		if(gridConf.getHeight() > 0){
			panel.setHeight(gridConf.getHeight());
	    }
		panel.setLayout(new FitLayout());
		
		if (gridConf.isAddButtonFlag()){
			Button buttonAdd = new Button(FeedbackAuthoringStrings.ADD_UPC_LABEL + " " + gridConf.getCommonLabel()){
				@Override
				protected void onClick(ComponentEvent ce) {
					addElement();
					
				}
			};
			
			buttonAdd.setIconStyle("icon-plus-circle");//## icon-round-add
			ToolBar toolBar = new ToolBar();
			toolBar.setAlignment(HorizontalAlignment.RIGHT);
			toolBar.add(buttonAdd);
			panel.setTopComponent(toolBar);
		}
		
		final Vector<ColumnConf> colConfigList = gridConf.getColConfigList();
		Vector<ColumnConfig> colList = new Vector<ColumnConfig>();
		
		for(ColumnConf colConf: colConfigList){
			ColumnConfig tmpCol = new ColumnConfig(colConf.getId(), "<div style=\"color:#000000;\">" + 
									colConf.getLabel() + "</div>", colConf.getWidth());
			tmpCol.setStyle("color:black;");
			colList.add(tmpCol);
		}
				
		ColumnConfig actionsCol = colList.lastElement();
		
		ColumnModel activeSessionsCM = new ColumnModel(colList);
		elementsGrid = new Grid<ElementModel>(gridStore, activeSessionsCM);
		//elementsGrid.addStyleName("text-black");
		
		ColumnConfig nameCol = activeSessionsCM.getColumnById(GridElementLabel.NAME);
		if(nameCol != null){
			nameCol.setRenderer(new GridCellRenderer<ElementModel>(){
				@Override
				public String render(ElementModel model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<ElementModel> store, Grid<ElementModel> grid) {
					String name = null;
					Integer tmp = model.get(GridElementLabel.DATA_TYPE); //the type of object that we are displaying
					int type = INVALID_TYPE;
					if(tmp != null){
						type = tmp;
					}
					switch(type){
						case INVALID_TYPE:
							name = new String("<div style=\"color:#000000;\">" + 
									model.get(GridElementLabel.NAME) + "</div>");
							break;
						case AGENT_TYPE:{
							int compileStatus = model.get(GridElementLabel.COMPILE_STATUS);//FATConstants.AGENT_STATUS_OK;
							if(tmp != null){
								//compileStatus = Integer.valueOf(tmp);
								if(compileStatus == FATConstants.AGENT_STATUS_OK){
									name = new String("<div style=\"color:#000000;\">" + 
											model.get(GridElementLabel.NAME) + "</div>");
				            	} else if(compileStatus == FATConstants.AGENT_STATUS_ERROR){
				            		name = new String("<div style=\"color:#FF0000;\">" + 
											model.get(GridElementLabel.NAME) + "</div>");
				            	} else  if(compileStatus == FATConstants.AGENT_STATUS_COMPILED){
				            		name = new String("<div style=\"color:#000000;\">" + 
											model.get(GridElementLabel.NAME) + "</div>");  //green #40FF00
				            	}
							}
							break;
						}
						default:{
							name = new String("<div style=\"color:#000000;\">" + 
									model.get(GridElementLabel.NAME) + "</div>");
							break;
						}
					}
					
					return name;
				}
			});
		}
		
		actionsCol.setRenderer(new GridCellRenderer<ElementModel>(){
			
			@Override
//			public Widget render(ModelData model, String property,
//					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
//					int rowIndex, int colIndex, ListStore store, Grid grid) {
			public Object render(ElementModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ElementModel> store, Grid<ElementModel> grid) {
				
				return getActionButtonsPanel(model, property, config, rowIndex, colIndex, store, grid);
			}
			
			private ContentPanel getActionButtonsPanel(final ElementModel model, String property,
					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
					int rowIndex, int colIndex, final ListStore<ElementModel> store, final Grid<ElementModel> grid){
				ContentPanel panel = new ContentPanel();
				panel.setLayout(new FillLayout(Orientation.HORIZONTAL));
				panel.setHeaderVisible(false);
				panel.setBodyBorder(false);
				panel.setLayoutOnChange(true);
				panel.setBodyStyle("backgroundColor: transparent;");
				
				Button markReadyButton = new Button();
				Button viewButton = new Button();
				Button editButton = new Button();
				Button deleteButton = new Button();
				Button duplicateButton = new Button();
				
				markReadyButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String elementId = model.get(GridElementLabel.ID);
				    	  markReadyElement(elementId);
				      }
				});
				
				viewButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String elementId = model.get(GridElementLabel.ID);
				    	  viewElement(elementId);
				    	  //Info.display(elementId, "<ul><li>" + model.get(colConfigList.get(0).getLabel()) + "</li></ul>");
				      }
				});
				editButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  String elementId = model.get(GridElementLabel.ID);
				    	  editElement(elementId);
				      }
				});
				deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  final String elementId = model.get(GridElementLabel.ID);
				    	  final String elementName = model.get(GridElementLabel.NAME);
				    	  
				    	  ce.setCancelled(true);
				    	  deleteElement(elementId, elementName);
				    	  
//				    	  //Initialize message box
//				    	  MessageBox box = new MessageBox();
//				    	  box.setButtons(MessageBox.YESNO);
//				    	  box.setIcon(MessageBox.QUESTION);
//				    	  box.setTitle(FeedbackAuthoringStrings.DELETE_LABEL + " " + gridConf.getCommonLabel());
//				    	  box.setMessage(FeedbackAuthoringStrings.DELETE_LABEL + " " + elementName + FeedbackAuthoringStrings.QUESTION_MARK);
//				    	  box.addCallback(new Listener<MessageBoxEvent>() {
//				    		  public void handleEvent(MessageBoxEvent be) {
//				    			  if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
//				    				  //grid.getStore().remove(bm);
//				    				  FATDebug.print(FATDebug.DEBUG, "[CustomizedGid][deleteButton][OK]");
//							    	  deleteElement(elementId, elementName);
//				    			  }
//				    		  }
//				    	  });
//				    	  box.show();
				      }
				});
				duplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>(){  
				      @Override
				      public void componentSelected(ButtonEvent ce){
				    	  //String element = (String) model.get(colConfigList.get(0).getId());
				    	  String elementId = model.get(GridElementLabel.ID);
				    	  duplicateElement(elementId);
				      }
				});
				
				markReadyButton.setStyleName("xfa-btn");
				viewButton.setStyleName("xfa-btn");
				editButton.setStyleName("xfa-btn");
				deleteButton.setStyleName("xfa-btn");
				duplicateButton.setStyleName("xfa-btn");
				
				markReadyButton.setStyleName("x-btn-text", true);
				viewButton.setStyleName("x-btn-text", true);
				editButton.setStyleName("x-btn-text", true);
				deleteButton.setStyleName("x-btn-text", true);
				duplicateButton.setStyleName("x-btn-text", true);
				
				markReadyButton.setStyleName("x-btn", true);
				viewButton.setStyleName("x-btn", true);
				editButton.setStyleName("x-btn", true);
				deleteButton.setStyleName("x-btn", true);
				duplicateButton.setStyleName("x-btn", true);
				
				markReadyButton.setStylePrimaryName("xfa-btn");
				viewButton.setStylePrimaryName("xfa-btn");
				editButton.setStylePrimaryName("xfa-btn");
				deleteButton.setStylePrimaryName("xfa-btn");
				duplicateButton.setStylePrimaryName("xfa-btn");
				
				markReadyButton.setWidth(16);
				viewButton.setWidth(16);
				editButton.setWidth(16);
				deleteButton.setWidth(16);
				duplicateButton.setWidth(16);
				
				markReadyButton.setHeight(16);
				viewButton.setHeight(16);
				editButton.setHeight(16);
				deleteButton.setHeight(16);
				duplicateButton.setHeight(16);
				
				markReadyButton.setMinWidth(16);
				viewButton.setMinWidth(16);
				editButton.setMinWidth(16);
				deleteButton.setMinWidth(16);
				duplicateButton.setMinWidth(16);
				
				markReadyButton.setToolTip(FeedbackAuthoringStrings.MARK_LABEL + " " + gridConf.getCommonLabel() + " " + FeedbackAuthoringStrings.READY_LABEL);
				viewButton.setToolTip(FeedbackAuthoringStrings.VIEW_LABEL + " " + gridConf.getCommonLabel());
				editButton.setToolTip(FeedbackAuthoringStrings.EDIT_LABEL + " " + gridConf.getCommonLabel());
				deleteButton.setToolTip(FeedbackAuthoringStrings.DELETE_LABEL + " " + gridConf.getCommonLabel());
				duplicateButton.setToolTip(FeedbackAuthoringStrings.DUPLICATE_LABEL + " " + gridConf.getCommonLabel());
				
				Integer tmp = model.get(GridElementLabel.DATA_TYPE); //the type of object that we are displaying
				int type = INVALID_TYPE;
				if(tmp != null){
					type = tmp;
				}
				
				tmp = model.get(GridElementLabel.COMPILE_STATUS);
				
				switch(type){
					case INVALID_TYPE:
						markReadyButton.setIconStyle("icon-compile-green");
						viewButton.setIconStyle("icon-view");
						duplicateButton.setIconStyle("icon-duplicate");
						editButton.setIconStyle("icon-edit-gear");
						deleteButton.setIconStyle("icon-delete-circle");
						break;
					case AGENT_TYPE:{
						int compileStatus = FATConstants.AGENT_STATUS_OK;
						if(tmp != null){ //COMPILE_STATUS
							compileStatus = Integer.valueOf(tmp);
							if(compileStatus == FATConstants.AGENT_STATUS_OK){
			            		markReadyButton.setIconStyle("icon-compile-green");
			            	} else if(compileStatus == FATConstants.AGENT_STATUS_ERROR){
			            		markReadyButton.setIconStyle("icon-compile-red");
			            	} else  if(compileStatus == FATConstants.AGENT_STATUS_COMPILED){ //FATConstants.AGENT_STATUS_COMPILED
			            		markReadyButton.setIconStyle("icon-compile-gray");
			            		markReadyButton.disable();
			            	}
						}
						Boolean tmp2 = model.get(GridElementLabel.READ);
						boolean isRead = (tmp2!=null)?tmp2:false;
						tmp2 = model.get(GridElementLabel.WRITE);
						boolean isWrite = (tmp2!=null)?tmp2:false;
						if(isRead){
							viewButton.setIconStyle("icon-view");
							duplicateButton.setIconStyle("icon-duplicate");
							if(isWrite){
								deleteButton.setIconStyle("icon-delete-circle");
								if(compileStatus == FATConstants.AGENT_STATUS_COMPILED){
									editButton.setIconStyle("icon-edit-gear-gray");
									editButton.disable();
								} else{
									editButton.setIconStyle("icon-edit-gear");
								}
							} else{
								deleteButton.disable();
								deleteButton.setIconStyle("icon-delete-circle-gray");
								editButton.disable();
								editButton.setIconStyle("icon-edit-gear-gray");
							}
						} else {
							duplicateButton.disable();
							duplicateButton.setIconStyle("icon-duplicate-gray");
							viewButton.disable(); //TODO check if we can leave this button enabled, i.e. doesn't affect anything.
							viewButton.setIconStyle("icon-view-gray");
							deleteButton.disable();
							deleteButton.setIconStyle("icon-delete-circle-gray");
							editButton.disable();
							editButton.setIconStyle("icon-edit-gear-gray");
						}
						
						break;
					}
					default:{
						markReadyButton.setIconStyle("icon-compile-green");
						viewButton.setIconStyle("icon-view");
						duplicateButton.setIconStyle("icon-duplicate");
						editButton.setIconStyle("icon-edit-gear");
						deleteButton.setIconStyle("icon-delete-circle");
						FATDebug.print(FATDebug.WAR, "[CustomizedGrid][CreateGridPanel]getActionButtonsPanel unkown type=" + type);
					}
				}
				
				//display buttons.
				if(gridConf.isMarkReadyButtonFlag()){
					panel.add(markReadyButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}
				if(gridConf.isViewButtonFlag()){
					panel.add(viewButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}
				if(gridConf.isDuplicateButtonFlag()){
					panel.add(duplicateButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}
				if(gridConf.isEditButtonFlag()){
					panel.add(editButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}
				if(gridConf.isDeleteButtonFlag()){
					panel.add(deleteButton, new RowData(Style.DEFAULT, 1.0, new Margins(0)));
				}

				return panel;
			}

		});
		
		CustomizedGridView gridView = new CustomizedGridView();

//	    Grid<ElementModel> agentsGrid = new Grid<ElementModel>(gridStore, activeSessionsCM);
	    //the view is used to handle the mouseOver and mouserOut events 
	    elementsGrid.setView(gridView);
	    elementsGrid.setLazyRowRender(0);
	    elementsGrid.setStyleAttribute("color", "#000000");
	    	    
	    elementsGrid.setHideHeaders(gridConf.isHideHeaders());//for column headers
	    elementsGrid.setBorders(false);
	    elementsGrid.setAutoExpandColumn(gridConf.getAutoExpandColumn());
	    elementsGrid.setAutoExpandMax(800);
	    elementsGrid.setAutoWidth(true);
	    
	    elementsGrid.setTrackMouseOver(true);
	    if(!gridConf.isEnableSelection()){
	    	elementsGrid.getSelectionModel().setLocked(true);
	    }
	    elementsGrid.setStripeRows(true);
	    
	    elementsGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ElementModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ElementModel> se) {
				FATDebug.print(FATDebug.DEBUG, "[CustomizedGid][selectionChanged]");
				ElementModel selectedRow = se.getSelectedItem();
//				List<ElementModel> selectedRows = se.getSelection();
				if(selectedRow != null){
					FATDebug.print(FATDebug.DEBUG, "[CustomizedGid][selectionChanged] selectedRow OK");
					selectedRowEvent(selectedRow);
				} else{
					FATDebug.print(FATDebug.DEBUG, "[CustomizedGid][selectionChanged] selectedRow =" + null);
				}
			}
	    });

	    
	    
	    panel.add(elementsGrid);
	    
	    return panel;
	}
	/**
	 * Adds an element to the store
	 * @param element
	 */
	public void addElement2GridStore(ElementModel element){
		gridStore.add(element);
		//agentsOntoTree.getView().refresh(false);
	}
	/**
	 * Deletes an element from the store
	 * @param element
	 */
	public void deleteElementFromStore(ElementModel element){
		gridStore.remove(element);
	}
	/**
	 * Updates an element from the store
	 * @param element
	 */
	public void updateElementFromStore(ElementModel element){
		gridStore.update(element);
	}
	/**
	 * Selects the element's row
	 * @param element
	 */
	public void selectElementRow(ElementModel element){
		ElementModel model = gridStore.findModel(element);
		int rowIndex = gridStore.indexOf(model);
		if(rowIndex != -1){
			elementsGrid.getView().focusRow(rowIndex);
			elementsGrid.getSelectionModel().select(model, false);
		}
	}
	
//	private void populateGrid(){
//		Map<String, String> agents = TestData.getAgents();
//		
//		int counter = 0;
//		for(String a : agents.keySet()){
//			ElementModel m = new ElementModel();
//			m.set(GridElementLabel.ID, ++counter);
//			m.set(GridElementLabel.NAME, a);
//			m.set(GridElementLabel.STATUS, agents.get(a));
//			
//			gridStore.add(m);
//		}		
//	}
	
	
}
