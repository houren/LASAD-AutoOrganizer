package lasad.logging.commonformat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Actiontype;
import lasad.logging.commonformat.util.jaxb.Object;
import lasad.logging.commonformat.util.jaxb.ObjectFactory;
import lasad.logging.commonformat.util.jaxb.Preamble;
import lasad.logging.commonformat.util.jaxb.Properties;
import lasad.logging.commonformat.util.jaxb.Property;
import lasad.logging.commonformat.util.jaxb.User;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

public class ActionTranslatorHelper {
//	private static Log logger = LogFactory.getLog(ActionTranslatorHelper.class);
	
	public static final String LOGGED_VALUE = "true";
	public static final String USER_ROLE_VALUE = CFVocabulary.USER_ROLE_ORIGINATOR;
	public static final String SEPARATOR = "_";
	
	// debugging setting
	//private boolean REQUEST_RAW_RESULTS = false;

	private HashMap<String, CFObjectBasicData> objectTracker = new HashMap<String, CFObjectBasicData>();

	public HashMap<String, CFGUIAction> guiActionTracker = new HashMap<String, CFGUIAction>();
	public HashSet<String> replayedGuiActionTracker = new HashSet<String>();

	// flag indicating that replayed events should be generated
	public boolean generateReplayedEvent = false;

	// used to generate unique event ids in case of replayed actions
	private int replayedEventCounter = 0; // 
	
	// will be ignored for the time being
	private Map<String, Set<String>> mapID2feedbackElementIDs = new HashMap<String, Set<String>>();

	private PreambleTranslator preambleTrans;
	
	public ActionTranslatorHelper(){
		//this.objFactory = objFactory;
		preambleTrans = new PreambleTranslator();
	}
	
	public void addObjectDef(String elementId, String elementType, 
			Map<String, String> propertiesMap){
		lasad.logging.commonformat.util.jaxb.ObjectDef newObjectDef = new lasad.logging.commonformat.util.jaxb.ObjectDef();
		newObjectDef.setId(elementId);
		newObjectDef.setType(elementType);
		
		Iterator<String> iter = propertiesMap.keySet().iterator();
		
		while(iter.hasNext()){
			String key = iter.next();
			
			Property property = new Property();
			property.setName(key);
			property.setValue(propertiesMap.get(key));
			newObjectDef.getProperties().getProperty().add(property);
		}
	}
	
	public static final String STR_ELEMENT = "element";
	
	public Preamble translateOntology(String actionCategory, String actionCommand, ParamMap paramMap) {
		String ontology = paramMap.getFirstValue(Commands.Ontology.getOldCommands());
		return preambleTrans.translateOntology(ontology);
	}
	
	public String translateCreateUpdateDeleteAction(String actionCategory, String actionCommand, ParamMap paramMap) {

//		String dataType = paramMap.getFirstValue(LASADVocabulary.ACTION_PROP_TYPE);
//		if(dataType != null && dataType.equalsIgnoreCase(LASADVocabulary.ACTION_TYPE_TRANS_LINK)){
//			dataType = "";
//		}
		
		String displayedID = paramMap
				.getFirstValue(ParameterTypes.RootElementId.getOldParameter());

		String sessionID = paramMap
				.getFirstValue(ParameterTypes.MapId.getOldParameter());

		String objectID = paramMap
				.getFirstValue(ParameterTypes.Id.getOldParameter());

		if (isFeedbackElement(sessionID, objectID)) {
			// ignore
			return null;
		}

		if (displayedID != null && sessionID != null) {
			// top-level element: we extract the displayed id (== ROOTELEMENTID)
			DisplayedObjectIDTracker.addMapping(sessionID, objectID, displayedID);
		}

		String guiActionID = getGuiActionID(paramMap);

		if (guiActionID == null) {
			System.out.println(ParameterTypes.UserActionId.getOldParameter()
					+ " is not present in Action, action discarded");
		} else {
			if (guiActionTracker.containsKey(guiActionID)) {
				// we receive a new action of an existing UserActionEvent
				processExistingGuiActionID(guiActionID, actionCommand, paramMap);

			} else {
				// we receive a new action, and the GuiActionID is new too,
				// start tracking it.
				processNewGuiAction(guiActionID, actionCommand, paramMap);
			}
		}

		return guiActionID;
	}
	
