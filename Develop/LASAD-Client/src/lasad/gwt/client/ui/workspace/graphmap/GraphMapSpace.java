package lasad.gwt.client.ui.workspace.graphmap;

//import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.ui.workspace.chat.AbstractChat;
import lasad.gwt.client.ui.workspace.chat.ExtendedChatPanel;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

//is like ArgumentMapSpace
public abstract class GraphMapSpace extends LayoutContainer {
	protected lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class); // i18N

//	// Basic panels for additions
//	private ContentPanel eastPanel = null, westPanel = null;

//	 //Configurable additions
//	private Chat chatPanel = null;
//	private ExtendedChatPanel extendedChatPanel = null;
//	private FeedbackPanel feedbackPanel = null;
//	private UserListPanel userListPanel = null;
//	private Transcript transcriptPanel = null;
//	private Tutorial tutorialPanel = null;
//	private SelectionDetailsPanel selectionDetailsPanel = null;

//	protected MVCViewSession session;
	protected AbstractGraphMap myMap;
	protected GraphMapMenuBar menuBar;
	protected ContentPanel workspaceArea;
//	private boolean sentenceopenerActive=false;

//	private boolean transcriptActive = false, tutorialActive = false;
//	private boolean chatActive = false, feedbackActive = false, userListActive = false;
	protected boolean selectionDetailsActive = false;
	protected boolean trackCursor = false, directLinkingDenied = false;
	
	
//	public GraphMapSpace(MVCViewSession session, Size size) {
//		
//	}
	public GraphMapSpace(AbstractMVCViewSession session, Size size) {
//		this.session = session;

		//sentenceopener=OntologyReader.enableSentenceOpener;
		this.setLayout(new BorderLayout());
		this.setSize(size.width, size.height);
		this.setStyleAttribute("position", "relative");
		this.setBorders(false);
		this.setLayoutOnChange(true);

		// ArgumentMap space
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		centerData.setMargins(new Margins(0));

		VerticalPanel centerDataDivider = new VerticalPanel();
		centerDataDivider.setSize("100%", "100%");

		workspaceArea = new ContentPanel(new FitLayout());

		workspaceArea.setHeaderVisible(false);
		add(workspaceArea, centerData);

//		menuBar = new ArgumentMapMenuBar(this);
//		workspaceArea.setTopComponent(menuBar);

		//Make this call from child constructor
		//createDrawingAreaSpace();
		
//		ArgumentEditionStyleEnum editionStyle = LASAD_Client.getMapEditionStyle(String.valueOf(session.getController().getMapID()));
//		
//		if (editionStyle == ArgumentEditionStyleEnum.GRAPH) {
//			myMap = new ArgumentMap(this);
//
//		} else {
//			myMap = new ArgumentMapTable(this);
//		}
//		workspaceArea.add(myMap);
//		this.layout();
	}
	
	/*
	 *  Call createDrawingAreaSpace methods from child constructor 
	 */
	protected abstract void createDrawingAreaSpace();
	
	public boolean isDirectLinkingDenied() {
		return directLinkingDenied;
	}

	public void setDirectLinkingDenied(boolean directLinkingDenied) {
		this.directLinkingDenied = directLinkingDenied;
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		// centers the map view
		// this.getMyMap().getLayoutTarget().dom.setScrollLeft(this.getMyMap().getMapDimensionSize().width
		// / 2);
		// this.getMyMap().getLayoutTarget().dom.setScrollTop(this.getMyMap().getMapDimensionSize().height
		// / 2);
	}

	public GraphMapMenuBar getMenuBar() {
		return menuBar;
	}

	public AbstractGraphMap getMyMap() {
		return myMap;
	}
	
	public boolean isTrackCursor() {
		return this.trackCursor;
	}
	
	public void setMenuBar(GraphMapMenuBar menuBar) {
		this.menuBar = menuBar;
	}

	public void setMyMap(GraphMap myMap) {
		this.myMap = myMap;
	}
	
	public abstract AbstractMVCViewSession getSession();
	public abstract void setSession(AbstractMVCViewSession session);

	public void setTrackCursor(boolean b) {
		this.trackCursor = b;
		if (b) {
			this.myMap.enableCursorTracking();
		}
	}
	
	public abstract void changeSelectionDetailsPanelTo(SelectionDetailsPanel sdp);
