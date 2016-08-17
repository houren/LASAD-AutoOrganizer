package lasad.gwt.client.ui.link;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.gwt.client.ui.LASADBoxComponent;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainer;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.ExtendedElementContainerMasterInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.fade.FadeableElementInterface;
import lasad.gwt.client.ui.common.helper.TextSelection;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.link.helper.AbstractLinkHeaderButtonListener;
import lasad.gwt.client.ui.link.helper.LinkDraggable;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

public abstract class AbstractLinkPanel extends LASADBoxComponent implements ExtendedElementContainerMasterInterface, ExtendedElementContainerInterface, HighlightableElementInterface, FadeableElementInterface {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	protected AbstractLink myLink;
	protected boolean details = true; // If true, then the normal link panel will
									// be drawn, i.e. with all elements, title
									// text, etc.
	// If false, there will be only a button to delete the link

//	private MVController myController;

	private int height = 10;
	private int width = 10;

	int modelHeight, modelWidth;

	private int generalOffsetHeight = 2; // 2 Pixel for the Border
	private int generalOffsetWidth = 2; // 2 Pixel for the Border
	private int generalOwnHeight = 14; // Header + Header BottomBorder

	
	
	private Element root;
	private ExtendedElementContainer extendedElementContainer = null;

	private AbstractLinkHeaderButtonListener myButtonListener;

	// Highlighting Timer to ensure online one Timer per box
	private Timer timer = null;
	
	private boolean isReplay;

