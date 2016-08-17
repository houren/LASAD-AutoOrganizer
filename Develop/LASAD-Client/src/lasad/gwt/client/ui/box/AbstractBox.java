package lasad.gwt.client.ui.box;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.helper.connector.UIObjectConnector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.LASADBoxComponent;
import lasad.gwt.client.ui.box.elements.AbstractBoxHeaderElement;
import lasad.gwt.client.ui.box.helper.BoxConnectorElement;
import lasad.gwt.client.ui.box.helper.BoxDraggable;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainer;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.ExtendedElementContainerMasterInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.SelectableInterface;
import lasad.gwt.client.ui.common.elements.AbstractExtendedTextElement;
import lasad.gwt.client.ui.common.fade.FadeableElementInterface;
import lasad.gwt.client.ui.common.helper.TextSelection;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

/**
 * Visual representation of each box in the graph-layout
 */
public abstract class AbstractBox extends LASADBoxComponent implements MVCViewRecipient, ExtendedElementContainerInterface, ExtendedElementContainerMasterInterface, HighlightableElementInterface, FadeableElementInterface, SelectableInterface {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private SelectionDetailsPanel myDetails; // Contains all elements in the
	// box, even those which should
	// not be visible in the normal
	// view, but are defined in the
	// ontology to appear in the
	// details.

	private ExtendedElementContainer extendedElementContainer = null; // The
	// container that holds all
	// elements of the box,
	// e.g. text fields, url, etc.

//	private MVController myController;

	protected GraphMap myMap; // Reference to the corresponding map

	private Connector cn = null; // The connector needed to be able to create
	// curves / lines

	private String title; // Title of the box, which appears in the header
	private String color; // Color code of the header
	private String border = "standard"; // The outter border of the box
	private TranscriptLinkData tData = null;
	private boolean selected = false, autogrow = false;

	// Constants to calculate elements size correctly
	public static final int CONNECTOR_WIDTH = 24, CONNECTOR_HEIGHT = 12;
	public static final int BORDER_WIDTH = 5, BORDER_HEIGHT = 5;
	private int HEADER_HEIGHT = 13;

	private final int HIGHLIGHT_TIMER = 30000; // Time until a highlight will
	// disappear
	// Size of the box
	private int width = 0, height = 0;

	// This is a DIV element which is essential for correct positioning in
	// Firefox
	private Element positioningElement;

	// DOM elements for 9x9 layout, please don't change the indentation
	private Element outerGrid;

	private Element outerGridTBody;

	private Element outerTopRow;
	private Element outerTopRowLeftCell;

	private Element resizeCornerNE;
	private Element outerTopRowMiddleCell;
	private Element northArea;
	private BoxConnectorElement northConnector;
	private Element outerTopRowRightCell;
	private Element resizeCornerNW;
	private Element outerMiddleRow;
	private Element outerMiddleRowLeftCell;

	private Element westArea;
	private BoxConnectorElement westConnector;
	private Element outerMiddleRowMiddleCell;
	private Element innerGrid;
	private Element innerGridTBody;
	private Element innerTopRow;
	private Element innerTopRowLeftCell;

	private Element borderULC;
	private Element innerTopRowMiddleCell;
	private Element borderUMC;
	private Element innerTopRowRightCell;
	private Element borderURC;
	private Element innerMiddleRow;
	private Element innerMiddleRowLeftCell;

	private Element borderMLC;
	private Element innerMiddleRowMiddleCell;
	private Element boxContentDiv;
	private Element innerMiddleRowRightCell;
	private Element borderMRC;
	private Element innerBottomRow;
	private Element innerBottomRowLeftCell;

	private Element borderBLC;
	private Element innerBottomRowMiddleCell;
	private Element borderBMC;
	private Element innerBottomRowRightCell;
	private Element borderBRC;
	private Element outerMiddleRowRightCell;
	public Element eastArea;

	private BoxConnectorElement eastConnector;
	private Element outerBottomRow;
	private Element outerBottomRowLeftCell;
	private Element resizeCornerSW;
	private Element outerBottomRowMiddleCell;
	private Element southArea;
	private BoxConnectorElement southConnector;
	private Element outerBottomRowRightCell;
	private Element resizeCornerSE;
	private Element rootElement; // Main element to make the box visible
	private AbstractBoxHeaderElement boxHeading; // Header of the box. Present in all
	// boxes. Needed for the
	// DragListener

	// Highlighting Timer to ensure online one Timer per box
	private Timer timer = null;
	private Listener<ComponentEvent> componentListener = null;
	
	private boolean isReplay;

	private Timer hoverTimer = new Timer() {
		public void run() {
			AbstractBox.this.setResizeCornerVisibility(false);
			AbstractBox.this.boxHeading.setEditButtonVisibility(false);
		}
	};

	private Resizable r = null;
	
	/**
	 * @param map
	 */
	private AbstractBox(GraphMap map) {
		this.myMap = map;
		this.initBoxElements();
		this.setVisible(false);

		// Multiply values by 2, because of complicated structure of the box widget
		// 3x3 outer grid, etc.
		cn = UIObjectConnector.wrap(this, BORDER_WIDTH*2, BORDER_HEIGHT*2); // To set the whole box as connector

		disableTextSelection();
	}

	/**
	 * Creates a new box called by ArgumentMapMVWViewSession
	 * 
	 * @param map
	 * @param width
	 * @param height
	 * @param title
	 * @param typeColor
	 * @param model
	 */
	private AbstractBox(GraphMap map, ElementInfo config, boolean isR) {
		this(map);
		this.config = config;
		String width = config.getUiOption(ParameterTypes.Width);
		String height = config.getUiOption(ParameterTypes.Height);
		
		this.setSize(Integer.parseInt(width), Integer.parseInt(height));
		this.title = config.getElementOption(ParameterTypes.Heading);
		this.color = config.getUiOption(ParameterTypes.BackgroundColor);
		setBorder(config.getUiOption(ParameterTypes.Border));

		//boxHeading = new BoxHeaderElement(this, this.title,isR);
		
		boxHeading = createBoxHeaderElement(this, this.title,isR);
		
		boxHeading.render(boxContentDiv, 0);
		ComponentHelper.doAttach(boxHeading);
		
	}
	protected abstract AbstractBoxHeaderElement createBoxHeaderElement(AbstractBox correspondingBox, String title, boolean isReplay);

	public AbstractBox(GraphMap map, ElementInfo info, SelectionDetailsPanel sdp, boolean isR) {
		this(map, info,isR);
		this.myDetails = sdp;
		this.isReplay = isR;
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element) {
		extendedElementContainer.addExtendedElement(element);
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
		extendedElementContainer.addExtendedElement(element, pos);
	}

