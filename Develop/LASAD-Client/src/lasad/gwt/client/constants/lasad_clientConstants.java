package lasad.gwt.client.constants;

/**
 * Interface to represent the constants contained in resource bundle:
 * 'LASADConstants.properties'.
 */
public interface lasad_clientConstants extends com.google.gwt.i18n.client.ConstantsWithLookup {

	@DefaultStringValue("About")
	@Key("About")
	String Introduction();

	@DefaultStringValue("Login & Map Overview")
	@Key("LoginTabText")
	String LoginTabText();

	@DefaultStringValue("<div align=center><h1>Welcome to LASAD!</h1><p>Please login and choose an argument map to work on...</p></div>")
	@Key("LoginTabTopic")
	String LoginTabTopic();

	@DefaultStringValue("Login")
	@Key("Login")
	String Login();

	@DefaultStringValue("Login")
	@Key("LoginButton")
	String LoginButton();

	@DefaultStringValue("Map")
	@Key("MapTabText")
	String MapTabText();

	@DefaultStringValue("Username")
	@Key("Username")
	String Username();

	@DefaultStringValue("Password")
	@Key("Password")
	String Password();

	@DefaultStringValue("Given text")
	@Key("Given text")
	String Transcript();

	@DefaultStringValue("Active maps")
	@Key("ActiveMaps")
	String ActiveMaps();

	@DefaultStringValue("Edit")
	@Key("EditMenu")
	String EditMenu();

	@DefaultStringValue("Add...")
	@Key("AddMenu")
	String AddMenu();
	
	@DefaultStringValue("Group tools")
	@Key("GroupTools")
	String GroupTools();

	@DefaultStringValue("Ontology / Map name")
	@Key("TableMapNames")
	String TableMapNames();

	@DefaultStringValue("Author")
	@Key("TableMapAuthor")
	String TableMapAuthor();

	@DefaultStringValue("Users")
	@Key("TableUsers")
	String TableUsers();

	@DefaultStringValue("Create a new map...")
	@Key("CreateNewMap")
	String CreateNewMap();

	@DefaultStringValue("Loading template details...")
	@Key("LoadingTemplateDetails")
	String LoadingTemplateDetails();

	@DefaultStringValue("Loading map details...")
	@Key("MapInformationPanelLoadingMapDetails")
	String MapInformationPanelLoadingMapDetails();

	@DefaultStringValue("Map details")
	@Key("MapInformationPanelHeading")
	String MapInformationPanelHeading();

	@DefaultStringValue("Map ID:")
	@Key("MapInformationPanelMapID")
	String MapInformationPanelMapID();

	@DefaultStringValue("Map name:")
	@Key("MapInformationPanelMapName")
	String MapInformationPanelMapName();

	@DefaultStringValue("Author:")
	@Key("MapInformationPanelOriginator")
	String MapInformationPanelOriginator();

	@DefaultStringValue("Ontology:")
	@Key("MapInformationPanelOntology")
	String MapInformationPanelOntology();

	@DefaultStringValue("Description:")
	@Key("MapInformationPanelDescription")
	String MapInformationPanelDescription();

	@DefaultStringValue("Active users:")
	@Key("MapInformationPanelActiveUsers")
	String MapInformationPanelActiveUsers();

	@DefaultStringValue("Possible map actions:")
	@Key("MapInformationPanelMapActions")
	String MapInformationPanelMapActions();

	@DefaultStringValue("Join map")
	@Key("MapInformationPanelPossibleActionsJoin")
	String MapInformationPanelPossibleActionsJoin();
	
//David Drexler Edit-BEGIN
	
	@DefaultStringValue("MiniMap with anchor")
	@Key("MiniMapHeading")
	String MiniMapHeading();
	
	@DefaultStringValue("Jump To Anchor")
	@Key("MiniMapUseAnchorButtonText")
	String MiniMapUseAnchorButtonText();  
	  
	@DefaultStringValue("Jump to anchor area")
	@Key("MiniMapUseAnchorButtonTitle")
	String MiniMapUseAnchorButtonTitle(); 
	
	@DefaultStringValue("New Anchor = Current Area")
	@Key("MiniMapNewAnchorButtonText")
	String MiniMapNewAnchorButtonText();  
	  
