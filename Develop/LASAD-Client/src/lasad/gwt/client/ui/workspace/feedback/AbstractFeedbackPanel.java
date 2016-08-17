package lasad.gwt.client.ui.workspace.feedback;

import java.util.HashMap;

import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractFeedbackPanel extends ContentPanel {
	protected AbstractGraphMap myMap = null;
//	MVController controller = null;
	
	public AbstractFeedbackPanel(AbstractGraphMap map) {
		this.myMap = map;

		this.setHeading("Feedback");

		this.setBodyBorder(false);
			
//		controller = LASAD_Client.getMVCController(myMap.getID());
	}
	
	public abstract AbstractMVController getController();
//	{
//		if(controller == null) {
//			LASAD_Client.getMVCController(myMap.getID());
//		}
//		return controller;
//	}

	public abstract void setController(AbstractMVController controller); 
//	{
//		this.controller = controller;
//	}

	public AbstractGraphMap getMyMap() {
		return myMap;
	}

	public void setMyMap(AbstractGraphMap myMap) {
		this.myMap = myMap;
	}

	HashMap<Integer, Element> clusterToMessage = new HashMap<Integer, Element>();

	Element chat = null;
	EventListener domListener = null;

	private void buildGUI() {
		chat = DOM.createDiv();
		chat.setClassName("chatChat-div");

		this.getBody().appendChild(chat);
		DOM.setStyleAttribute(this.getBody().dom, "position", "relative");

		Element textFieldDummy = DOM.createDiv();
		textFieldDummy.setClassName("chatTextField-div");
		this.getBody().appendChild(textFieldDummy);
	}

	public void addFeedbackMessage(int clusterID, String feedback) {
		Element messageFrame = DOM.createDiv();
		messageFrame.setClassName("chatChatMessageFrame");
		DOM.appendChild(chat, messageFrame);

		// Name
		Element chatMessage = DOM.createDiv();
		chatMessage.setClassName("feedbackText-div");
		chatMessage.setInnerHTML(feedback + "<br>");
		DOM.appendChild(messageFrame, chatMessage);

		DOM.scrollIntoView(messageFrame);

		clusterToMessage.put(clusterID, messageFrame);
		
//		AbstractFeedbackPanelListener myListener = new FeedbackPanelListenerArgument(this, clusterID);
		AbstractFeedbackPanelListener myListener = getFeedbackPanelListener(this, clusterID);
		DOM.sinkEvents(chatMessage, Events.OnClick.getEventCode() | Events.OnContextMenu.getEventCode() | Events.OnMouseOver.getEventCode() | Events.OnMouseOut.getEventCode());
		DOM.setEventListener(chatMessage, myListener);
	}
	
	protected abstract AbstractFeedbackPanelListener getFeedbackPanelListener(AbstractFeedbackPanel myReference, int clusterID);

	public void removeFeedbackMessage(int clusterID) {
		Element msg = null;
		if (clusterToMessage.get(clusterID) != null) {
			msg = clusterToMessage.get(clusterID);
		}

		try {
			DOM.removeChild(chat, msg);
		} catch (Exception E) {}

		clusterToMessage.remove(clusterID);
	}

	protected void afterRender() {
		super.afterRender();

		buildGUI();
	}
}