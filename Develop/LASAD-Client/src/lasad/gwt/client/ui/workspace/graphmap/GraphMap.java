package lasad.gwt.client.ui.workspace.graphmap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connection.GenericStraightTwoEndedConnection;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.SimpleConnector;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.GenericSelectionHandler;
import lasad.gwt.client.ui.common.fade.GenericFadeableElementHandler;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

/*
 * refactoring of argumentmap.ArgumentMap
 * This is the basis for the argument map, that will later be inherited by Argument Map.
 * This class contains some of the tools for modifying the graph, such as dragging and dropping
 * graph nodes, connecting graph nodes, clickable functionality, etc.
 *
 * Super: AbstractGraphMap
 * Sub(s): ArgumentMap
 */
public abstract class GraphMap extends AbstractGraphMap{

	// For Drag and drop
		private DropTarget dropTarget = null;
		private Connector startPoint, endPoint;
		private Connection myCon;
		private SimpleConnector endConnector;
		
		private Listener<ComponentEvent> baseEventListener;
		private ContentPanel scrollPanel;
		
		private boolean clicked = false;
		private boolean cursorTrackingEnabled = false;

		public GraphMap(GraphMapSpace parentElement) {
			super(parentElement);
			
			sinkEvents(Event.ONCONTEXTMENU);

			this.setHeaderVisible(false);
			this.setBodyBorder(false);
			this.setBorders(false);
			this.setLayout(new AbsoluteLayout());

			baseEventListener = new Listener<ComponentEvent>() {

				public void handleEvent(ComponentEvent be) {
					// This seams to be buggy. Even if one does not move the mouse
					// there is a OnMouseMove event. Thus we need to calculate of
					// there is a position change...
					if (be.getType() == Events.OnMouseMove) {
						clicked = false;
						if (cursorTrackingEnabled && myAwarenessCursorID != -1 && LASAD_Client.getInstance().isAuthed()) {
							int newMouseX = (be.getClientX() - getAbsoluteLeft() + getHScrollPosition());
							int newMouseY = (be.getClientY() - getAbsoluteTop() + getVScrollPosition());

							int totalChange = Math.abs((oldMouseX - newMouseX)) + Math.abs((oldMouseY - newMouseY));

							if (refreshMyCursor && !disableNormalCursorUpdates) {
								// If the number is too low there will be to many
								// messages.
								if (totalChange > 20) {
									//communicator.sendActionPackage(actionBuilder.updateMyCursorPositionNonPersistent(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY));
									sendHandleEventResultToServer(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY);
									oldMouseX = newMouseX;
									oldMouseY = newMouseY;
									refreshMyCursor = false;
								}
							}

							else if (totalChange > 20 && refreshMyCursorPersistent) {
								//communicator.sendActionPackage(actionBuilder.updateMyCursorPositionPersistent(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY));
								sendHandleEventResultToServer(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), newMouseX, newMouseY);
								oldMouseX = newMouseX;
								oldMouseY = newMouseY;
								refreshMyCursorPersistent = false;
								numberOfCursorRecords++;
							}
						}
					}
					else if (be.getType() == Events.OnMouseDown) {
						clicked = true;
					}
					else if ((be.getType() == Events.OnClick && clicked) || be.getType() == Events.OnDoubleClick) {
						focusHandler.releaseAllFocus();
					} 
					else if (be.getType() == Events.OnContextMenu){
						//Modified by Darlan Santana Farias (DSF) in July 2015
						//If the user is trying to access the context menu
							// and the selected element is a text area, then it shows the default context menu
							// otherwise, it shows LASAD's context menu
						Logger.log("be.getTarget().getClassName(): "+be.getTarget().getClassName(), Logger.DEBUG);
						if(!be.getTarget().getClassName().equals("extendedTextElement-TextArea-EditMode")){
							be.preventDefault();
							eventContextMenu(be.getClientX() - getAbsoluteLeft() + getHScrollPosition(), be.getClientY() - getAbsoluteTop() + getVScrollPosition());
						}
					}
					//end of DSF's modifications
					be.cancelBubble();
				}
			};

			this.addListener(Events.OnDoubleClick, baseEventListener);
			this.addListener(Events.OnContextMenu, baseEventListener);
			this.addListener(Events.OnClick, baseEventListener);
			this.addListener(Events.OnMouseDown, baseEventListener);
			
			this.addListener(Events.OnMouseMove, baseEventListener);
			this.sinkEvents(Events.OnMouseMove.getEventCode());

			this.sinkEvents(Events.OnClick.getEventCode() | Events.OnDoubleClick.getEventCode() | Events.OnContextMenu.getEventCode() | Events.OnMouseDown.getEventCode());

			scrollPanel = new ContentPanel();
			scrollPanel.setBodyStyleName("connectorPanel_normal");
			scrollPanel.setHeaderVisible(false);
			scrollPanel.setBorders(false);
			scrollPanel.setBodyBorder(false);
			this.add(scrollPanel);

