package lasad.gwt.client.ui.workspace.tableview;
//package lasad.gwt.client.ui.table;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import lasad.gwt.client.communication.LASADActionSender;
//import lasad.gwt.client.communication.objects.ActionFactory;
//import lasad.gwt.client.model.ElementInfo;
//import lasad.gwt.client.model.MVCHelper;
//import lasad.gwt.client.model.MVCViewRecipient;
//import lasad.gwt.client.model.UnspecifiedElementModel;
//
//import com.extjs.gxt.ui.client.event.BaseEvent;
//import com.extjs.gxt.ui.client.event.Events;
//import com.extjs.gxt.ui.client.event.IconButtonEvent;
//import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.util.Margins;
//import com.extjs.gxt.ui.client.util.Point;
//import com.extjs.gxt.ui.client.util.Size;
//import com.extjs.gxt.ui.client.widget.Component;
//import com.extjs.gxt.ui.client.widget.ContentPanel;
//import com.extjs.gxt.ui.client.widget.LayoutContainer;
//import com.extjs.gxt.ui.client.widget.button.IconButton;
//import com.extjs.gxt.ui.client.widget.button.ToolButton;
//import com.extjs.gxt.ui.client.widget.layout.AnchorData;
//import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
//import com.extjs.gxt.ui.client.widget.layout.CardLayout;
//import com.google.gwt.user.client.Event;
//import com.google.gwt.user.client.ui.FlexTable;
//import com.google.gwt.user.client.ui.Widget;
//
///**
// * A RelationCell with child components(text, user....) for Argument Relations
// * 
// * 
// * @author erkang
// *
// */
//public class RelationCell extends ContentPanel implements MVCViewRecipient, CellContainer{
//
//	//*************************************************************************************
//	//	Fields
//	//*************************************************************************************
//	
//	private final ActionFactory actionFactory = ActionFactory.getInstance();
//	private final LASADActionSender actionSender = LASADActionSender.getInstance();
//	
//	private UnspecifiedElementModel model;
//	private ElementInfo info;
//	
//	private TableCellTypeEnum type;
//	
//	private TableCellInfo tableCellInfo;
//	
//	private CardLayout cardLayout;
//	private LayoutContainer preview;
//	private LayoutContainer details;
//	
//	private int index;
//	private String rootElementId;
//	private boolean editMode;
//	
//	
//	//*************************************************************************************
//	//	Constructor
//	//*************************************************************************************
//	
//	public RelationCell(final ElementInfo info) {
//		
//		this.info = info;
//		
//		setBorders(false);
//		setBodyBorder(false);
//		setAutoHeight(true);
//		
//		cardLayout = new CardLayout();
//		setLayout(cardLayout);
//		
//		//***********************
//		// Header
//		//***********************
//		setHeading(info.getElementOption(ParameterTypes.Heading));
//		getHeader().setStyleAttribute("background", info.getElementOption(ParameterTypes.BackgroundColor));
//		getHeader().setStyleAttribute("color", info.getElementOption(ParameterTypes.FontColor));
//
//		
//		//***********************
//		// Body
//		//***********************
//		
//		preview = new LayoutContainer();
//		add(preview);
//		
//		
//		details = new LayoutContainer();
//		details.setBorders(false);
//		details.setLayout(new AnchorLayout());
//		add(details);
//		
//		
//		//***********************
//		// Bottom Bar
//		//***********************
////		AwarenessBar awarenessBar = new AwarenessBar();
////		awarenessBar.setVisible(false);
////		setBottomComponent(awarenessBar);
//		
//	}
//	
//	
//	
//	@Override
//	protected void afterRender() {
//		super.afterRender();
//		
//		Listener<BaseEvent> listener = new Listener<BaseEvent>() {
//			
//			@Override
//			public void handleEvent(BaseEvent be) {
//				
//				if (be.getType().equals(Events.OnMouseMove)) {
//					
//					setupToolButtons();
//					
//					updateIndex();
//					
//				}
//				
//				if (be.getType().equals(Events.OnMouseOut)) {
//					
//					hideToolButtons();
//				}
//			}
//		};
//		
//		el().addEventsSunk(Event.ONMOUSEMOVE);
//		el().addEventsSunk(Event.ONMOUSEOUT);
//		addListener(Events.OnMouseMove, listener);
//		addListener(Events.OnMouseOut, listener);
//		
//	}
//	
//	
//	private void updateIndex() {
//		
//		if (getParent() instanceof RelationTableCell) {
//			
//			RelationTableCell relationTableCell = (RelationTableCell)getParent();
//			
//			index = relationTableCell.indexOf(this);
//			
//		}
//	}
//	
//	//*************************************************************************************
//	//	Methods
//	//*************************************************************************************
//	
//	
//	public void showDetails() {
//		
//		
//		rootElementId = model.getValue("ROOTELEMENTID");
//		setHeading(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
//		
//		cardLayout.setActiveItem(details);
//		resize();
//		layout();
//		
//		getBottomComponent().setVisible(true);
//	}
//	
//	public void showPreview() {
//		
//		cardLayout.setActiveItem(preview);
//		
//		getBottomComponent().setVisible(false);
//		
//	}
//	
//	
//	public void setupToolButtons() {
//		
//		removeAllTools();
//		
//		if (editMode) {
//			
//			for(ElementInfo childInfo : info.getChildElements().values()){		
//			
//				int elementCount = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID()).size();
//				if((childInfo.getElementOption(ParameterTypes.ManualAdd) == null || !childInfo.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childInfo.getMaxQuantity()){
//				
//					initPlusButton();
//					break;
//				}
//			}
//			
//			for(ElementInfo childInfo : info.getChildElements().values()){		
//				
//				int elementCount = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID()).size();
//				
//				if(elementCount > childInfo.getMinQuantity()){
//					
//					initMinusButton();
//					break;
//				}
//			}
//			
//			
//			initXButton();
//			
//		} else {
//			
//			initGearButton();
//			
//		}
//		
//	}
//	
//	
//	private void initPlusButton() {
//		
//		ToolButton plusButton = new ToolButton("x-tool-plus", new SelectionListener<IconButtonEvent>() {
//			@Override
//			public void componentSelected(IconButtonEvent ce) {
//			
//				IconButton button = ce.getIconButton();
//				Point point = button.getPosition(false);
//			
//				final BoxElementAddtionPopup popup = new BoxElementAddtionPopup();
//			
//				for (ElementInfo childElement : info.getChildElements().values()) {		
//				
//					// Calculate current element Count
//					int elementCount = MVCHelper.getChildModelsByElementID(model, childElement.getElementID()).size();
//				
//					if ((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()) {
//				
//						// Set elementname
//						if (childElement.getElementOption(ParameterTypes.LongLabel)!= null) {
//						
//							popup.addLabel(childElement.getElementOption(ParameterTypes.LongLabel), childElement);
//						
//						} else if (childElement.getElementOption(ParameterTypes.Label)!= null) {
//						
//							popup.addLabel(childElement.getElementOption(ParameterTypes.Label), childElement);
//						
//						} else {
//						
//							popup.addLabel(childElement.getElementID(), childElement);
//						}
//					
//					}
//				
//				}
//			
//				popup.showAt(point.x, point.y);
//			
//				if (getParent() instanceof ElementEditDialog) {
//				
//					ElementEditDialog dialog = (ElementEditDialog)getParent();
//					dialog.setEnabled(false);
//				
//					popup.addListener(Events.Hide, new Listener<BaseEvent>() {
//
//						@Override
//						public void handleEvent(BaseEvent be) {
//							ElementEditDialog dialog = (ElementEditDialog)getParent();
//							dialog.setEnabled(true);
//							
//							ElementInfo selectedLabel = popup.getSelectedLabel();
//						
//							if (selectedLabel != null) {
//								int mapId = Integer.valueOf(model.getValue("MAP-ID"));
//								int modelId = model.getId();
//							
//								String elementId = selectedLabel.getElementID();
//								String elementType = selectedLabel.getElementType();
//							
//								actionSender.sendActionPackage(actionFactory.addElement(mapId, modelId, elementType, elementId));
//							}
//						}
//					});
//				} 
//			}
//			
//		});
//		
//		getHeader().addTool(plusButton);
//		
//	}
//	
//	
//	
//	private void initMinusButton() {
//		
//		ToolButton minusButton = new ToolButton("x-tool-minus", new SelectionListener<IconButtonEvent>() {
//			@Override
//			public void componentSelected(IconButtonEvent ce) {
//				
//				
//				IconButton button = ce.getIconButton();
//				Point point = button.getPosition(false);
//				
//				final BoxElementDeletionPopup popup = new BoxElementDeletionPopup();
//				
//				for (ElementInfo childInfo : info.getChildElements().values()) {
//					
//					List<UnspecifiedElementModel> childModels = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID());
//					
//					if (childModels.size() > childInfo.getMinQuantity()) {
//						
//						for (UnspecifiedElementModel model: childModels) {
//						
//							String text = model.getValue("TEXT");
//							String suffix = new String();
//							
//							if (text != null) {
//								
//								if (text.length() > 5) {
//									suffix = text.substring(0, 5);
//								} else {
//									suffix = text;
//								}
//							}
//							
//							if (childInfo.getElementOption(ParameterTypes.LongLabel) != null) {
//							
//								popup.addLabel(childInfo.getElementOption(ParameterTypes.LongLabel), suffix, childInfo);
//							
//							} else if (childInfo.getElementOption(ParameterTypes.Label) != null) {
//							
//								popup.addLabel(childInfo.getElementOption(ParameterTypes.Label), suffix, childInfo);
//							
//							} else {
//							
//								popup.addLabel(childInfo.getElementID(), suffix, childInfo);
//							}
//
//						}
//					}
//				}
//				
//				popup.showAt(point.x, point.y);
//				
//				if (getParent() instanceof ElementEditDialog) {
//					
//					ElementEditDialog dialog = (ElementEditDialog)getParent();
//					dialog.setEnabled(false);
//				
//					popup.addListener(Events.Hide, new Listener<BaseEvent>() {
//
//						@Override
//						public void handleEvent(BaseEvent be) {
//							ElementEditDialog dialog = (ElementEditDialog)getParent();
//							dialog.setEnabled(true);
//							
//						
//							if (popup.getSelectedLabelText() != null) {
//							
//								ElementInfo childInfo = popup.getSelectedInfo();
//						
//							
//								if (childInfo != null) {
//									int mapId = Integer.valueOf(model.getValue("MAP-ID"));
//									int modelId = 0;
//							
//									List<UnspecifiedElementModel> childModels = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID());
//								
//									for (UnspecifiedElementModel model: childModels) {
//									
//										
//										if (model.getValue("TEXT") == null && "".equals(popup.getSelectedLabelText())) {
//											modelId = model.getId();
//											break;
//										}
//									
//										if (model.getValue("TEXT").equals(popup.getSelectedLabelText())) {
//											modelId = model.getId();
//											break;
//										}
//									}
//								
//									//*************************************************************************************
//									//	ACTIONPACKAGE: 
//									//		ACTION: 
//									//			Category: MAP
//									//			Command: REMOVE-ELEMENT
//									//			Parameter: MAP-ID : 49
//									//			Parameter: ID : 16
//									//*************************************************************************************
//									actionSender.sendActionPackage(actionFactory.removeElement(mapId, modelId));
//								
//								}
//							}
//						}
//					});
//				} 
//				
//				
//			}
//		});
//		
//		getHeader().addTool(minusButton);
//	}
//	
//	
//	
//	private void initXButton() {
//	
//		ToolButton xButton = new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {
//
//			@Override
//			public void componentSelected(IconButtonEvent ce) {
//				
//				
//				if (tableCellInfo.getMap().isDeleteElementsWithoutConfirmation()) {
//					
//					if (getParent() instanceof ElementEditDialog) {
//						
//						ElementEditDialog dialog = (ElementEditDialog)getParent();
//						restoreToTable();
//						dialog.hide();
//					}
//					
//					//*************************************************************************************
//					//	ACTIONPACKAGE: 
//					//		ACTION: 
//					//			Category: MAP
//					//			Command: REMOVE-ELEMENT
//					//			Parameter: MAP-ID : 49
//					//			Parameter: ID : 7
//					//*************************************************************************************
//					
//					int mapId = Integer.parseInt(model.getValue("MAP-ID"));
//					int modelId = model.getId();
//					
//					actionSender.sendActionPackage(actionFactory.removeElement(mapId, modelId));
//
//				} else {		
//
//					IconButton button = ce.getIconButton();
//					Point point = button.getPosition(false);
//					
//					final BoxClosePopup popup = new BoxClosePopup();
//					popup.showAt(point.x, point.y);
//					
//					
//					if (getParent() instanceof ElementEditDialog) {
//						
//						ElementEditDialog dialog = (ElementEditDialog)getParent();
//						dialog.setEnabled(false);
//					
//						popup.addListener(Events.Hide, new Listener<BaseEvent>() {
//
//							@Override
//							public void handleEvent(BaseEvent be) {
//								ElementEditDialog dialog = (ElementEditDialog)getParent();
//								dialog.setEnabled(true);
//								
//								Boolean isClose = popup.isClose();
//								Boolean conformChecked = popup.getConform().getValue();
//								
//								
//								if (isClose) {
//									
//									restoreToTable();
//									dialog.hide();
//									
//									int mapId = Integer.parseInt(model.getValue("MAP-ID"));
//									int modelId = model.getId();
//									
//									
//									actionSender.sendActionPackage(actionFactory.removeElement(mapId, modelId));
//									
//								}
//								
//								tableCellInfo.getMap().setDeleteElementsWithoutConfirmation(conformChecked);
//								
//							}
//						});
//					} 
//				}
//			}
//		});
//		
//		getHeader().addTool(xButton);
//		
//	}
//	
//	
//	
//	private void initGearButton() {
//		
//		ToolButton gearButton = new ToolButton("x-tool-gear", new SelectionListener<IconButtonEvent>() {
//
//			@Override
//			public void componentSelected(IconButtonEvent ce) {
//				
//				FlexTable flexTable = tableCellInfo.getFlexTable();
//				
//				RelationTableCell cell = (RelationTableCell) flexTable.getWidget(tableCellInfo.getRowNumber(), tableCellInfo.getColNumber());
//				
//				ElementEditDialog dialog = new ElementEditDialog((RelationCell)cell.getItem(index));
//				
//				dialog.show();
//				
//			}
//		});
//		
//		getHeader().addTool(gearButton);
//		
//	}
//	
//	
//	public void restoreToTable() {
//		
////		setSize(width, height);
//		
//		FlexTable flexTable = tableCellInfo.getFlexTable();
//		Widget widget = flexTable.getWidget(tableCellInfo.getRowNumber(), tableCellInfo.getColNumber());
//		
//		if (widget instanceof RelationTableCell) {
//			
//			RelationTableCell relationTableCell = (RelationTableCell)widget;
//			
//			setEditMode(false);
//			showPreview();
//			
//			relationTableCell.insert(this, getIndex(), new AnchorData("100% 10%"));
//			relationTableCell.layout();
//		}
//		
//	}
//	
//	
//	
//	private void removeAllTools() {
//		
//		List<Component> buttons = new ArrayList<Component>();
//		
//		for(int i= 0; i < getHeader().getToolCount() ; i++) {
//			buttons.add(getHeader().getTool(i));
//		}
//		
//		for (Component button: buttons) {
//			getHeader().removeTool(button);
//		}
//		
//	}
//	
//	
//	private void hideToolButtons() {
//		
//		for(int i= 0; i < getHeader().getToolCount() ; i++) {
//			getHeader().getTool(i).setVisible(false);
//		}
//	}
//	
//	
//	
//	private int calElementPosition(String newChildElementID){
//
//		if(details.getItemCount() == 0){
//			return 0;
//		}
//
//		// Calculate ElementID before
//		String beforeElementID = "";
//		for (String tempElementID: info.getChildElements().keySet()) {
//			if (!tempElementID.equals(newChildElementID)) {
//				
//				for(Component component : details.getItems()){	
//					if (component instanceof TextElement) {
//						TextElement tempElement = (TextElement)component;
//						if(tempElement.getConnectedModel().getElementid().equals(tempElementID)){
//							beforeElementID = tempElementID;
//							break;
//						}
//					}
//					
//				}
//			}
//			else{
//				break;
//			}
//		}
//
//		// Calculate new Position
//		int pos = 0;
//		boolean elementIDreached = false;
//		boolean beforeElementIDreached = false;
//		for(Component component : details.getItems()){
//			if (component instanceof TextElement) {
//				
//				TextElement tempElement = (TextElement)component;
//				String tempElementID = tempElement.getConnectedModel().getElementid();		
//
//				if(beforeElementID.equals(tempElementID)){
//					beforeElementIDreached= true;
//					pos++;
//					continue;
//				}
//
//				if(beforeElementIDreached && !tempElementID.equals(beforeElementID)){
//					break;
//				}
//
//				if(tempElementID.equals(newChildElementID)){
//					elementIDreached=true;
//					pos++;
//					continue;
//				}
//
//				if(elementIDreached && !tempElementID.equals(newChildElementID)){
//					break;
//				}
//
//				pos++;
//			}
//		}
//		return pos;
//	}
//	
//	//*************************************************************************************
//	//  add by Erkang:  display Argument with another ViewSession
//	//
//	//  ACTION: 
//	//		Category: MAP
//	//		Command: CREATE-ELEMENT
//	//		Parameter: ID : 7
//	//		Parameter: TYPE : box
//	//		Parameter: USERNAME : t1
//	//		Parameter: MAP-ID : 49
//	//		Parameter: ELEMENT-ID : fact
//	//		Parameter: POS-X : 2772
//	//		Parameter: POS-Y : 2747
//	//		Parameter: USERACTION-ID : xrlcfgk49z7u1277471052924
//	//		Parameter: NUM-ACTIONS : 3
//	//		Parameter: ROOTELEMENTID : 1
//	//
//	//*************************************************************************************
//	
//	//*************************************************************************************
//	// methods of Inteface MVCViewRecipient
//	//*************************************************************************************
//	
//	@Override
//	public void changeValueMVC(UnspecifiedElementModel model, String vname) {
//		// TODO Auto-generated method stub
//		
//		
//		if (vname.equals("ROOTELEMENTID")) {
//			rootElementId = model.getValue("ROOTELEMENTID");
//			
//			setHeading(rootElementId + " . " + getHeading());
//		}
//		
//	}
//
//	@Override
//	public void deleteModelConnection(UnspecifiedElementModel model) {
//		
////		removeFromParent();		
//	}
//
//	@Override
//	public boolean establishModelConnection(UnspecifiedElementModel model) {
//		
//		if (this.model == null) {
//			this.model = model;
//			return true;
//		}
//		else{
//			return false;
//		}
//	}
//	
//
//	@Override
//	public UnspecifiedElementModel getConnectedModel() {
//		return model;
//	}
//
//	
//	//*************************************************************************************
//	// CellContainer
//	//*************************************************************************************
//
//	@Override
//	public MVCViewRecipient addComponent(String type, String id) {
//		
////		details.setAutoHeight(false);
//		
//		if ("awareness".equalsIgnoreCase(type)) {
//			
//			MVCViewRecipient recipient = (MVCViewRecipient)getBottomComponent();
//			
//			
//			return recipient;
//			
//		} else if ("text".equalsIgnoreCase(type)) {
//			
//			ElementInfo childInfo = info.getChildElements().get(id);
//			TextElement textElement = new TextElement(childInfo);
//			
//			
//			details.insert(textElement, calElementPosition(id));
//			details.layout();
//			
//			
//			
//			return textElement;
//			
//		} else if ("rating".equalsIgnoreCase(type)) {
//			
//			ElementInfo childInfo = info.getChildElements().get(id);
//			RatingElement ratingElement = new RatingElement(childInfo);
//			
//			details.insert(ratingElement, calElementPosition(id));
//			resize();
//			details.layout();
//			
//			
//			return ratingElement;
//			
//		} else if ("url".equalsIgnoreCase(type)) {
//			
//			ElementInfo childInfo = info.getChildElements().get(id);
//			UrlElement urlElement = new UrlElement(childInfo);
//			
//			details.insert(urlElement, calElementPosition(id));
//			resize();
//			details.layout();
//			
//			
//			return urlElement;
//			
//		} else {
//			
//			
//		}
//		
//		return null;
//	}
//
//	
//	private void resize() {
//		
//		if (details.getItemCount() > 0) {
//		
////			details.setSize(width, height);
//			
//			Size size = ArgumentMapTable.cellSize.get(TableZoomEnum.SIZE100);
//			details.setSize(size.width, size.height);
//			
//			int fieldCount = 0;
//			int areaCount = 0;
//			int ratingCount = 0;
//			int urlCount = 0;
//			
//			for (Component component : details.getItems()) {
//				
//				if (component instanceof TextElement) {
//					
//					TextElement textElement = (TextElement)component;
//					
//					if ("textfield".equalsIgnoreCase(textElement.getTexttype())) {
//						fieldCount ++;
//					} else {
//						areaCount ++;
//					}
//				}
//				
//				if (component instanceof RatingElement) {
//					
//					ratingCount ++;
//				}
//				
//				if (component instanceof UrlElement) {
//					
//					urlCount ++;
//				}
//			}
//			
//			AnchorData ratingAnchorData = new AnchorData("100% 10%", new Margins(1));
//			AnchorData urlAnchorData = new AnchorData("100% 10%", new Margins(1));
//			AnchorData fieldAnchorData = new AnchorData("100% 10%", new Margins(1));
//			AnchorData areaAnchorData = new AnchorData();
//			
//			if (areaCount > 0 ) {
//				
//				String areaHeight = String.valueOf((100 - fieldCount * 10 - ratingCount * 10 - urlCount * 10) /areaCount) + "%";
//				areaAnchorData = new AnchorData("100%" + " " + areaHeight , new Margins(1));
//			}
//			
//			for (Component component : details.getItems()) {
//				
//				if (component instanceof TextElement) {
//					
//					TextElement textElement = (TextElement)component;
//					
//					if ("textfield".equalsIgnoreCase(textElement.getTexttype())) {
//
//						details.setLayoutData(textElement, fieldAnchorData);
//					} else {
//
//						details.setLayoutData(textElement, areaAnchorData);
//					}
//				}
//				
//				if (component instanceof RatingElement) {
//					
//					RatingElement ratingBar = (RatingElement)component;
//					
//					details.setLayoutData(ratingBar, ratingAnchorData);
//				}
//				
//				if (component instanceof UrlElement) {
//					
//					UrlElement urlBar = (UrlElement)component;
//					
//					details.setLayoutData(urlBar, urlAnchorData);
//				}
//			}
//		
//		} 
//		
//	}
//	
//	
//
//	@Override
//	public void removeComponent(UnspecifiedElementModel model) {
//		
//		List<Component> components = details.getItems();
//		
//		for (Component component : components) {
//			
//			if (component instanceof MVCViewRecipient) {
//				
//				MVCViewRecipient recipient = (MVCViewRecipient)component;
//				UnspecifiedElementModel elementModel = recipient.getConnectedModel();
//				
//				if (model.equals(elementModel)) {
//					
//					details.remove(component);
//					details.setAutoHeight(true);
//					resize();
//					details.layout();
//					
//					break;
//				}
//				
//			}
//		}
//		
//	}
//
//	
//	public TableCellInfo getTableCellInfo() {
//		return tableCellInfo;
//	}
//
//
//	public void setTableCellInfo(TableCellInfo tableCellInfo) {
//		this.tableCellInfo = tableCellInfo;
//	}
//
//	
//	
//	public void changeView(TableZoomEnum zoom) {
//		
//		if (zoom == TableZoomEnum.SIZE25) {
//			
//			setHeading(rootElementId);
////			getHeader().setHeight("3");
//		}
//		
//		if (zoom == TableZoomEnum.SIZE50) {
//			
//			setHeading(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
//		}
//		
//		
//		if (zoom == TableZoomEnum.SIZE75) {
//			
//			setHeading(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
//			
//		}
//		
//		
//		if (zoom == TableZoomEnum.SIZE100) {
//			
//			setHeading(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
//		}
//		
//	}
//
//	//*************************************************************************************
//	// getter & setter
//	//*************************************************************************************
//	
//	@SuppressWarnings("unchecked")
//	public UnspecifiedElementModel getModel() {
//		return model;
//	}
//
//
//	public void setModel(UnspecifiedElementModel model) {
//		this.model = model;
//	}
//
//
//	public ElementInfo getInfo() {
//		return info;
//	}
//
//
//	public void setInfo(ElementInfo info) {
//		this.info = info;
//	}
//
//	
//	public Size getInitSize() {
//		
//		Size size = ArgumentMapTable.cellSize.get(TableZoomEnum.SIZE100);
//		
//		return size;
//		
//	}
//
//
//	public boolean isEditMode() {
//		return editMode;
//	}
//
//
//	public void setEditMode(boolean editMode) {
//		this.editMode = editMode;
//	}
//
//
//
//	public int getIndex() {
//		return index;
//	}
//
//
//
//	public void setIndex(int index) {
//		this.index = index;
//	}
//
//
//
//	/**
//	 * @return the type
//	 */
//	public TableCellTypeEnum getType() {
//		return type;
//	}
//
//
//
//	/**
//	 * @param type the type to set
//	 */
//	public void setType(TableCellTypeEnum type) {
//		this.type = type;
//	}
//
//
//}