	@DefaultStringValue("Overwrite old anchor with current area")
	@Key("MiniMapNewAnchorButtonTitle")
	String MiniMapNewAnchorButtonTitle(); 
	
	@DefaultStringValue("Your browser does not support this mini map. Please check for updates.")
	@Key("MiniMapCanvasErrorMessage")
	String MiniMapCanvasErrorMessage();
	  
	@DefaultStringValue("Start Replay")
	@Key("MapInformationPanelPossibleActionsReplay")
	String MapInformationPanelPossibleActionsReplay();
	  
	@DefaultStringValue("Progress")
	@Key("MapInformationPanelReplayWaitBoxTitle")
	String MapInformationPanelReplayWaitBoxTitle();
	  
	@DefaultStringValue("Loading replay, please wait...")
	@Key("MapInformationPanelReplayWaitBoxMsg")
	String MapInformationPanelReplayWaitBoxMsg();
	  
	@DefaultStringValue("Initializing...")
	@Key("MapInformationPanelReplayWaitBoxProgressText")
	String MapInformationPanelReplayWaitBoxProgressText();
	  
	@DefaultStringValue("Map Is Empty")
	@Key("MapInformationPanelReplayEmptyMapTitle")
	String MapInformationPanelReplayEmptyMapTitle();
	  
	@DefaultStringValue("No replay possible.")
	@Key("MapInformationPanelReplayEmptyMapText")
	String MapInformationPanelReplayEmptyMapText();

	@DefaultStringValue("No Map")
	@Key("MapInformationPanelReplayNoMapTitle")
	String MapInformationPanelReplayNoMapTitle();
	    
	@DefaultStringValue("No replay possible.")
	@Key("MapInformationPanelReplayNoMapText")
	String MapInformationPanelReplayNoMapText();

	@DefaultStringValue("User Error")
	@Key("MapInformationPanelReplayNoUserTitle")
	String MapInformationPanelReplayNoUserTitle();
	    
	@DefaultStringValue("No replay possible. User is not logged in.")
	@Key("MapInformationPanelReplayNoUserText")
	String MapInformationPanelReplayNoUserText();

	@DefaultStringValue("No Action")
	@Key("MapInformationPanelReplayNoActionTitle")
	String MapInformationPanelReplayNoActionTitle();
	    
	@DefaultStringValue("No replay possible. There exists no action in map.")
	@Key("MapInformationPanelReplayNoActionText")
	String MapInformationPanelReplayNoActionText();  
	  
	@DefaultStringValue("Error")
	@Key("MapInformationPanelReplayErrorTitle")
	String MapInformationPanelReplayErrorTitle();
	    
	@DefaultStringValue("No replay possible. An error has occured.")
	@Key("MapInformationPanelReplayErrorText")
	String MapInformationPanelReplayErrorText();
	    
	@DefaultStringValue("Replay - Map")
	@Key("ReplayTabTitle")
	String ReplayTabTitle();
	  
	@DefaultStringValue("Replay Control - Map")
	@Key("ReplayControlTitle")
	String ReplayControlTitle();

	@DefaultStringValue("<<")
	@Key("ReplayControlSkipBack10Text")
	String ReplayControlSkipBack10Text();  
	  
	@DefaultStringValue("Skip -10 Action")
	@Key("ReplayControlSkipBack10Title")
	String ReplayControlSkipBack10Title();
	  
	@DefaultStringValue("<")
	@Key("ReplayControlSkipBack1Text")
	String ReplayControlSkipBack1Text();  
	  
	@DefaultStringValue("Skip -1 Action")
	@Key("ReplayControlSkipBack1Title")
	String ReplayControlSkipBack1Title();

	@DefaultStringValue("PLAY")
	@Key("ReplayControlSkipPlayText")
	String ReplayControlSkipPlayText();   
	  
	@DefaultStringValue("Play")
	@Key("ReplayControlSkipPlayTitle")
	String ReplayControlSkipPlayTitle();  

	@DefaultStringValue("PAUSE")
	@Key("ReplayControlSkipPauseText")
	String ReplayControlSkipPauseText();  
	  
	@DefaultStringValue("Pause")
	@Key("ReplayControlSkipPauseTitle")
	String ReplayControlSkipPauseTitle();