	private void processNewGuiAction(String guiActionID, String actionCommand,
			ParamMap paramMap) {

		CFGUIAction guiAction = new CFGUIAction(guiActionID);

		guiAction.setAtClassification(actionCommand);

		String sessionID = paramMap.getFirstValue(ParameterTypes.MapId.getOldParameter());
		String userID = paramMap
				.removeAndGetFirst(ParameterTypes.UserName.getOldParameter());
		String numActions = paramMap
				.removeAndGetFirst(ParameterTypes.NumActions.getOldParameter());
		String time = paramMap
				.removeAndGetFirst(ParameterTypes.Time.getOldParameter()); // TIME

		guiAction.setSessionID(sessionID);

		if (userID == null) {
			// update or delete action don't have an userID is not provided...
			// ... Look up userID in tracker system.
			String objectID = paramMap
					.getFirstValue(ParameterTypes.Id.getOldParameter());
			if (objectID != null) {
				CFObjectBasicData objectBasicData = getEUEObjectFromTracker(objectID);
				if (objectBasicData == null) {
					System.out.println(ParameterTypes.UserName.getOldParameter()
							+ " was not provided," + " node: " + objectID
							+ " is not in tracking system, "
							+ "we cannot determine the owner");
					return;
				} else {
					userID = objectBasicData.getUserID();
				}
			} else {
				System.out.println("Param 'objectID' not available in Action.");
			}
		}
		
		String objectID = paramMap.getFirstValue(ParameterTypes.Id.getOldParameter()); // ID
		String dataType = paramMap.getFirstValue(ParameterTypes.Type.getOldParameter()); // TYPE
		// "ELEMENT-ID" and "TYPE" are not provided in COMMANDs of type
		// UPDATE-ELEMENT and DELETE-ELEMENT
		if (dataType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (objPreviousState == null) {
					System.out.println("#node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					//TODO check this
					return ;
				}
				dataType = objPreviousState.getDataType();
			} else {
				System.out.println("Param userID not available");
			}
		}

		guiAction.setUserID(userID);
		guiAction.setNumActions(Integer.parseInt(numActions));
		guiAction.setGuiActionID(guiActionID);
		if(time != null){
			guiAction.setTime(Long.parseLong(time));
		}
		else{
			guiAction.setTime(getCurrentTimeAsLong());
		}
		guiAction.setAtType(dataType);

		if (paramMap.getFirstValue(ParameterTypes.Replay.getOldParameter()) != null) {
			generateReplayedEvent = true;

			String creationDate = paramMap
					.removeAndGetFirst(ParameterTypes.CreationDate.getOldParameter());
			if (creationDate != null) {
				guiAction.setCreationDate(Long.parseLong(creationDate));
			}
			String modDate = paramMap
					.removeAndGetFirst(ParameterTypes.ModificationDate.getOldParameter());
			if (modDate != null) {
				guiAction.setModificationDate(Long.parseLong(modDate));
			}

			String firstModDate = paramMap
					.removeAndGetFirst(ParameterTypes.FirstModificationDate.getOldParameter());
			if (firstModDate != null) {
				guiAction.setFirstModDate(Long.parseLong(firstModDate));
			}

			// remove parameters that are not required
			paramMap.remove(ParameterTypes.Replay.getOldParameter());

			guiAction.setIsReplayedAction(true);

		} else {
			// TODO each time we receive one of this actions check if
			// list of replay action is empty, if not generate event.
			// not from a graph
		}

		if (paramMap.containsKey(ParameterTypes.Direction.getOldParameter())) {
			String paramValue = paramMap
					.getFirstValue(ParameterTypes.Direction.getOldParameter());

			if (paramValue.equals(ParameterTypes.Changed.getOldParameter())) {
				String oldObjectID = paramMap
						.getFirstValue(ParameterTypes.Id.getOldParameter()); // ID
				CFObjectBasicData oldObjBasic = getEUEObjectFromTracker(oldObjectID);
				
				if(oldObjBasic != null){
					changeLinkDirection(oldObjBasic);

					// NOTE: LASAD does only support 2 parents: 1. parent == source,
					// 2. parent == target
					for (String sourceID : oldObjBasic.getSourceList()) {
						paramMap.addParam(ParameterTypes.Parent.getOldParameter(), sourceID);
					}
					for (String targetID : oldObjBasic.getTargetList()) {
						paramMap.addParam(ParameterTypes.Parent.getOldParameter(), targetID);
					}
				}
				
			}
		}
		
		Object newObject = createNewObject(paramMap);
		
		String nodeType = paramMap.getFirstValue(ParameterTypes.ElementId.getOldParameter()); // ELEMENT_ID
		
		//System.out.println("$objectID:" + objectID + ", nodeType: " + nodeType);
		if (nodeType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (objPreviousState == null) {
					System.out.println("##node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					return ;
				}
				nodeType = objPreviousState.getNodeType();
			} else {
				System.out.println("Param userID not available");
			}
		}
		
		if (newObject != null) {
			CFObjectBasicData objBasicDesc = new CFObjectBasicData();
			objBasicDesc.setId(objectID);
			objBasicDesc.setDataType(dataType);
			objBasicDesc.setNodeType(nodeType);
			objBasicDesc.setUserID(userID);

			if (dataType.equalsIgnoreCase(LASADVocabulary.ACTION_PROP_VALUE_RELATION)) {
				List<String> parentPropValues = paramMap.getAllValues(ParameterTypes.Parent.getOldParameter());
				if (parentPropValues != null) {
					if (parentPropValues.size() >= 1) {
						int source = 0;
						objBasicDesc.getSourceList().add(parentPropValues.get(source));
					}
					if (parentPropValues.size() >= 2) {
						int target = 1;
						objBasicDesc.getTargetList().add(parentPropValues.get(target));
					}
				}
			}		
			//System.out.println(objBasicDesc);
			guiAction.increaseCurrentActionsByOne();

			// track/untrack the node/link/child
			if (actionCommand.equals(Commands.CreateElement.getOldCommands())) {
				guiAction.addEueObject2CreateList(newObject);
				addEUEObjectToTracker(objBasicDesc);
			} else if (actionCommand.equals(Commands.UpdateElement.getOldCommands())) {
				//this is the timestamp we will use for the update event when creates and updates are mixed
				guiAction.addEueObject2UpdateList(newObject);
				if(time != null){
					guiAction.setModificationDate(Long.parseLong(time));
				}
				else{
					guiAction.setModificationDate(getCurrentTimeAsLong());
				}
				updateEUEObjectToTracker(objBasicDesc);
			} else if (actionCommand.equals(Commands.DeleteElement.getOldCommands())) {
				guiAction.addEueObject2DeleteList(newObject);
				if(time != null){
					guiAction.setModificationDate(Long.parseLong(time));
				}
				else{
					guiAction.setModificationDate(getCurrentTimeAsLong());
				}
				deleteEUEObjectFromTracker(objBasicDesc.getId());
			}
		} else {
			// the action was a timestamp, action was malformed or a problem
			// occurred, then decrease the total number of actions for that
			// userActionID
			guiAction.decreaseNumActionsByOne();
		}
		if (guiAction.getNumActions() > 0) {
			guiActionTracker.put(guiActionID, guiAction);
			if (guiAction.isReplayedAction()) {
				replayedGuiActionTracker.add(guiActionID);
			}
		}
	}
	
