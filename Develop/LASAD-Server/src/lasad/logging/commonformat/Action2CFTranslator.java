package lasad.logging.commonformat;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lasad.logging.commonformat.util.ActionTranslatorHelper;
import lasad.logging.commonformat.util.CFConstants;
import lasad.logging.commonformat.util.CFGUIAction;
import lasad.logging.commonformat.util.CFTranslationResult;
import lasad.logging.commonformat.util.CFVocabulary;
import lasad.logging.commonformat.util.ParamMap;
import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Actiontype;
import lasad.logging.commonformat.util.jaxb.Object;
import lasad.logging.commonformat.util.jaxb.ObjectFactory;
import lasad.logging.commonformat.util.jaxb.User;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.categories.Categories;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;


public class Action2CFTranslator {

	//private static Log logger = LogFactory.getLog(Action2CFTranslator.class);
//	private Hashtable<Integer, Integer> sessionIDTracker;
	
	private ActionTranslatorHelper transHelper;
//	private CFWriter cfWriter;
	
	public Action2CFTranslator(){
//		sessionIDTracker = new Hashtable<Integer, Integer>();
		transHelper = new ActionTranslatorHelper();
//		cfWriter = new CFFileWriter();
	}
	
	//We assume that all actions in the ActionPackage belong to the same sessionActionPackage
	public CFTranslationResult translateAP2CF(ActionPackage ap){
		System.out.println("Translating:" + ap);
		CFTranslationResult cfTranslationResult = new CFTranslationResult();
		List<Action> generatedActionList = new Vector<Action>();
		String sessionID = ""; //mapID
		boolean sessionFlag = false;

		List<lasad.shared.communication.objects.Action> actionList = ap.getActions();

		for (lasad.shared.communication.objects.Action action : actionList) {
			String actionCategory = action.getCategory().getOldCategories();
			String actionCommand = action.getCmd().getOldCommands();
			ParamMap paramMap = extractParams(action.getParameters());
			
			if (!sessionFlag){
				sessionID = paramMap.getFirstValue(ParameterTypes.MapId.getOldParameter());
				if (sessionID == null){
					sessionID = "0";
				}
				cfTranslationResult.setSessionID(sessionID);
				sessionFlag = true;
			}
			// translate user logins into LASAD
			else if (isUserLogin(actionCategory, actionCommand)) {
				Action newAct =  transHelper.translateUserLoginAction(actionCategory, actionCommand, paramMap);
				generatedActionList.add(newAct);
			}
			// translate user logouts from LASAD
			else if (isUserLogout(actionCategory, actionCommand)) {
				Action newAct =  transHelper.translateUserLogoutAction(actionCategory, actionCommand, paramMap);
				generatedActionList.add(newAct);
			}
			// translate user joins session
			else if (isUserJoin(actionCategory, actionCommand)) {
				Action newAct =  transHelper.translateUserJoinAction(actionCategory, actionCommand, paramMap);
				generatedActionList.add(newAct);
			}
			// translate user leaves session
			else if (isUserLeave(actionCategory, actionCommand)) {
				Action newAct =  transHelper.translateUserLeaveAction(actionCategory, actionCommand, paramMap);
				generatedActionList.add(newAct);
			}
			// translate session actions (create, modify, delete object)
			else if (isCreateUpdateDelete(actionCategory, actionCommand)) {
				String guiActionID = transHelper.translateCreateUpdateDeleteAction(actionCategory, actionCommand, paramMap);
				if (guiActionID == null) {
					System.out.println("IGNORE Action: Property '"
							+ ParameterTypes.UserActionId.getOldParameter()
							+ "' is not present");
				} else {
					CFGUIAction guiAction = transHelper.guiActionTracker.get(guiActionID);
					if (guiAction != null && guiAction.isComplete()
							&& !guiAction.isReplayedAction()) {
						transHelper.guiActionTracker.remove(guiActionID);
						List<Action> actList = createAction(guiAction);
						generatedActionList.addAll(actList);
					}
				}
			}
			// translate chat message
			else if (isChatMsg(actionCategory, actionCommand)) {
				Action newAct =  transHelper.translateChatMsgAction(actionCategory, actionCommand, paramMap);
				generatedActionList.add(newAct);
			}
			else {
				System.out.println("IGNORE Action: ActionCategory '" + actionCategory + 
						"' ActionCommand '" + actionCommand + "'");
			}
		}

//		System.out.println("Replayed Actions? " + transHelper.generateReplayedEvent
//				+ ", number of replayed Actions:"
//				+ transHelper.replayedGuiActionTracker.size());
		if (transHelper.generateReplayedEvent) {
			List<Action> replayedEvents = generateReplayedActions();
			generatedActionList.addAll(replayedEvents);
			transHelper.generateReplayedEvent = false;
		}
		
		if (generatedActionList.size() > 0){
//			cfWriter.writeActionList(sessionID, generatedActionList);
			cfTranslationResult.addActionList(generatedActionList);
		}
		return cfTranslationResult;
	}
	