//	public void changeSelectionDetailsPanelTo(SelectionDetailsPanel sdp) {
//	this.selectionDetailsPanel = sdp;
//	
//	if(selectionDetailsActive) {
//		this.updateEastPanel(true);	
//	}
//}
	
	public abstract AbstractChat getChatPanel();

//public Chat getChatPanel() {
//	return chatPanel;
//}


	public abstract ExtendedChatPanel getExtendedChatPanel();
//public ExtendedChatPanel getExtendedChatPanel() {
//	return extendedChatPanel;
//}


//public FeedbackPanel getFeedbackPanel() {
//	return feedbackPanel;
//}

//	public SelectionDetailsPanel getSelectionDetails() {
//		return selectionDetailsPanel;
//	}

	

//	public Transcript getTranscript() {
//		return transcriptPanel;
//	}

//	public Tutorial getTutorial() {
//		return tutorialPanel;
//	}

//	public UserListPanel getUserListPanel() {
//		return userListPanel;
//	}

//	public void setChat(boolean visible) {
//		if (visible && !chatActive) {
//			// Shows up the chat
//			if (chatPanel == null) {
//				chatPanel = new Chat(myMap);
//			}
//			chatActive = true;
//			updateEastPanel(true);
//		} else if (!visible && chatActive) {
//			chatActive = false;
//			updateEastPanel(true);
//		}
//	}
	
//	public void setSentenceOpener(boolean visible,String xmlConfig ){
//		if (visible && !sentenceopenerActive) {
//			// Shows up the chat
//			if (extendedChatPanel == null) {
//				extendedChatPanel = new ExtendedChatPanel(myMap,xmlConfig);
//			}
//			sentenceopenerActive = true;
//			updateEastPanel(true);
//		} else if (!visible && sentenceopenerActive) {
//			sentenceopenerActive = false;
//			updateEastPanel(true);
//		}
//	}

//	public void setFeedback(boolean visible) {
//		if (visible && !feedbackActive) {
//			// Shows up the chat
//			if (feedbackPanel == null) {
//				feedbackPanel = new FeedbackPanel(myMap);
//			}
//			feedbackActive = true;
//			updateWestPanel();
//		} else if (!visible && feedbackActive) {
//			feedbackActive = false;
//			updateWestPanel();
//		}
//	}

//	public void setSelectionDetails(boolean visible) {
//		if (visible && !selectionDetailsActive) {
//			// Shows up the tutorial
//			if (selectionDetailsPanel == null) {
//				selectionDetailsPanel = new SelectionDetailsPanel(myMap);
//			}
//			selectionDetailsActive = true;
//			updateEastPanel(true);
//		} else if (!visible && selectionDetailsActive) {
//			selectionDetailsActive = false;
//			updateEastPanel(true);
//		}
//	}

//	public void setTranscript(boolean visible) {
//		if (visible && !transcriptActive) {
//			// Activate the Transcript
//			BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300);
//			westData.setSplit(true);
//			westData.setCollapsible(true);
//			westData.setMargins(new Margins(0));
//			transcriptPanel = new Transcript(myMap);
//			add(transcriptPanel, westData);
//			layout();
//			transcriptActive = true;
//		} else if (!visible && !transcriptActive) {
//			// Deactivte the Transcript
//			remove(transcriptPanel);
//			transcriptActive = false;
//		}
//	}

//	public void setTutorial(boolean visible) {
//		if (visible && !tutorialActive) {
//			// Shows up the tutorial
//			if (tutorialPanel == null) {
//				tutorialPanel = new Tutorial(myMap);
//			}
//			tutorialActive = true;
//			updateEastPanel(true);
//		} else if (!visible && tutorialActive) {
//			tutorialActive = false;
//			updateEastPanel(true);
//		}
//	}

//	public void setUserList(boolean visible) {
//		if (visible && !userListActive) {
//			// Shows up the list of users
//			if (userListPanel == null) {
//				userListPanel = new UserListPanel(myMap,this);
//			}
//			userListActive = true;
//			updateEastPanel(true);
//		} else if (!visible && userListActive) {
//			userListActive = false;
//			updateEastPanel(true);
//		}
//	}