	private void processExistingGuiActionID(String guiActionID,
			String actionCommand, ParamMap paramMap) {
		// we receive a new action of an existing UserActionEvent
		CFGUIAction guiAction = guiActionTracker.remove(guiActionID);

		String userID = guiAction.getUserID();

		String time = paramMap.removeAndGetFirst(ParameterTypes.Time.getOldParameter()); // TIME
		// remove parameters that are not required
		paramMap.remove(ParameterTypes.Replay.getOldParameter());
		paramMap.remove(ParameterTypes.NumActions.getOldParameter());
		paramMap.remove(ParameterTypes.UserName.getOldParameter());

		if (paramMap.containsKey(ParameterTypes.Direction.getOldParameter())) {
			String paramValue = paramMap
					.getFirstValue(ParameterTypes.Direction.getOldParameter());

			if (paramValue.equals(ParameterTypes.Changed.getOldParameter())) {
				String objectID = paramMap
						.getFirstValue(ParameterTypes.Id.getOldParameter()); // ID
				CFObjectBasicData oldObjBasic = getEUEObjectFromTracker(objectID);

				if(oldObjBasic != null){
					changeLinkDirection(oldObjBasic);

					// NOTE: LASAD does only support 2 parents: 1. parent == source,
					// 2. parent == target
					for (String sourceID : oldObjBasic.getSourceList()) {
						paramMap.addParam(ParameterTypes.Parent.getOldParameter(), sourceID);
					}
					for (String targetID : oldObjBasic.getTargetList()) {
						paramMap.addParam(ParameterTypes.Parent.getOldParameter(), targetID);
					}
				}
				
			}
		}
		
		String objectID = paramMap.getFirstValue(ParameterTypes.Id.getOldParameter()); // ID
		String dataType = paramMap.getFirstValue(ParameterTypes.Type.getOldParameter()); // TYPE
		// "ELEMENT-ID" and "TYPE" are not provided in COMMANDs of type
		// UPDATE-ELEMENT and DELETE-ELEMENT
		if (dataType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (objPreviousState == null) {
					System.out.println("###node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					//return ;
				}
				else{
					dataType = objPreviousState.getDataType();
				}
			} else {
				System.out.println("Param userID not available");
			}
		}
		
		Object newObject = createNewObject(paramMap);
		
		String nodeType = paramMap.getFirstValue(ParameterTypes.ElementId.getOldParameter()); // ELEMENT_ID
		if (nodeType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (objPreviousState == null) {
					System.out.println("####node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					//return ;
				}
				else{
					nodeType = objPreviousState.getNodeType();
				}
			} else {
				System.out.println("Param userID not available");
			}
		}

		if (newObject != null) {
			CFObjectBasicData objBasicDesc = new CFObjectBasicData();
			objBasicDesc.setId(objectID);
			objBasicDesc.setDataType(dataType);
			objBasicDesc.setNodeType(nodeType);
			objBasicDesc.setUserID(userID);

			if (dataType.equalsIgnoreCase(LASADVocabulary.ACTION_PROP_VALUE_RELATION)) {
				List<String> parentPropValues = paramMap.getAllValues(ParameterTypes.Parent.getOldParameter());
				if (parentPropValues != null) {
					if (parentPropValues.size() >= 1) {
						int source = 0;
						objBasicDesc.getSourceList().add(parentPropValues.get(source));
					}
					if (parentPropValues.size() >= 2) {
						int target = 1;
						objBasicDesc.getTargetList().add(parentPropValues.get(target));
					}
				}
			}
			guiAction.increaseCurrentActionsByOne();
			//System.out.println(objBasicDesc);
			// track/untrack the node/link/child
			if (actionCommand.equals(Commands.CreateElement.getOldCommands())) {
				if(time != null){
					guiAction.setCreationDate(Long.parseLong(time));
				}
				guiAction.addEueObject2CreateList(newObject);
				addEUEObjectToTracker(objBasicDesc);
			} else if (actionCommand.equals(Commands.UpdateElement.getOldCommands())) {
				//this is the timestamp we will use for the update event when creates and updates are mixed
				guiAction.addEueObject2UpdateList(newObject);
				updateEUEObjectToTracker(objBasicDesc);
				if(time != null){
					guiAction.setModificationDate(Long.parseLong(time));
				}
				else{
					guiAction.setModificationDate(guiAction.getCreationDate());
				}
			} else if (actionCommand
					.equals(Commands.DeleteElement.getOldCommands())) {
				guiAction.addEueObject2DeleteList(newObject);
				deleteEUEObjectFromTracker(objBasicDesc.getId());
				if(time != null){
					guiAction.setModificationDate(Long.parseLong(time));
				}
				else{
					guiAction.setModificationDate(guiAction.getCreationDate());
				}
			}
			

		} else {
			// the action was a timestamp, action was malformed or a problem
			// occurred, then decrease the total number of actions for that
			// guiActionID
			guiAction.decreaseNumActionsByOne();
		}

		// After the insertions/deletions check if we should continue tracking
		// this guiActionID
		if (guiAction.getNumActions() > 0) {
			guiActionTracker.put(guiActionID, guiAction);
		}
	}
	