	private List<Action> generateReplayedActions() {
//		System.out.println("Start generating Events for replayed Actions ...");
		transHelper.resetReplayedEventCounter();
		List<Action> replayedEvents = createReplayedEvents();
//		System.out.println("Number of generated replayed Events:"
//				+ replayedEvents.size());
		return replayedEvents;
	}
	
	private boolean isChatMsg(String actionCategory, String actionCommand) {
		boolean isChatMsg = false;
		if(actionCategory.equalsIgnoreCase(Categories.Communication.getOldCategories())
				&& actionCommand.equalsIgnoreCase(Commands.ChatMsg.getOldCommands())){
			isChatMsg = true;
		}
		return isChatMsg;
	}
	
	//Join a session
	private boolean isUserJoin(String actionCategory, String actionCommand) {
		boolean isUserJoin = actionCommand
				.equalsIgnoreCase(Commands.UserJoin.getOldCommands())
				&& actionCategory.equalsIgnoreCase(Categories.UserEvent.getOldCategories());
		return isUserJoin;
	}
	//Leave a session
	private boolean isUserLeave(String actionCategory, String actionCommand) {
		boolean isUserLeave = actionCommand
				.equalsIgnoreCase(Commands.UserLeave.getOldCommands())
				&& actionCategory.equalsIgnoreCase(Categories.UserEvent.getOldCategories());
		return isUserLeave;
	}
	
	//Login into LASAD
	private boolean isUserLogin(String actionCategory, String actionCommand) {
		boolean isUserLogin = actionCommand
				.equalsIgnoreCase(Commands.Login.getOldCommands())
				&& actionCategory.equalsIgnoreCase(Categories.Auth.getOldCategories());
		return isUserLogin;
	}
	//Logout from LASAD
	private boolean isUserLogout(String actionCategory, String actionCommand) {
		boolean isUserLogout = actionCommand
				.equalsIgnoreCase(Commands.Logout.getOldCommands());
				//&& actionCategory.equalsIgnoreCase(LASADVocabulary.ACTION_CAT_AUTH);
		return isUserLogout;
	}

	private boolean isCreateUpdateDelete(String actionCategory, String actionCommand) {
		boolean isCreateUpdateDelete = actionCommand
				.equalsIgnoreCase(Commands.CreateElement.getOldCommands())
				|| actionCommand
						.equalsIgnoreCase(Commands.UpdateElement.getOldCommands())
				|| actionCommand
						.equalsIgnoreCase(Commands.DeleteElement.getOldCommands());
		return isCreateUpdateDelete;
	}
	
