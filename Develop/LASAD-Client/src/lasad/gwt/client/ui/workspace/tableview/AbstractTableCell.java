package lasad.gwt.client.ui.workspace.tableview;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractRatingElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTextElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTranscriptElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractUrlElement;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * A TableCell with child components(text, user, ...) for Argument Element. 
 * 
 * 
 * @author erkang
 *
 */
public abstract class AbstractTableCell extends LayoutContainer implements MVCViewRecipient, CellContainer, FocusableInterface {

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
//	private final ActionFactory actionFactory = ActionFactory.getInstance();
//	private final LASADActionSender actionSender = LASADActionSender.getInstance();
	
	private AbstractUnspecifiedElementModel model;
	private ElementInfo info;
	
	private TableCellTypeEnum type;
	private TableZoomEnum zoom;
	private Size size;
	
	private ToolBar titleBar;
	private LabelToolItem titleLabel;
	private AwarenessElement awarenessElement;
	
	private LayoutContainer cellBody;
	private TableCellInfo tableCellInfo;
	
	private String rootElementId;
	private boolean editMode;
	
	private String color;
	
//	private MVCViewSession session;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractTableCell(ElementInfo info, TableCellTypeEnum type) {
		
		this.info = info;
		this.type = type;
		
		setBorders(true);
		setLayout(new BorderLayout());
		
		if (type == TableCellTypeEnum.BOX) {
			setupDrogTarget();
		}
		

		//***********************
		// Header
		//***********************

		titleBar = new ToolBar();
		titleBar.setEnableOverflow(false);
		titleBar.setStyleAttribute("background", info.getUiOption(ParameterTypes.BackgroundColor));
		
		titleLabel = new LabelToolItem();
		titleLabel.setStyleAttribute("color", info.getUiOption(ParameterTypes.FontColor));
		
		titleBar.add(titleLabel);
		titleBar.add(new FillToolItem());
		
		add(titleBar, new BorderLayoutData(LayoutRegion.NORTH, 25));
		
		color = info.getUiOption(ParameterTypes.BackgroundColor);
		
		//***********************
		// Body
		//***********************
		cellBody = new LayoutContainer();
		cellBody.setLayout(new RowLayout());
		cellBody.setBorders(false);
		cellBody.setScrollMode(Scroll.AUTOY);
		
		add(cellBody, new BorderLayoutData(LayoutRegion.CENTER));
		