	public Object createNewObject(ParamMap paramMap) {
		ObjectFactory objFactory = new ObjectFactory();
		Object newObject = objFactory.createObject();

		String dataType = paramMap.removeAndGetFirst(ParameterTypes.Type.getOldParameter()); // TYPE
		if (LASADVocabulary.ACTION_PROP_VALUE_AWARENESS_CURSOR.equalsIgnoreCase(dataType)
				|| LASADVocabulary.ACTION_PROP_VALUE_GROUP_CURSOR.equalsIgnoreCase(dataType)) {
			return null;
		}

		// EUEObject fields
		String objectID = paramMap
				.removeAndGetFirst(ParameterTypes.Id.getOldParameter()); // ID
		String nodeType = paramMap
				.removeAndGetFirst(ParameterTypes.ElementId.getOldParameter()); // ELEMENT_ID

		// remove params not required
		//paramMap.remove(LASADVocabulary.ACTION_PROP_STATUS);

		// "ELEMENT-ID" and "TYPE" are not provided in COMMANDs of type
		// UPDATE-ELEMENT and DELETE-ELEMENT
		if (nodeType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (objPreviousState == null) {
					System.out.println("#####node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					return null;
				}
				nodeType = objPreviousState.getNodeType();
			} else {
				System.out.println("Param userID not available");
			}
		}
		if (dataType == null) {
			if (objectID != null) {
				CFObjectBasicData objPreviousState = getEUEObjectFromTracker(objectID);
				if (null == objPreviousState) {
					System.out.println("#####node: " + objectID
							+ " is not in tracking system, should have type:"
							+ LASADVocabulary.ACTION_PROP_VALUE_AWARENESS);
					return null;
				}
				dataType = objPreviousState.getDataType();
			} else {
				System.out.println("Param userID not available");
			}
		}
		
		Properties propList = objFactory.createProperties();

		// add parent props (i.e., parent of child elements,
		// sources and targets of links)
		if (dataType.equalsIgnoreCase(LASADVocabulary.ACTION_PROP_VALUE_RELATION)) {
			List<String> parentPropValues = paramMap.getAllValues(ParameterTypes.Parent.getOldParameter());
			if (parentPropValues != null) {
				if (parentPropValues.size() >= 1) {
					int source = 0;
					Property sourceProp = objFactory.createProperty();
					sourceProp.setName(CFVocabulary.OBJECT_PROP_SOURCE);
					sourceProp.setValue(parentPropValues.get(source));
					propList.getProperty().add(sourceProp);
				}
				if (parentPropValues.size() >= 2) {
					int target = 1;
					Property targetProp = objFactory.createProperty();
					targetProp.setName(CFVocabulary.OBJECT_PROP_TARGET);
					targetProp.setValue(parentPropValues.get(target));
					propList.getProperty().add(targetProp);
				}
			}
		} else if (dataType
				.equalsIgnoreCase(LASADVocabulary.ACTION_PROP_VALUE_BOX)) {
			//
		} else {
			List<String> parentPropValues = paramMap.getAllValues(ParameterTypes.Parent.getOldParameter());
			if (parentPropValues != null && parentPropValues.size() >= 1){
				int source = 0;
				Property parentProp = objFactory.createProperty();
				parentProp.setName(CFVocabulary.OBJECT_PROP_PARENTID);
				String parentIDValue = parentPropValues.get(source);
				parentProp.setValue(parentIDValue);
				propList.getProperty().add(parentProp);
				
				if(objectTracker.containsKey(parentIDValue)){
					CFObjectBasicData parent = objectTracker.get(parentIDValue);
					nodeType = parent.getNodeType() + SEPARATOR + nodeType;
				}
				else{
					System.err.println("Parent" + parentIDValue + " of " + objectID + " is unknown");
				}
			}
		}
		paramMap.addParam(ParameterTypes.ElementId.getOldParameter(), nodeType);
		//System.out.println("$$objectID:" + objectID + ", nodeType: " + nodeType);

		// add basic props
		newObject.setId(objectID);
		newObject.setType(nodeType);
		
		
		Property typeProp = objFactory.createProperty();
		typeProp.setName(ParameterTypes.Type.getOldParameter());
		typeProp.setValue(dataType);
		propList.getProperty().add(typeProp);

		// add generic props
		Iterator<String> iter = paramMap.keySet().iterator();
		while (iter.hasNext()) {
			String paramName = iter.next();
			String paramValue = paramMap.getFirstValue(paramName);
			if (paramName.equals(ParameterTypes.Parent.getOldParameter()) ||
					paramName.equals(ParameterTypes.ElementId.getOldParameter())){
				continue;
			}
			
			Property newProp = objFactory.createProperty();
			newProp.setName(paramName);
			newProp.setValue(paramValue);
			propList.getProperty().add(newProp);
		}
		newObject.setProperties(propList);

		return newObject;

	}
	