	@DefaultStringValue(">")
	@Key("ReplayControlSkipForw1Text")
	String ReplayControlSkipForw1Text();  
	  
	@DefaultStringValue("Skip +1 Action")
	@Key("ReplayControlSkipForw1Title")
	String ReplayControlSkipForw1Title();
	  
	@DefaultStringValue(">>")
	@Key("ReplayControlSkipForw10Text")
	String ReplayControlSkipForw10Text();  
	  
	@DefaultStringValue("Skip +10 Actions")
	@Key("ReplayControlSkipForw10Title")
	String ReplayControlSkipForw10Title();
	  
	@DefaultStringValue("Skip to:")
	@Key("ReplayControlUserSkipText")
	String ReplayControlUserSkipText();  
	 
	@DefaultStringValue("Skip to action of")
	@Key("ReplayControlUserSkipTitle")
	String ReplayControlUserSkipTitle();
	  
	@DefaultStringValue("Each User")
	@Key("ReplayControlUserSkipEachText")
	String ReplayControlUserSkipEachText();  
	  
	@DefaultStringValue("each user")
	@Key("ReplayControlUserSkipEachTitle")
	String ReplayControlUserSkipEachTitle();
	
	@DefaultStringValue("Close Replay")
	@Key("ReplayTabExitTitle")
	String ReplayTabExitTitle();
	
	@DefaultStringValue("Do you really want to leave the replay?")
	@Key("ReplayTabExitMessage")
	String ReplayTabExitMessage();	
	
	
//David Drexler Edit-END


	@DefaultStringValue("Map options:")
	@Key("CreateMapPanelMapOptions")
	String CreateMapPanelMapOptions();

	@DefaultStringValue("Map name")
	@Key("CreateMapPanelMapName")
	String CreateMapPanelMapName();

	@DefaultStringValue("Create and join map")
	@Key("CreateMapPanelCreateAndJoinButton")
	String CreateMapPanelCreateAndJoinButton();

	@DefaultStringValue("List of active users: Who is online?")
	@Key("ListOfUsersHeading")
	String ListOfUsersHeading();

	@DefaultStringValue("Supporting resources")
	@Key("SupportingResources")
	String SupportingResources();

	@DefaultStringValue("Connection status")
	@Key("Connection")
	String Connection();

	@DefaultStringValue("Ontology / Templatename:")
	@Key("CreateMapPanelOverviewHeading")
	String CreateMapPanelOverviewHeading();

	@DefaultStringValue("not logged in")
	@Key("NotLoggedIn")
	String NotLoggedIn();

	@DefaultStringValue("Center contribution")
	@Key("CenterContributionTitle")
	String CenterContributionTitle();

	@DefaultStringValue("Specify number of contribution")
	@Key("CenterContributionText")
	String CenterContributionText();

	@DefaultStringValue("Center contribution")
	@Key("SearchMenuItem")
	String SearchMenuItem();

	@DefaultStringValue("Group pointer")
	@Key("GroupPointer")
	String GroupPointer();

	@DefaultStringValue("Clear all feedback")
	@Key("DeleteAllFeedbackMenuItem")
	String DeleteAllFeedbackMenuItem();

	@DefaultStringValue("Close map")
	@Key("CloseMapHeader")
	String CloseMapHeader();

	@DefaultStringValue("Do you really want to leave the map?")
	@Key("CloseMapText")
	String CloseMapText();
	
	
//Oliver Scheuer Edit-BEGIN
	
	@DefaultStringValue("Contribution")
	@Key("ContributionMenuItem")
	String ContributionMenuItem();
	
	@DefaultStringValue("Relation")
	@Key("RelationMenuItem")
	String RelationMenuItem();

	@DefaultStringValue("Are you sure?")
	@Key("DeleteDialogueTitle")
	String DeleteDialogueTitle();
	
	@DefaultStringValue("Yes, delete it")
	@Key("DeleteDialogueYes")
	String DeleteDialogueYes();
	
	@DefaultStringValue("No, keep it")
	@Key("DeleteDialogueNo")
	String DeleteDialogueNo();
	
	@DefaultStringValue("Do not ask me again")
	@Key("DeleteDialogueAskAgainCheckbox")
	String DeleteDialogueAskAgainCheckbox();
	
//Oliver Scheuer Edit-END
}