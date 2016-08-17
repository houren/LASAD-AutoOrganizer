package lasad.gwt.client.ui.workspace.argumentmap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.chat.ExtendedChatPanel;
import lasad.gwt.client.ui.workspace.chat.argument.ChatArgument;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.feedback.argument.FeedbackPanelArgument;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;
import lasad.gwt.client.ui.workspace.minimap.MiniMapPanel;
import lasad.gwt.client.ui.workspace.tableview.ArgumentEditionStyleEnum;
import lasad.gwt.client.ui.workspace.tableview.argument.MapTableArgument;
import lasad.gwt.client.ui.workspace.transcript.Transcript;
import lasad.gwt.client.ui.workspace.tutorial.argument.TutorialArgument;
import lasad.gwt.client.ui.workspace.userlist.UserListPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class ArgumentMapSpace extends GraphMapSpace {
	
	// Basic panels for additions
	private ContentPanel eastPanel = null, westPanel = null;

	 //Configurable additions
	private ChatArgument chatPanel = null;
	private ExtendedChatPanel extendedChatPanel = null;
	private FeedbackPanelArgument feedbackPanel = null;
	private UserListPanel userListPanel = null;
	private Transcript transcriptPanel = null;
	private TutorialArgument tutorialPanel = null;
	private SelectionDetailsPanel selectionDetailsPanel = null;
	private MiniMapPanel miniMapPanel = null;

	protected MVCViewSession session;
	
	private boolean sentenceopenerActive=false;

	private boolean miniMapActive = false, transcriptActive = false, tutorialActive = false;
	private boolean chatActive = false, feedbackActive = false, userListActive = false;

	public ArgumentMapSpace(AbstractMVCViewSession session, Size size){
		super(session, size);
		this.session = (MVCViewSession)session;
		menuBar = new ArgumentMapMenuBar(this);
		workspaceArea.setTopComponent(menuBar);
		createDrawingAreaSpace();
	}
	
//	public ArgumentMapSpaceNew(Size size) {
//		super(size);
//	}

	@Override
	protected void createDrawingAreaSpace() {
		ArgumentEditionStyleEnum editionStyle = LASAD_Client.getMapEditionStyle(String.valueOf(session.getController().getMapID()));
		if (editionStyle == ArgumentEditionStyleEnum.GRAPH) {
			myMap = new ArgumentMap(this);

		} else {
			myMap = new MapTableArgument(this);
		}
		workspaceArea.add(myMap);
		this.layout();

	}

	@Override
	public MVCViewSession getSession() {
		return session;
	}

	@Override
	public void setSession(AbstractMVCViewSession session) {
		this.session = (MVCViewSession) session;
	}

	@Override
	public void changeSelectionDetailsPanelTo(SelectionDetailsPanel sdp) {
		this.selectionDetailsPanel = sdp;
		
		if(selectionDetailsActive) {
			this.updateEastPanel(true);	
		}
	}

	@Override
	public ChatArgument getChatPanel() {
		return chatPanel;
	}

	@Override
	public ExtendedChatPanel getExtendedChatPanel() {
		return extendedChatPanel;
	}

	public FeedbackPanelArgument getFeedbackPanel() {
		return feedbackPanel;
	}

	public SelectionDetailsPanel getSelectionDetails() {
		return selectionDetailsPanel;
	}

	public Transcript getTranscript() {
		return transcriptPanel;
	}

	public TutorialArgument getTutorial() {
		return tutorialPanel;
	}

	public UserListPanel getUserListPanel() {
		return userListPanel;
	}

	public void setChat(boolean visible) {
		if (visible && !chatActive) {
			// Shows up the chat
			if (chatPanel == null) {
				chatPanel = new ChatArgument(myMap);
			}
			chatActive = true;
			updateEastPanel(true);
		} else if (!visible && chatActive) {
			chatActive = false;
			updateEastPanel(true);
		}
	}
	
	public void setSentenceOpener(boolean visible,String xmlConfig ){
		if (visible && !sentenceopenerActive) {
			// Shows up the chat
			if (extendedChatPanel == null) {
				extendedChatPanel = new ExtendedChatPanel(myMap,xmlConfig);
			}
			sentenceopenerActive = true;
			updateEastPanel(true);
		} else if (!visible && sentenceopenerActive) {
			sentenceopenerActive = false;
			updateEastPanel(true);
		}
	}

	public void setFeedback(boolean visible) {
		if (visible && !feedbackActive) {
			// Shows up the chat
			if (feedbackPanel == null) {
				feedbackPanel = new FeedbackPanelArgument(myMap);
			}
			feedbackActive = true;
			updateWestPanel();
		} else if (!visible && feedbackActive) {
			feedbackActive = false;
			updateWestPanel();
		}
	}

	public void setSelectionDetails(boolean visible) {
		if (visible && !selectionDetailsActive) {
			// Shows up the tutorial
			if (selectionDetailsPanel == null) {
				selectionDetailsPanel = new SelectionDetailsPanel(myMap);
			}
			selectionDetailsActive = true;
			updateEastPanel(true);
		} else if (!visible && selectionDetailsActive) {
			selectionDetailsActive = false;
			updateEastPanel(true);
		}
	}

	public void setTranscript(boolean visible) {
		if (visible && !transcriptActive) {
			// Activate the Transcript
			BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300);
			westData.setSplit(true);
			westData.setCollapsible(true);
			westData.setMargins(new Margins(0));
			transcriptPanel = new Transcript(myMap);
			add(transcriptPanel, westData);
			layout();
			transcriptActive = true;
		} else if (!visible && !transcriptActive) {
			// Deactivte the Transcript
			remove(transcriptPanel);
			transcriptActive = false;
		}
	}

	public void setTutorial(boolean visible) {
		if (visible && !tutorialActive) {
			// Shows up the tutorial
			if (tutorialPanel == null) {
				tutorialPanel = new TutorialArgument(myMap);
			}
			tutorialActive = true;
			updateEastPanel(true);
		} else if (!visible && tutorialActive) {
			tutorialActive = false;
			updateEastPanel(true);
		}
	}

	public void setUserList(boolean visible) {
		if (visible && !userListActive) {
			// Shows up the list of users
			if (userListPanel == null) {
				userListPanel = new UserListPanel(myMap,this);
			}
			userListActive = true;
			updateEastPanel(true);
		} else if (!visible && userListActive) {
			userListActive = false;
			updateEastPanel(true);
		}
	}

	/**
	 * The east panels may contain: list of active users, chat,
	 * tutorial/selection details
	 */
	public void updateEastPanel(boolean isFirstCall) {
		if(isFirstCall){
			int east=0;
			if(sentenceopenerActive){
				east=500;
			}
			else{
				east=300;
			}
			BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, east);
			eastData.setSplit(true);
			eastData.setCollapsible(true);
			eastData.setMargins(new Margins(0));

			if (eastPanel == null) {
				eastPanel = new ContentPanel();

				BorderLayout borderLayout = new BorderLayout();
				borderLayout.setEnableState(false);

				eastPanel.setLayout(borderLayout);
				eastPanel.setHeading(myConstants.SupportingResources());
				eastPanel.setHeaderVisible(true);
				eastPanel.setBodyBorder(false);
				eastPanel.setBorders(false);
				eastPanel.setSize("100%", "100%");

				add(eastPanel, eastData);
			} else {
				eastPanel.removeAll();
			}
			//Minimap
			if (miniMapActive) {
				//Set BorderLayout of MiniMap
				//BorderLayoutData miniMapBorder = new BorderLayoutData(LayoutRegion.CENTER);
				BorderLayoutData miniMapBorder = new BorderLayoutData(LayoutRegion.NORTH, 363);
				miniMapBorder.setMargins(new Margins(90,0,0,0));
				//miniMapBorder.setMargins(new Margins(0));
				miniMapBorder.setSplit(true);
				miniMapBorder.setCollapsible(false);
				miniMapBorder.setMaxSize(563);
						
				//Add MiniMap to EastPanel of ArgumentMapSpace
				eastPanel.add(miniMapPanel, miniMapBorder);
			}
			// UserList
			if (userListActive) {
				BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,90);
				northData.setSplit(true);
				northData.setCollapsible(true);
				northData.setMargins(new Margins(0));
				eastPanel.add(userListPanel, northData);
			}
			if (chatActive) {
				BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,200);
				southData.setSplit(true);
				southData.setCollapsible(true);
				southData.setMargins(new Margins(0));
				if(chatPanel!=null)
					eastPanel.add(chatPanel, southData);
			} else if(sentenceopenerActive){
				BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,550);
				southData.setSplit(true);
				southData.setCollapsible(true);
				southData.setMargins(new Margins(0));
				if(extendedChatPanel!=null)
					eastPanel.add(extendedChatPanel, southData);
			}
			if (selectionDetailsActive || tutorialActive) {
				BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
				centerData.setSplit(true);
				centerData.setCollapsible(true);
				centerData.setMargins(new Margins(0));

				if ((selectionDetailsActive && tutorialActive) || (selectionDetailsActive && !tutorialActive)) {
					eastPanel.add(selectionDetailsPanel, centerData);
				} else if (!selectionDetailsActive && tutorialActive) {
					eastPanel.add(tutorialPanel, centerData);
				}
			}
		} else{
			BorderLayoutData northData;
			if(userListPanel.getNumOfItems()<4){
				northData = new BorderLayoutData(LayoutRegion.NORTH,90);
			} else{
				if(userListPanel.getNumOfItems()<9) {
					northData = new BorderLayoutData(LayoutRegion.NORTH,90+(userListPanel.getNumOfItems()-3)*20);
				} else{
					northData = new BorderLayoutData(LayoutRegion.NORTH,190);
				}
			}
			northData.setSplit(true);
			northData.setCollapsible(true);
			northData.setMargins(new Margins(0));
			eastPanel.add(userListPanel, northData);
		}
		eastPanel.layout();
		layout();
	}

	/**
	 * Updates the west panel. The west panel includes the transcript and the
	 * feedback
	 */
	private void updateWestPanel() {

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 300);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0));

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		centerData.setCollapsible(true);
		centerData.setMargins(new Margins(0));

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 200);
		southData.setSplit(true);
		southData.setCollapsible(true);
		southData.setMargins(new Margins(0));

		// Two Panels are active, use a container
		if (westPanel == null) {
			westPanel = new ContentPanel();

			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setEnableState(false);
			westPanel.setLayout(borderLayout);

			westPanel.setHeaderVisible(true);
			westPanel.setHeading("External resources");
			westPanel.setBodyBorder(false);
			westPanel.setBorders(false);
			westPanel.setSize("100%", "100%");
			add(westPanel, westData);
		} else {
			westPanel.removeAll();
		}

		// Transcript
		if (transcriptActive) {
			westPanel.add(transcriptPanel, centerData);
		}

		// Feedback
		if (feedbackActive && !transcriptActive) {
			westPanel.add(feedbackPanel, centerData);
		} else if (feedbackActive && transcriptActive) {
			westPanel.add(feedbackPanel, southData);
		}
		
		if(sentenceopenerActive)
		westPanel.collapse();
		layout();
	}
	
	public MiniMapPanel getMiniMapPanel() {
		return miniMapPanel;
	}
	public void setMiniMap(boolean visible) {
		if (visible && !miniMapActive) {
			// Shows up MiniMap with Anchor
			if (miniMapPanel == null) {
				miniMapPanel = new MiniMapPanel(myMap,this);
			}
			miniMapActive = true;
			updateEastPanel(true);
			
		} else if (!visible && miniMapActive) {
			// Hides MiniMap with Anchor
			miniMapActive = false;
			updateEastPanel(true);
		}
	}
	
}