	private boolean addEUEObjectToTracker(CFObjectBasicData newObject) {
		boolean flag = false;
		String objectID = newObject.getId();
		if (!objectTracker.containsKey(objectID)) {
			objectTracker.put(objectID, newObject);
			flag = true;
		}
		return flag;
	}
	
	private boolean updateEUEObjectToTracker(CFObjectBasicData newObject) {
		boolean flag = false;
		String objectID = newObject.getId();
		if (objectTracker.containsKey(objectID)) {
			/*CFObjectBasicData currentObj = */ objectTracker.remove(objectID);
			objectTracker.put(objectID, newObject);
			flag = true;
		}
		return flag;
	}

	private boolean deleteEUEObjectFromTracker(String objectID) {
		boolean flag = false;
		if (objectTracker.containsKey(objectID)) {
			objectTracker.remove(objectID);
			flag = true;
		}
		return flag;
	}

	private CFObjectBasicData getEUEObjectFromTracker(String objectID) {
		CFObjectBasicData tmpEUEObject = null;
		if (objectTracker.containsKey(objectID)) {
			tmpEUEObject = objectTracker.get(objectID);
		}
		return tmpEUEObject;
	}
	
	private boolean isFeedbackElement(String mapID, String objectID) {
		Set<String> objectsForMap = mapID2feedbackElementIDs.get(mapID);
		if (objectsForMap == null) {
			return false;
		}
		return objectsForMap.contains(objectID);
	}
	
