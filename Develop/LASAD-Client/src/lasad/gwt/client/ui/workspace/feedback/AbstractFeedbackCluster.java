package lasad.gwt.client.ui.workspace.feedback;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public abstract class AbstractFeedbackCluster extends BoxComponent implements MVCViewRecipient {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private GraphMap myMap;
	private int ID;
	private String feedbackMsg, detailedFeedbackMsg = null;
	private boolean detailsVisible = false, responseRequired = false;

	private Element rootElement;
	private Vector<Integer> highlightedElements = new Vector<Integer>();
	private ShowFeedbackTextWindow myFeedback = null;

	public AbstractFeedbackCluster(GraphMap correspondingMap, int elementID, String feedbackMsg, String posX, String posY, boolean responseReq) {
		this.myMap = correspondingMap;
		this.ID = elementID;
		this.feedbackMsg = feedbackMsg;
		this.responseRequired = responseReq;
		
		this.setPosition(Integer.parseInt(posX), Integer.parseInt(posY));
		initElements();
		this.setVisible(false);
	}
	
	public AbstractFeedbackCluster(GraphMap correspondingMap, int elementID, String feedbackMsg, String detailedFeedbackMsg, String posX, String posY, boolean responseReq) {
		this(correspondingMap, elementID, feedbackMsg, posX, posY, responseReq);
		this.detailedFeedbackMsg = detailedFeedbackMsg;
	}
	
	private void initElements() {
		rootElement = DOM.createDiv();
		rootElement.setClassName("feedback_cluster_icon");
		this.setSize(20, 20);
	}

	private Listener<ComponentEvent> componentListener = null;
	
	final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {   
	      public void handleEvent(MessageBoxEvent ce) {   
	        detailsVisible = false;
			showHighlights(false);
			myMap.layout();
	      }   
	    };   

	/**
	 * Handle the whole set of the BoxComponentEvent handling Don't call this
	 * method directly, it will be done afterRender
	 */
	private void setComponentHandling() {

		componentListener = new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if (be.getEventTypeInt() == Events.OnMouseOver.getEventCode()) {
					onMouseOver(be);
				} else if (be.getEventTypeInt() == Events.OnMouseOut.getEventCode()) {
					onMouseOut(be);
				} else if (be.getEventTypeInt() == Events.OnClick.getEventCode()) {
					onClick(be);
				} else if (be.getEventTypeInt() == Events.OnContextMenu.getEventCode()) {
					onContextMenu(be);
				}
			}

			private void onMouseOver(ComponentEvent be) {
				showHighlights(true);
				myFeedback = new ShowFeedbackTextWindow(myMap, feedbackMsg, be.getXY().x - myMap.getAbsoluteLeft() + myMap.getHScrollPosition() + 20, be.getXY().y - myMap.getAbsoluteTop() + myMap.getVScrollPosition());
				myMap.add(myFeedback);
				myMap.layout();
			}

			private void onMouseOut(ComponentEvent be) {
				
				if(!detailsVisible) {
					if (myFeedback != null) {
						myFeedback.removeFromParent();
						myFeedback = null;
					}
					showHighlights(false);
					myMap.layout();
				}
			}

			private void onContextMenu(ComponentEvent be) {
				be.preventDefault();
//				communicator.sendActionPackage(actionBuilder.removeElement(myMap.getID(), ID));
				sendRemoveElementToServer(myMap.getID(), ID);
				
				be.cancelBubble();
			}
			
			private void onClick(ComponentEvent be) {
				if (detailedFeedbackMsg != null) {
					detailsVisible = true;
					
					showHighlights(true);
					myMap.layout();
					
					if (myFeedback != null) {
						myFeedback.removeFromParent();
						myFeedback = null;
					}
					
					if(responseRequired) {
						MessageBox box = MessageBox.prompt("Detailed feedback", detailedFeedbackMsg+ "<br><br>Please explain your thoughts:", true);   
				        box.addCallback(new Listener<MessageBoxEvent>() {   
				          public void handleEvent(MessageBoxEvent be) {   
				            //String v = be.getValue();   
				            
					        detailsVisible = false;
							showHighlights(false);
							myMap.layout();
//				            LASADInfo.display("MessageBox", "You entered '{0}'", new Params(v));   
				            
				            // TODO Send to server and connected clients...
				            //actionBuilder.updateElementWithMultipleValues(mapID, id, values)
				          }   
				        });   
					}
					else {
						MessageBox.alert("Detailed feedback", detailedFeedbackMsg, l);
					}
				}

				be.cancelBubble();
			}
		};

		this.addListener(Events.OnMouseOver, componentListener);
		this.sinkEvents(Events.OnMouseOver.getEventCode());
		
		this.addListener(Events.OnClick, componentListener);
		this.sinkEvents(Events.OnClick.getEventCode());

		this.addListener(Events.OnMouseOut, componentListener);
		this.sinkEvents(Events.OnMouseOut.getEventCode());

		this.addListener(Events.OnContextMenu, componentListener);
		this.sinkEvents(Events.OnContextMenu.getEventCode());
	}
	
	protected abstract void sendRemoveElementToServer(String mapID, int elementID);

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vName) {
		Logger.log("[lasad.gwt.client.ui.workspace.feedback.FeedbackCluster] changeValueMVC: name: " + vName + " value: " + model.getValue(vName), Logger.DEBUG);
		if (vName == ParameterTypes.HighlightElementId) {
			this.highlightedElements.add(Integer.parseInt(model.getValue(ParameterTypes.HighlightElementId)));

			recalculatePosition();

			this.setZIndex(XDOM.getTopZIndex());
			this.setVisible(true);
		}
		else if (vName == ParameterTypes.Highlight) {
			if(model.getValue(ParameterTypes.Highlight).equalsIgnoreCase("TRUE")) {
				this.showHighlights(true);
			}
			else {
				this.showHighlights(false);
			}
		}
	}

	private void recalculatePosition() {
		MVController controller = LASAD_Client.getMVCController(myMap.getID());

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

		for (Integer i : highlightedElements) {
			int currentX = 0, currentY = 0;

			if (controller.getElement(i.intValue()) != null) {

				if (controller.getElement(i.intValue()).getType().equalsIgnoreCase("box") || controller.getElement(i.intValue()).getType().equalsIgnoreCase("relation")) {

					if (controller.getElement(i.intValue()).getValue(ParameterTypes.PosX) != null) {
						currentX = Integer.parseInt(controller.getElement(i).getValue(ParameterTypes.PosY));
					}

					if (controller.getElement(i.intValue()).getValue(ParameterTypes.PosY) != null) {
						currentY = Integer.parseInt(controller.getElement(i).getValue(ParameterTypes.PosY));
					}

					if (minY > currentY) {
						minY = currentY;
					}

					if (minX > currentX) {
						minX = currentX;
					}

					if (controller.getElement(i.intValue()).getValue(ParameterTypes.Width) != null) {
						Logger.log("WIDTH: " + controller.getElement(i.intValue()).getValue(ParameterTypes.Width), Logger.DEBUG_DETAILS);
						currentX += Integer.parseInt(controller.getElement(i).getValue(ParameterTypes.Width));
					}

					if (controller.getElement(i.intValue()).getValue(ParameterTypes.Height) != null) {
						Logger.log("HEIGHT: " + controller.getElement(i.intValue()).getValue(ParameterTypes.Height), Logger.DEBUG_DETAILS);
						currentY += Integer.parseInt(controller.getElement(i).getValue(ParameterTypes.Height));
					}

					if (maxX < currentX) {
						maxX = currentX;
					}

					if (maxY < currentY) {
						maxY = currentY;
					}
				}
			}
		}

		int newPositionX = (minX + maxX) / 2;
		int newPositionY = (minY + maxY) / 2;

		// Randomly move the stars slightly to make stars visible that would appear stacked
		int plusOrMinus = (int) (Math.random()*2);
		int xRand = (int) (Math.random()*25);
		if(plusOrMinus <= 1) {
			xRand *= -1;
		}
		
		plusOrMinus = (int) (Math.random()*2);
		int yRand = (int) (Math.random()*25);
		if(plusOrMinus <= 1) {
			yRand *= -1;
		}
		
		// TODO Send update to server and update model
		this.setPosition((newPositionX+xRand), (newPositionY+yRand));

		myMap.layout();
	}

	public void showHighlights(boolean show) {
		if (show) {
			MVController controller = null;
			controller = LASAD_Client.getMVCController(myMap.getID());

			for (int id : highlightedElements) {
				if (controller.getElement(id) != null) {
					if (controller.getElement(id).getType().equals("box") || controller.getElement(id).getType().equals("relation")) {
						Vector<Parameter> tmpVector = new Vector<Parameter>();
						Parameter param = new Parameter(ParameterTypes.Highlighted, "TRUE");
						tmpVector.add(param);
						controller.updateElement(id, tmpVector);
					}
				}
			}
		} else {
			MVController controller = null;
			controller = LASAD_Client.getMVCController(myMap.getID());

			for (int id : highlightedElements) {
				if (controller.getElement(id) != null) {
					if (controller.getElement(id).getType().equals("box") || controller.getElement(id).getType().equals("relation")) {
						Vector<Parameter> tmpVector = new Vector<Parameter>();
						Parameter param = new Parameter(ParameterTypes.Highlighted, "FALSE");
						tmpVector.add(param);
						controller.updateElement(id, tmpVector);
					}
				}
			}
		}
	}

	private AbstractUnspecifiedElementModel connectedModel;

	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}

	public void setConnectedModel(AbstractUnspecifiedElementModel connectedModel) {
		this.connectedModel = connectedModel;
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		
		if (myFeedback != null) {
			myFeedback.removeFromParent();
			myFeedback = null;
		}
		showHighlights(false);
		myMap.layout();
		
		MVController controller = LASAD_Client.getMVCController(myMap.getID());

		if (controller != null) {
			LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getFeedbackPanel().removeFeedbackMessage(ID);
		}
		
		this.removeFromParent();
		myMap.layout();
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		return false;
	}

	protected void afterRender() {
		super.afterRender();
		setComponentHandling();
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
}