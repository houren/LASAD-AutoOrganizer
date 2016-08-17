package lasad.gwt.client.ui.workspace.chat;

import java.util.Date;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractChat extends ContentPanel implements CommonChatInterface {

	private Vector<ChatMessage> messages = new Vector<ChatMessage>();
	
//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private Element chat = null, textField = null;
	private EventListener domListener = null;
	private AbstractGraphMap myMap = null;

	public AbstractChat(AbstractGraphMap map) {
		this.myMap = map;
		this.setHeading("Chat");
		this.setBodyBorder(false);
	}

	protected void afterRender() {
		super.afterRender();
		buildGUI();
	}

	private void buildGUI() {
		chat = DOM.createDiv();
		chat.setClassName("chatChat-div");

		this.getBody().appendChild(chat);
		DOM.setStyleAttribute(this.getBody().dom, "position", "relative");

		Element textFieldDummy = DOM.createDiv();
		textFieldDummy.setClassName("chatTextField-div");
		this.getBody().appendChild(textFieldDummy);

		textField = DOM.createInputText();
		textField.setClassName("chatTextField");

		DOM.appendChild(textFieldDummy, textField);

		// TextFieldListener
		domListener = new EventListener() {

			public void onBrowserEvent(Event event) {
				if (event.getCurrentEventTarget().equals(textField)) {
					int code = event.getTypeInt();
					if (code == Event.ONKEYDOWN) {
						if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
							// Send Message to Server
							if (!DOM.getElementProperty(textField, "value").equals("")) {
								myMap.recordCursorTracking(); 
								
								// Save cursor position in database
//								communicator.sendActionPackage(actionBuilder.sendChatMessage(myMap.getID(), DOM.getElementProperty(textField, "value")));
								sendChatMessage(myMap.getID(), DOM.getElementProperty(textField, "value"));

								// Clear TextField
								DOM.setElementProperty(textField, "value", "");
							}
						}
					}
				}
				event.stopPropagation();
			}
		};

		DOM.setEventListener(textField, domListener);
		DOM.sinkEvents(textField, Event.ONKEYDOWN);
	}
	
	protected abstract void sendChatMessage(String mapID, String msg);
	
	public void addChatMessage(String nickname, String time, String text, boolean replay) {
		// Format Time
		DateTimeFormat df = DateTimeFormat.getFormat("HH:mm:ss");
		Date dateT = new Date(Long.parseLong(time));
		
		//Hack for feedback coming into chat from DFKI user
		if ("DFKI".equals(nickname)){
			nickname = "Feedback";
		}
		addChatMessage(new ChatMessage(df.format(dateT), nickname, text));
		
		StringBuilder sb = new StringBuilder();

		sb.append(chat.getInnerHTML());
		sb.append("(");
		sb.append(df.format(dateT));
		sb.append(") <b>");
		sb.append(nickname);
		sb.append("</b>: ");
		sb.append(text);
		sb.append("<br>");

		String chatMsg = sb.toString();
		chat.setInnerHTML(chatMsg);
		chat.setScrollTop(chat.getScrollHeight());

		if (!replay) {
			if (!nickname.equals(LASAD_Client.getInstance().getUsername())) {
				sb = new StringBuilder(nickname);
				sb.append(": ");
				sb.append(text);

//				String infoMsg = sb.toString();
//
//				LASADInfo.display("New chat message", infoMsg);

			}
		}
		else
		{
			sb = new StringBuilder(nickname);
			sb.append(": ");
			sb.append(text);
		}
	}

	@Override
	public void addChatMessage(ChatMessage msg) {
		this.messages.add(msg);
	}

	@Override
	public Vector<ChatMessage> getChatMessages() {
		return messages;
	}
	
	public void disableTextField()
	{
		((InputElement) textField.cast()).setReadOnly(true);
	}
	
	public void clearBoard()
	{
		chat.setInnerHTML("");
	}

}