	protected void afterRender() {
		super.afterRender();

		this.setColor(this.getColor());
		if(!this.isReplay)
		{
			makeDraggable(myMap);
			makeResizeable();
			createDNDTarget();
		}
		
		setComponentHandling();
		AbstractBox.this.el().updateZIndex(1);

		setResizeCornerVisibility(false);
		calculateElementsSize();
	}

	/**
	 * This method calculates the elements' sizes. It should be called each time
	 * the box size changes. Thus it is called in the setSize() method.
	 * 
	 * Don't change anything unless you know what you are doing! Otherwise,
	 * increase counter: TOTAL_HOURS_WASTED = 0;
	 * 
	 * @author Frank Loll
	 */
	public void calculateElementsSize() {
		// Outer 3x3 table
		DOM.setStyleAttribute(outerGrid, "height", this.height + "px");
		DOM.setStyleAttribute(outerGrid, "width", this.width + "px");

		DOM.setStyleAttribute(outerGridTBody, "height", this.height + "px");
		DOM.setStyleAttribute(outerGridTBody, "width", this.width + "px");

		DOM.setStyleAttribute(outerTopRow, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerTopRowLeftCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerTopRowMiddleCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerTopRowRightCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerTopRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerMiddleRow, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerMiddleRowLeftCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerMiddleRowMiddleCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerMiddleRowRightCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(outerMiddleRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerBottomRow, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRow, "width", this.width + "px");

		DOM.setStyleAttribute(outerBottomRowLeftCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowLeftCell, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(outerBottomRowMiddleCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(outerBottomRowRightCell, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(outerBottomRowRightCell, "width", CONNECTOR_HEIGHT + "px");

		// Inner 3x3 table
		DOM.setStyleAttribute(innerGrid, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(innerGrid, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		
		DOM.setStyleAttribute(innerGridTBody, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");
		DOM.setStyleAttribute(innerGridTBody, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRow, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRowLeftCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerTopRowMiddleCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerTopRowRightCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerTopRowRightCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerMiddleRow, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerMiddleRowLeftCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerMiddleRowMiddleCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerMiddleRowRightCell, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(innerMiddleRowRightCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerBottomRow, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRow, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT) + "px");

		DOM.setStyleAttribute(innerBottomRowLeftCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowLeftCell, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(innerBottomRowMiddleCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowMiddleCell, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(innerBottomRowRightCell, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(innerBottomRowRightCell, "width", BORDER_HEIGHT + "px");

		// Borders
		// Top row
		DOM.setStyleAttribute(borderULC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderULC, "width", BORDER_WIDTH + "px");

		DOM.setStyleAttribute(borderUMC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderUMC, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(borderURC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderURC, "width", BORDER_WIDTH + "px");

		// Middle row
		DOM.setStyleAttribute(borderMLC, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(borderMLC, "width", BORDER_HEIGHT + "px");

		DOM.setStyleAttribute(borderMRC, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(borderMRC, "width", BORDER_WIDTH + "px");

		// Bottom row
		DOM.setStyleAttribute(borderBLC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBLC, "width", BORDER_WIDTH + "px");

		DOM.setStyleAttribute(borderBMC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBMC, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		DOM.setStyleAttribute(borderBRC, "height", BORDER_HEIGHT + "px");
		DOM.setStyleAttribute(borderBRC, "width", BORDER_WIDTH + "px");

		// Resize corners
		DOM.setStyleAttribute(resizeCornerSW, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerSW, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerNE, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerNE, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerNW, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerNW, "width", CONNECTOR_HEIGHT + "px");

		DOM.setStyleAttribute(resizeCornerSE, "height", CONNECTOR_HEIGHT + "px");
		DOM.setStyleAttribute(resizeCornerSE, "width", CONNECTOR_HEIGHT + "px");

		// Box content Div: Contains all elements of the concrete box.
		DOM.setStyleAttribute(boxContentDiv, "height", (this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");
		DOM.setStyleAttribute(boxContentDiv, "width", (this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT) + "px");

		// Update Extended Element
				//MODIFIED BY BM ---------------------------------------------------------------------------------------------
				if (!autogrow) {
					if (extendedElementContainer != null) {
						extendedElementContainer.setWidth(this.width - CONNECTOR_HEIGHT
								- CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT);
						if(this.height
								- CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT
								- BORDER_HEIGHT - this.fontSize - BORDER_HEIGHT > 0){
							extendedElementContainer.setHeight(this.height
									- CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT
									- BORDER_HEIGHT - this.fontSize - BORDER_HEIGHT);
						}else{
							extendedElementContainer.setHeight(this.height
									- CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT
									- BORDER_HEIGHT - HEADER_HEIGHT);
						}
					}
				}
				//MODIFIED END BY BM ---------------------------------------------------------------------------------------------
				
		// Update Extended Element

//		if (extendedElementContainer != null) {
//			extendedElementContainer.setWidth(this.width - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT);
//			extendedElementContainer.setHeight(this.height - CONNECTOR_HEIGHT - CONNECTOR_HEIGHT - BORDER_HEIGHT - BORDER_HEIGHT - HEADER_HEIGHT);
//		}

		// Connectors, i.e. the knobs used to create relations

		// North
		DOM.setStyleAttribute(northConnector.getElement(), "left", ((this.width - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// South
		DOM.setStyleAttribute(southConnector.getElement(), "left", ((this.width - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// East
		DOM.setStyleAttribute(eastConnector.getElement(), "top", ((this.height - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");

		// West
		DOM.setStyleAttribute(westConnector.getElement(), "top", ((this.height - CONNECTOR_WIDTH) / 2 - CONNECTOR_HEIGHT) + "px");
		
		this.setVisible(true);
	}

	//private int calltime = 0;
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vName) {

		if (vName == ParameterTypes.PosX || vName == ParameterTypes.PosY) {
			try{
				double doubleX = Double.parseDouble(model.getValue(ParameterTypes.PosX));
				int intX = (int)doubleX;
				double doubleY = Double.parseDouble(model.getValue(ParameterTypes.PosY));
				int intY = (int)doubleY;
				this.setPositionReal(intX, intY);
			}
			catch (Exception e){
				System.err.println("ERROR: in Box.changeValueMVC: bad coordiante values: " +  model.getValue(ParameterTypes.PosX) + " - " + model.getValue(ParameterTypes.PosY));
			}
		} else if ((vName == ParameterTypes.Width || vName == ParameterTypes.Height) && (model.getValue(ParameterTypes.Width) != null && model.getValue(ParameterTypes.Height) != null)) {
			this.setSize(Integer.parseInt(model.getValue(ParameterTypes.Width)), Integer.parseInt(model.getValue(ParameterTypes.Height)));
		} else if (vName == ParameterTypes.Highlighted) {
			if (model.getValue(ParameterTypes.Highlighted).equalsIgnoreCase("TRUE")) {
				this.setHighlight(true);
			} else if (model.getValue(ParameterTypes.Highlighted).equalsIgnoreCase("FALSE")) {
				this.setHighlight(false);
			}
		} else if (vName == ParameterTypes.Faded) {

			if (model.getValue(ParameterTypes.Faded).equalsIgnoreCase("TRUE")) {
				this.setFadedOut(true);
			} else if (model.getValue(ParameterTypes.Faded).equalsIgnoreCase("FALSE")) {
				this.setFadedOut(false);
			}
		} else if (vName == ParameterTypes.RootElementId) {
			this.checkForHighlight(model.getValue(ParameterTypes.UserName));
			if (this.boxHeading != null) {
				String elementID = model.getValue(ParameterTypes.RootElementId);
				this.boxHeading.setRootElementID(elementID);
				if(myDetails != null)
					this.myDetails.setTitle(elementID + "-" + this.title);
			}
		}
	}

	/**
	 * Check if author of action and current user are the same if not highlight
	 * the box
	 * 
	 * @param username
	 */
	public void checkForHighlight(String username) {
//		if (!username.equalsIgnoreCase(LASAD_Client.getInstance().getUsername())) {
		if (username != null && !LASAD_Client.getInstance().getUsername().equalsIgnoreCase(username)) {
			this.setHighlightAndBack();
		}
	}

	/**
	 * Handles drag and drop events like connecting two boxes
	 * 
	 * @return
	 */
	private DropTarget createDNDTarget() {
		DropTarget target = myMap.createBoxDropTarget(this);

		target.addDNDListener(new DNDListener() {
			public void dragDrop(DNDEvent e) {

				// Create link between two boxes.
				if (e.getDragSource().getData() instanceof AbstractBox) {
					// Clear Drag & Drop Line
					if (myMap.getMyCon() != null) {
						myMap.getMyCon().remove();
						myMap.setMyCon(null);
					}
					myMap.setStartPoint(null);
					myMap.setEndPoint(null);

					if (myMap.getEndConnector() != null) {
						myMap.getEndConnector().removeFromParent();
						myMap.setEndConnector(null);
					}

					// Open LinkDialog
					AbstractBox tmpBox = (AbstractBox) e.getData();
					//CreateLinkDialog tmpDialog = new CreateLinkDialog(AbstractBox.this.getMap(), tmpBox, AbstractBox.this, e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					AbstractCreateLinkDialog tmpDialog = createLinkDialog(AbstractBox.this.getMap(), tmpBox, AbstractBox.this, e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					myMap.add(tmpDialog);
					myMap.layout();
				}

				else if (e.getDragSource().getData() instanceof TranscriptLinkData) {
						// Drag & Drop from Transcript
						TranscriptLinkData tData = (TranscriptLinkData) e.getData();
						tData.setElementID(AbstractBox.this.getConnectedModel().getId());
	
						// Build ActionSet with all needed Updates
	
						if (AbstractBox.this.tData != null && AbstractBox.this.tData.getModel() != null) {
							tData.setModel(AbstractBox.this.tData.getModel());
	
							// A Transcript Link already exists, just update the Values
							//communicator.sendActionPackage(actionBuilder.updateTranscriptLink(getMap().getID(), connectedModel.getId(), tData));
							sendUpdateTranscriptLinkToServer(getMap().getID(), connectedModel.getId(), tData);
						} else {
							// Create a new TranscriptLink
							//communicator.sendActionPackage(new ActionPackage().addAction(actionBuilder.createTranscriptLink(getMap().getID(), String.valueOf(connectedModel.getId()), tData)));
							sendCreateTranscriptLinkToServer(getMap().getID(), String.valueOf(connectedModel.getId()), tData);
						}
				}
				e.cancelBubble();
			}

			@Override
			public void dragEnter(DNDEvent e) {

				e.getStatus().setStatus(true);

				if (e.getDragSource().getData() instanceof AbstractBox) {
//					if (myController != null) {
//						myController = LASAD_Client.getMVCController(myMap.getID());
//					}
					if (getMyController() != null) {
						//myController = LASAD_Client.getMVCController(myMap.getID());
						setMyControllerFromLASADClient();
					}

					
					List<Component> elements = myMap.getItems();
					int listSize = elements.size();
					int i = 0;
					
					while (i < listSize) {
						Component element;
						if ((element = elements.get(i)) instanceof AbstractLinkPanel) {
							if (((AbstractLinkPanel) element).getMyLink()
									.getConnectedModel().getParents().size() >= 2) {
								if ((((AbstractLinkPanel) element).getMyLink()
										.getConnectedModel().getParents()
										.get(0).getId() == AbstractBox.this
										.getConnectedModel().getId() && ((AbstractLinkPanel) element)
										.getMyLink().getConnectedModel()
										.getParents().get(1).getId() == ((AbstractBox) e
										.getDragSource().getData())
										.getConnectedModel().getId())
										|| (((AbstractLinkPanel) element).getMyLink()
												.getConnectedModel()
												.getParents().get(1).getId() == AbstractBox.this
												.getConnectedModel().getId() && ((AbstractLinkPanel) element)
												.getMyLink()
												.getConnectedModel()
												.getParents().get(0).getId() == ((AbstractBox) e
												.getDragSource().getData())
												.getConnectedModel().getId())) {
									e.getStatus().setStatus(false);
									e.getStatus().update(
											"Only one Relation allowed");
									// Clear Drag & Drop Line
									if (myMap.getMyCon() != null) {
										myMap.getMyCon().remove();
										myMap.setMyCon(null);
										
										//Hack to manage Java Iterator issues
										listSize--;
									}
									myMap.setStartPoint(null);
									myMap.setEndPoint(null);

									if (myMap.getEndConnector() != null) {
										myMap.getEndConnector()
												.removeFromParent();
										myMap.setEndConnector(null);
										
										//Hack to manage Java Iterator issues
										listSize--;
									}
								}
							}
						}
						i++;
					}

					if ((AbstractBox) e.getDragSource().getData() == AbstractBox.this) {
						e.getStatus().setStatus(false);
						e.getStatus().update("Cannot be connected to itself");

						if (myMap.getMyCon() != null) {
							myMap.getMyCon().remove();
							myMap.setMyCon(null);
						}
						myMap.setStartPoint(null);
						myMap.setEndPoint(null);

						if (myMap.getEndConnector() != null) {
							myMap.getEndConnector().removeFromParent();
							myMap.setEndConnector(null);
						}

					} else if (e.getStatus().getStatus() != false) {
						e.getStatus().update("Create new Relation");
					}
				} else if (e.getDragSource().getData() instanceof TranscriptLinkData) {
					boolean supportsTranscript = false;
					for(String key : AbstractBox.this.getElementInfo().getChildElements().keySet()) {
						if(AbstractBox.this.getElementInfo().getChildElements().get(key).getElementType().equalsIgnoreCase("transcript-link")) {
							supportsTranscript = true;
						}
					}
					if(supportsTranscript) {
						e.getStatus().update("Link Box with Transcript");
					}
					else {
						e.getStatus().update("This box type cannot be linked to a transcript line");
						e.getStatus().setStatus(false);
					}
				}
				e.cancelBubble();
				
			}

			@Override
			public void dragLeave(DNDEvent e) {
				e.getStatus().setStatus(false);
				e.getStatus().update("");
				e.cancelBubble();
			}

			@Override
			public void dragMove(DNDEvent e) {
				if (myMap.getEndConnector() != null) {
					myMap.getEndConnector().setPosition(e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					myMap.getMyCon().update();
				}
			}
		});
		return target;
	}
	protected abstract void sendUpdateTranscriptLinkToServer(String mapID, int elementID, TranscriptLinkData tData);
	protected abstract void sendCreateTranscriptLinkToServer(String mapID, String boxID, TranscriptLinkData tData);

	protected abstract AbstractCreateLinkDialog createLinkDialog(GraphMap graphMap, AbstractBox b1, AbstractBox b2, int x, int y); 
	
	/**
	 * Disconnects all connected curves / lines and removes the box from parent,
	 * once it has no longer a connected model. Important! Never forget to
	 * disconnect first, otherwise, there will be a high memory leak.
	 */
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		try {
			if(cn != null) {
				cn.disconnect();				
			}
		} catch (Exception e) {
			Logger.log("Could not disconnect connector of element: "+model.getId(), Logger.DEBUG_ERRORS);
		} finally {
			this.removeFromParent();
		}
	}

	/** Avoids unwanted selection of GUI elements on drag & drop **/
	private void disableTextSelection() {
		TextSelection.disableTextSelection(borderULC);
		TextSelection.disableTextSelection(borderUMC);
		TextSelection.disableTextSelection(borderURC);
		TextSelection.disableTextSelection(borderMLC);
		TextSelection.disableTextSelection(borderMRC);
		TextSelection.disableTextSelection(borderBLC);
		TextSelection.disableTextSelection(borderBMC);
		TextSelection.disableTextSelection(borderBRC);

		TextSelection.disableTextSelection(resizeCornerNE);
		TextSelection.disableTextSelection(resizeCornerNW);
		TextSelection.disableTextSelection(resizeCornerSE);
		TextSelection.disableTextSelection(resizeCornerSW);

		TextSelection.disableTextSelection(southArea);
		TextSelection.disableTextSelection(northArea);
		TextSelection.disableTextSelection(eastArea);
		TextSelection.disableTextSelection(westArea);
	}

	public void extendedElementContainerCallNewHeight(int height) {
		setSize(width, height + CONNECTOR_HEIGHT + CONNECTOR_HEIGHT + BORDER_HEIGHT + BORDER_HEIGHT + HEADER_HEIGHT);
	}

	public void extendedElementContainerPublishedNewMaxHeight(int height) {
		if(!this.isReplay)
		{
			r.setMaxHeight(height + CONNECTOR_HEIGHT + CONNECTOR_HEIGHT + BORDER_HEIGHT + BORDER_HEIGHT + HEADER_HEIGHT);
		}
	}

	public void extendedElementContainerPublishedNewMinHeight(int height) {
		if(!this.isReplay)
		{
			r.setMinHeight(height + CONNECTOR_HEIGHT + CONNECTOR_HEIGHT + BORDER_HEIGHT + BORDER_HEIGHT + HEADER_HEIGHT);
		}
	}

	/**
	 * Checks whether box is near the sides or not and extends the map view if
	 * necessary
	 * 
	 * @param x
	 *            x-value of current box position
	 * @param y
	 *            y-value of current box position
	 * @author Marcel Bergmann
	 */
	private void extendMapView() {
		if (this.getMap().getMapDimensionSize().width - this.getPosition(true).x - this.getWidth() < 250) {
			this.getMap().extendMapDimension(Direction.RIGHT, 1000);
		} else if (this.getMap().getMapDimensionSize().height - this.getPosition(true).y - this.getHeight() < 250) {
			this.getMap().extendMapDimension(Direction.DOWN, 1000);
		}
	}

	public String getBorder() {
		return this.border;
	}

	public String getColor() {
		return color;
	}
	public Connector getConnector() {
		return cn;
	}

	public Connector getConnector(Direction dir) {
		return cn;
	}

	public Direction getDirection() {
		return null;
	}

	public ElementInfo getElementInfo() {
		return config;
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return extendedElementContainer.getExtendedElements();
	}

	public GenericFocusHandler getFocusHandler() {
		return myMap.getFocusHandler();
	}

	public FocusableInterface getFocusParent() {
		return myMap;
	}

	public int getHeight() {
		return height;
	}

	public GenericHighlightHandler getHighlightHandler() {
		return myMap.getHighlightHandler();
	}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		return myMap;
	}

	public GraphMap getMap() {
		return myMap;
	}

	@Override
	public AbstractMVCViewSession getMVCViewSession() {
		return this.getMap().getMyViewSession();
	}

	public abstract AbstractMVController getMyController();

	@Override
	public Element getRootElement() {
		return rootElement;
	}

	public TranscriptLinkData getTData() {
		return tData;
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	private void initBoxElements() {
		positioningElement = DOM.createDiv();
		DOM.setStyleAttribute(positioningElement, "position", "absolute");

		// Create outer 3x3 Grid
		outerGrid = DOM.createTable();
		outerGrid.setClassName("box-outerGrid-table");
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", XDOM.getTopZIndex());
		DOM.setIntStyleAttribute(outerGrid, "zIndex", XDOM.getTopZIndex());

		this.outerGridTBody = DOM.createTBody();
		outerGridTBody.setClassName("box-outerGridTBody");
		DOM.appendChild(outerGrid, outerGridTBody);

		this.outerTopRow = DOM.createTR();
		outerTopRow.setClassName("tr-connector");
		this.outerTopRowLeftCell = DOM.createTD();
		outerTopRowLeftCell.setClassName("box-outerGrid-tlc");
		this.resizeCornerNW = DOM.createDiv();
		resizeCornerNW.setClassName("resizeCornerNW");
		DOM.appendChild(outerTopRowLeftCell, resizeCornerNW);
		DOM.appendChild(outerTopRow, outerTopRowLeftCell);

		this.outerTopRowMiddleCell = DOM.createTD();
		outerTopRowMiddleCell.setClassName("box-outerGrid-tmc");
		DOM.appendChild(outerTopRow, outerTopRowMiddleCell);

		this.northArea = DOM.createDiv();
		this.northArea.setClassName("box-areas");
		DOM.appendChild(outerTopRowMiddleCell, this.northArea);

		//this.northConnector = new BoxConnectorElement(this, "north-connector");
		this.northConnector = createBoxConnectorElement(this, "north-connector");
		this.northConnector.render(northArea);
		this.northConnector.getElement().getStyle().setCursor(Cursor.POINTER); 

		this.outerTopRowRightCell = DOM.createTD();
		outerTopRowRightCell.setClassName("box-outerGrid-trc");
		this.resizeCornerNE = DOM.createDiv();
		resizeCornerNE.setClassName("resizeCornerNE");
		DOM.appendChild(outerTopRowRightCell, resizeCornerNE);
		DOM.appendChild(outerTopRow, outerTopRowRightCell);

		DOM.appendChild(outerGridTBody, outerTopRow);

		this.outerMiddleRow = DOM.createTR();
		outerMiddleRow.setClassName("tr-fullHeight");
		this.outerMiddleRowLeftCell = DOM.createTD();
		outerMiddleRowLeftCell.setClassName("box-outerGrid-mlc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowLeftCell);

		this.westArea = DOM.createDiv();
		this.westArea.setClassName("box-areas");
		DOM.appendChild(outerMiddleRowLeftCell, this.westArea);

		//this.westConnector = new BoxConnectorElement(this, "west-connector");
		this.westConnector = createBoxConnectorElement(this, "west-connector");
		this.westConnector.render(westArea);
		this.westConnector.getElement().getStyle().setCursor(Cursor.POINTER); 

		this.outerMiddleRowMiddleCell = DOM.createTD();
		outerMiddleRowMiddleCell.setClassName("box-outerGrid-mmc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowMiddleCell);

		this.outerMiddleRowRightCell = DOM.createTD();
		outerMiddleRowRightCell.setClassName("box-outerGrid-mrc");
		DOM.appendChild(outerMiddleRow, outerMiddleRowRightCell);

		this.eastArea = DOM.createDiv();
		this.eastArea.setClassName("box-areas");
		DOM.appendChild(outerMiddleRowRightCell, this.eastArea);

		//this.eastConnector = new BoxConnectorElement(this, "east-connector");
		this.eastConnector = createBoxConnectorElement(this, "east-connector");
		this.eastConnector.render(eastArea);
		this.eastConnector.getElement().getStyle().setCursor(Cursor.POINTER); 

		DOM.appendChild(outerGridTBody, outerMiddleRow);

		this.outerBottomRow = DOM.createTR();
		outerBottomRow.setClassName("tr-connector");
		this.outerBottomRowLeftCell = DOM.createTD();
		outerBottomRowLeftCell.setClassName("box-outerGrid-blc");
		this.resizeCornerSW = DOM.createDiv();
		resizeCornerSW.setClassName("resizeCornerSW");
		DOM.appendChild(outerBottomRowLeftCell, resizeCornerSW);
		DOM.appendChild(outerBottomRow, outerBottomRowLeftCell);

		this.outerBottomRowMiddleCell = DOM.createTD();
		outerBottomRowMiddleCell.setClassName("box-outerGrid-bmc");
		DOM.appendChild(outerBottomRow, outerBottomRowMiddleCell);

		this.southArea = DOM.createDiv();
		this.southArea.setClassName("box-areas");
		DOM.appendChild(outerBottomRowMiddleCell, this.southArea);

		//this.southConnector = new BoxConnectorElement(this, "south-connector");
		this.southConnector = createBoxConnectorElement(this, "south-connector");
		this.southConnector.render(southArea);
		this.southConnector.getElement().getStyle().setCursor(Cursor.POINTER); 

		this.outerBottomRowRightCell = DOM.createTD();
		outerBottomRowRightCell.setClassName("box-outerGrid-brc");
		this.resizeCornerSE = DOM.createDiv();
		resizeCornerSE.setClassName("resizeCornerSE");
		DOM.appendChild(outerBottomRowRightCell, resizeCornerSE);
		DOM.appendChild(outerBottomRow, outerBottomRowRightCell);

		DOM.appendChild(outerGridTBody, outerBottomRow);

		// Create inner 3x3 Grid
		this.innerGrid = DOM.createTable();
		innerGrid.setClassName("box-innerGrid-table");
		DOM.appendChild(outerMiddleRowMiddleCell, innerGrid);

		innerGridTBody = DOM.createTBody();
		innerGridTBody.setClassName("box-innerGridTBody");
		DOM.appendChild(innerGrid, innerGridTBody);

		this.innerTopRow = DOM.createTR();
		innerTopRow.setClassName("upperRow");
		DOM.appendChild(innerGridTBody, innerTopRow);

		this.innerTopRowLeftCell = DOM.createTD();
		innerTopRowLeftCell.setClassName("box-innerGrid-ulc");
		this.borderULC = DOM.createDiv();

		DOM.appendChild(innerTopRowLeftCell, borderULC);

		this.innerTopRowMiddleCell = DOM.createTD();
		innerTopRowMiddleCell.setClassName("box-innerGrid-umc");
		this.borderUMC = DOM.createDiv();

		DOM.appendChild(innerTopRowMiddleCell, borderUMC);

		this.innerTopRowRightCell = DOM.createTD();
		innerTopRowRightCell.setClassName("box-innerGrid-urc");
		this.borderURC = DOM.createDiv();

		DOM.appendChild(innerTopRowRightCell, borderURC);

		DOM.appendChild(innerTopRow, innerTopRowLeftCell);
		DOM.appendChild(innerTopRow, innerTopRowMiddleCell);
		DOM.appendChild(innerTopRow, innerTopRowRightCell);

		innerMiddleRow = DOM.createTR();
		innerMiddleRow.setClassName("middleRow");
		DOM.appendChild(innerGridTBody, innerMiddleRow);

		this.innerMiddleRowLeftCell = DOM.createTD();
		innerMiddleRowLeftCell.setClassName("box-innerGrid-mlc");
		this.borderMLC = DOM.createDiv();
		DOM.appendChild(innerMiddleRowLeftCell, borderMLC);

		this.innerMiddleRowMiddleCell = DOM.createTD();
		innerMiddleRowMiddleCell.setClassName("box-innerGrid-mmc");
		this.boxContentDiv = DOM.createDiv();
		boxContentDiv.setClassName("box-content-div");
		DOM.appendChild(innerMiddleRowMiddleCell, boxContentDiv);

		this.innerMiddleRowRightCell = DOM.createTD();
		innerMiddleRowRightCell.setClassName("box-innerGrid-mrc");
		this.borderMRC = DOM.createDiv();

		DOM.appendChild(innerMiddleRowRightCell, borderMRC);

		DOM.appendChild(innerMiddleRow, innerMiddleRowLeftCell);
		DOM.appendChild(innerMiddleRow, innerMiddleRowMiddleCell);
		DOM.appendChild(innerMiddleRow, innerMiddleRowRightCell);

		this.innerBottomRow = DOM.createTR();
		innerBottomRow.setClassName("bottomRow");
		DOM.appendChild(innerGridTBody, innerBottomRow);

		this.innerBottomRowLeftCell = DOM.createTD();
		innerBottomRowLeftCell.setClassName("box-innerGrid-blc");
		this.borderBLC = DOM.createDiv();

		DOM.appendChild(innerBottomRowLeftCell, borderBLC);

		this.innerBottomRowMiddleCell = DOM.createTD();
		innerBottomRowMiddleCell.setClassName("box-innerGrid-bmc");
		this.borderBMC = DOM.createDiv();

		DOM.appendChild(innerBottomRowMiddleCell, borderBMC);

		this.innerBottomRowRightCell = DOM.createTD();
		innerBottomRowRightCell.setClassName("box-innerGrid-brc");
		this.borderBRC = DOM.createDiv();

		DOM.appendChild(innerBottomRowRightCell, borderBRC);

		DOM.appendChild(innerBottomRow, innerBottomRowLeftCell);
		DOM.appendChild(innerBottomRow, innerBottomRowMiddleCell);
		DOM.appendChild(innerBottomRow, innerBottomRowRightCell);

		DOM.appendChild(positioningElement, outerGrid);
		this.rootElement = positioningElement;

		// Init ExtendedElementContainer
		initExtendedElementContainer();
	}
	protected abstract BoxConnectorElement createBoxConnectorElement(AbstractBox abstractBox, String style);

	private void initExtendedElementContainer() {
		this.extendedElementContainer = new ExtendedElementContainer(this);
		this.extendedElementContainer.setElementSpacing(1);
		this.extendedElementContainer.setPaddingTop(1);
		this.extendedElementContainer.setPaddingBottom(0);
		this.extendedElementContainer.setPaddingLeft(0);
		this.extendedElementContainer.setPaddingRight(0);
		// add Container Element to DIV
		DOM.appendChild(boxContentDiv, extendedElementContainer.getContainerElement());
	}

	/**
	 * Init the DragListener to move the box via drag & drop
	 * 
	 * @param myMap
	 */
	protected void makeDraggable(GraphMap myMap) {
		BoxDraggable d = new BoxDraggable(this, this.boxHeading);
		d.setUseProxy(true);
		d.setMoveAfterProxyDrag(false);

		// Remove the comments to use the concrete box instead of the proxy
		// object while moving; not recommended because the connected curves /
		// lines will be updated immediately as well.
		// d.setUseProxy(false);

		d.setContainer(myMap);
		d.addDragListener(new DragListener());
	}

	protected void makeResizeable() {
		r = new Resizable(this, "nw ne sw se");

		ResizeListener rListener = new ResizeListener() {
			@Override
			public void resizeEnd(ResizeEvent re) {
				super.resizeEnd(re);
				//communicator.sendActionPackage(actionBuilder.updateBoxSize(getMap().getID(), connectedModel.getId(), width, height));
				onClickSendUpdateBoxSizeToServer(getMap().getID(), connectedModel.getId(), width, height);
			}
		};
		
		r.addResizeListener(rListener);
		
	
	}
	protected abstract void onClickSendUpdateBoxSizeToServer(String mapID, int boxID, int width, int height);

	@Override
	protected void onPosition(int x, int y) {
		super.onPosition(x, y);

		// Update corresponding Connections after Box movement
		updateConnections();
		extendMapView();
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		this.extendMapView();
	}

	@Override
	public void removeExtendedElement(AbstractExtendedElement element) {
		extendedElementContainer.removeExtendedElement(element);
	}

	@Override
	public void select() {
		// Open selectionDetailsPanel only if the user is allowed to
		if (isModificationAllowed()) {
			DOM.setStyleAttribute(this.rootElement, "border", "1pt solid red");
			this.selected = true;
			if(myDetails != null)
				this.myMap.getMyArgumentMapSpace().changeSelectionDetailsPanelTo(myDetails);
		}
	}

	/**
	 * Changes the box border to a predefined class or a passed #color
	 * 
	 * Classes are predefined in boxHighlight.css and box.css
	 * 
	 * @param String
	 *            border, predefined class or hex color code
	 */
	public void setBorder(String border) {

		if (border.equals("highlighted")) {

			borderULC.setClassName("border-highlighted");
			borderUMC.setClassName("border-highlighted");
			borderURC.setClassName("border-highlighted");
			borderMLC.setClassName("border-highlighted");
			borderMRC.setClassName("border-highlighted");
			borderBLC.setClassName("border-highlighted");
			borderBMC.setClassName("border-highlighted");
			borderBRC.setClassName("border-highlighted");
		}

		else if (border.equals("standard")) {

			borderULC.setClassName("border-ulc-standard");
			borderUMC.setClassName("border-umc-standard");
			borderURC.setClassName("border-urc-standard");
			borderMLC.setClassName("border-mlc-standard");
			borderMRC.setClassName("border-mrc-standard");
			borderBLC.setClassName("border-blc-standard");
			borderBMC.setClassName("border-bmc-standard");
			borderBRC.setClassName("border-brc-standard");

		}

		else if (border.equals("double")) {
			borderULC.setClassName("border-ulc-double");
			borderUMC.setClassName("border-umc-double");
			borderURC.setClassName("border-urc-double");
			borderMLC.setClassName("border-mlc-double");
			borderMRC.setClassName("border-mrc-double");
			borderBLC.setClassName("border-blc-double");
			borderBMC.setClassName("border-bmc-double");
			borderBRC.setClassName("border-brc-double");
		}

		else if (border.equals("round")) {
			borderULC.setClassName("border-ulc-round");
			borderUMC.setClassName("border-umc-round");
			borderURC.setClassName("border-urc-round");
			borderMLC.setClassName("border-mlc-round");
			borderMRC.setClassName("border-mrc-round");
			borderBLC.setClassName("border-blc-round");
			borderBMC.setClassName("border-bmc-round");
			borderBRC.setClassName("border-brc-round");
		}

		else if (border.equals("dashed")) {
			borderULC.setClassName("border-ulc-dashed");
			borderUMC.setClassName("border-umc-dashed");
			borderURC.setClassName("border-urc-dashed");
			borderMLC.setClassName("border-mlc-dashed");
			borderMRC.setClassName("border-mrc-dashed");
			borderBLC.setClassName("border-blc-dashed");
			borderBMC.setClassName("border-bmc-dashed");
			borderBRC.setClassName("border-brc-dashed");
		}

		else if (border.equals("zigzag")) {
			borderULC.setClassName("border-ulc-zigzag");
			borderUMC.setClassName("border-umc-zigzag");
			borderURC.setClassName("border-urc-zigzag");
			borderMLC.setClassName("border-mlc-zigzag");
			borderMRC.setClassName("border-mrc-zigzag");
			borderBLC.setClassName("border-blc-zigzag");
			borderBMC.setClassName("border-bmc-zigzag");
			borderBRC.setClassName("border-brc-zigzag");
		} else {
			// Change Class to border-highlighted-color defined in
			// boxHighlight.css
			// It has no style attributes but is necessary for a "clean" change
			borderULC.setClassName("border-highlighted-color");
			borderUMC.setClassName("border-highlighted-color");
			borderURC.setClassName("border-highlighted-color");
			borderMLC.setClassName("border-highlighted-color");
			borderMRC.setClassName("border-highlighted-color");
			borderBLC.setClassName("border-highlighted-color");
			borderBMC.setClassName("border-highlighted-color");
			borderBRC.setClassName("border-highlighted-color");

			// Look for hex color code and change background via DOM to given
			// color
			DOM.setStyleAttribute(borderULC, "background", border);
			DOM.setStyleAttribute(borderUMC, "background", border);
			DOM.setStyleAttribute(borderURC, "background", border);
			DOM.setStyleAttribute(borderMLC, "background", border);
			DOM.setStyleAttribute(borderMRC, "background", border);
			DOM.setStyleAttribute(borderBLC, "background", border);
			DOM.setStyleAttribute(borderBMC, "background", border);
			DOM.setStyleAttribute(borderBRC, "background", border);
		}
	}

	public void setColor(String color) {
		this.color = color;
		DOM.setStyleAttribute(boxHeading.getElement(), "backgroundColor", color);

		if(myDetails != null)
			this.myDetails.setColor(color);
	}
	
	//necessary for controlling box size
	int fontSize = 0;
	boolean changedFontSize = false;
	int previousHeaderHeight = 0;
	
	public int getHeaderFontSize(){
		return this.fontSize;
	}
	
	public void setHeaderFontSize(int fontSize){
		if(this.boxHeading != null){
			if(this.fontSize != fontSize){
				changedFontSize = true;
				previousHeaderHeight = this.fontSize+BORDER_HEIGHT;
			}

			HEADER_HEIGHT = fontSize+3;
			this.fontSize = fontSize;
			this.boxHeading.getContentText().getStyle().setFontSize(fontSize,com.google.gwt.dom.client.Style.Unit.PX);
			this.boxHeading.getContentText().getStyle().setHeight(fontSize+BORDER_HEIGHT/2,com.google.gwt.dom.client.Style.Unit.PX);
			this.boxHeading.getElement().getStyle().setHeight(fontSize+BORDER_HEIGHT,com.google.gwt.dom.client.Style.Unit.PX);
			
			this.setSize(Math.max(this.boxHeading.getElement().getScrollWidth()+32, this.width), this.height);
			
			changedFontSize = false;
		}
	}

	/**
	 * Handle the whole set of the BoxComponentEvent handling Don't call this
	 * method directly, it will be done afterRender
	 */
	private void setComponentHandling() {

		componentListener = new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if(!isReplay)
				{
					if (be.getEventTypeInt() == Events.OnMouseOver.getEventCode()) {
						onMouseOver(be);
					} else if (be.getEventTypeInt() == Events.OnMouseOut.getEventCode()) {
						onMouseOut(be);
					} else if (be.getEventTypeInt() == Events.OnClick.getEventCode()) {
						onClick(be);
						be.cancelBubble();
					} else if (be.getEventTypeInt() == Events.OnDoubleClick.getEventCode()) {
						onDoubleClick(be);
						be.cancelBubble();
					}else if (be.getEventTypeInt() == Events.OnPaste.getEventCode()){
						onPaste(be);
					}
				}
			}
			
			private void onPaste(ComponentEvent be){
				Logger.log("OnPaste : AbstractBox", Logger.DEBUG);
						
				Timer t = new Timer() {
					@Override
					public void run() {
						AbstractBox box = AbstractBox.this;
						box.setSize(box.getWidth(), 100);
						
						for (AbstractExtendedElement childElement : box.getExtendedElements())
						{
							if (childElement instanceof AbstractExtendedTextElement)
							{
								AbstractExtendedTextElement textElt = (AbstractExtendedTextElement) childElement;
								box.textAreaCallNewHeightgrow(textElt.determineBoxHeightChange());
								break;
							}
						}
					}
				};

				// Schedule the timer to run once in half a second.
				// The delay is necessary, so the method gets called after the text is actually pasted
				t.schedule(500);
			}

			private void onClick(ComponentEvent be) {
				AbstractBox.this.el().updateZIndex(1);

				if (AbstractBox.this.selected) {
					AbstractBox.this.getMap().getSelectionHandler().unselectElement(AbstractBox.this);
				} else {
					AbstractBox.this.getMap().getSelectionHandler().selectElement(AbstractBox.this);
				}
			}

			private void onDoubleClick(ComponentEvent be) {
				AbstractBox.this.getMap().getFadeHandler().fadeElement(AbstractBox.this.getConnectedModel().getId());
			}

			private void onMouseOut(ComponentEvent be) {
				if (northConnector.getElement().getClassName().equals("north-connector-dark")) {
					northConnector.getElement().setClassName("north-connector");
				}
				if (eastConnector.getElement().getClassName().equals("east-connector-dark")) {
					eastConnector.getElement().setClassName("east-connector");
				}
				if (southConnector.getElement().getClassName().equals("south-connector-dark")) {
					southConnector.getElement().setClassName("south-connector");
				}
				if (westConnector.getElement().getClassName().equals("west-connector-dark")) {
					westConnector.getElement().setClassName("west-connector");
				}
				//DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", Style.Cursor.DEFAULT);
				hoverTimer.schedule(500);
			}

			private void onMouseOver(ComponentEvent be) {
				if (northConnector.getElement().getClassName().equals("north-connector")) {
					northConnector.getElement().setClassName("north-connector-dark");
				}
				if (eastConnector.getElement().getClassName().equals("east-connector")) {
					eastConnector.getElement().setClassName("east-connector-dark");
				}
				if (southConnector.getElement().getClassName().equals("south-connector")) {
					southConnector.getElement().setClassName("south-connector-dark");
				}
				if (westConnector.getElement().getClassName().equals("west-connector")) {
					westConnector.getElement().setClassName("west-connector-dark");
				}

				AbstractBox.this.setResizeCornerVisibility(true);
				AbstractBox.this.boxHeading.setEditButtonVisibility(true);
				hoverTimer.cancel();
			}
		};

		this.addListener(Events.OnMouseOver, componentListener);
		this.addListener(Events.OnMouseOut, componentListener);
		this.addListener(Events.OnClick, componentListener);
		this.addListener(Events.OnDoubleClick, componentListener);
		this.addListener(Events.OnPaste, componentListener);
		this.sinkEvents(Events.OnPaste.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode() | Events.OnClick.getEventCode() | Events.OnDoubleClick.getEventCode());
	}

	public void setElementFocus(boolean focus) {
	}

	@Override
	public void setFadedOut(boolean fadedOut) {
		if (fadedOut) {
			DOM.setStyleAttribute(boxHeading.getElement(), "backgroundColor", "#F9F9F9");
		} else {
			DOM.setStyleAttribute(boxHeading.getElement(), "backgroundColor", color);
		}
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	/**
	 * setHighlight class setBorder to change the appearance of the border to a predefined highlighted class / back to standard border
	 * 
	 * NOTICE: It is not used to highlight a box of a different user, because current function specification doesn't allow a set back to a
	 * specific class! Instead setHighlightAndBack() is used
	 */
	public void setHighlight(boolean highlight) {
		if (highlight) {
			setBorder("highlighted");
		} else {
			setBorder(this.border);
		}
	}

	/**
	 * sets box highlighted and back to original border after 30s seconds
	 */
	public void setHighlightAndBack() {
		final String tmpBorder = this.config.getUiOption(ParameterTypes.Border);
		this.setBorder("#46FC00");

		// check if there is already a timer running
		// in case of , cancel running timer to ensure only one timer per box
		if (this.timer != null) {
			this.timer.cancel();
			this.timer = null;
		}

		this.timer = new Timer() {
			public void run() {
				// Box.this.setHighlight(false);
				AbstractBox.this.setBorder(tmpBorder);
				AbstractBox.this.timer = null;
			}
		};

		// Schedule the timer to run once in 30 seconds in order to deactivate
		// highlightning
		this.timer.schedule(HIGHLIGHT_TIMER);

	}

	public abstract void setMyController(AbstractMVController myController);
	public abstract void setMyControllerFromLASADClient();

	/**
	 * Overwrite the originally Method, to direct the PositionUpdate of the Box
	 * through the Server
	 * 
	 * Call updatePosition() -> Create Action -> Send to Server -> Server send
	 * to Client -> Model -> Call setPostitionReal()
	 * 
	 * @author Jan Brenner
	 * 
	 */
	@Override
	public void setPosition(int left, int top) {
		Logger.log("[BOX]setPosition: left: " + left + " top: " + top, Logger.DEBUG_DETAILS);
		if (this.getPosition(true).x != left || this.getPosition(true).y != height) {
			if (this.isVisible() != true) {
				super.setPosition(left, top);
				this.setVisible(true);

			} else {
				this.updatePosition(left, top);
			}
		}
	}

	/**
	 * Calls directly the Superclass setPosition() Method to Update the Box
	 * Position
	 * 
	 */
	public void setPositionReal(int left, int top) {
		super.setPosition(left, top);
	}

	/**
	 * Set the visible status of the ResizeCorners
	 * 
	 * @param visible
	 */
	public void setResizeCornerVisibility(boolean visible) {
		if (visible) {
			DOM.setStyleAttribute(this.resizeCornerNE, "visibility", "visible");
			DOM.setStyleAttribute(this.resizeCornerNW, "visibility", "visible");
			DOM.setStyleAttribute(this.resizeCornerSE, "visibility", "visible");
			DOM.setStyleAttribute(this.resizeCornerSW, "visibility", "visible");
		} else {
			DOM.setStyleAttribute(this.resizeCornerNE, "visibility", "hidden");
			DOM.setStyleAttribute(this.resizeCornerNW, "visibility", "hidden");
			DOM.setStyleAttribute(this.resizeCornerSE, "visibility", "hidden");
			DOM.setStyleAttribute(this.resizeCornerSW, "visibility", "hidden");
		}
	}

	@Override
	public void setSize(int width, int height) { //TODO Vergleiche was in den Projekten unterschiedlich ist!!!
		if(changedFontSize)
			height += Math.max(this.fontSize+BORDER_HEIGHT-previousHeaderHeight, 0);
		
		if (this.width != width || this.height != height) {
			if (this.getConnectedModel() != null) {
				this.getConnectedModel().setValue(ParameterTypes.Width, "" + width);
				this.getConnectedModel().setValue(ParameterTypes.Height, "" + height);
			}

			this.width = width;
			this.height = height;
			super.setSize(width, height);

			if (this.isRendered()) {
				calculateElementsSize();
				updateConnections();
			}
		}
	}

	public void setTData(TranscriptLinkData data) {
		tData = data;
	}

	public void setTitle(String title) {
		this.title = title;
		if(myDetails != null)
			this.myDetails.setTitle(title);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void unselect() {
		DOM.setStyleAttribute(this.rootElement, "border", "none");
		this.selected = false;

		//this.myMap.getMyArgumentMapSpace().changeSelectionDetailsPanelTo(new SelectionDetailsPanel(myMap));
		this.myMap.getMyArgumentMapSpace().changeSelectionDetailsPanelTo(createSelectionDetailsPanel(myMap));
	}
	protected abstract SelectionDetailsPanel createSelectionDetailsPanel(GraphMap mymap);

	/**
	 * This one just updates the connections' position in case the size of the
	 * box changes
	 */
	public void updateConnections() {
		try {
			for (Iterator<Connection> i = cn.getConnections().iterator(); i
					.hasNext();) {
				Connection con = (Connection) i.next();
				con.update();
			}
		} catch (Exception e) {
			Logger.log("updateConnections in class Box.java failed", Logger.DEBUG_ERRORS);
		}
	}

	/**
	 * Send a ActionSet to the Server to update the Model
	 * 
	 * updatePosition() -> Create Action -> Send to Server -> Server send to
	 * Client -> Model -> Call setPostitionReal()
	 * 
	 * @param x
	 *            new X-Position
	 * @param y
	 *            new Y-Position
	 */
	public void updatePosition(int x, int y) {
		//communicator.sendActionPackage(actionBuilder.updateBoxPosition(getMap().getID(), connectedModel.getId(), x, y));
		onClickSendUpdatePositionToServer(getMap().getID(), connectedModel.getId(), x, y);
	}
	protected abstract void onClickSendUpdatePositionToServer(String mapID, int boxID, int x, int y);
	
	public int getCONNECTOR_WIDTH() {
		return CONNECTOR_WIDTH;
	}

	public int getCONNECTOR_HEIGHT() {
		return CONNECTOR_HEIGHT;
	}

	public int getBORDER_WIDTH() {
		return BORDER_WIDTH;
	}

	public int getBORDER_HEIGHT() {
		return BORDER_HEIGHT;
	}
	
	@Override
	/**
	 * @author BM
	 * @date 25.06.2012
	 */	
	public void textAreaCallNewHeightgrow(int heightToAdd) {
		if (heightToAdd != 0)
		{
			autogrow = true;
			setSize(getWidth(), getHeight()+ heightToAdd);
			LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().autoResizeTextBox(myMap.getID(), AbstractBox.this.getConnectedModel().getId(), getWidth(), getHeight()));
			autogrow = false;
		}
	}
}