	public AbstractLinkPanel(AbstractLink myLink, String details, boolean isR) {
		this.myLink = myLink;
		this.modelHeight = Integer.parseInt(myLink.getElementInfo().getUiOption(ParameterTypes.Height));
		this.modelWidth = Integer.parseInt(myLink.getElementInfo().getUiOption(ParameterTypes.Width));
		this.isReplay = isR;
		this.config = myLink.getElementInfo();
		
		if (details.equalsIgnoreCase("false")) {
			this.details = false;
		}

		this.root = DOM.createDiv();

		initHeader();

		if (this.details) {
			this.root.setClassName("link-content");
			if(!this.isReplay)
			{
				makeResizeable();
			}
			initExtendedElementContainer();
			this.setLocalSize(modelWidth, modelHeight);
		} else {
			this.root.setClassName("link-content-nondetails");
			//other methods look at extendedElement container, e.g. when exporting
			this.extendedElementContainer = new ExtendedElementContainer(this);

		}
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.root, target, index);
		this.el().setZIndex(XDOM.getTopZIndex());

	}

	private Element header = null;
	protected Element headerText = null;
	private Element delButton;
	private Element addButton;
	private Element closeButton;
	private Element switchDirectionButton;
	private Element configButton;

	protected abstract AbstractLinkHeaderButtonListener createLinkHeaderButtonListener(AbstractLinkPanel linkPanel);
	
	private void initHeader() {

		//this.myButtonListener = new LinkHeaderButtonListener(this);
		this.myButtonListener = createLinkHeaderButtonListener(this);

		// Set HEADER

		header = DOM.createDiv();
		DOM.appendChild(root, header);
		DOM.setStyleAttribute(header, "height", "13px");

		if (details) {
			header.setClassName("link-heading");
			DOM.setStyleAttribute(header, "background", myLink.getElementInfo().getUiOption(ParameterTypes.BackgroundColor));
		} else {
			header.setClassName("link-heading-nondetails");
		}

		if (details) {
			headerText = DOM.createDiv();
			headerText.setInnerHTML(myLink.getElementInfo().getElementOption(ParameterTypes.Heading));
			headerText.setClassName("link-heading-text");
			DOM.appendChild(header, headerText);

			TextSelection.disableTextSelection(headerText);
		}

		closeButton = DOM.createDiv();
		closeButton.setClassName("link-close-button");
		DOM.appendChild(header, closeButton);
		
		configButton = DOM.createDiv();
		configButton.setClassName("link-config-button");
		DOM.setStyleAttribute(this.configButton, "visibility", "hidden");
		String configButtonFlag = myLink.getElementInfo().getElementOption(ParameterTypes.ConfigButton);
		if(configButtonFlag != null && Boolean.parseBoolean(configButtonFlag)){
			DOM.appendChild(header, configButton);
		}

		if (details) {

			switchDirectionButton = DOM.createDiv();
			switchDirectionButton.setClassName("switch-direction-button");

			if (myLink.getElementInfo().getElementOption(ParameterTypes.Endings).equalsIgnoreCase("TRUE")) {
				DOM.appendChild(header, switchDirectionButton);

				if(!isReplay)
				{
					DOM.sinkEvents(switchDirectionButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
					DOM.setEventListener(switchDirectionButton, myButtonListener);
				}
			}

			addButton = DOM.createDiv();
			DOM.appendChild(header, addButton);

			delButton = DOM.createDiv();
			DOM.appendChild(header, delButton);

			addButton.setClassName("add-button");
			delButton.setClassName("del-button");

			DOM.setStyleAttribute(this.addButton, "visibility", "hidden");
			DOM.setStyleAttribute(this.delButton, "visibility", "hidden");
			DOM.setStyleAttribute(this.switchDirectionButton, "visibility", "hidden");
			if(!isReplay)
			{
				// Create Listener for the buttons in the header
			    DOM.sinkEvents(addButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
				DOM.setEventListener(addButton, myButtonListener);

				DOM.sinkEvents(delButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
				DOM.setEventListener(delButton, myButtonListener);
			}
		}
		
		if(!isReplay)
		{
			DOM.sinkEvents(closeButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
			DOM.setEventListener(closeButton, myButtonListener);
			
			if(configButtonFlag != null || Boolean.parseBoolean(configButtonFlag)){
				DOM.sinkEvents(configButton, Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
				DOM.setEventListener(configButton, myButtonListener);
			}
		}

		Listener<ComponentEvent> componentListener = new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if(!isReplay)
				{
					if (be.getEventTypeInt() == Events.OnMouseOver.getEventCode()) {
						onMouseOver(be);
					} else if (be.getEventTypeInt() == Events.OnMouseOut.getEventCode()) {
						onMouseOut(be);
					} else if (be.getEventTypeInt() == Events.OnClick.getEventCode()) {
						AbstractLinkPanel.this.el().updateZIndex(1);
						be.cancelBubble();
					} else if (be.getEventTypeInt() == Events.OnDoubleClick.getEventCode()) {
						onDoubleClick(be);
						be.cancelBubble();
					} 
				}
			}

			private void onMouseOver(ComponentEvent be) {
				onMouseEnter();
			}

			private void onMouseOut(ComponentEvent be) {
				onMouseLeave();
			}

			private void onDoubleClick(ComponentEvent be) {
				AbstractLinkPanel.this.getMyLink().getMap().getFadeHandler().fadeElement(AbstractLinkPanel.this.getMyLink().getConnectedModel().getId());
			}
		};

		this.addListener(Events.OnMouseOver, componentListener);
		this.addListener(Events.OnClick, componentListener);
		this.addListener(Events.OnMouseOut, componentListener);
		this.addListener(Events.OnDoubleClick, componentListener);

		this.sinkEvents(Events.OnMouseOut.getEventCode() | Events.OnDoubleClick.getEventCode() | Events.OnClick.getEventCode() | Events.OnMouseOver.getEventCode());
	}

	private void initExtendedElementContainer() {
		this.extendedElementContainer = new ExtendedElementContainer(this);
		this.extendedElementContainer.setElementSpacing(1);
		this.extendedElementContainer.setPaddingTop(1);
		this.extendedElementContainer.setPaddingBottom(1);
		this.extendedElementContainer.setPaddingLeft(1);
		this.extendedElementContainer.setPaddingRight(1);
		// add Container Element to DIV
		DOM.appendChild(root, extendedElementContainer.getContainerElement());
	}

	protected void afterRender() {
		super.afterRender();
		//ZyG, 18.05.11
		if(!isReplay)
		{
			makeDraggable(myLink.getMap());
		}

		createDNDTarget();	
	}

	//ZyG, 18.05.11
	/**
	 * Init the DragListener to move the box via drag & drop
	 * 
	 * @param myMap
	 */
	protected void makeDraggable(GraphMap myMap) {

		LinkDraggable l = new LinkDraggable(this);
		l.setUseProxy(true);
		l.setMoveAfterProxyDrag(false);

		// Remove the comments to use the concrete box instead of the proxy
		// object while moving; not recommended because the connected curves /
		// lines will be updated immediately as well.
		// d.setUseProxy(false);

		l.setContainer(myMap);
		l.addDragListener(new DragListener());
	}
	
	private Resizable resizable = null;

	protected void makeResizeable() {
		resizable = new Resizable(this, "s");

		ResizeListener rListener = new ResizeListener() {
			@Override
			public void resizeEnd(ResizeEvent re) {
//				communicator.sendActionPackage(actionBuilder.updateLinkSize(myLink.getMap().getID(), myLink.getConnectedModel().getId(), height));
				sendUpdateLinkSize(myLink.getMap().getID(), myLink.getConnectedModel().getId(), height);
			}
		};
		resizable.addResizeListener(rListener);
	}
	
	protected abstract void sendUpdateLinkSize(String mapID, int linkID, int height);

	public void setEditButtonVisibility(boolean visible) {
		if (details) {
			if (visible) {
				for (ElementInfo childElement : getElementInfo().getChildElements().values()) {
					// Calculate current element Count
					int elementCount = MVCHelper.getChildModelsByElementID(getMyLink().getConnectedModel(), childElement.getElementID()).size();
					if ((childElement.getElementOption(ParameterTypes.ManualAdd) == null || !childElement.getElementOption(ParameterTypes.ManualAdd).equals("false")) && elementCount < childElement.getMaxQuantity()) {
						DOM.setStyleAttribute(this.addButton, "visibility", "visible");
					}
					if (elementCount > childElement.getMinQuantity()) {
						DOM.setStyleAttribute(this.delButton, "visibility", "visible");
					}
				}
				//these buttons should be visible no matter the link doesn't have child elements
				DOM.setStyleAttribute(this.switchDirectionButton, "visibility", "visible");
				DOM.setStyleAttribute(this.configButton, "visibility", "visible");
			} else {
				if (addButton.getClassName().equals("add-button") && delButton.getClassName().equals("del-button") && closeButton.getClassName().equals("link-close-button")) {
					DOM.setStyleAttribute(this.addButton, "visibility", "hidden");
					DOM.setStyleAttribute(this.switchDirectionButton, "visibility", "hidden");
					DOM.setStyleAttribute(this.delButton, "visibility", "hidden");
				}
				if(configButton.getClassName().equals("link-config-button")){
					DOM.setStyleAttribute(this.configButton, "visibility", "hidden");
				}
			}
		}
	}

	private Timer hoverTimer = new Timer() {
		public void run() {
			setEditButtonVisibility(false);
		}
	};

	/** Will be fired automatically when the mouse is entering the BoxArea */
	private void onMouseEnter() {
		// this.el().updateZIndex(1);
		setEditButtonVisibility(true);
		hoverTimer.cancel();
	}

	/**
	 * Will be fired automatically when the mouse is leaving the BoxArea
	 */
	private void onMouseLeave() {
		hoverTimer.schedule(500);
	}

	private DropTarget createDNDTarget() {

		final DropTarget target = new DropTarget(this);

		final GraphMap myMap = myLink.getMap();

		target.addDNDListener(new DNDListener() {
			public void dragDrop(DNDEvent e) {

				Logger.log("Drag Drop of link", Logger.DEBUG);
				boolean allowsTranscript = false;

				for (ElementInfo elem : myLink.getElementInfo().getChildElements().values()) {
					if (elem.getElementType().equalsIgnoreCase("transcript-link")) {
						allowsTranscript = true;
					}
				}

				if (e.getDragSource().getData() instanceof AbstractBox) {
					if(myMap.getMyCon() != null) {
						myMap.getMyCon().remove();
						myMap.setMyCon(null);
					}
					myMap.setStartPoint(null);
					myMap.setEndPoint(null);
					if (myMap.getEndConnector() != null) {
						myMap.getEndConnector().removeFromParent();
						myMap.setEndConnector(null);
					}

					AbstractBox tmpBox = (AbstractBox) e.getData();

//					CreateLinkDialog tmpDialog = new CreateLinkDialog(myMap, tmpBox, myLink, e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					AbstractCreateLinkDialog tmpDialog = createCreateLinkDialog(myMap, tmpBox, myLink, e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					myMap.add(tmpDialog);
					myMap.layout();
				}

				else if (e.getDragSource().getData() instanceof TranscriptLinkData && allowsTranscript) {
					// Drag & Drop from Transcript
					TranscriptLinkData tData = (TranscriptLinkData) e.getData();
					tData.setElementID(myLink.getConnectedModel().getId());
				}
				
				super.dragDrop(e);
				e.cancelBubble();
			}

			@Override
			public void dragEnter(DNDEvent e) {

				boolean allowsTranscript = false;

				// Collect IDs

				for (ElementInfo elem : myLink.getElementInfo().getChildElements().values()) {
					if (elem.getElementType().equalsIgnoreCase("transcript-link")) {
						allowsTranscript = true;
					}
				}

				e.getStatus().setStatus(false);
				e.getStatus().update("");

				if (e.getDragSource().getData() instanceof TranscriptLinkData && allowsTranscript) {
					e.getStatus().setStatus(true);
					e.getStatus().update("Link relation with transcript");
				} else if (e.getDragSource().getData() instanceof TranscriptLinkData) {
					// Do nothing
				}
				else if (myLink.getMap().getMyViewSession().getController().getMapInfo().isAllowLinksToLinks())
				{
					if (e.getDragSource().getData() instanceof AbstractBox && (((AbstractLinkPanel) target.getComponent()).getMyLink().getConnectedModel().getParents().get(0).getValue(ParameterTypes.RootElementId).equals(((AbstractBox) e.getDragSource().getData()).getConnectedModel().getValue(ParameterTypes.RootElementId)) || ((AbstractLinkPanel) target.getComponent()).getMyLink().getConnectedModel().getParents().get(1).getValue(ParameterTypes.RootElementId).equals(((AbstractBox) e.getDragSource().getData()).getConnectedModel().getValue(ParameterTypes.RootElementId))))
					{
						e.getStatus().setStatus(false);
						e.getStatus().update("Cannot be connected to this relation");

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
					}
					else
					{
						e.getStatus().setStatus(true);
						e.getStatus().update("Create new Relation");
					}
				}
				else
				{
					e.getStatus().setStatus(false);
					e.getStatus().update("Cannot connect to relations in this ontology.");

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
				}

				if (getController() != null) {
//					myController = LASAD_Client.getMVCController(myMap.getID());
					setController(myMap);
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
									.getConnectedModel().getParents().get(0)
									.getId() == AbstractLinkPanel.this.getMyLink()
									.getConnectedModel().getId() && ((AbstractLinkPanel) element)
									.getMyLink().getConnectedModel()
									.getParents().get(1).getId() == ((AbstractBox) e
									.getDragSource().getData())
									.getConnectedModel().getId())
									|| (((AbstractLinkPanel) element).getMyLink()
											.getConnectedModel().getParents()
											.get(1).getId() == AbstractLinkPanel.this
											.getMyLink().getConnectedModel()
											.getId() && ((AbstractLinkPanel) element)
											.getMyLink().getConnectedModel()
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
									myMap.getEndConnector().removeFromParent();
									myMap.setEndConnector(null);
									
									//Hack to manage Java Iterator issues
									listSize--;
								}
							}
						}
					}
					i++;
				}
				super.dragEnter(e);
			}

			@Override
			public void dragMove(DNDEvent e) {
				if (myMap.getEndConnector() != null) {
					myMap.getEndConnector().setPosition(e.getClientX() - myMap.getAbsoluteLeft() + myMap.getHScrollPosition(), e.getClientY() - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
					myMap.getMyCon().update();
				}
			}

			@Override
			public void dragLeave(DNDEvent e) {
				e.getStatus().setStatus(false);
				e.getStatus().update("");
				super.dragLeave(e);
			}
		});
		return target;
	}
	protected abstract AbstractCreateLinkDialog createCreateLinkDialog(GraphMap map, AbstractBox start, AbstractLink end, int posX, int posY);
	protected abstract AbstractMVController getController();
	protected abstract void setController(GraphMap myMap);
	

	private void setLocalSize(int width, int height) {
		if (width != -1 && width != this.width && height == -1) {
			// Set the Root Width
			this.width = width;
			super.setSize(width, -1);
			extendedElementContainer.setWidth(width);
		} else if (height != -1 && height != this.height && width == -1) {
			// Set the Root Height
			this.height = height;
			super.setSize(-1, height + generalOffsetHeight);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		} else if (height != -1 && width != -1) {
			this.width = width;
			this.height = height;
			super.setSize(width, height + generalOffsetHeight);
			extendedElementContainer.setWidth(this.width);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		}

		Logger.log("[LinkPanel][setLocalSize]: width: " + this.width + " height: " + this.height, Logger.DEBUG_DETAILS);
		myLink.updateLinkPanelPosition();
	}

	public void setSize(int width, int height) {
		if (width != -1 && width - generalOffsetWidth != this.width && height == -1) {
			// Set the Root Width
			this.width = width - generalOffsetWidth;
			super.setSize(width, -1);
			extendedElementContainer.setWidth(width);
		} else if (height != -1 && height - generalOffsetHeight != this.height && width == -1) {
			// Set the Root Height
			this.height = height - generalOffsetHeight;
			super.setSize(-1, height);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		} else if (height != -1 && width != -1) {
			this.width = width - generalOffsetWidth;
			this.height = height - generalOffsetHeight;
			super.setSize(width, height);
			extendedElementContainer.setWidth(this.width);
			extendedElementContainer.setHeight(this.height - generalOwnHeight);
		}
		Logger.log("[LinkPanel][setSize]: width: " + this.width + " height: " + this.height, Logger.DEBUG_DETAILS);
		myLink.updateLinkPanelPosition();
	}

	public void setRootElementID(String rootElementID) {
		if (details) {
			headerText.setInnerHTML(rootElementID + "&nbsp;&middot;&nbsp;<b>" + myLink.getElementInfo().getElementOption(ParameterTypes.Heading) + "</b>");
		}
	}

	public AbstractLink getMyLink() {
		return myLink;
	}

	public GenericFocusHandler getFocusHandler() {
		return myLink.getFocusHandler();
	}

	public void extendedElementContainerCallNewHeight(int height) {
		setLocalSize(-1, height + generalOwnHeight);
	}

	public void addExtendedElement(AbstractExtendedElement element) {
		// Redirect call to the Container
		extendedElementContainer.addExtendedElement(element);
	}

	public ElementInfo getElementInfo() {
		return myLink.getElementInfo();
	}

	public AbstractMVCViewSession getMVCViewSession() {
		return myLink.getMVCViewSession();
	}

	public void removeExtendedElement(AbstractExtendedElement element) {
		// Redirect call to the Container
		extendedElementContainer.removeExtendedElement(element);
	}

	public FocusableInterface getFocusParent() {
		// Redirect call to the Link itself
		return myLink.getFocusParent();
	}

	public void setElementFocus(boolean focus) {
		// In Future Panel Highlight
	}

	public void extendedElementContainerPublishedNewMaxHeight(int height) {
		// Updates the Resizable Element
		if(!this.isReplay)
		{
			resizable.setMaxHeight(generalOwnHeight + height + generalOffsetHeight);
		}
	}

	public void extendedElementContainerPublishedNewMinHeight(int height) {
		// Updates the Resizable Element
		if(!this.isReplay)
		{
			resizable.setMinHeight(generalOwnHeight + height + generalOffsetHeight);
		}
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
//		if (element.getConfig().getElementType().equalsIgnoreCase("text")) {
//			extendedElementContainer.addExtendedElement(element, true);
//		} else {
			extendedElementContainer.addExtendedElement(element, pos);
//		}
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return extendedElementContainer.getExtendedElements();
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return null;
	}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		return myLink.getHighlightParent();
	}

	@Override
	public void setHighlight(boolean highlight) {
		if (highlight) {
			this.root.setClassName("link-content-highlighted");
		} else {
			this.root.setClassName("link-content");
		}
	}

	// Check if author and current user are the same
	// if not highlight Box
	public void checkForHighlight(String username) {
		// LASADInfo.display("Username LinkPanel ", username);

		if (username != null && !LASAD_Client.getInstance().getUsername().equalsIgnoreCase(username)) {
			this.setHighlightAndBack();
		}
	}

	/**
	 * sets LinkPanel highlighted and back to original border after 30s seconds
	 */
	public void setHighlightAndBack() {

		if (details) {
			generalOffsetHeight = 10; // 5 Pixel for the top and bottom border
			generalOffsetWidth = 10; // 5 Pixel for the left and right border

			syncSize();

			this.root.setClassName("link-content-highlighted-color");

			DOM.setStyleAttribute(this.root, "borderColor", "#46FC00");
			DOM.setStyleAttribute(this.root, "borderWidth", "5px");
			DOM.setStyleAttribute(this.root, "borderStyle", "solid");

			// check if there is already a timer running
			// in case of , cancel running timer to ensure only one timer per
			// box
			if (this.timer != null) {
				this.timer.cancel();
				this.timer = null;

			}

			this.timer = new Timer() {
				public void run() {

					generalOffsetHeight = 2; // 2 Pixel for the Border
					generalOffsetWidth = 2; // 2 Pixel for the Border
					DOM.setStyleAttribute(AbstractLinkPanel.this.root, "borderWidth", "1px");
					AbstractLinkPanel.this.root.setClassName("link-content");

					// LinkPanel.this.syncSize(); // Does not work.
				}
			};

			// Schedule the timer to run once in 30 seconds in order to
			// deactivate
			// highlighting
			this.timer.schedule(30000);
		}

	}

	boolean faded = false;

	@Override
	public void setFadedOut(boolean fadedOut) {
		if (fadedOut) {
			if (details) {
				DOM.setStyleAttribute(header, "background", "#F9F9F9");
			}
			myLink.setLineColor("#F9F9F9");
			myLink.update();
		} else {
			if (details) {
				DOM.setStyleAttribute(header, "background", myLink.getElementInfo().getUiOption(ParameterTypes.BackgroundColor));
			}
			myLink.setLineColor(myLink.getElementInfo().getUiOption(ParameterTypes.BackgroundColor));
			myLink.update();
		}
	}

	public int getGeneralOffsetHeight() {
		return generalOffsetHeight;
	}

	public int getGeneralOffsetWidth() {
		return generalOffsetWidth;
	}		
	
	public void setPercent(float per)
	{
//		communicator.sendActionPackage(actionBuilder.updateLinkPosition(myLink.getMap().getID(), myLink.getConnectedModel().getId(), per));
		sendUpdateLinkPosition(myLink.getMap().getID(), myLink.getConnectedModel().getId(), per);
	}
	
	protected abstract void sendUpdateLinkPosition(String mapID, int linkID, float per);
	
	@Override
	protected void onPosition(int x, int y) {
		super.onPosition(x, y);

		// Update corresponding Connections after Box movement
		updateConnections();
	}
	
	/**
	 * This one just updates the connections' position in case the size of the
	 * box changes
	 */
	public void updateConnections() {
		try {
			for (Iterator<Connection> i = myLink.getConnector().getConnections().iterator(); i
					.hasNext();) {
				Connection con = (Connection) i.next();
				con.update();
			}
		} catch (Exception e) {
			Logger.log("updateConnections in class AbstractLinkPanel.java failed", Logger.DEBUG_ERRORS);
		}
	}
	
	@Override
	public void textAreaCallNewHeightgrow(int height) {
	}
}