	private String getGuiActionID(ParamMap paramMap) {

		String guiActionID = null;

		String replayValueAsString = paramMap
				.getFirstValue(ParameterTypes.Replay.getOldParameter());
		boolean replayParam = Boolean.valueOf(replayValueAsString);

		if (replayParam) {
			// Since the USERACTION-ID (i.e., guiActionID) value might be the
			// same for different action packages we need to generate unique
			// values for each action package.
			paramMap.remove(ParameterTypes.UserActionId.getOldParameter());

			if (!paramMap.containsKey(ParameterTypes.CreationDate.getOldParameter())) {
				// it is a root, i.e. is the box node
				guiActionID = String.valueOf(getReplayedEventCount());
			} else {
				// it is a child of the box, e.g., header or comment section
				guiActionID = String
						.valueOf(getReplayedEventCountAndIncrement());
			}
		} else {
			// we can get the USERACTION-ID value directly, it's a unique value
			// for each actionpackage
			List <String> attrList =  paramMap.remove(ParameterTypes.UserActionId.getOldParameter());
			if(attrList != null && attrList.size() > 0){
				guiActionID = attrList.get(0);
			}
		}
		return guiActionID;
	}
	
	private int getReplayedEventCount() {
		return replayedEventCounter;
	}

	private int getReplayedEventCountAndIncrement() {
		return ++replayedEventCounter;
	}

	public void resetReplayedEventCounter() {
		replayedEventCounter = 0;
	}
	
	public void changeLinkDirection(CFObjectBasicData objBasicData) {

		List<String> newSourceList = new ArrayList<String>();
		List<String> newTargetList = new ArrayList<String>();

		for (String objID : objBasicData.getSourceList()) {
			newTargetList.add(objID);
		}
		for (String objID : objBasicData.getTargetList()) {
			newSourceList.add(objID);
		}
		objBasicData.getSourceList().clear();
		objBasicData.getTargetList().clear();

		objBasicData.getSourceList().addAll(newSourceList);
		objBasicData.getTargetList().addAll(newTargetList);

	}
	
	/**
	 * Maps a UserJoin event to a CF Action
	 * Returns a CF Action. This method is
	 * call each time a user joins a map.
	 * @param		actionCategory			the Object to compare with
	 * @param		actionCommand			Maps to the classification of the actiontype
	 * @return		the Action generated by the join
	 */
	
	public Action translateUserJoinAction(String actionCategory, String actionCommand, ParamMap paramMap){
		Action action = createAction(CFVocabulary.ACTIONTYPE_COTHER, CFVocabulary.ACTIONTYPE_TYPE_JOIN, paramMap); 
		return action;
	}
	
	public Action translateUserLeaveAction(String actionCategory, String actionCommand, ParamMap paramMap){
		Action action = createAction(CFVocabulary.ACTIONTYPE_COTHER, CFVocabulary.ACTIONTYPE_TYPE_LEAVE, paramMap); 
		return action;
	}
	
	public Action translateUserLoginAction(String actionCategory, String actionCommand, ParamMap paramMap){
		Action action = createAction(CFVocabulary.ACTIONTYPE_COTHER, CFVocabulary.ACTIONTYPE_TYPE_LOGIN, paramMap); 
		return action;
	}
	
