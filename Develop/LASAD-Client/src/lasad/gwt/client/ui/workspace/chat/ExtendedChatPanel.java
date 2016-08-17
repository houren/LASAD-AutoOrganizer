package lasad.gwt.client.ui.workspace.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.xml.ExtendedChatXMLReader;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExtendedChatPanel extends ContentPanel implements CommonChatInterface {
	
	private Vector<ChatMessage> messages = new Vector<ChatMessage>();

	Element tbody = null;
	Element Buttonrow = null;
	Element ProponentTD = null;
	Element CriticTD = null;
	Element TableDiv = null;

	Element ButtonTable = null;

	private Label openerlbl = null;
	private Boolean forceselection = false;
	private Boolean sentenceopenerselected = false;
	private Boolean senderpaneldisabled = false;

	private final LASADActionSender communicator = LASADActionSender
			.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	 private ScrollPanel chatpanel=null;
	
	private AbstractGraphMap myMap = null;

	private com.extjs.gxt.ui.client.widget.button.Button sendbtn = null;
	private com.extjs.gxt.ui.client.widget.button.Button clearbtn = null;
	private TextBox txtchat = null;

	private String sendtxt = "";
	private String cleartxt = "";
	private String defaulttxt = "";
	private String endstring = "";
	private String sentenceOpenerConfig = "";
	private String fontsize = "";
	private String fontstyle = "";
	private Button activebutton = null;
	private boolean isReplay = false;

	VerticalPanel panel;
	DialogBox dialogbox;
	PopupPanel glass;
	VerticalPanel DialogBoxContents;
	HTML message;
	Button button;
	SimplePanel holder;

	public ExtendedChatPanel(AbstractGraphMap map, String xmlConfig) {
		this.myMap = map;

		this.setBodyBorder(false);
		sentenceOpenerConfig = xmlConfig;

		Map<String, String> openerrules = ExtendedChatXMLReader
				.GetSentenceOpenerRules(sentenceOpenerConfig);
		System.out.println(openerrules.toString());
		getConfiguration(openerrules);
		activebutton = new Button();

	}

	// Reading Sentence Opener Configs from Server
	void getConfiguration(Map<String, String> openerrules) {

		String keyvalue = "";
		keyvalue = openerrules.get("Force");
		if (!"".equals(keyvalue) && keyvalue != null) {
			forceselection = Boolean.parseBoolean(keyvalue);

		} else {

			forceselection = false;
		}

		keyvalue = openerrules.get("DefaultText");
		if (!"".equals(keyvalue) && keyvalue != null) {
			defaulttxt = keyvalue;

		} else {

			defaulttxt = "Chose the sentence opener:";
		}

		keyvalue = openerrules.get("SaveButtonText");
		if (!"".equals(keyvalue) && keyvalue != null) {
			sendtxt = keyvalue;

		} else {

			sendtxt = "Send";
		}

		keyvalue = openerrules.get("ClearButtonText");
		if (!"".equals(keyvalue) && keyvalue != null) {
			cleartxt = keyvalue;

		} else {

			cleartxt = "Clear";
		}

		keyvalue = openerrules.get("HeaderText");
		if (!"".equals(keyvalue) && keyvalue != null) {

			this.setHeading(keyvalue);

		} else {

			this.setHeading("Extended Chat");
		}

		keyvalue = openerrules.get("EndString");
		if (!"".equals(keyvalue) && keyvalue != null) {

			endstring = keyvalue;

		} else {

			endstring = ":";
		}

		keyvalue = openerrules.get("FontSize");
		if (keyvalue == null || "".equals(keyvalue)) {
			fontsize = "8";
		} else {
			fontsize = keyvalue;
		}

		keyvalue = openerrules.get("FontStyle");
		if (keyvalue == null || "".equals(keyvalue)) {
			
			fontstyle = "Verdana, sans-serif";
		} else {
			fontstyle = keyvalue;
		}

	}

	protected void afterRender() {
		super.afterRender();
		buildGUI();

		if (forceselection) {
			DisableSenderPanel();
			senderpaneldisabled = true;
		}
	}

	
	HorizontalPanel buildOpenerGrid() {

		HorizontalPanel sentenceOpenerPanel = new HorizontalPanel();

		sentenceOpenerPanel.addStyleName("paddedHorizontalPanel");
		sentenceOpenerPanel.setSpacing(2);

		ArrayList<ArrayList<Map<String, String>>> sentenceopenerconfig = new ArrayList<ArrayList<Map<String, String>>>();
		sentenceopenerconfig = ExtendedChatXMLReader
				.GetSentenOpenerConfig(sentenceOpenerConfig);

		int columncount = sentenceopenerconfig.size();
		if (columncount > 3) {

			columncount = 3;
		}
		if (columncount <= 0)
			columncount = 1;
		if (columncount > 2)
			columncount = 2;

		int columnwidth = 480 / columncount;
		VerticalPanel openercolumn = null;
		for (int i = 0; i < columncount; i++) {
			openercolumn = new VerticalPanel();

			openercolumn.addStyleName("paddedVerticalPanel");

			openercolumn.setSpacing(2);
			openercolumn.setWidth(columnwidth + "px");

			ArrayList<Map<String, String>> elementconfig = new ArrayList<Map<String, String>>();
			elementconfig = sentenceopenerconfig.get(i);

			int rowcount = elementconfig.size();
			if (rowcount > 7)
				rowcount = 7;

			for (int j = 0; j < rowcount; j++) {

				String type = elementconfig.get(j).get("Type");
				String text = elementconfig.get(j).get("Text");
				// String fontsize=elementconfig.get(j).get("FontSize");
				//String bold = elementconfig.get(j).get("Bold");
				//String color = elementconfig.get(j).get("Color");
				String backgroundcolor = elementconfig.get(j).get(
						"Backgroundcolor");
				

				if (type.equalsIgnoreCase("Button")) {

					final Button mybutton = new Button(text);

					mybutton.addStyleName("sentenceOpenerButton");
					mybutton.getElement().setAttribute("backcolor",
							backgroundcolor);

					

					DOM.setStyleAttribute(mybutton.getElement(), "fontSize",
							fontsize + "pt");

					DOM.setStyleAttribute(mybutton.getElement(), "fontFamily",
							fontstyle);

					
					mybutton.setWidth(columnwidth - 12 + "px");

					mybutton.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							if (openerlbl != null && !isReplay) {
								openerlbl.setText(mybutton.getText()
										+ endstring);

								openerlbl.setStyleAttribute(
										"backgroundColor",
										mybutton.getElement().getAttribute(
												"backcolor"));

								openerlbl.getElement().setAttribute(
										"backcolor",
										mybutton.getElement().getAttribute(
												"backcolor"));
								mybutton.getElement().getAttribute("backcolor");

								activebutton = mybutton;
								sentenceopenerselected = true;
								if (senderpaneldisabled) {

									EnableSenderPanel();
									senderpaneldisabled = false;

								}

							}

						}
					});

					DOM.setStyleAttribute(mybutton.getElement(), "background",
							backgroundcolor);

					openercolumn.add(mybutton);

				}

				else if (type.equalsIgnoreCase("Label")) {

					Label lbl = new Label();
					lbl.setText(text);
					lbl.addStyleName("sentenceOpenerLabel");

				
					openercolumn.add(lbl);

				}
			}

			openercolumn
					.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

			sentenceOpenerPanel.add(openercolumn);
			openercolumn = null;

		}

		return sentenceOpenerPanel;

	}

	void EnableSenderPanel() {
		if (sendbtn != null && txtchat != null) {

			sendbtn.enable();
			txtchat.setValue("");
			txtchat.setEnabled(true);
			senderpaneldisabled = false;
		}

	}

	void DisableSenderPanel() {
		if (sendbtn != null && txtchat != null) {
			sendbtn.disable();
			txtchat.setValue("Please select a sentence opener!");

			txtchat.setEnabled(false);
			senderpaneldisabled = true;

		}
	}

	private void buildGUI() {

		VerticalPanel vp = new VerticalPanel();

		vp.setWidth("300" + "px");
		HTML chatcontent = new HTML("");

		chatpanel = new ScrollPanel();

		chatpanel.add(chatcontent);

		chatpanel.setWidth("480" + "px");
		chatpanel.setHeight("200" + "px");
		DOM.setStyleAttribute(chatpanel.getElement(), "background", "#F0F8FF");
		chatpanel.setAlwaysShowScrollBars(false);

		vp.add(chatpanel);
		openerlbl = new Label();
		openerlbl.setText(defaulttxt);

		openerlbl.setWidth("100%");

		openerlbl.setStyleAttribute("color", "black");
		DOM.setStyleAttribute(openerlbl.getElement(), "fontSize", "11pt");

		HorizontalPanel chatsender = new HorizontalPanel();
		chatsender.setWidth("500" + "px");

		Grid chatsendergrid = new Grid(1, 3);
		txtchat = new TextBox();
		txtchat.setWidth("360" + "px");
		sendbtn = new com.extjs.gxt.ui.client.widget.button.Button(sendtxt);
		sendbtn.setWidth("50" + "px");
		clearbtn = new com.extjs.gxt.ui.client.widget.button.Button(
				cleartxt);
		clearbtn.setWidth("50" + "px");
		chatsendergrid.setWidget(0, 0, txtchat);
		chatsendergrid.setWidget(0, 1, sendbtn);
		chatsendergrid.setWidget(0, 2, clearbtn);
		chatsendergrid.setBorderWidth(1);
		chatsendergrid.setCellPadding(2);

		chatsendergrid.setWidth("480" + "px");

		chatsender.add(chatsendergrid);

		vp.add(openerlbl);

		vp.add(chatsender);
		vp.setBorderWidth(2);
		vp.setSpacing(5);
		vp.setWidth("100%");
		vp.setHeight("100%");

		sendbtn.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (!txtchat.getValue().equals("") || sentenceopenerselected) {

					if (activebutton != null) {
						myMap.recordCursorTracking();

//						String messageTxt = activebutton.getText()
//								+ "ã�„ï¿½
//								+ openerlbl.getElement().getAttribute(
//										"backcolor") + "ã�„ï¿½
//								+ txtchat.getValue() + "ã�„ï¿½
//								+ sentenceopenerselected;
//						communicator.sendActionPackage(actionBuilder
//								.sendChatMessage(myMap.getID(), messageTxt));
						
						String sentenceOpener = "";
						if (sentenceopenerselected) {
							sentenceOpener = activebutton.getText();
						}
						
						communicator.sendActionPackage(actionBuilder.sendChatMessage(myMap.getID(), 
								txtchat.getValue(), 
								sentenceOpener, 
								openerlbl.getElement().getAttribute("backcolor")));
						
						txtchat.setValue("");
						openerlbl.setText(defaulttxt);
						DOM.setStyleAttribute(openerlbl.getElement(),
								"background", "#ADD8E6");
						sentenceopenerselected = false;

						if (forceselection) {

							DisableSenderPanel();
							senderpaneldisabled = true;

						}

					}

				}
			}
		});

		txtchat.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {

				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {

					if (!txtchat.getValue().equals("")
							|| sentenceopenerselected) {

						if (activebutton != null) {
							myMap.recordCursorTracking();

//							String messageTxt = activebutton.getText()
//									+ "ã�„ï¿½
//									+ openerlbl.getElement().getAttribute(
//											"backcolor") + "ã�„ï¿½
//									+ txtchat.getValue() + "ã�„ï¿½
//									+ sentenceopenerselected;
//							communicator.sendActionPackage(actionBuilder
//									.sendChatMessage(myMap.getID(), messageTxt));
							
							String sentenceOpener = "";
							if (sentenceopenerselected) {
								sentenceOpener = activebutton.getText();
							}
							
							communicator.sendActionPackage(actionBuilder.sendChatMessage(myMap.getID(), 
									txtchat.getValue(), 
									sentenceOpener, 
									openerlbl.getElement().getAttribute("backcolor")));
							
							txtchat.setValue("");

							openerlbl.setText(defaulttxt);
							DOM.setStyleAttribute(openerlbl.getElement(),
									"background", "#ADD8E6");
							sentenceopenerselected = false;

							if (forceselection) {

								DisableSenderPanel();
								senderpaneldisabled = true;

							}

						}

					}
				}

				event.stopPropagation();

			}
		});

		clearbtn.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				openerlbl.setText(defaulttxt);
				DOM.setStyleAttribute(openerlbl.getElement(), "background",
						"#ADD8E6");
				txtchat.setValue("");
				sentenceopenerselected = false;

				if (forceselection) {
					DisableSenderPanel();
					senderpaneldisabled = true;
				}

			}
		});

		DOM.setStyleAttribute(vp.getElement(), "background", "#ADD8E6");

		HorizontalPanel mysentenceOpenerPanel = new HorizontalPanel();

		mysentenceOpenerPanel = buildOpenerGrid();

		vp.add(mysentenceOpenerPanel);



		this.add(vp);

	}

	public void addChatMessage(String nickname, String time, String text, String opener, String textColor, boolean replay) {
		// Format Time
		DateTimeFormat df = DateTimeFormat.getFormat("HH:mm:ss");
		Date dateT = new Date(Long.parseLong(time));

		StringBuilder sb = new StringBuilder();

//		String[] temp = new String[4];
//		
//		temp = text.split("ã�„ï¿½);
		
		//temp[0] is the sentence opener
		addChatMessage(new ChatMessage(df.format(dateT), nickname, opener, textColor, text));

		sb.append(((HTML) chatpanel.getWidget()).getHTML());

		sb.append("<b style=\"color: black;font-weight: bold\">");
		sb.append("(" + df.format(dateT) + ")");

		sb.append(" " + nickname + ": ");
		sb.append("</b>");
		if (!opener.equals("")) {
			sb.append("<label style=\"color: black;font-style:italic;background-color:"
					+ textColor + "\">");
			sb.append(opener + " ");
			sb.append("</label>");
		}
		
		sb.append("<label style=\"color: black;\">");
		sb.append(text);
		sb.append("</label>");

		
		sb.append("<br>");

		String chatMsg = sb.toString();

		((HTML) chatpanel.getWidget()).setHTML(chatMsg);

		chatpanel.scrollToBottom();
		if (!replay) {
			if (!nickname.equals(LASAD_Client.getInstance().getUsername())) {
				sb = new StringBuilder(nickname);
				sb.append(": ");
				sb.append(opener + " " + text);

				String infoMsg = sb.toString();

				LASADInfo.display("New chat message", infoMsg);

			}
		}
		else
		{
			sb = new StringBuilder(nickname);
			sb.append(": ");
			sb.append(opener + " " + text);
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
		txtchat.setReadOnly(true);
		this.isReplay = true;
		sendbtn.removeAllListeners();
		clearbtn.removeAllListeners();
	}
	
	public void clearBoard()
	{
		((HTML) chatpanel.getWidget()).setHTML("");
	}

}