			initMapDimensions();
			this.setScrollMode(Scroll.ALWAYS);
			createDropTarget();
		}
		
		protected abstract void sendHandleEventResultToServer(String mapID, int cursorID, String username, int x, int y);

		public DropTarget createBoxDropTarget(AbstractBox b) {
			// The Map Target must be added after the Box Targets to work on the boxes
			this.dropTarget.release();
			DropTarget target = new DropTarget(b);
			createDropTarget();
			return target;
		}

		protected abstract AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map, int posX, int posY, TranscriptLinkData tData);
		protected abstract AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map, int posX, int posY);
		protected abstract AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(GraphMap map, AbstractBox start, int posX, int posY, int step);
		
		private void createDropTarget() {
			dropTarget = new DropTarget(this) {

				@Override
				protected void onDragDrop(DNDEvent e) {
					
					if (e.getData() instanceof TranscriptLinkData) {
						
						//GraphMap.this.add(new GenericCreateBoxDialog(GraphMap.this, e.getXY().x - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getXY().y - GraphMap.this.getAbsoluteTop() + getVScrollPosition(), (TranscriptLinkData) e.getData()));
						GraphMap.this.add(createCreateBoxDialog(GraphMap.this, e.getXY().x - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getXY().y - GraphMap.this.getAbsoluteTop() + getVScrollPosition(), (TranscriptLinkData) e.getData()));
						GraphMap.this.layout();
					} else if (e.getData() instanceof AbstractBox) {
						
						myCon.remove();
//						myCon.disconnect(endConnector.getConnector());
						myCon = null;
						endConnector.removeFromParent();
						endConnector = null;
						startPoint = null;
						endPoint = null;
//						GraphMap.this.add(new GenericCreateBoxLinkDialog(GraphMap.this, ((AbstractBox) e.getData()), e.getXY().x - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getXY().y - GraphMap.this.getAbsoluteTop() + getVScrollPosition(), 1));
						GraphMap.this.add(createCreateBoxLinkDialog(GraphMap.this, ((AbstractBox) e.getData()), e.getXY().x - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getXY().y - GraphMap.this.getAbsoluteTop() + getVScrollPosition(), 1));
						GraphMap.this.layout();
					}
				}

				@Override
				protected void onDragFail(DNDEvent event) {
					super.onDragFail(event);
					myCon.remove();
//					myCon.disconnect(endConnector.getConnector());
					myCon = null;
					endConnector.removeFromParent();
					endConnector = null;
					startPoint = null;
					endPoint = null;
					
				}

				// This creates the link between two nodes
				@Override
				protected void onDragEnter(DNDEvent e) {
				
					e.getStatus().setStatus(true);
					e.getStatus().update("Create new Contribution");
			
					if (e.getDragSource().getData() instanceof AbstractBox) {
						
						if(GraphMap.this.myArgumentMapSpace.isDirectLinkingDenied()) {
							e.getStatus().setStatus(false);
							e.getStatus().update("");
						}
						
						if (startPoint == null) {
							startPoint = ((AbstractBox) e.getData()).getConnector();
						}
						if (endConnector == null) {
							endConnector = new SimpleConnector(e.getClientX() - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getClientY() - GraphMap.this.getAbsoluteTop() + getVScrollPosition());
							GraphMap.this.add(endConnector);
						}
						if (endPoint == null) {
							endPoint = endConnector.getConnector();
						}
						if (myCon == null) {
							myCon = new GenericStraightTwoEndedConnection(GraphMap.this, startPoint, endPoint, "2px", "#000000");
							myCon.appendTo(GraphMap.this);
							GraphMap.this.layout();
						}
						

					}
				}

				@Override
				protected void onDragLeave(DNDEvent e) {
					e.getStatus().setStatus(false);
					e.getStatus().update("");

					super.onDragLeave(e);
				}

				@Override
				protected void onDragMove(DNDEvent e) {
					super.onDragMove(e);
					if (endConnector != null && myCon != null) {
						endConnector.setPosition(e.getClientX() - GraphMap.this.getAbsoluteLeft() + getHScrollPosition(), e.getClientY() - GraphMap.this.getAbsoluteTop() + getVScrollPosition());
						myCon.update();
					}
				}
			};
		}

		public void enableCursorTracking() {
			if (!LASAD_Client.getInstance().getRole().equalsIgnoreCase("Observer")) {
				this.cursorTrackingEnabled = true;

				// Timer to allow cursor update
				com.google.gwt.user.client.Timer t = new com.google.gwt.user.client.Timer() {
					public void run() {
						refreshMyCursor = true;
					}
				};
				t.scheduleRepeating(1500);
			}
		}

		/**
		 * Shows up the context menu
		 * 
		 * @param x
		 *            X Position
		 * @param y
		 *            Y Position
		 */
		private void eventContextMenu(int x, int y) {
//			add(new GenericCreateBoxDialog(this, x, y));
			add(createCreateBoxDialog(this, x, y));
			
			this.layout();
		}

		public void extendMapDimension(Direction dir, int value) {
			mapDimensions.put(dir, mapDimensions.get(dir) + value);
			scrollPanel.setSize(getMapDimensionSize().width, getMapDimensionSize().height);
		}

		public SimpleConnector getEndConnector() {
			return endConnector;
		}

		public Connector getEndPoint() {
			return endPoint;
		}

		public GenericFadeableElementHandler getFadeHandler() {
			return fadeHandler;
		}

		public GenericFocusHandler getFocusHandler() {
			return focusHandler;
		}

		public FocusableInterface getFocusParent() {
			// The Map is the Focus Root, so return null
			return null;
		}

		public GenericHighlightHandler getHighlightHandler() {
			return highlightHandler;
		}

		public HighlightableElementInterface getHighlightParent() {
			return null;
		}

		public Size getMapDimensionSize() {
			return new Size(mapDimensions.get(Direction.LEFT) + mapDimensions.get(Direction.RIGHT), mapDimensions.get(Direction.UP) + mapDimensions.get(Direction.DOWN));
		}

		public int getMyAwarenessCursorID() {
			return myAwarenessCursorID;
		}

		public Connection getMyCon() {
			return myCon;
		}

		public abstract AbstractMVCViewSession getMyViewSession();