	public Action translateUserLogoutAction(String actionCategory, String actionCommand, ParamMap paramMap){
		Action action = createAction(CFVocabulary.ACTIONTYPE_COTHER, CFVocabulary.ACTIONTYPE_TYPE_LOGOUT, paramMap); 
		return action;
	}
	
	private Action createAction(String classification, String type, ParamMap paramMap){
		ObjectFactory objFactory = new ObjectFactory();
		Action action = objFactory.createAction();
		
		action.setTime(getCurrentTime());
		
		Actiontype actionType = objFactory.createActiontype();
		actionType.setClassification(classification);
		actionType.setType(type);
		actionType.setLogged(LOGGED_VALUE);
		
		action.setActiontype(actionType);
		
		String sessionID = paramMap.removeAndGetFirst(ParameterTypes.MapId.getOldParameter());
		String userID = paramMap.removeAndGetFirst(ParameterTypes.UserName.getOldParameter());
		
		if(sessionID == null){
			sessionID = "0";
		}
		
		User user = createUser(userID);
		action.getUser().add(user);
		
		Object container = objFactory.createObject();
		container.setId(CFConstants.CONTAINER_ID);
		container.setType(CFConstants.CONTAINER_TYPE);
		
		Properties propList = objFactory.createProperties();
		Property sessionProp = objFactory.createProperty();
		sessionProp.setName(ParameterTypes.MapId.getOldParameter());
		sessionProp.setValue(sessionID);
		propList.getProperty().add(sessionProp);
		
		container.setProperties(propList);
		action.setObject(container);
		
		return action;
	}
	
	/**
	 * Maps a ChatMsg action to a CF Action
	 * Returns a CF Action. This method is
	 * call each time a ChatMsg is set.
	 * @param		actionCategory			the Object to compare with
	 * @param		actionCommand			Maps to the classification of the actiontype
	 * @return		the Action generated by the join
	 */
	
	public Action translateChatMsgAction(String actionCategory, String actionCommand, ParamMap paramMap){
		ObjectFactory objFactory = new ObjectFactory();
		Action action = objFactory.createAction();
		
		action.setTime(paramMap.removeAndGetFirst(ParameterTypes.Time.getOldParameter()));
		
		Actiontype actionType = objFactory.createActiontype();
		actionType.setClassification(CFVocabulary.ACTIONTYPE_COTHER);
		actionType.setType(CFVocabulary.ACTIONTYPE_TYPE_CHAT_MSG);
		actionType.setLogged(LOGGED_VALUE);
		action.setActiontype(actionType);
		
		String userID = paramMap.removeAndGetFirst(ParameterTypes.UserName.getOldParameter());
		User user = createUser(userID);
		action.getUser().add(user);
		
		Object container = objFactory.createObject();
		container.setId(CFVocabulary.CONTAINER_ID);
		container.setType(CFVocabulary.CONTAINER_TYPE);
		
		//obj ChatMsg
		Object boxEle = objFactory.createObject();
		boxEle.setId(CFVocabulary.MSG_CONTAINER_ID);
		boxEle.setType(CFVocabulary.MSG_CONTAINER_TYPE);
		
		Properties boxProps = objFactory.createProperties();
		
		String msg = paramMap.removeAndGetFirst(ParameterTypes.Message.getOldParameter());
		Property propMapID = objFactory.createProperty();
		propMapID.setName(ParameterTypes.Message.getOldParameter());
		propMapID.setValue(msg);
		boxProps.getProperty().add(propMapID);
		
		String sessionID = paramMap.removeAndGetFirst(ParameterTypes.MapId.getOldParameter());
		Property propRoot = objFactory.createProperty();
		propRoot.setName(ParameterTypes.MapId.getOldParameter());
		propRoot.setValue(sessionID);
		boxProps.getProperty().add(propRoot);
		
		boxEle.setProperties(boxProps);
		
		container.getObject().add(boxEle);
		action.setObject(container);
		
		return action;
	}
	
	public User createUser(String userID){
		ObjectFactory objFactory = new ObjectFactory();
		User user = objFactory.createUser();
		user.setId(userID);
		user.setRole(USER_ROLE_VALUE);
		return user;
	}
	
	public static String getCurrentTime(){
		return new String("" + System.currentTimeMillis());
	}
	public static long getCurrentTimeAsLong(){
		return System.currentTimeMillis();
	}
}