	private ParamMap extractParams(List<Parameter> paramList) {
		ParamMap paramMap = new ParamMap();

		for (Parameter param : paramList) {
			String paramName = param.getType().getOldParameter();//.toUpperCase()
//			String paramName = param.getType().toUpperCase();
			String paramValue = param.getValue();

			paramMap.addParam(paramName, paramValue);
		}

		return paramMap;
	}
	
	
	private List<Action> createAction(CFGUIAction guiAction) {
		ObjectFactory objFactory = new ObjectFactory();
		List<Action> actionList = new ArrayList<Action>();

		Action createAction = null;
		Action updateAction = null;
		Action deleteAction = null;
		
		if (guiAction.getCreateEueObjectList().size() > 0){
			createAction = objFactory.createAction();
			createAction.setTime(Long.toString(guiAction.getTime()));
			
			Actiontype actionType = objFactory.createActiontype();
			actionType.setClassification(CFVocabulary.ACTIONTYPE_CCREATE);
			actionType.setType(guiAction.getAtType());
			actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
			createAction.setActiontype(actionType);
			
			User user = transHelper.createUser(guiAction.getUserID());
//			user.setId(guiAction.getUserID());
//			user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
			createAction.getUser().add(user);
			
			Object container = objFactory.createObject();
			container.setId(CFConstants.CONTAINER_ID);
			container.setType(CFConstants.CONTAINER_TYPE);
			
			List<Object> objList = guiAction.getCreateEueObjectList();
			for (Object obj : objList) {
				container.getObject().add(obj);
			}
			
			createAction.setObject(container);
			
			actionList.add(createAction);
		}
		if (guiAction.getUpdateEueObjectList().size() > 0){
			updateAction = objFactory.createAction();
			updateAction.setTime(Long.toString(guiAction.getTime()));
			
			Actiontype actionType = objFactory.createActiontype();
			actionType.setClassification(CFVocabulary.ACTIONTYPE_CMODIFY);
			actionType.setType(guiAction.getAtType());
			actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
			updateAction.setActiontype(actionType);
			
			User user = transHelper.createUser(guiAction.getUserID());
//			user.setId(guiAction.getUserID());
//			user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
			updateAction.getUser().add(user);
			
			Object container = objFactory.createObject();
			container.setId(CFConstants.CONTAINER_ID);
			container.setType(CFConstants.CONTAINER_TYPE);
			
			List<Object> objList = guiAction.getUpdateEueObjectList();
			for (Object obj : objList) {
				container.getObject().add(obj);
			}
			
			updateAction.setObject(container);
			
			actionList.add(updateAction);
		}
		if (guiAction.getDeleteEueObjectList().size() > 0){
			deleteAction = objFactory.createAction();
			deleteAction.setTime(Long.toString(guiAction.getTime()));
			
			Actiontype actionType = objFactory.createActiontype();
			actionType.setClassification(CFVocabulary.ACTIONTYPE_CDELETE);
			actionType.setType(guiAction.getAtType());
			actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
			deleteAction.setActiontype(actionType);
			
			User user = transHelper.createUser(guiAction.getUserID());
//			user.setId(guiAction.getUserID());
//			user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
			deleteAction.getUser().add(user);
			
			Object container = objFactory.createObject();
			container.setId(CFConstants.CONTAINER_ID);
			container.setType(CFConstants.CONTAINER_TYPE);
			
			List<Object> objList = guiAction.getDeleteEueObjectList();
			for (Object obj : objList) {
				container.getObject().add(obj);
			}
			
			deleteAction.setObject(container);
			
			actionList.add(deleteAction);
		}
		return actionList;
	}