//		public MVCViewSession getMyViewSession() {
//			return myViewSession;
//		}

		public GenericSelectionHandler getSelectionHandler() {
			return selectionHandler;
		}

		public Connector getStartPoint() {
			return startPoint;
		}

		private void initMapDimensions() {
			mapDimensions.put(Direction.UP, 2500);
			mapDimensions.put(Direction.RIGHT, 2500);
			mapDimensions.put(Direction.DOWN, 2500);
			mapDimensions.put(Direction.LEFT, 2500);
			scrollPanel.setSize(getMapDimensionSize().width, getMapDimensionSize().height);
		}

		public boolean isDeleteElementsWithoutConfirmation() {
			return deleteElementsWithoutConfirmation;
		}

		public void recordCursorTracking() {
			if (!LASAD_Client.getInstance().getRole().equalsIgnoreCase("Observer")) {
				if (disableNormalCursorUpdates == false) {

					// Timer to allow cursor update
					disableNormalCursorUpdates = true;

					t = new Timer() {
						public void run() {
							if (numberOfCursorRecords < 10) {
								refreshMyCursorPersistent = true;
							} else {
								numberOfCursorRecords = 0;
								refreshMyCursorPersistent = false;
								disableNormalCursorUpdates = false;

								if (GraphMap.this.t != null) {
									GraphMap.this.t.cancel();
								}
							}
						}
					};
					t.scheduleRepeating(750);
				}
			}
		}

		public void setDeleteElementsWithoutConfirmation(boolean deleteElementsWithoutConfirmation) {
			this.deleteElementsWithoutConfirmation = deleteElementsWithoutConfirmation;
		}

		public void setElementFocus(boolean focus) {
		}

		public void setEndConnector(SimpleConnector endConnector) {
			this.endConnector = endConnector;
		}

		public void setEndPoint(Connector endPoint) {
			this.endPoint = endPoint;
		}

		public void setFadeHandler(GenericFadeableElementHandler fadeHandler) {
			this.fadeHandler = fadeHandler;
		}

		public void setFocusHandler(GenericFocusHandler focusHandler) {
			this.focusHandler = focusHandler;
		}

		public void setHighlight(boolean highlight) {
		}

		public void setHighlightHandler(GenericHighlightHandler highlightHandler) {
			this.highlightHandler = highlightHandler;
		}

		public void setMyAwarenessCursorID(int myAwarenessCursorID) {
			this.myAwarenessCursorID = myAwarenessCursorID;
		}

		public void setMyCon(Connection myCon) {
			this.myCon = myCon;
		}

		public abstract void setMyViewSession(AbstractMVCViewSession myViewSession);
//		public void setMyViewSession(MVCViewSession myViewSession) {
//			this.myViewSession = myViewSession;
//		}

		public void setRefreshMyCursor(boolean refreshMyCursor) {
			this.refreshMyCursor = refreshMyCursor;
		}

		public void setSelectionHandler(GenericSelectionHandler selectionHandler) {
			this.selectionHandler = selectionHandler;
		}

		public void setStartPoint(Connector startPoint) {
			this.startPoint = startPoint;
		}
		
		public void releaseAllListeners()
		{
			this.dropTarget.release();
			this.removeAllListeners();
		}

		@Override
		public abstract void deleteAllFeedbackClusters();
}
