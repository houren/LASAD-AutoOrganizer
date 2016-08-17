package lasad.gwt.client.ui.replay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionReceiver;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.argument.MVController;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.commands.Commands;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

/**
 * The client's visual controller for replaying the building of an argument.
 */

public class ReplayControl extends Window{
	
	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);
	
	private int current_replay_sec;
	private boolean play_replay;
	private boolean skip_replay;
	private String userSkip;
	private MenuItem userSkip_item;
	
	private final Timer timer;
	
	public ReplayControl(final MVController controller,
						final int totalSec,
						final List<Integer> indexSecondsReplay,
						final HashMap<Integer, ActionPackage> indexForwReplay,
						final HashMap<Integer, List<Action>> indexElementReplay,
						final HashMap<Integer, List<Integer>> lastActionReplay,
						final HashMap<Integer, List<Action>> indexChatReplay,
						TreeMap<String, List<Integer>> treeUserReplay) {
		
		//INITIALIZE
		//Replay modes
		this.play_replay = false;
		this.skip_replay = false;
		//Current position
		this.current_replay_sec = 0;
		//Currently selected user (skip to)
		this.userSkip = "";
		
		//Initialize strings for time display
		String totalTime = getStringTime(totalSec);
		final String timeStyle1 = "<span style=\"color:#333;\">";
		final String timeStyle2 = "/" + totalTime + "</span>";
		
		//Initialize slider width
		int sliderWidth = 230;
		//Initialize height for graphical action display
		int graphHeight = 8;
		//Initialize width for graphical action display
		int graphWidth = sliderWidth - 6 - 8;

		//Maximum time for replay
		//Integer.MAX_VALUE = 2147483647 seconds
		//Minutes: 2147483647 / 60 = 35791394,116 minutes
		//Hours: 35791394,116 / 60 = 596523,235 hours
		//Days: 596523,235 / 24 = 24855,134 days
		//Years: 24855,134 / 365 = 68,096 years + 17 switching days ;-)

		//Initialize control window
		this.setSize(363, 100);
		this.setMinWidth(325);
		this.setMinHeight(100);
		this.setPlain(true);  
		this.setModal(false);  
		this.setBlinkModal(false);
		this.setMinimizable(false);
		this.setHeading(myConstants.ReplayControlTitle() + " "+String.valueOf(controller.getMapInfo().getMapID())+": "+ controller.getMapInfo().getTitle());
		this.setClosable(false);
		
		//Initialize horizontal panel for slider and time display
	    HorizontalPanel hp1 = new HorizontalPanel();  
	    hp1.setSpacing(5);
	    hp1.setHeight(15);
	    //Initialize horizontal panel for graphical action display
	    HorizontalPanel hp2 = new HorizontalPanel();  
	    hp2.setSpacing(5);
	    hp2.setHeight(graphHeight + 14);
	    //Initialize horizontal panel for all buttons
	    HorizontalPanel hp3 = new HorizontalPanel();  
	    hp3.setSpacing(5);
	    hp3.setHeight(22);

		//Build strings for selectable userlist
		final List<String> userList = new ArrayList<String>(treeUserReplay.keySet());
	    
	    //Initialize second of last action
		List<Integer> eachUserActions = treeUserReplay.get("");
		int endActionIndex = eachUserActions.size() - 1;
		final int endActionSec = eachUserActions.get(endActionIndex);
		
		//Transfer slower TreeMap in faster HashMap
		final HashMap<String, List<Integer>> indexUserReplay = new HashMap<String, List<Integer>>(treeUserReplay);
		
		// Create ReplayTab		
//		final ReplayTab item = new ReplayTab(controller.getMapInfo());
	    
	    //New slider
	    final Slider slider = new Slider();
	    
	    //Initialize html of current time and total time
		final HTML currentPos = new HTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
		    
		//Build information for graphical action display
		float widthFactor = (float) graphWidth / totalSec;
		//Get all action-positions and transform to slider-width
		List<Integer> actionList = new ArrayList<Integer>(indexForwReplay.size());
		Iterator<Integer> it1 = indexForwReplay.keySet().iterator();
		while (it1.hasNext()) {
			int key = it1.next();
			//Transform to slider-width
			int graphAction = Math.round(key * widthFactor);
			//Save transformed position, if it's new
			if(!actionList.contains(graphAction)) {
				actionList.add(graphAction);
			}
		}		
		
		
		//TIMER PLAY-MODE
		/////////////////////////////////
	    //Initialize timer for play-mode
		timer = new Timer() {     
			@Override  
			public void run() {
				//Increase Current Time-Position
				current_replay_sec++;
				//Is there an ActionPackage?
				if(indexForwReplay.containsKey(current_replay_sec)) {
					//Do ActionPackage
					ActionPackage p = indexForwReplay.get(current_replay_sec);
					LASADActionReceiver.getInstance().doActionPackage(p);
					
					// to keep the control box in front of others
					hide();
					show();

				}
				//Update current time display
				currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
				//Update Slider-Position and supress ChangeEvent
				slider.setValue(current_replay_sec, true);
				
				//Stop Timer at the end of total time
				if(current_replay_sec == totalSec) {
					cancel();
				}
			}  
		};
	    
		
		//SLIDER
		/////////////////////////////////
	    //Initialize slider
	    slider.setWidth(sliderWidth);
	    slider.setIncrement(1);  
	    slider.setMaxValue(totalSec);
	    slider.setMessage("{0} sec"); 
	    slider.setUseTip(false);
	    //Add listener
	    slider.addListener(Events.Change, new Listener<SliderEvent>() {  
	    	public void handleEvent(SliderEvent se) {
	    		
	    		//Check if some skip-action is already running
				if(skip_replay == false) {
					skip_replay = true;
				
					//Check if play-mode is running
					if(play_replay == true) {
						//Stop play-mode to avoid problems
						timer.cancel();
					}
					//Get old and new value
					int oldSec = se.getOldValue();
		    		int newSec = se.getNewValue();
					
					//Get last second with an action of old and new value
					int lastActionSec = indexSecondsReplay.get(oldSec);
					int newActionSec = indexSecondsReplay.get(newSec);
					
					//Update all relevant map-elements
					skipActionReplay(lastActionSec, newActionSec, indexElementReplay,indexChatReplay,controller);				

					//Valid new index - get new action second
					current_replay_sec = newSec;
					//Update current time display
					currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
					//Update Slider-Position
					slider.setValue(current_replay_sec, false);
					//Start play-mode (again)
					if(play_replay == true) {
						timer.scheduleRepeating(1000);
					}
					skip_replay = false;
				}
	    	}  
	    });
	    //Add slider
	    hp1.add(slider);
	    hp1.add(currentPos);
	    this.add(hp1);
	    
	    
		//GRAPHICAL ACTION DISPLAY
		/////////////////////////////////
	    //Build html
	    String htmlString = "";
	    //Start spacing 6px
	    htmlString = htmlString + "<img src=\"resources/images/replay-pixel-t.png\" border=\"0\" width=\"6\" height=\"1\">";
	    //Build html with pixel
	    for(int key=0; key <= graphWidth; key++) {
	    	if(actionList.contains(key)) {
	    		//action exists: vertical pixel line
			    htmlString = htmlString + "<img src=\"resources/images/replay-pixel.png\" border=\"0\" width=\"1\" height=\""+ graphHeight +"\">";
			}
	    	else {
	    		//no action: transparent pixel
	    		htmlString = htmlString + "<img src=\"resources/images/replay-pixel-t.png\" border=\"0\" width=\"1\" height=\"1\">";
	    	}
	    }
	    //Add graphical action display
	    HTML graphPixel = new HTML(htmlString);
	    hp2.add(graphPixel);
	    this.add(hp2);

	    
	    //SKIP -10 BUTTON
		/////////////////////////////////
	  	//Initialize skip -10 button
	    //skip back 10 times - depending on the selected user
	    Button skipBack10 = new Button(
	    		myConstants.ReplayControlSkipBack10Text(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				
				//Skip2Position
				int skipPos = -10;
				
				//Check if some skip-action is already running
				if(skip_replay == false) {
					skip_replay = true;
				
					//Check if play-mode is running
					if(play_replay == true) {
						//Stop play-mode to avoid problems
						timer.cancel();
					}
					
					//Get last second with an action
					int lastActionSec = indexSecondsReplay.get(current_replay_sec);
					//Get action-list of selected user
					List<Integer> userActions = indexUserReplay.get(userSkip);
					//Get index of selected user
					int userIndex = userList.indexOf(userSkip);
					//Get last second with an action of selected user
					int lastUserSec = lastActionReplay.get(lastActionSec).get(userIndex);
					//Skip to new index
					int newIndex = userActions.indexOf(lastUserSec) + skipPos;
					//New index out of range?
					int newActionSec;
					if(newIndex >= userActions.size()) {
						//Not a valid index - end is reached
						newActionSec = endActionSec;
						current_replay_sec = totalSec;
						
					}
					else if(newIndex < 0) {
						//Not a valid index - begin is reached
						newActionSec = 0;
						current_replay_sec = 0;
					}
					else {
						//Valid new index - get new action second
						newActionSec = userActions.get(newIndex);
						current_replay_sec = newActionSec;
					}
					
					//Update all relevant map-elements
					skipActionReplay(lastActionSec, newActionSec, indexElementReplay,indexChatReplay,controller);				

					//Update current time display
					currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
					//Update Slider-Position
					slider.setValue(current_replay_sec, false);
					//Start play-mode (again)
					if(play_replay == true) {
						timer.scheduleRepeating(1000);
					}
					skip_replay = false;
				}
			}
		});
	    //Add skip -10 button 
	    skipBack10.setScale(ButtonScale.SMALL);
	    skipBack10.setTitle(myConstants.ReplayControlSkipBack10Title());
	    hp3.add(skipBack10); 
	
	    
	    //SKIP -1 BUTTON
		/////////////////////////////////
	  	//Initialize skip -1 button
	    //skip back 1 time - depending on the selected user
	    Button skipBack1 = new Button(
	    		myConstants.ReplayControlSkipBack1Text(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				
				//Skip2Position
				int skipPos = -1;
				
				//Check if some skip-action is already running
				if(skip_replay == false) {
					skip_replay = true;
				
					//Check if play-mode is running
					if(play_replay == true) {
						//Stop play-mode to avoid problems
						timer.cancel();
					}
					
					//Get last second with an action
					int lastActionSec = indexSecondsReplay.get(current_replay_sec);
					//Get action-list of selected user
					List<Integer> userActions = indexUserReplay.get(userSkip);
					//Get index of selected user
					int userIndex = userList.indexOf(userSkip);
					//Get last second with an action of selected user
					int lastUserSec = lastActionReplay.get(lastActionSec).get(userIndex);
					//Skip to new index
					int newIndex = userActions.indexOf(lastUserSec) + skipPos;
					//New index out of range?
					int newActionSec;
					if(newIndex >= userActions.size()) {
						//Not a valid index - end is reached
						newActionSec = endActionSec;
						current_replay_sec = totalSec;
						
					}
					else if(newIndex < 0) {
						//Not a valid index - begin is reached
						newActionSec = 0;
						current_replay_sec = 0;
					}
					else {
						//Valid new index - get new action second
						newActionSec = userActions.get(newIndex);
						current_replay_sec = newActionSec;
					}
					
					//Update all relevant map-elements
					skipActionReplay(lastActionSec, newActionSec, indexElementReplay,indexChatReplay,controller);				

					//Update current time display
					currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
					//Update Slider-Position
					slider.setValue(current_replay_sec, false);
					//Start play-mode (again)
					if(play_replay == true) {
						timer.scheduleRepeating(1000);
					}
					skip_replay = false;
				}
			}
		});
	    //Add skip -1 button
	    skipBack1.setScale(ButtonScale.SMALL);
	    skipBack1.setTitle(myConstants.ReplayControlSkipBack1Title());
	    hp3.add(skipBack1); 

	    
		//PLAY BUTTON
		/////////////////////////////////
	  	//Initialize play button
	    Button play = new Button(
	    		myConstants.ReplayControlSkipPlayText(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				//Avoid play when current time is at the end
				if(current_replay_sec != totalSec) {
					//Check if some skip-action is still running
					if(skip_replay == true) {
						//Set play-flag true, so the skip action,
						//will start the play-action at the end.
						play_replay = true;
					}
					else {
						//Check if play-mode is already running
						//To avoid double-clicks or more ;-)
						if(play_replay == false) {
							//Start timer every second
							timer.scheduleRepeating(1000);
							//Set play-flag
							play_replay = true;
						}
					}
				}
			}
	    });
	    //Add play button
	    play.setScale(ButtonScale.SMALL);
	    play.setTitle(myConstants.ReplayControlSkipPlayTitle());
	    hp3.add(play);

	    
		//PAUSE BUTTON
		/////////////////////////////////
	  	//Initialize pause button
	    Button pause = new Button(
	    		myConstants.ReplayControlSkipPauseText(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				//Cancel Timer
				timer.cancel();
				//Set play-flag
				play_replay = false;
			}
	    });
	    //Add pause button
	    pause.setScale(ButtonScale.SMALL);
	    pause.setTitle(myConstants.ReplayControlSkipPauseTitle());
	    hp3.add(pause);
	    
	    
		//SKIP +1 BUTTON
		/////////////////////////////////
	  	//Initialize skip +1 button
	    //skip forward 1 time - depending on the selected user
	    Button skipForw1 = new Button(
	    		myConstants.ReplayControlSkipForw1Text(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				
				//Skip2Position
				int skipPos = 1;
				
				//Check if some skip-action is already running
				if(skip_replay == false) {
					skip_replay = true;
				
					//Check if play-mode is running
					if(play_replay == true) {
						//Stop play-mode to avoid problems
						timer.cancel();
					}
					
					//Get last second with an action
					int lastActionSec = indexSecondsReplay.get(current_replay_sec);
					//Get action-list of selected user
					List<Integer> userActions = indexUserReplay.get(userSkip);
					//Get index of selected user
					int userIndex = userList.indexOf(userSkip);
					//Get last second with an action of selected user
					int lastUserSec = lastActionReplay.get(lastActionSec).get(userIndex);
					//Skip to new index
					int newIndex = userActions.indexOf(lastUserSec) + skipPos;
					//New index out of range?
					int newActionSec;
					if(newIndex >= userActions.size()) {
						//Not a valid index - end is reached
						newActionSec = endActionSec;
						current_replay_sec = totalSec;
						
					}
					else if(newIndex < 0) {
						//Not a valid index - begin is reached
						newActionSec = 0;
						current_replay_sec = 0;
					}
					else {
						//Valid new index - get new action second
						newActionSec = userActions.get(newIndex);
						current_replay_sec = newActionSec;
					}
					
					//DEBUG
					/*
					Logger.log("current_replay_sec: " + current_replay_sec);
					Logger.log("endActionSec: " + endActionSec);
					Logger.log("----------------------------------------");
					*/
					
					//Update all relevant map-elements
					skipActionReplay(lastActionSec, newActionSec, indexElementReplay,indexChatReplay,controller);

					//Update current time display
					currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
					Logger.log("currentPos: " + currentPos, Logger.DEBUG_DETAILS);
					
					//Update Slider-Position
					slider.setValue(current_replay_sec, false);
					//Start play-mode (again)
					if(play_replay == true) {
						timer.scheduleRepeating(1000);
					}
					skip_replay = false;
				}
			}
		});
	    //Add skip +1 button
	    skipForw1.setScale(ButtonScale.SMALL);
	    skipForw1.setTitle(myConstants.ReplayControlSkipForw1Title());
	    hp3.add(skipForw1); 
	    
	    
		//SKIP +10 BUTTON
		/////////////////////////////////
	  	//Initialize skip +10 button
	    //skip forward 10 times - depending on the selected user
	    Button skipForw10 = new Button(
	    		myConstants.ReplayControlSkipForw10Text(),
	    		new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				
				//Skip2Position
				int skipPos = 10;
				
				//Check if some skip-action is already running
				if(skip_replay == false) {
					skip_replay = true;
				
					//Check if play-mode is running
					if(play_replay == true) {
						//Stop play-mode to avoid problems
						timer.cancel();
					}
					
					//Get last second with an action
					int lastActionSec = indexSecondsReplay.get(current_replay_sec);
					//Get action-list of selected user
					List<Integer> userActions = indexUserReplay.get(userSkip);
					//Get index of selected user
					int userIndex = userList.indexOf(userSkip);
					//Get last second with an action of selected user
					int lastUserSec = lastActionReplay.get(lastActionSec).get(userIndex);
					//Skip to new index
					int newIndex = userActions.indexOf(lastUserSec) + skipPos;
					//New index out of range?
					int newActionSec;
					if(newIndex >= userActions.size()) {
						//Not a valid index - end is reached
						newActionSec = endActionSec;
						current_replay_sec = totalSec;
						
					}
					else if(newIndex < 0) {
						//Not a valid index - begin is reached
						newActionSec = 0;
						current_replay_sec = 0;
					}
					else {
						//Valid new index - get new action second
						newActionSec = userActions.get(newIndex);
						current_replay_sec = newActionSec;
					}
					
					//Update all relevant map-elements
					skipActionReplay(lastActionSec, newActionSec, indexElementReplay,indexChatReplay,controller);				

					//Update current time display
					currentPos.setHTML(timeStyle1 + getStringTime(current_replay_sec) + timeStyle2);
					//Update Slider-Position
					slider.setValue(current_replay_sec, false);
					//Start play-mode (again)
					if(play_replay == true) {
						timer.scheduleRepeating(1000);
					}
					skip_replay = false;
				}
			}
		});
	    //Add skip +10 button
	    skipForw10.setScale(ButtonScale.SMALL);
	    skipForw10.setTitle(myConstants.ReplayControlSkipForw10Title());
	    hp3.add(skipForw10); 

	    
		//USER MENU
		/////////////////////////////////
	  	//Initialize user menu
	    final Button userSkipButton = new Button(myConstants.ReplayControlUserSkipText() + " " + myConstants.ReplayControlUserSkipEachText());
	    userSkipButton.setScale(ButtonScale.SMALL);
	    userSkipButton.setTitle(myConstants.ReplayControlUserSkipTitle() + " " + myConstants.ReplayControlUserSkipEachTitle());
	    
	    final Menu menu = new Menu();
	    //MenuItem for each user
    	for(final String user : userList) {
    		//Initialize itemText and itemTitle
    		final String itemText;
    		final String itemTitle;
    		if(user == userList.get(0)) {
    			itemText = myConstants.ReplayControlUserSkipText() + " " + myConstants.ReplayControlUserSkipEachText();
    			itemTitle = myConstants.ReplayControlUserSkipTitle() + " " + myConstants.ReplayControlUserSkipEachTitle();
    		}
    		else {
    			itemText = myConstants.ReplayControlUserSkipText() + " " + user;
    			itemTitle = myConstants.ReplayControlUserSkipTitle() + " " + user;
    		}
    		//Initialize menuItem
	    	final MenuItem userItem = new MenuItem(itemText);
	    	//Add selection listener
	    	userItem.addListener(Events.Select, new SelectionListener<ComponentEvent>(){
				public void componentSelected(ComponentEvent ce) {
					//Set text and title of the button
					userSkipButton.setText(itemText);
					userSkipButton.setTitle(itemTitle);
					//Set new chosen item invisible
					userItem.setVisible(false);
					//Set former chosen item visible
					userSkip_item.setVisible(true);
					//Set new selected user
					userSkip_item = userItem;
					userSkip = user;
				}
			});
	    	//Initialize default - at the beginning
	    	if (user == userList.get(0)) {
	    		userSkip = user;
	    		userSkip_item = userItem;
	    		userItem.setVisible(false);
	    	}
	    	menu.add(userItem);
	    	
	    }
	  	userSkipButton.setMenu(menu);
    	//Add user menu
	    hp3.add(userSkipButton); 
	    this.add(hp3);
	    this.show();
	}
	
	private String getStringTime(int current_seconds) {

		//Calculate
		int hours = current_seconds / (60*60);
		int minutes = current_seconds / 60 - (hours*60);
		int seconds = current_seconds %60;
		String time;
		
		//Build string
		if(seconds<10) {
			time = minutes + ":0" + seconds;	
		}
		else {
			time = minutes + ":" + seconds;
		}
		
		if(hours!=0 && minutes<10) {
			time = hours + ":0" + time;
		}
		else if(hours!=0) {
			time = hours + ":" + time;
		}
		
		return time;
	}
	
	
	private void skipActionReplay(int oldSec, int newSec, HashMap<Integer, List<Action>> indexElementReplay,HashMap<Integer,List<Action>> indexChatReplay, MVController controller) {

		//Just in case
		if(oldSec == newSec) {
			//Nothing to do
			return;
		}
		
		//Get all lists
		List<Action> oldList = indexElementReplay.get(oldSec);
		List<Action> newList = indexElementReplay.get(newSec);
		List<Action> deleteList = indexElementReplay.get(0);
		List<Action> createList = indexElementReplay.get(-1);
		
		//Create action packages
		ActionPackage deleteP = new ActionPackage();
		ActionPackage createP = new ActionPackage();
		ActionPackage updateP = new ActionPackage();
		ActionPackage chatP = new ActionPackage();
		//For each element
		for(int i = 0; i < oldList.size(); i++) {
			//Initialize actions
			Action oldAction = oldList.get(i);
			Action newAction = newList.get(i);
			Action deleteAction = deleteList.get(i);
			Action createAction = createList.get(i);
			//Get action command
			Commands oldState = oldAction.getCmd();
			Commands newState = newAction.getCmd();
			
			//ALL POSSIBLE CASES
			//oldAction delete + newAction delete: ignore
			//oldAction delete + newAction create: create action
			//oldAction delete + newAction update: create + update action
			//oldAction create/update + newAction delete: delete action
			//skip back -> oldAction update + newAction create: delete + create action
			//skip back -> oldAction update + newAction update: delete + create + update action
			//skip forward -> oldAction create/update + newAction update: update action
			if(oldState.equals(Commands.DeleteElement)) {
				if(!newState.equals(Commands.DeleteElement)) {
					createP.addAction(createAction);
					if(newState.equals(Commands.UpdateElement)) {
						updateP.addAction(newAction);
					}	
				}
			}
			else if(newState.equals(Commands.DeleteElement)) {
				deleteP.addAction(deleteAction);		
			}
			else {
				if(oldSec > newSec) {
					deleteP.addAction(deleteAction);
					createP.addAction(createAction);
				}
				if(newState.equals(Commands.UpdateElement)) {
					updateP.addAction(newAction);
				}		
			}
		}
		//Do ActionPackages
		if(deleteP.getActions() != null) {
			LASADActionReceiver.getInstance().doActionPackage(deleteP);
		}
		if(createP.getActions() != null) {
			LASADActionReceiver.getInstance().doActionPackage(createP);
		}
		if(updateP.getActions() != null) {
			LASADActionReceiver.getInstance().doActionPackage(updateP);
		}
		
		
		if(LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getChatPanel() != null)
		{
			LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getChatPanel().clearBoard();
		}
		
		if(LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getExtendedChatPanel() != null)
		{
			LASAD_Client.getMapTab(controller.getMapID()).getMyMapSpace().getExtendedChatPanel().clearBoard();
		}
		
		Integer chatKey=0;
		// Prepare lists of the actions (second) of each user
		Iterator<Integer> it1 = indexChatReplay.keySet().iterator();
		while (it1.hasNext()) {
			Integer temoKey = it1.next();
			if(temoKey <= newSec)
			{
				chatKey = temoKey;
			}
			else
			{
				break;
			}
		}
		
		if(chatKey > 0)
		{
			List<Action> chatActions = indexChatReplay.get(chatKey);
			for(int i = 0;i <chatActions.size();i++)
			{
				chatP.addAction(chatActions.get(i));
			}
			LASADActionReceiver.getInstance().doActionPackage(chatP);
		}
		// to keep the control box in front of others
		this.hide();
		this.show();
	}

	public Timer getTimer() {
		return timer;
	}
}