		//***********************
		// Bottom Bar
		//***********************
		awarenessElement = new AwarenessElement(new ElementInfo());
		add(awarenessElement, new BorderLayoutData(LayoutRegion.SOUTH, 20));
		
	}
	
	
	@Override
	protected void afterRender() {
		super.afterRender();
		
		Listener<BaseEvent> listener = new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				
				if (be.getType().equals(Events.OnMouseMove)) {
					
					if (!editMode) {
						
						setToolTipVisible(true);
					} else {
						
						setToolTipVisible(false);
					}
					
					setupToolButtons();
					
				}
				
				if (be.getType().equals(Events.OnMouseOut)) {
					
					hideToolButtons();
				}
			}
		};
		
		el().addEventsSunk(Event.ONMOUSEMOVE);
		el().addEventsSunk(Event.ONMOUSEOUT);
		addListener(Events.OnMouseMove, listener);
		addListener(Events.OnMouseOut, listener);
		
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	public void setDefaultSize(TableZoomEnum zoom) {
		
		this.zoom = zoom;
		size = AbstractMapTable.cellSize.get(zoom);
		setSize(size.width, size.height);
	}
	
	public void initToolTip() {
		
		StringBuilder builder = new StringBuilder("");
		
		if (zoom == TableZoomEnum.SIZE25) {
		
			builder.append("<p align=\"center\"><b><font color=\"black\" size=\"4\">  " + info.getElementOption(ParameterTypes.Heading) + "</font></b></p><br/>");
		}
		
		builder.append("<table width=\"240\" style=\"table-layout:fixed\" cellpadding=\"5\" border=\"1\">");
		
		for (Component component: cellBody.getItems()) {
		
			if (component instanceof AbstractChildElement) {
			
				AbstractChildElement childElement = (AbstractChildElement)component;
			
				ChildElementTypeEnum type = childElement.getType();
				
				if (ChildElementTypeEnum.TEXT == type) {
					
					AbstractTextElement textElement = (AbstractTextElement)childElement;
					
					setupToolTipColumn(builder, textElement.getLabelText(), childElement.getSummary());
					
				} else {
					
					setupToolTipColumn(builder, type.toString(), childElement.getSummary());
				}
				
			}
		
		}
	
		if (zoom == TableZoomEnum.SIZE25 || zoom == TableZoomEnum.SIZE50) {
			
			setupToolTipColumn(builder, ChildElementTypeEnum.AWARENESS.toString().toUpperCase(), awarenessElement.getSummary());
		}
		
		
		builder.append("</table>");
		
		ToolTipConfig tipConfig = new ToolTipConfig(builder.toString());
		tipConfig.setAutoHide(false);
		tipConfig.setTrackMouse(true);
		setToolTip(tipConfig);
		
		getToolTip().setWidth(250);
		
	}
	

	private void setupToolTipColumn(StringBuilder builder, String leftString, String rightString) {
		
		builder.append("<tr>");
		builder.append("<td width=\"40%\" align=\"center\"><b><font color=\"black\">" + leftString + "</font></b></td>");
		builder.append("<td width=\"60%\" align=\"center\" style=\"word-break:break-all; overflow:hidden;\"><font color=\"black\">" + rightString + "</font></td>");
		builder.append("<tr>");
	}
	
	
	private void setToolTipVisible(boolean visible) {
		
		initToolTip();
		ToolTip toolTip = getToolTip();
		
		if (toolTip != null) {
			
			if (visible) {
				
				if (zoom == TableZoomEnum.SIZE75 || zoom == TableZoomEnum.SIZE100) {
					toolTip.setVisible(false);
				
				} else {
					
					toolTip.setVisible(true);
				}
				
			} else {
				
				toolTip.setVisible(false);
			}
		}

	}
	
	
	private void setupDrogTarget() {
		
		DropTarget dropTarget = new DropTarget(this);
		dropTarget.addDNDListener(new DNDListener(){

			@Override
			public void dragDrop(DNDEvent e) {
				
				if (e.getDragSource().getData() instanceof TranscriptLinkData) {

					TranscriptLinkData tData = (TranscriptLinkData)e.getData();
					tData.setElementID(model.getId());
					

					String mapId = model.getValue(ParameterTypes.MapId);
					int modelId = model.getId();
					
					if(containsTranscript()){
						
						//get the current model for Transcript, later updateTranscriptLink method will compare the values
						tData.setModel(getTranscriptModel());
						
//						actionSender.sendActionPackage(actionFactory.updateTranscriptLink(mapId, modelId, tData));
						sendUpdateTranscriptLinkToServer(mapId, modelId, tData);
					}
					else{
						
//						actionSender.sendActionPackage(new ActionPackage().addAction(actionFactory.createTranscriptLink(mapId, String.valueOf(modelId), tData)));
						sendCreateTranscriptLinkToServer(mapId, String.valueOf(modelId), tData);
					}					
				}
				
				super.dragDrop(e);
				e.cancelBubble();
			
			}
			
			@Override
			public void dragEnter(DNDEvent e) {
				
				e.getStatus().setStatus(true);
				
				if(e.getDragSource().getData() instanceof TranscriptLinkData){
					e.getStatus().update("Link Box with Transcript");	
				}
				
				super.dragEnter(e);
			}

			@Override
			public void dragLeave(DNDEvent e) {
				e.getStatus().setStatus(false);
				e.getStatus().update("");
				super.dragLeave(e);
			}
			
		});
		
	}
	
	protected abstract void sendUpdateTranscriptLinkToServer(String mapID, int elementID, TranscriptLinkData tData);
	protected abstract void sendCreateTranscriptLinkToServer(String mapID, String boxID, TranscriptLinkData tData);
	protected abstract void sendAddElementToServer(String mapID, int boxID, String elementType, String elementID);
	protected abstract void sendRemoveElementToServer(String mapId, int elementId);
	
	public void setupToolButtons() {
		
		removeAllTools();
		
		if (editMode) {
			
			for(ElementInfo childInfo : info.getChildElements().values()){		
			
				int elementCount = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID()).size();
				if((childInfo.getElementOption(ParameterTypes.ManualAdd) == null || !childInfo.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childInfo.getMaxQuantity()){
				
					initPlusButton();
					break;
				}
			}
			
			for(ElementInfo childInfo : info.getChildElements().values()){		
				
				int elementCount = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID()).size();
				
				if(elementCount > childInfo.getMinQuantity()){
					
					initMinusButton();
					break;
				}
			}
			
			
			initXButton();
			
		} else {
			
			initGearButton();
			initHelpButton();
			
		}
		
	}
	
	
	private void initPlusButton() {
		
		ToolButton plusButton = new ToolButton("x-tool-plus", new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(IconButtonEvent ce) {
			
				IconButton button = ce.getIconButton();
				Point point = button.getPosition(false);
			
				final BoxElementAdditionPopup popup = new BoxElementAdditionPopup();
			
				for (ElementInfo childElement : info.getChildElements().values()) {		
				
					// Calculate current element Count
					int elementCount = MVCHelper.getChildModelsByElementID(model, childElement.getElementID()).size();
				
					if ((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()) {
				
						// Set elementname
						if (childElement.getElementOption(ParameterTypes.LongLabel)!= null) {
						
							popup.addLabel(childElement.getElementOption(ParameterTypes.LongLabel), childElement);
						
						} else if (childElement.getElementOption(ParameterTypes.Label)!= null) {
						
							popup.addLabel(childElement.getElementOption(ParameterTypes.Label), childElement);
						
						} else {
						
							popup.addLabel(childElement.getElementID(), childElement);
						}
					
					}
				
				}
			
				hideToolTip();
				popup.showAt(point.x, point.y);
			
				if (getParent() instanceof ElementEditDialog) {
				
					ElementEditDialog dialog = (ElementEditDialog)getParent();
					dialog.setEnabled(false);
					
					popup.addListener(Events.Hide, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							ElementEditDialog dialog = (ElementEditDialog)getParent();
							dialog.setEnabled(true);
							
							ElementInfo selectedLabel = popup.getSelectedLabel();
						
							if (selectedLabel != null) {
								String mapId = model.getValue(ParameterTypes.MapId);
								int modelId = model.getId();
							
								String elementId = selectedLabel.getElementID();
								String elementType = selectedLabel.getElementType();
							
//								actionSender.sendActionPackage(actionFactory.addElement(mapId, modelId, elementType, elementId));
								sendAddElementToServer(mapId, modelId, elementType, elementId);
							}
						}
					});
				} 
				
			}
			
		});
		
		titleBar.add(plusButton);
		
	}
	
	
	private void initMinusButton() {
		
		ToolButton minusButton = new ToolButton("x-tool-minus", new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(IconButtonEvent ce) {
				
				IconButton button = ce.getIconButton();
				Point point = button.getPosition(false);
				
				final BoxElementDeletionPopup popup = new BoxElementDeletionPopup();
				
				for (Component component : cellBody.getItems()) {
					
					if (component instanceof AbstractChildElement) {
						
						popup.addLabel(model, (AbstractChildElement)component);
					}
				}
				
				
				hideToolTip();
				popup.showAt(point.x, point.y);
				
				if (getParent() instanceof ElementEditDialog) {
					
					ElementEditDialog dialog = (ElementEditDialog)getParent();
					dialog.setEnabled(false);
				
					popup.addListener(Events.Hide, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							ElementEditDialog dialog = (ElementEditDialog)getParent();
							dialog.setEnabled(true);
							
							AbstractUnspecifiedElementModel selectedModel = popup.getSelectedModel();
							if (selectedModel != null) {
								
								//*************************************************************************************
								//	ACTIONPACKAGE: 
								//		ACTION: 
								//			Category: MAP
								//			Command: REMOVE-ELEMENT
								//			Parameter: MAP-ID : 49
								//			Parameter: ID : 16
								//*************************************************************************************
								
								String mapId = model.getValue(ParameterTypes.MapId);
//								actionSender.sendActionPackage(actionFactory.removeElement(mapId, selectedModel.getId()));
								sendRemoveElementToServer(mapId, selectedModel.getId());
							}
						}
					});
				} 
				
			}
		});
		
		titleBar.add(minusButton);
	}
	
	
	private void initXButton() {
	
		ToolButton xButton = new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				
				Logger.log("===== X button model id = " + model.getId() + " =====", Logger.DEBUG_DETAILS);
				
				if (tableCellInfo.getMap().isDeleteElementsWithoutConfirmation()) {
					
					if (getParent() instanceof ElementEditDialog) {
						
						ElementEditDialog dialog = (ElementEditDialog)getParent();
						restoreToTable();
						dialog.hide();
					}
					
					//*************************************************************************************
					//	ACTIONPACKAGE: 
					//		ACTION: 
					//			Category: MAP
					//			Command: REMOVE-ELEMENT
					//			Parameter: MAP-ID : 49
					//			Parameter: ID : 7
					//*************************************************************************************
					
					String mapId = model.getValue(ParameterTypes.MapId);
					int modelId = model.getId();
					
//					actionSender.sendActionPackage(actionFactory.removeElement(mapId, modelId));
					sendRemoveElementToServer(mapId, modelId);

				} else {		

					IconButton button = ce.getIconButton();
					Point point = button.getPosition(false);
					
					hideToolTip();
					
					final BoxClosePopup popup = new BoxClosePopup();
					popup.showAt(point.x, point.y);
					
					
					if (getParent() instanceof ElementEditDialog) {
						
						ElementEditDialog dialog = (ElementEditDialog)getParent();
						dialog.setEnabled(false);
						
						popup.addListener(Events.Hide, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								ElementEditDialog dialog = (ElementEditDialog)getParent();
								dialog.setEnabled(true);
								
								Boolean isClose = popup.isClose();
								Boolean conformChecked = popup.getConform().getValue();
								
								if (isClose) {
									
									restoreToTable();
									dialog.hide();
									
									String mapId = model.getValue(ParameterTypes.MapId);
									int modelId = model.getId();
									
									Logger.log("====== mapId = " + mapId + " ====== modelId = " + modelId + " =======", Logger.DEBUG_DETAILS);
									
//									actionSender.sendActionPackage(actionFactory.removeElement(mapId, modelId));
									sendRemoveElementToServer(mapId, modelId);
									
								}
								
								tableCellInfo.getMap().setDeleteElementsWithoutConfirmation(conformChecked);
								
							}
						});
					}
					
				}
			}
		});
		
		titleBar.add(xButton);
	}
	
	
	
	private void initGearButton() {
		
		ToolButton gearButton = new ToolButton("x-tool-gear", new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				Logger.log("===== model id = " + model.getId() + " =====", Logger.DEBUG_DETAILS);
				
				ElementEditDialog dialog = new ElementEditDialog(AbstractTableCell.this);
				dialog.show();
			}
		});
		
		titleBar.add(gearButton);
	}
	
	
	private void initHelpButton() {
		
		if (TableCellTypeEnum.RELEATION == type) {
			
			Vector<AbstractUnspecifiedElementModel> children = model.getChildren();
			final Vector<AbstractUnspecifiedElementModel> relatedRelationModels = new Vector<AbstractUnspecifiedElementModel>();
			
			for (AbstractUnspecifiedElementModel m: children) {
				
				if (m.getParents().size() == 2 && m.getParents().get(1) == model) {
					
					relatedRelationModels.add(m);
				}
				
			}
			
			ToolButton helpButton = new ToolButton("x-tool-help", new SelectionListener<IconButtonEvent>() {

				@Override
				public void componentSelected(IconButtonEvent ce) {
					
//					new RelatedRelationDialog(relatedRelationModels, session);
					getRelatedRelationDialog(relatedRelationModels, getSession());
				}
				
			});
			
			if (relatedRelationModels.size() > 0) {
				
				titleBar.add(helpButton);
			}
			
		}
	}
	protected abstract AbstractRelatedRelationDialog getRelatedRelationDialog(Vector<AbstractUnspecifiedElementModel> models, AbstractMVCViewSession session);
	
	public void restoreToTable() {
		
		changeView(zoom);
		setSize(size.width, size.height);
		
		FlexTable flexTable = tableCellInfo.getFlexTable();
		flexTable.setWidget(tableCellInfo.getRowNumber(), tableCellInfo.getColNumber(), this);
		
		
	}
	
	
	public void restoreView(boolean expand) {
		
		if (expand) {
			
			titleLabel.setLabel(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
			cellBody.setVisible(true);
			Size size = AbstractMapTable.cellSize.get(TableZoomEnum.SIZE100);
			setSize(size.width, size.height);
			
			setAwarenessVisible(true);
			
		} else {
			
			changeView(zoom);
			setSize(size.width, size.height);
		}
		
	}
	
	
	private void removeAllTools() {
		
		List<Component> list = new ArrayList<Component>();
		
		for (Component component: titleBar.getItems()) {
			
			if (component instanceof ToolButton) {
				
				list.add(component);
			}
		}
		
		for (Component component: list) {
			
			component.removeFromParent();
		}
	}
	
	
	private void hideToolButtons() {
		
		for (Component component: titleBar.getItems()) {
			
			if (component instanceof ToolButton) {
				
				component.setVisible(false);
			}
		}
		
		setToolTipVisible(false);
	}
	
	
	private int calElementPosition(String newChildElementID){

		if(cellBody.getItemCount() == 0){
			return 0;
		}

		// Calculate ElementID before
		String beforeElementID = "";
		for (String tempElementID: info.getChildElements().keySet()) {
			if (!tempElementID.equals(newChildElementID)) {
				
				for(Component component : cellBody.getItems()){	
					if (component instanceof AbstractTextElement) {
						AbstractTextElement tempElement = (AbstractTextElement)component;
						if(tempElement.getConnectedModel().getElementid().equals(tempElementID)){
							beforeElementID = tempElementID;
							break;
						}
					}
					
				}
			}
			else{
				break;
			}
		}

		// Calculate new Position
		int pos = 0;
		boolean elementIDreached = false;
		boolean beforeElementIDreached = false;
		for(Component component : cellBody.getItems()){
			if (component instanceof AbstractTextElement) {
				
				AbstractTextElement tempElement = (AbstractTextElement)component;
				String tempElementID = tempElement.getConnectedModel().getElementid();		

				if(beforeElementID.equals(tempElementID)){
					beforeElementIDreached= true;
					pos++;
					continue;
				}

				if(beforeElementIDreached && !tempElementID.equals(beforeElementID)){
					break;
				}

				if(tempElementID.equals(newChildElementID)){
					elementIDreached=true;
					pos++;
					continue;
				}

				if(elementIDreached && !tempElementID.equals(newChildElementID)){
					break;
				}

				pos++;
			}
		}
		return pos;
	}

	
	//*************************************************************************************
	//  add by Erkang:  display Argument with another ViewSession
	//
	//  ACTION: 
	//		Category: MAP
	//		Command: CREATE-ELEMENT
	//		Parameter: ID : 7
	//		Parameter: TYPE : box
	//		Parameter: USERNAME : t1
	//		Parameter: MAP-ID : 49
	//		Parameter: ELEMENT-ID : fact
	//		Parameter: POS-X : 2772
	//		Parameter: POS-Y : 2747
	//		Parameter: USERACTION-ID : xrlcfgk49z7u1277471052924
	//		Parameter: NUM-ACTIONS : 3
	//		Parameter: ROOTELEMENTID : 1
	//
	//*************************************************************************************
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************
	
	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {
		
		if (vname.equals("ROOTELEMENTID")) {
			
			rootElementId = model.getValue(ParameterTypes.RootElementId);
			
			// UI View
			if (zoom == TableZoomEnum.SIZE25) {
				
				titleLabel.setLabel(rootElementId);
				
			} else if (zoom == TableZoomEnum.SIZE50) {
				
				titleLabel.setLabel(getShortTitle(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading)));
				
			} else {
				
				titleLabel.setLabel(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
			}
			
			titleBar.layout();
		}
		
	}

	
	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		
//		removeFromParent();		
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		
		if (this.model == null) {
			this.model = model;
			return true;
		}
		else{
			return false;
		}
	}
	

	@Override
	public AbstractUnspecifiedElementModel getConnectedModel() {
		return model;
	}

	
	//*************************************************************************************
	// CellContainer
	//*************************************************************************************

	@Override
	public MVCViewRecipient addComponent(String type, String id) {
		
		RowData areaElementData = new RowData(0.95, 1, new Margins(1, 0, 1, 12));
		RowData otherElementData = new RowData(0.95, 18, new Margins(1, 0, 1, 12));
		
		if ("awareness".equalsIgnoreCase(type)) {
			
			// UI View
			if (zoom != TableZoomEnum.SIZE25) {
				
				setAwarenessVisible(true);
				
			} else {
				
				setAwarenessVisible(false);
			}
			
			return awarenessElement;
			
		} else if ("text".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			TextElement textElement = new TextElement(childInfo);
			AbstractTextElement textElement = getTextElement(childInfo);
			textElement.setArgumentMapTable(tableCellInfo.getMap());
			
			if (tableCellInfo.getColNumber() == 0) {
				textElement.setReadOnly(true);
//				textElement.setEnabled(false);
			}
			
			
			cellBody.insert(textElement, calElementPosition(id));
			
			if ("textfield".equalsIgnoreCase(textElement.getTexttype())) {
				
				cellBody.setLayoutData(textElement, otherElementData);
			} else {
				
				cellBody.setLayoutData(textElement, areaElementData);
			}
			
			cellBody.layout();
			
			// UI View
			if (getParent() instanceof ElementEditDialog) {
				
				cellBody.setVisible(true);
				
			} else {
				
				if (zoom == TableZoomEnum.SIZE100 || zoom == TableZoomEnum.SIZE75) {
					cellBody.setVisible(true);
				} else {
					cellBody.setVisible(false);
				}
			}
			
			return textElement;
			
		} else if ("rating".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			RatingElement ratingElement = new RatingElement(childInfo);
			AbstractRatingElement ratingElement = getRatingElement(childInfo);
			ratingElement.setArgumentMapTable(tableCellInfo.getMap());
			
			cellBody.insert(ratingElement, calElementPosition(id));
			cellBody.setLayoutData(ratingElement, otherElementData);
			cellBody.layout();
			
			return ratingElement;
			
		} else if ("url".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			UrlElement urlElement = new UrlElement(childInfo);
			AbstractUrlElement urlElement = getUrlElement(childInfo);
			urlElement.setArgumentMapTable(tableCellInfo.getMap());
			
			cellBody.insert(urlElement, calElementPosition(id));
			cellBody.setLayoutData(urlElement, otherElementData);
			cellBody.layout();
			
			return urlElement;
			
		} else if ("transcript-link".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			TranscriptElement transcriptElement = new TranscriptElement(childInfo);
			AbstractTranscriptElement transcriptElement = getTranscriptElement(childInfo);
			transcriptElement.setArgumentMapTable(tableCellInfo.getMap());
			
			cellBody.insert(transcriptElement, calElementPosition(id));
			cellBody.setLayoutData(transcriptElement, otherElementData);
			cellBody.layout();
			
			return transcriptElement;
			
		} else {
			
			
		}
		
		
		return null;
	}

	protected abstract AbstractRatingElement getRatingElement(ElementInfo info);
	protected abstract AbstractTextElement getTextElement(ElementInfo info);
	protected abstract AbstractTranscriptElement getTranscriptElement(ElementInfo childInfo);
	protected abstract AbstractUrlElement getUrlElement(ElementInfo childInfo);
	
	
	private boolean containsTranscript() {
		
		List<Component> components = cellBody.getItems();
		
		for (Component component : components) {
			
			if (component instanceof AbstractTranscriptElement) {
				
				return true;
			}
		}
		
		return false;
	}
	
	private AbstractUnspecifiedElementModel getTranscriptModel() {
		
		List<Component> components = cellBody.getItems();
		
		for (Component component : components) {
			
			if (component instanceof AbstractTranscriptElement) {
				
				AbstractTranscriptElement transcriptElement = (AbstractTranscriptElement)component;
				
				return transcriptElement.getConnectedModel();
			}
		}
		
		return null;
	}
	
	

	@Override
	public void removeComponent(AbstractUnspecifiedElementModel model) {
		
		List<Component> components = cellBody.getItems();
		
		for (Component component : components) {
			
			if (component instanceof MVCViewRecipient) {
				
				MVCViewRecipient recipient = (MVCViewRecipient)component;
				AbstractUnspecifiedElementModel elementModel = recipient.getConnectedModel();
				
				if (model.equals(elementModel)) {
					
					cellBody.remove(component);
					cellBody.layout();
					
					break;
				}
				
			}
		}
		
	}

	
	public TableCellInfo getTableCellInfo() {
		return tableCellInfo;
	}


	public void setTableCellInfo(TableCellInfo tableCellInfo) {
		this.tableCellInfo = tableCellInfo;
	}

	
	
	public void changeView(TableZoomEnum zoom) {
		
		setDefaultSize(zoom);
		
		if (zoom == TableZoomEnum.SIZE25) {
			
			titleLabel.setLabel(rootElementId);
			cellBody.setVisible(false);
			setAwarenessVisible(false);

			initToolTip();
		}
		
		
		if (zoom == TableZoomEnum.SIZE50) {
			
			titleLabel.setLabel(getShortTitle(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading)));
			cellBody.setVisible(false);
			setAwarenessVisible(true);

			initToolTip();
		}
		
		
		if (zoom == TableZoomEnum.SIZE75) {
			
			titleLabel.setLabel(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
			cellBody.setVisible(true);
			setAwarenessVisible(true);

			setToolTipVisible(false);
		}
		
		
		if (zoom == TableZoomEnum.SIZE100) {
			
			titleLabel.setLabel(rootElementId + " . " + info.getElementOption(ParameterTypes.Heading));
			cellBody.setVisible(true);
			setAwarenessVisible(true);
			
			setToolTipVisible(false);
		}
		
	}
	
	
	private String getShortTitle(String string) {
		
		if (string.length() > 10) {
			
			return string.substring(0, 10) + " ...";
		}
		
		return string;
	}
	
	
	private void setAwarenessVisible(boolean visible) {
		
		if (awarenessElement != null) {
			
			if (visible) {
				
				add(awarenessElement, new BorderLayoutData(LayoutRegion.SOUTH, 20));
				
				layout();
				
			} else {
				
				awarenessElement.removeFromParent();
			}
		}
	}
	
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************
	
	@Override
	public FocusableInterface getFocusParent() {
		
		return tableCellInfo.getMap();
	}


	@Override
	public void setElementFocus(boolean focus) {

		
	}
	
	
	//*************************************************************************************
	// getter & setter
	//*************************************************************************************
	
	@SuppressWarnings("unchecked")
	public AbstractUnspecifiedElementModel getModel() {
		return model;
	}


	public void setModel(AbstractUnspecifiedElementModel model) {
		this.model = model;
	}


	/**
	 * @return the info
	 */
	public ElementInfo getInfo() {
		return info;
	}


	/**
	 * @param info the info to set
	 */
	public void setInfo(ElementInfo info) {
		this.info = info;
	}



	public Size getInitSize() {
		
		return size;
		
	}


	public boolean isEditMode() {
		return editMode;
	}


	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}


	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}


	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}



	/**
	 * @return the type
	 */
	public TableCellTypeEnum getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(TableCellTypeEnum type) {
		this.type = type;
	}



	/**
	 * @return the session
	 */
	public abstract AbstractMVCViewSession getSession();
//	{
//		return session;
//	}



	/**
	 * @param session the session to set
	 */
	public abstract void setSession(AbstractMVCViewSession session);
//	{
//		this.session = session;
//	}

}