//	/**
//	 * The east panels may contain: list of active users, chat,
//	 * tutorial/selection details
//	 */
//	public void updateEastPanel(boolean isFirstCall) {
//		if(isFirstCall){
//			int east=0;
//			if(sentenceopenerActive){
//				east=500;
//			}
//			else{
//				east=300;
//			}
//			BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, east);
//			eastData.setSplit(true);
//			eastData.setCollapsible(true);
//			eastData.setMargins(new Margins(0));
//
//			if (eastPanel == null) {
//				eastPanel = new ContentPanel();
//
//				BorderLayout borderLayout = new BorderLayout();
//				borderLayout.setEnableState(false);
//
//				eastPanel.setLayout(borderLayout);
//				eastPanel.setHeading(myConstants.SupportingResources());
//				eastPanel.setHeaderVisible(true);
//				eastPanel.setBodyBorder(false);
//				eastPanel.setBorders(false);
//				eastPanel.setSize("100%", "100%");
//
//				add(eastPanel, eastData);
//			} else {
//				eastPanel.removeAll();
//			}
//			// UserList
//			if (userListActive) {
//				BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,90);
//				northData.setSplit(true);
//				northData.setCollapsible(true);
//				northData.setMargins(new Margins(0));
//				eastPanel.add(userListPanel, northData);
//			}
//			if (chatActive) {
//				BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,200);
//				southData.setSplit(true);
//				southData.setCollapsible(true);
//				southData.setMargins(new Margins(0));
//				if(chatPanel!=null)
//					eastPanel.add(chatPanel, southData);
//			} else if(sentenceopenerActive){
//				BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,550);
//				southData.setSplit(true);
//				southData.setCollapsible(true);
//				southData.setMargins(new Margins(0));
//				if(extendedChatPanel!=null)
//					eastPanel.add(extendedChatPanel, southData);
//			}
//			if (selectionDetailsActive || tutorialActive) {
//				BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
//				centerData.setSplit(true);
//				centerData.setCollapsible(true);
//				centerData.setMargins(new Margins(0));
//
//				if ((selectionDetailsActive && tutorialActive) || (selectionDetailsActive && !tutorialActive)) {
//					eastPanel.add(selectionDetailsPanel, centerData);
//				} else if (!selectionDetailsActive && tutorialActive) {
//					eastPanel.add(tutorialPanel, centerData);
//				}
//			}
//		} else{
//			BorderLayoutData northData;
//			if(userListPanel.getNumOfItems()<4){
//				northData = new BorderLayoutData(LayoutRegion.NORTH,90);
//			} else{
//				if(userListPanel.getNumOfItems()<9) {
//					northData = new BorderLayoutData(LayoutRegion.NORTH,90+(userListPanel.getNumOfItems()-3)*20);
//				} else{
//					northData = new BorderLayoutData(LayoutRegion.NORTH,190);
//				}
//			}
//			northData.setSplit(true);
//			northData.setCollapsible(true);
//			northData.setMargins(new Margins(0));
//			eastPanel.add(userListPanel, northData);
//		}
//		eastPanel.layout();
//		layout();
//	}

//	/**
//	 * Updates the west panel. The west panel includes the transcript and the
//	 * feedback
//	 */
//	private void updateWestPanel() {
//
//		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300);
//		westData.setSplit(true);
//		westData.setCollapsible(true);
//		westData.setMargins(new Margins(0));
//
//		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
//		centerData.setSplit(true);
//		centerData.setCollapsible(true);
//		centerData.setMargins(new Margins(0));
//
//		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 200);
//		southData.setSplit(true);
//		southData.setCollapsible(true);
//		southData.setMargins(new Margins(0));
//
//		// Two Panels are active, use a container
//		if (westPanel == null) {
//			westPanel = new ContentPanel();
//
//			BorderLayout borderLayout = new BorderLayout();
//			borderLayout.setEnableState(false);
//			westPanel.setLayout(borderLayout);
//
//			westPanel.setHeaderVisible(true);
//			westPanel.setHeading("External resources");
//			westPanel.setBodyBorder(false);
//			westPanel.setBorders(false);
//			westPanel.setSize("100%", "100%");
//			add(westPanel, westData);
//		} else {
//			westPanel.removeAll();
//		}
//
//		// Transcript
//		if (transcriptActive) {
//			westPanel.add(transcriptPanel, centerData);
//		}
//
//		// Feedback
//		if (feedbackActive && !transcriptActive) {
//			westPanel.add(feedbackPanel, centerData);
//		} else if (feedbackActive && transcriptActive) {
//			westPanel.add(feedbackPanel, southData);
//		}
//		
//		if(sentenceopenerActive)
//		westPanel.collapse();
//		layout();
//	}

	public boolean isSelectionDetailsActive() {
		return selectionDetailsActive;
	}
	
	public ContentPanel getWorspace()
	{
		return workspaceArea;
	}
}
