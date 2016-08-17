package lasad.gwt.client.ui.workspace.tableview;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractRatingElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTextElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractTranscriptElement;
import lasad.gwt.client.ui.workspace.tableview.elements.AbstractUrlElement;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Event;

/**
 * A Component with child components(text, user, ...) for the Relation, which is created with 
 * an Element and a Relation. 
 * 
 * @author erkang
 *
 */
public abstract class AbstractRelatedRelation extends ContentPanel implements MVCViewRecipient, CellContainer{

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
	
	private LayoutContainer cellBody;
	private TableCellInfo tableCellInfo;
	
	private String rootElementId;
//	private boolean editMode;
	
	private String color;
	
//	private MVCViewSession session;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractRelatedRelation(final ElementInfo info, TableCellTypeEnum type) {
		
		this.info = info;
		this.type = type;
		
		setBodyBorder(false);
		setLayout(new FitLayout());
		setCollapsible(true);
		
		//***********************
		// Header
		//***********************
		setHeading(info.getElementOption(ParameterTypes.Heading));
		getHeader().setStyleAttribute("background", info.getUiOption(ParameterTypes.BackgroundColor));
		getHeader().setStyleAttribute("color", info.getUiOption(ParameterTypes.FontColor));

		color = info.getUiOption(ParameterTypes.BackgroundColor);
		
		//***********************
		// Body
		//***********************
		cellBody = new LayoutContainer();
		cellBody.setLayout(new RowLayout());
		cellBody.setBorders(false);
		cellBody.setScrollMode(Scroll.AUTOY);
//		cellBody.setEnabled(false);
		
		add(cellBody, new FitData(1));
		
		//***********************
		// Bottom Bar
		//***********************
		AwarenessElement awarenessElement = new AwarenessElement(new ElementInfo());
		awarenessElement.setHeight(20);
		awarenessElement.setVisible(true);
		setBottomComponent(awarenessElement);
		
	}
	
	@Override
	protected void afterRender() {
		super.afterRender();
		
		Listener<BaseEvent> listener = new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				
				if (be.getType().equals(Events.OnMouseMove)) {
					
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
	
	
	public void setDefaultSize(TableZoomEnum zoom) {
		
		this.zoom = zoom;
		size = AbstractMapTable.cellSize.get(zoom);
		setSize(size.width, size.height);
	}
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	public void setupToolButtons() {
		
		removeAllTools();
		
		initXButton();
		initHelpButton();
		
	}
	
	
	private void hideToolButtons() {
		
		for(int i= 0; i < getHeader().getToolCount() ; i++) {
			getHeader().getTool(i).setVisible(false);
		}
	}
	
	
	private void removeAllTools() {
		
		List<Component> buttons = new ArrayList<Component>();
		
		for(int i= 0; i < getHeader().getToolCount() ; i++) {
			buttons.add(getHeader().getTool(i));
		}
		
		for (Component button: buttons) {
			
			getHeader().removeTool(button);
		}
		
	}
	
	
	private void initXButton() {
		
		ToolButton xButton = new ToolButton("x-tool-close", new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				
				Logger.log("===== X button model id = " + model.getId() + " =====", Logger.DEBUG_DETAILS);
				
				if (tableCellInfo.getMap().isDeleteElementsWithoutConfirmation()) {
					
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
					
					final BoxClosePopup popup = new BoxClosePopup();
					popup.showAt(point.x, point.y);
					
					
					if (getParent() instanceof AbstractRelatedRelationDialog) {
						
						AbstractRelatedRelationDialog dialog = (AbstractRelatedRelationDialog)getParent();
						dialog.setEnabled(false);
					
						popup.addListener(Events.Hide, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								AbstractRelatedRelationDialog dialog = (AbstractRelatedRelationDialog)getParent();
								dialog.setEnabled(true);
								
								Boolean isClose = popup.isClose();
								Boolean conformChecked = popup.getConform().getValue();
								
								if (isClose) {
									
									String mapId = model.getValue(ParameterTypes.MapId);
									int modelId = model.getId();
									
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
		
		getHeader().addTool(xButton);
		
	}
	
	protected abstract void sendRemoveElementToServer(String mapID, int elementID);
	
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
					
					setEnabled(false);
					
//					RelatedRelationDialog dialog = new RelatedRelationDialog(relatedRelationModels, session);
					AbstractRelatedRelationDialog dialog = getRelatedRelationDialog(relatedRelationModels, getSession());
					dialog.setPosition(ce.getClientX() + 10, ce.getClientY() + 10);
					dialog.addListener(Events.Hide, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							setEnabled(true);
//							cellBody.setEnabled(false);
						}
					
					});
				}
				
			});
			
			if (relatedRelationModels.size() > 0) {
				
				getHeader().addTool(helpButton);
			}
			
		}
	}
	
	protected abstract AbstractRelatedRelationDialog getRelatedRelationDialog(Vector<AbstractUnspecifiedElementModel> models, AbstractMVCViewSession session);
	
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
			if (zoom != TableZoomEnum.SIZE25) {
				
				setHeading(rootElementId + " . " + getHeading());
				
			} else {
				
				setHeading(rootElementId);
			}
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
				
				getBottomComponent().setVisible(true);
			}
			
			MVCViewRecipient recipient = (MVCViewRecipient)getBottomComponent();
			
			return recipient;
			
		} else if ("text".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			TextElement textElement = new TextElement(childInfo);
			AbstractTextElement textElement = getTextElement(childInfo); 
			textElement.setArgumentMapTable(tableCellInfo.getMap());
			textElement.setReadOnly(true);
			
			cellBody.insert(textElement, calElementPosition(id));
			
			if ("textfield".equalsIgnoreCase(textElement.getTexttype())) {
				
				cellBody.setLayoutData(textElement, otherElementData);
			} else {
				
				cellBody.setLayoutData(textElement, areaElementData);
			}
			
			cellBody.layout();
			
			
			// UI View
			if (getParent() instanceof AbstractRelatedRelationDialog) {
				
				cellBody.setVisible(true);
				
			} else {
				
				if (zoom == TableZoomEnum.SIZE100) {
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
			ratingElement.setReadOnly(true);
			
			cellBody.insert(ratingElement, calElementPosition(id));
			cellBody.setLayoutData(ratingElement, otherElementData);
			cellBody.layout();
			
			return ratingElement;
			
		} else if ("url".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			UrlElement urlElement = new UrlElement(childInfo);
			AbstractUrlElement urlElement = getUrlElement(childInfo);
			urlElement.setArgumentMapTable(tableCellInfo.getMap());
			urlElement.setReadOnly(true);
			
			cellBody.insert(urlElement, calElementPosition(id));
			cellBody.setLayoutData(urlElement, otherElementData);
			cellBody.layout();
			
			return urlElement;
			
		} else if ("transcript-link".equalsIgnoreCase(type)) {
			
			ElementInfo childInfo = info.getChildElements().get(id);
//			TranscriptElement transcriptElement = new TranscriptElement(childInfo);
			AbstractTranscriptElement transcriptElement = getTranscriptElement(childInfo);
			transcriptElement.setArgumentMapTable(tableCellInfo.getMap());
			transcriptElement.setReadOnly(true);
			
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