	private List<Action> createReplayedEvents() {

		List<Action> actionList = new Vector<Action>();
		Vector<String> completedGuiActionIDs = new Vector<String>();

		for (String replayedGuiActionID : transHelper.replayedGuiActionTracker) {
			if (transHelper.guiActionTracker.containsKey(replayedGuiActionID)) {
				CFGUIAction guiAction = transHelper.guiActionTracker.get(replayedGuiActionID);
				
				Action createAction = null;
				Action firstModAction = null;
				Action modAction = null;

				long creationDate = guiAction.getCreationDate();
				long firstModDate = guiAction.getFirstModDate();
				long modificationDate = guiAction.getModificationDate();

				//String srcCompID = LASADDataService.class.toString();
				
				if (creationDate > 0L) {
					createAction = new Action();
					createAction.setTime(Long.toString(creationDate));
					
					Actiontype actionType = new Actiontype();
					actionType.setClassification(CFVocabulary.ACTIONTYPE_CCREATE);
					actionType.setType(guiAction.getAtType());
					actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
					createAction.setActiontype(actionType);
					
					User user = new User();
					user.setId(guiAction.getUserID());
					user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
					createAction.getUser().add(user);
					
					Object container = new Object();
					container.setId(CFConstants.CONTAINER_ID);
					container.setType(CFConstants.CONTAINER_TYPE);
					
					List<Object> objList = guiAction.getCreateEueObjectList();
					for (Object obj : objList) {
						container.getObject().add(obj);
					}
					
					createAction.setObject(container);
					
					actionList.add(createAction);
				}
				
				if (firstModDate > 0L) {
					firstModAction = new Action();
					firstModAction.setTime(Long.toString(firstModDate));
					
					Actiontype actionType = new Actiontype();
					actionType.setClassification(CFVocabulary.ACTIONTYPE_CMODIFY);
					actionType.setType(guiAction.getAtType());
					actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
					firstModAction.setActiontype(actionType);
					
					User user = new User();
					user.setId(guiAction.getUserID());
					user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
					firstModAction.getUser().add(user);
					
					Object container = new Object();
					container.setId(CFConstants.CONTAINER_ID);
					container.setType(CFConstants.CONTAINER_TYPE);
					
					List<Object> objList = guiAction.getCreateEueObjectList();
					for (Object obj : objList) {
						container.getObject().add(obj);
					}
					
					firstModAction.setObject(container);
					
					actionList.add(firstModAction);
				}

				if (modificationDate > 0L) {
					modAction = new Action();
					modAction.setTime(Long.toString(modificationDate));
					
					Actiontype actionType = new Actiontype();
					actionType.setClassification(CFVocabulary.ACTIONTYPE_CMODIFY);
					actionType.setType(guiAction.getAtType());
					actionType.setLogged(ActionTranslatorHelper.LOGGED_VALUE);
					modAction.setActiontype(actionType);
					
					User user = new User();
					user.setId(guiAction.getUserID());
					user.setRole(ActionTranslatorHelper.USER_ROLE_VALUE);
					modAction.getUser().add(user);
					
					Object container = new Object();
					container.setId(CFConstants.CONTAINER_ID);
					container.setType(CFConstants.CONTAINER_TYPE);
					
					List<Object> objList = guiAction.getCreateEueObjectList();
					for (Object obj : objList) {
						container.getObject().add(obj);
					}
					
					modAction.setObject(container);
					
					actionList.add(modAction);
				}
				
			} else {
				System.err.println(replayedGuiActionID 
						+ " does not exist in tracker");
			}
			completedGuiActionIDs.add(replayedGuiActionID);
		}

		// remove entries from tracker
		for (String key : completedGuiActionIDs) {
			transHelper.guiActionTracker.remove(key);
		}
		transHelper.replayedGuiActionTracker.clear();
		return actionList;

	}
	
//	private void registerSession(String sessionID){
//		if(sessionID == null){
//			return;
//		}
//		Integer sID = new Integer(sessionID);
//		if(!sessionIDTracker.containsKey(sID)){
//			sessionIDTracker.put(sID, new Integer(0));
//		}
//	}
//	
//	private void unregisterSession(String sessionID){
//		Integer sID = new Integer(sessionID);
//		sessionIDTracker.remove(sID);
//	}
//	
//	private List<String> getAllSessions(){
//		List<String> sessionList = new Vector<String>();
//		Set<Integer> sessionSet = sessionIDTracker.keySet();
//		
//		for(Integer session1 : sessionSet){
//			sessionList.add(session1.toString());
//		}
//		return sessionList;
//	}
//	
//	private void closeWriter(String sessionID){
//		unregisterSession(sessionID);
////		cfWriter.closeWriter(sessionID);
//	}
//	
//	public void closeWriters(){
//		List<String> sessionList = getAllSessions();
//		for(String session : sessionList){
//			closeWriter(session);
//		}
//		
//	}
}