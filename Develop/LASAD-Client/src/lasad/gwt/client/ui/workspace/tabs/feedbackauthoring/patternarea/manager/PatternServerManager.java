package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.elements.AgentModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.elements.PatternModel;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ComparisonUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.IdGenerator;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ToasterMessage;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.FAOntElementIds;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology.OntologyXML2ObjReader;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.authoring.frontenddata.AgentDescriptionFE;
import lasad.shared.dfki.meta.agents.analysis.AnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.StructureAnalysisType;
import lasad.shared.dfki.meta.agents.analysis.structure.model.Comparison;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.StructuralPattern;
import lasad.shared.dfki.meta.ontology.descr.TypePropDescr;

/**
 * This class emulates the work of the LASAD Server
 * Keeps tracks of agents, patterns and its elements.
 * 
 * @author anahuacv
 * 
 */
public class PatternServerManager {
	//AgentId to AgentModel relation 
	private Map<String, AgentModel> agentsMap = new LinkedHashMap<String, AgentModel>();
	
	/**
	 * Replays the elements of the given pattern, so it can be displayed 
	 * in the pattern working area.
	 * @param agentId
	 * @param patternId
	 */
	public void replay(String agentId, String patternId){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			Vector<PatternElement> replayList = new Vector<PatternElement>();
			PatternModel patternModel = agentsMap.get(agentId).getPattern(patternId);
			Vector<String> linkList = new Vector<String>();
			//ParameterTypes.Replay
			//get boxes and their children first
			for(String parentId : patternModel.getParentList()){
				PatternElement parent = patternModel.getPatternElement(parentId);
				if(parent.getParameterValue(ParameterTypes.Type).equals("box")){
					replayList.add(parent);
					for(String childId : patternModel.getChildrenOfParent(parentId)){
						replayList.add(patternModel.getPatternElement(childId));
					}
				} else{
					linkList.add(parentId);
				}
			}
			//get links and their children
			for(String parentId : linkList){
				replayList.add(patternModel.getPatternElement(parentId));
				for(String childId : patternModel.getChildrenOfParent(parentId)){
					replayList.add(patternModel.getPatternElement(childId));
				}
			}
			for(PatternElement elem : replayList){
				PatternReceiver.getInstance().processCreateElementAction(elem);
			}
		}
	}
	/**
	 * Adds the agent
	 * @param agentId
	 */
	public void addAgent(String agentId){
		agentsMap.put(agentId, new AgentModel(agentId));
	}
	/**
	 * deletes all the agents
	 * @return
	 */
	public void deleteAllAgents(){
		agentsMap.clear();
		//return new Vector<AgentModel>(agentsMap.values());
	}
	/**
	 * deletes the agent
	 * @param agentId
	 * @return
	 */
	public AgentModel deleteAgent(String agentId){
		return agentsMap.remove(agentId);
	}
	/**
	 * adds the pattern to the given agent
	 * @param agentId
	 * @param patternId
	 */
	public void addPattern(String agentId, String patternId){
		AgentModel agentModel = agentsMap.get(agentId);
		if(agentModel != null && agentModel.getPattern(patternId) == null){
			agentModel.addPattern(patternId);
		}
	}
	/**
	 * deletes the pattern from the given agent
	 * @param agentId
	 * @param patternId
	 */
	public void deletePattern(String agentId, String patternId){
		AgentModel agentModel = agentsMap.get(agentId);
		if(agentModel != null){
			agentModel.deletePattern(patternId);
		}		
	}
	/**
	 * Returns the pattern's controller
	 * @param agentId
	 * @param patternId
	 * @return {@link PatternController}
	 */
	public PatternController getController(String agentId, String patternId){
		PatternController retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).getController();
		}
		return retVal;
	}
	/**
	 * Adds the controller to the pattern
	 * @param agentId
	 * @param patternId
	 * @param patternController
	 */
	public void addController(String agentId, String patternId, PatternController patternController){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			agentsMap.get(agentId).getPattern(patternId).setController(patternController);
		}
	}
	/**
	 * Deletes/sets to null the pattern's controller
	 * @param agentId
	 * @param patternId
	 */
	public void deleteController(String agentId, String patternId){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			agentsMap.get(agentId).getPattern(patternId).setController(null);
		}
	}
	/**
	 * Adds the element to the pattern
	 * @param agentId
	 * @param patternId
	 * @param patternElement
	 */
	public void addPatternElement(String agentId, String patternId, PatternElement patternElement){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			agentsMap.get(agentId).getPattern(patternId).addPatternElement(patternElement);
		}
	}
	/**
	 * Deletes the given pattern element
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @return {@link PatternElement}
	 */
	public PatternElement deletePatternElement(String agentId, String patternId, String patternElementId){
		PatternElement retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).deletePatternElement(patternElementId);
		}
		return retVal;
	}
	/**
	 * Deletes the children of an element (box/link)
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @return List<{@link PatternElement}>
	 */
	private List<PatternElement> deletePatternElementChildren(String agentId, String patternId, String patternElementId){
		List<PatternElement> retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).deletePatternElementChildren(patternElementId);
		}
		return retVal;
	}
	/**
	 * Gets the pattern's element
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @return {@link PatternElement}
	 */
	public PatternElement getPatternElement(String agentId, String patternId, String patternElementId){
		PatternElement retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).getPatternElement(patternElementId);
		}
		return retVal;
	}
	/**
	 * * Gets the patterns' elements
	 * @param agentId
	 * @param patternId
	 * @return List<{@link PatternElement}>
	 */
	public List<PatternElement> getPatternElements(String agentId, String patternId){
		List<PatternElement> retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).getPatternElements();
		}
		return retVal;
	}
	/**
	 * Adds "To" link to the element 
	 * Used to track the element's list of links, i.e. to track the links related to a element
	 * @param agentId
	 * @param patternId
	 * @param elementId
	 * @param linkId
	 */
	private void addTolinks2ElementMap(String agentId, String patternId, String elementId, String linkId){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			agentsMap.get(agentId).getPattern(patternId).addTolinks2ElementMap(elementId, linkId);
		}
	}
	/**
	 * Removes a "To" link from the element 
	 * @param agentId
	 * @param patternId
	 * @param elementId
	 * @param linkId
	 */
	private void removeFromlinks2ElementMap(String agentId, String patternId, String elementId, String linkId){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			agentsMap.get(agentId).getPattern(patternId).removeFromlinks2ElementMap(elementId, linkId);
		}
	}
	/**
	 * Get the list of link Ids related to the element
	 * @param agentId
	 * @param patternId
	 * @param elementId
	 * @return List<{@link String}>
	 */
	private List<String> getlinks2ElementMap(String agentId, String patternId, String elementId){
		List<String> retVal = null;
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			retVal = agentsMap.get(agentId).getPattern(patternId).getlinks2ElementMap(elementId);
		}
		return retVal;
	}
	
	/**
	 * Removes an element
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 */
	public void removeElement(String agentId, String patternId, String patternElementId){
		PatternElement deletedElement = deletePatternElement(agentId, patternId, patternElementId);
		//remove children
		List<PatternElement> childrenDeleted =  deletePatternElementChildren(agentId, patternId, patternElementId);
		
		Vector<PatternElement> otherPatternElements = new Vector<PatternElement>();
		//if patternElementId is a Box with Link connections delete Links and their children.
		if(deletedElement.getParameterValue(ParameterTypes.Type).equalsIgnoreCase("box")){
			List<String> connectingLinksList = getlinks2ElementMap(agentId, patternId, patternElementId);
			if(connectingLinksList!=null){
				for(String linkId:connectingLinksList){//iterate over the links connecting the box
					PatternElement element = deletePatternElement(agentId, patternId, linkId); //delete link
					//remove children
					List<PatternElement> childrenList =  deletePatternElementChildren(agentId, patternId, linkId); //delete link's children
					otherPatternElements.addAll(childrenList);
					otherPatternElements.add(element);
					
					//delete map to link info
					Vector<String> parents =  element.getParameterValues(ParameterTypes.Parent);
					for(String parent:parents){
						if(!parent.equals(patternElementId))
							removeFromlinks2ElementMap(agentId, patternId, parent, linkId);
					}
				}
			}
		}else if (deletedElement.getParameterValue(ParameterTypes.Type).equalsIgnoreCase("relation")){
			//remove link connection from boxes
			Vector<String> parents =  deletedElement.getParameterValues(ParameterTypes.Parent);
			for(String parent:parents){
				removeFromlinks2ElementMap(agentId, patternId, parent, patternElementId);
			}
			
		}
		
		//put together all elements to be deleted
		otherPatternElements.addAll(childrenDeleted);
		otherPatternElements.add(deletedElement);
		
		for(PatternElement elem : otherPatternElements){
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[removeElement]: " + elem);
			PatternReceiver.getInstance().processDeleteElementAction(elem);
		}
	}
	
	/**
	 * Add a child to a Box or Link
	 * @param agentId
	 * @param patternId
	 * @param parentId
	 * @param elementType
	 * @param elementId
	 */
	public void addElement(String agentId, String patternId, String parentId, String elementType, String elementId){
		String id = String.valueOf(IdGenerator.getNewElementId());
		
		PatternElement patternElement = new PatternElement(id);
		patternElement.addParameter(ParameterTypes.Type, elementType); //e.g. box, link
		patternElement.addParameter(ParameterTypes.Id, String.valueOf(id));
		patternElement.addParameter(ParameterTypes.ElementId, elementId); //e.g. argument
		patternElement.addParameter(ParameterTypes.PatternId, patternId);
		patternElement.addParameter(ParameterTypes.AgentId, agentId);
		patternElement.addParameter(ParameterTypes.Parent, parentId);
		
		addPatternElement(agentId, patternId, patternElement);
		
		FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
				"[addElement]: " + patternElement);
		PatternReceiver.getInstance().processCreateElementAction(patternElement);
	}
	
	/**
	 * Handles the creation of a box, a link and its respective elements at the same time
	 * @param boxInfo
	 * @param linkInfo
	 * @param agentId
	 * @param patternId
	 * @param posX
	 * @param posY
	 * @param startId
	 * @param endId
	 */
	public void createBoxAndLink(ElementInfo boxInfo, ElementInfo linkInfo, String agentId, String patternId, int posX, int posY, String startId, String endId){
		Vector<PatternElement> allElements = new Vector<PatternElement>();
		
		PatternElement box = createBox(boxInfo, agentId, patternId, posX, posY);
		Vector<PatternElement> childrenList = createBoxElementsAction(boxInfo, agentId, patternId, box.getId());
		
		addPatternElement(agentId, patternId, box);
		for(PatternElement child: childrenList){
			addPatternElement(agentId, patternId, child);
		}
		
		//TODO seems that we need to pass the id of the box as the startId
		PatternElement link = createLink(linkInfo, agentId, patternId, startId, box.getId());
		Vector<PatternElement> childrenLinkList = createLinkElementsAction(linkInfo, agentId, patternId, link.getId(), null);
		
		addPatternElement(agentId, patternId, link);
		for(PatternElement child: childrenLinkList){
			addPatternElement(agentId, patternId, child);
		}
		allElements.add(box);
		allElements.addAll(childrenList);
		allElements.add(link);
		allElements.addAll(childrenLinkList);
		
		FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
				"[createBoxAndLink]: ");
		for(PatternElement elem : allElements){
			PatternReceiver.getInstance().processCreateElementAction(elem);
		}
		
	}
	/**
	 * Creates an element of type link 
	 * @param info
	 * @param agentId
	 * @param patternId
	 * @param startId
	 * @param endId
	 * @return {@link PatternElement}
	 */
	private PatternElement createLink(ElementInfo info, String agentId, String patternId, String startId, String endId){
		String id = String.valueOf(IdGenerator.getNewElementId());
		String rootElementId = String.valueOf(IdGenerator.getNewRootElementId());
		
		PatternElement link = new PatternElement(id);
		link.addParameter(ParameterTypes.Type, info.getElementType());
		link.addParameter(ParameterTypes.Id, String.valueOf(id));
		link.addParameter(ParameterTypes.ElementId, info.getElementID()); //e.g. argument
		link.addParameter(ParameterTypes.PatternId, patternId);
		link.addParameter(ParameterTypes.AgentId, agentId);
		link.addParameter(ParameterTypes.RootElementId, String.valueOf(rootElementId));
		link.addParameter(ParameterTypes.Parent, startId);
		link.addParameter(ParameterTypes.Parent, endId);
		//link.addParameter(ParameterTypes.Direction, startId + "," + endId);
		
		addTolinks2ElementMap(agentId, patternId, startId, id);
		addTolinks2ElementMap(agentId, patternId, endId, id);
		
		return link;
	}
	/**
	 * Creates the elements of a Link
	 * @param info
	 * @param agentId
	 * @param patternId
	 * @param parentId
	 * @param existingChildElements
	 * @return
	 */
	private Vector<PatternElement> createLinkElementsAction(ElementInfo info, String agentId, String patternId, String parentId, Vector<AbstractExtendedElement> existingChildElements){
		Vector<PatternElement> patternElementList = new Vector<PatternElement>();
		if (existingChildElements == null) {
			Collection<ElementInfo> childElements = info.getChildElements().values();
			for (ElementInfo childElement : childElements) {
				// Initializing loop variable and changing it in case there're
				// no existing child elements
				int quantity = childElement.getQuantity();
				int i = 0;
				while (i < quantity) {
					
					String idChild = String.valueOf(IdGenerator.getNewElementId());
					
					PatternElement child = new PatternElement(idChild);
					child.addParameter(ParameterTypes.Type, childElement.getElementType()); //e.g. box, link
					child.addParameter(ParameterTypes.Id, String.valueOf(idChild));
					child.addParameter(ParameterTypes.ElementId, childElement.getElementID()); //e.g. argument
					child.addParameter(ParameterTypes.PatternId, String.valueOf(patternId));
					child.addParameter(ParameterTypes.AgentId, agentId);
					child.addParameter(ParameterTypes.Parent, parentId);

					if (child != null) {
						patternElementList.add(child);
					}
					i++;
				}
			}
		} else {
			for (AbstractExtendedElement childElement : existingChildElements) {
				String idChild = String.valueOf(IdGenerator.getNewElementId());
				
				PatternElement child = new PatternElement(idChild);
				child.addParameter(ParameterTypes.Type, childElement.getConfig().getElementType()); //e.g. box, link
				child.addParameter(ParameterTypes.Id, String.valueOf(idChild));
				child.addParameter(ParameterTypes.ElementId, childElement.getConfig().getElementID()); //e.g. argument
				child.addParameter(ParameterTypes.PatternId, String.valueOf(patternId));
				child.addParameter(ParameterTypes.AgentId, agentId);
				child.addParameter(ParameterTypes.Parent, parentId);

				if (child != null) {
					patternElementList.add(child);
				}

				if (childElement.getConfig().getElementType().equals("rating")) {
					child.addParameter(ParameterTypes.Score, childElement.getConfig().getElementOption(ParameterTypes.Score));
				} else if (childElement.getConfig().getElementType().equals("text")) {
					String text;
					if (childElement.getConnectedModel().getValue(ParameterTypes.Text) == null) {
						text = "";
					} else {
						text = childElement.getConnectedModel().getValue(ParameterTypes.Text);
					}
					child.addParameter(ParameterTypes.Text, text);
				} else if (childElement.getConfig().getElementType().equals("awareness")) {
					child.addParameter(ParameterTypes.Time, String.valueOf(System.currentTimeMillis()));
				}
				if (child != null) {
					patternElementList.add(child);
				}
			}
		}
		
		return patternElementList;
	}
	
	/**
	 * Checks if a relation is valid based on the following syntax rules
	 * Each link connects exactly two boxes
	 * Red boxes can only be connected via red links
	 * Red boxes can only be connected to at most one other box
	 * --> Directed link, -- link, -/-> No directed link, -/- No Link
	 * b1 --> b2
	 * b1 -- b2
	 * b1 -/-> b2
	 * b1 -/- b2
	 * b1 -/-> No-b2
	 * No-b1 -/-> b2 //Oliver asked me to support this case too
	 * b1 -/- No-b2
	 * No-b1 -/-> No-b2
	 * No-b1 -/- No-b2
	 * @param linkInfo
	 * @param agentId
	 * @param patternId
	 * @param startElementID
	 * @param endElementID
	 * @return
	 */
	private boolean isValidRelation(ElementInfo linkInfo, String agentId, String patternId, String startElementID, String endElementID){
		boolean retVal = true;
		if(agentId != null && patternId != null && startElementID != null && endElementID != null){
			PatternElement sourceNode = getPatternElement(agentId, patternId, startElementID);
			PatternElement targetNode = getPatternElement(agentId, patternId, endElementID);
			if(sourceNode != null && targetNode != null){
				String linkElemId = linkInfo.getElementID();
				String sourceElemId = sourceNode.getParameterValue(ParameterTypes.ElementId);
				String targetElemId = targetNode.getParameterValue(ParameterTypes.ElementId);
				
				if(sourceElemId.equals(FAOntElementIds.NO_BOX)){
					List<String> sourceLinkList = getlinks2ElementMap(agentId, patternId, startElementID);
					if(sourceLinkList != null && sourceLinkList.size() >= 1){
						retVal = false; //Error trying to add a second link to a NoBox
						FATDebug.print(FATDebug.WAR,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
								"[isValidRelation] source, Error trying to add a second link to a NoBox");
					}
				}
				if(retVal && targetElemId.equals(FAOntElementIds.NO_BOX)){
					List<String> targetLinkList = getlinks2ElementMap(agentId, patternId, endElementID);
					if(targetLinkList != null && targetLinkList.size() >= 1){
						retVal = false; //Error trying to add a second link to a NoBox
						FATDebug.print(FATDebug.WAR,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
								"[isValidRelation] target, Error trying to add a second link to a NoBox");
					}
				}
				
				if(retVal && (FAOntElementIds.NO_LINK.equals(linkElemId)
						|| FAOntElementIds.NO_DIRECTED_LINK.equals(linkElemId))){
					//remove validation to support 
					//No-b1 -/-> b2
//					if(sourceElemId.equals(FAOntElementIds.NO_BOX)
//						&& !targetElemId.equals(FAOntElementIds.NO_BOX)){
//						retVal = false; //Error No-b1 -/-> b2, No-b1 -/- b2
//						FATDebug.print(FATDebug.WAR,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
//								"[isValidRelation] Error No-b1 -/-> b2, No-b1 -/- b2");
//					}
				} else if(FAOntElementIds.LINK.equals(linkElemId)
						|| FAOntElementIds.DIRECTED_LINK.equals(linkElemId)){
					if(sourceElemId.equals(FAOntElementIds.NO_BOX)
							|| targetElemId.equals(FAOntElementIds.NO_BOX)){
						retVal = false;
						FATDebug.print(FATDebug.WAR,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
								"[isValidRelation] Error link or directedlink and one or two nobox(es)");
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Handles the creation of a link and its elements
	 * @param linkInfo
	 * @param agentId
	 * @param patternId
	 * @param startElementID
	 * @param endElementID
	 */
	public void createLinkWithElements(ElementInfo linkInfo, String agentId, String patternId, String startElementID, String endElementID){
		if(!isValidRelation(linkInfo, agentId, patternId, startElementID, endElementID)){
			//TODO send error or print it in the FAT console
			FATDebug.print(FATDebug.WAR,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[createLinkWithElements] invalid relation");
			ToasterMessage.logErr(FeedbackAuthoringStrings.INVALID_RELATION);
			return;
		}
		PatternElement link = createLink(linkInfo, agentId, patternId, startElementID, endElementID);
		Vector<PatternElement> childrenLinkList = createLinkElementsAction(linkInfo, agentId, patternId, link.getId(), null);
		
		addPatternElement(agentId, patternId, link);
		for(PatternElement child: childrenLinkList){
			addPatternElement(agentId, patternId, child);
		}
		
		Vector<PatternElement> allElements = new Vector<PatternElement>();
		allElements.add(link);
		allElements.addAll(childrenLinkList);
		
		for(PatternElement elem : allElements){
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[createLinkWithElements]: " + elem);
			PatternReceiver.getInstance().processCreateElementAction(elem);
		}
		
	}
	
	/**
	 * Handles the creation of a box and its elements
	 * @param currentElement
	 * @param agentId
	 * @param patternId
	 * @param posX
	 * @param posY
	 */
	public void createBoxWithElements(ElementInfo currentElement, String agentId, String patternId, int posX, int posY){
		PatternElement box = createBox(currentElement, agentId, patternId, posX, posY);
		Vector<PatternElement> childrenList = createBoxElementsAction(currentElement, agentId, patternId, box.getId());
		
		addPatternElement(agentId, patternId, box);
		for(PatternElement child: childrenList){
			addPatternElement(agentId, patternId, child);
		}
		Vector<PatternElement> allElements = new Vector<PatternElement>();
		allElements.add(box);
		allElements.addAll(childrenList);
		
		for(PatternElement elem : allElements){
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[createBoxWithElements]: " + elem);
			PatternReceiver.getInstance().processCreateElementAction(elem);
		}
	}
	/**
	 * Creates box element 
	 * @param config
	 * @param agentId
	 * @param patternId
	 * @param posX
	 * @param posY
	 * @return
	 */
	private PatternElement createBox(ElementInfo config, String agentId, String patternId, int posX, int posY){
		String id = String.valueOf(IdGenerator.getNewElementId());
		String rootElementId = String.valueOf(IdGenerator.getNewRootElementId());
		
		PatternElement patternElement = new PatternElement(id);
		patternElement.addParameter(ParameterTypes.Type, config.getElementType());
		patternElement.addParameter(ParameterTypes.Id, String.valueOf(id));
		patternElement.addParameter(ParameterTypes.ElementId, config.getElementID()); //e.g. argument
		patternElement.addParameter(ParameterTypes.PatternId, patternId);
		patternElement.addParameter(ParameterTypes.AgentId, agentId);
		patternElement.addParameter(ParameterTypes.RootElementId, String.valueOf(rootElementId));
		patternElement.addParameter(ParameterTypes.PosX, String.valueOf(posX));
		patternElement.addParameter(ParameterTypes.PosY, String.valueOf(posY));
		
		return patternElement;
	}
	/**
	 * Creates the elements of a box
	 * @param currentElement
	 * @param agentId
	 * @param patternId
	 * @param parentId
	 * @return
	 */
	private Vector<PatternElement> createBoxElementsAction(ElementInfo currentElement, String agentId, String patternId, String parentId){
		Vector<PatternElement> patternElementList = new Vector<PatternElement>();
		
		for (ElementInfo childElement : currentElement.getChildElements().values()) {
			for (int i = 0; i < childElement.getQuantity(); i++) {
				
				String id = String.valueOf(IdGenerator.getNewElementId());
				
				PatternElement patternElement = new PatternElement(id);
				patternElement.addParameter(ParameterTypes.Type, childElement.getElementType()); //e.g. box, link, text
				patternElement.addParameter(ParameterTypes.Id, String.valueOf(id));
				patternElement.addParameter(ParameterTypes.ElementId, childElement.getElementID()); //e.g. argument
				patternElement.addParameter(ParameterTypes.PatternId, String.valueOf(patternId));
				patternElement.addParameter(ParameterTypes.AgentId, agentId);
				patternElement.addParameter(ParameterTypes.Parent, String.valueOf(parentId));

				if (patternElement != null) {
					patternElementList.add(patternElement);
				}
			}
		}

		return patternElementList;
	}
	/**
	 * Updates an element's property
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @param type
	 * @param newValue
	 */
	public void updatePatternElement(String agentId, String patternId, String patternElementId, String type, String newValue){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement= agentsMap.get(agentId).getPattern(patternId).updatePatternElement(patternElementId, type, newValue);
			if(patternElement != null){
				PatternReceiver.getInstance().processUpdateElementAction(patternElement);
			}
		}
	}
	/**
	 * Updates the box's size
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @param width
	 * @param height
	 */
	public void updateBoxSize(String agentId, String patternId, String patternElementId, int width, int height){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(patternElementId);
			patternElement.removeParameter(ParameterTypes.Width);
			patternElement.addParameter(ParameterTypes.Width, String.valueOf(width));
			patternElement.removeParameter(ParameterTypes.Height);
			patternElement.addParameter(ParameterTypes.Height, String.valueOf(height));
			
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[updateBoxSize]: " + patternElement);
			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
		}
	}
	
	/**
	 * Updates the box's position
	 * @param agentId
	 * @param patternId
	 * @param patternElementId
	 * @param x
	 * @param y
	 */
	public void updateBoxPosition(String agentId, String patternId, String patternElementId, int x, int y){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(patternElementId);
			patternElement.removeParameter(ParameterTypes.PosX);
			patternElement.addParameter(ParameterTypes.PosX, String.valueOf(x));
			patternElement.removeParameter(ParameterTypes.PosY);
			patternElement.addParameter(ParameterTypes.PosY, String.valueOf(y));
			
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[updateBoxPosition]: " + patternElement);
			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
		}
	}
//	/*
//	 * Removes all parameters on the map and adds new ones based on the provided newValues map 
//	 */
//	public void updateElement(String agentId, String patternId, String patternElementId, Map<ParameterTypes, String> newValues){
//		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
//			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(patternElementId);
//			for(ParameterTypes key : newValues.keySet()){
//				patternElement.removeParameters(key);
//				patternElement.addParameter(key, newValues.get(key));
//			}			
//			Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
//					"[updateElement]: " + patternElement, Logger.DEBUG);
//			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
//		}
//	}
	
	/**
	 * Sets or updates the Direction property of a link
	 * @param agentId
	 * @param patternId
	 * @param linkID
	 * @param startParentID
	 * @param endParentID
	 */
	public void setLinkDirection(String agentId, String patternId, String linkID, String startParentID, String endParentID){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(linkID);
			patternElement.removeParameter(ParameterTypes.Direction);
			patternElement.removeParameters(ParameterTypes.Parent);
			patternElement.addParameter(ParameterTypes.Parent, startParentID);
			patternElement.addParameter(ParameterTypes.Parent, endParentID);
			patternElement.addParameter(ParameterTypes.Direction, startParentID + "," + endParentID);
			
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[setLinkDirection]: " + patternElement);
			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
		}
	}
	
	/**
	 * Updates the size of a link
	 * @param agentId
	 * @param patternId
	 * @param linkID
	 * @param height
	 */
	public void updateLinkSize(String agentId, String patternId, int linkID, int height){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(String.valueOf(linkID));
			patternElement.removeParameter(ParameterTypes.Height);
			patternElement.addParameter(ParameterTypes.Height, String.valueOf(height));
			
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[updateLinkSize]: " + patternElement);
			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
		}
	}
	
	/**
	 * Updates the position of a link
	 * @param agentId
	 * @param patternId
	 * @param linkID
	 * @param per
	 */
	public void updateLinkPosition(String agentId, String patternId, int linkID, float per){
		if(agentsMap.containsKey(agentId) && agentsMap.get(agentId).getPattern(patternId) != null){
			PatternElement patternElement = agentsMap.get(agentId).getPattern(patternId).getPatternElement(String.valueOf(linkID));
			patternElement.removeParameter(ParameterTypes.Percent);
			patternElement.addParameter(ParameterTypes.Percent, String.valueOf(per));
			
			FATDebug.print(FATDebug.DEBUG,"[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternServerManager]" +
					"[updateLinkPosition]: " + patternElement);
			PatternReceiver.getInstance().processUpdateElementAction(patternElement);
		}
	}
	
	
	private HashMap<String, String> elemMap = new HashMap<String, String>();
	/**
	 * Adds an agent
	 * This code updates the PatternServerManager with all the info from the agent, i.e. patterns, pattern elements.
	 * This is required for a replay.
	 * It is called during loading, i.e. during the processing of the ListAgentsInfo message
	 * @param agent
	 */
	public void addAgentDescription(AgentDescriptionFE agent){
		addAgent(agent.getAgentID());
		for(AnalysisType pattern:agent.getConfData().getAnalysisTypes()){
			if(pattern instanceof StructureAnalysisType){
				elemMap.clear();
				StructureAnalysisType pat = (StructureAnalysisType) pattern;
				addPattern(agent.getAgentID(), pat.getServiceID().getTypeID());
				
				preprocessing(pat);
				
				for(NodeVariable elem: pat.getPattern().getNodeVars()){
					PatternElement box = createBox(agent.getAgentID(), pat.getServiceID().getTypeID(), elem.getVarID(), FAOntElementIds.BOX, 
							elem.getParameter(ParameterTypes.PosX), 
							elem.getParameter(ParameterTypes.PosY));
					addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), box);
					String childId = String.valueOf(Integer.valueOf(elem.getVarID()) + 1);
					PatternElement child = createTextChild(elem, agent.getAgentID(), pat.getServiceID().getTypeID(), childId, elem.getVarID());
					addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), child);
				}
				
				for (StructuralPattern notPattern : pat.getPattern().getNotPatterns()) {
					for (NodeVariable elem : notPattern.getNodeVars()) {
						PatternElement box = createBox(agent.getAgentID(), pat.getServiceID().getTypeID(), elem.getVarID(), FAOntElementIds.NO_BOX, 
								elem.getParameter(ParameterTypes.PosX), 
								elem.getParameter(ParameterTypes.PosY));
						addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), box);
						String childId = String.valueOf(Integer.valueOf(elem.getVarID()) + 1);
						PatternElement child = createTextChild(elem, agent.getAgentID(), pat.getServiceID().getTypeID(), childId, elem.getVarID());
						addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), child);
					}
					
					//at this time we have all the nodes, thus we start adding the links
					for (LinkVariable elem : notPattern.getLinkVars()) {
						PatternElement link = createLink(agent.getAgentID(), pat.getServiceID().getTypeID(), elem.getVarID(), 
								elem.getDirectionMatters()?FAOntElementIds.NO_DIRECTED_LINK:FAOntElementIds.NO_LINK, 
										elem.getSource().getVarID(), elem.getTarget().getVarID());
						addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), link);
						String childId = String.valueOf(Integer.valueOf(elem.getVarID()) + 1);
						PatternElement child = createTextChild(elem, agent.getAgentID(), pat.getServiceID().getTypeID(), childId, elem.getVarID());
						addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), child);
					}
				}
				
				
				for(LinkVariable elem: pat.getPattern().getLinkVars()){
					FATDebug.print(FATDebug.DEBUG, "agentId:" + agent.getAgentID()+" patternId:"+pat.getServiceID().getTypeID()+" elementId:"+elem.getVarID());
					PatternElement link = createLink(agent.getAgentID(), pat.getServiceID().getTypeID(), elem.getVarID(), 
							elem.getDirectionMatters()?FAOntElementIds.DIRECTED_LINK:FAOntElementIds.LINK, 
									elem.getSource().getVarID(), elem.getTarget().getVarID());
					addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), link);
					String childId = String.valueOf(Integer.valueOf(elem.getVarID()) + 1);
					PatternElement child = createTextChild(elem, agent.getAgentID(), pat.getServiceID().getTypeID(), childId, elem.getVarID());
					addPatternElement(agent.getAgentID(), pat.getServiceID().getTypeID(), child);
				}
			}
		}
	}
	
	private void preprocessing(StructureAnalysisType pat){
		for(NodeVariable elem: pat.getPattern().getNodeVars()){
			elemMap.put(elem.getVarID(), elem.getName());
		}
		
		for (StructuralPattern notPattern : pat.getPattern().getNotPatterns()) {
			for (NodeVariable elem : notPattern.getNodeVars()) {
				elemMap.put(elem.getVarID(), elem.getName());
			}
			
			//at this time we have all the nodes, thus we start adding the links
			for (LinkVariable elem : notPattern.getLinkVars()) {
				elemMap.put(elem.getVarID(), elem.getName());
			}
		}
		
		for(LinkVariable elem: pat.getPattern().getLinkVars()){
			elemMap.put(elem.getVarID(), elem.getName());
		}
	}
	
	private PatternElement createBox(String agentId, String patternId, String id, String elementID, String posX, String posY){	
		String rootElementId = String.valueOf(IdGenerator.getNewRootElementId());
		
		PatternElement patternElement = new PatternElement(id);
		patternElement.addParameter(ParameterTypes.Type, OntologyXML2ObjReader.ELEMENT_TYPE_BOX);
		patternElement.addParameter(ParameterTypes.Id, id);
		patternElement.addParameter(ParameterTypes.ElementId, elementID);
		patternElement.addParameter(ParameterTypes.PatternId, patternId);
		patternElement.addParameter(ParameterTypes.AgentId, agentId);
		patternElement.addParameter(ParameterTypes.RootElementId, rootElementId);
		if(posX != null){
			patternElement.addParameter(ParameterTypes.PosX, String.valueOf(posX));
		}
		if(posY != null){
			patternElement.addParameter(ParameterTypes.PosY, String.valueOf(posY));
		}
		
		return patternElement;
	}
	
	private PatternElement createLink(String agentId, String patternId, String id, String elementID, String startId, String endId){
		String rootElementId = String.valueOf(IdGenerator.getNewRootElementId());
		
		PatternElement link = new PatternElement(id);
		link.addParameter(ParameterTypes.Type, OntologyXML2ObjReader.ELEMENT_TYPE_RELATION);
		link.addParameter(ParameterTypes.Id, String.valueOf(id));
		link.addParameter(ParameterTypes.ElementId, elementID);
		link.addParameter(ParameterTypes.PatternId, patternId);
		link.addParameter(ParameterTypes.AgentId, agentId);
		link.addParameter(ParameterTypes.RootElementId, rootElementId);
		link.addParameter(ParameterTypes.Parent, startId);
		link.addParameter(ParameterTypes.Parent, endId);
		//link.addParameter(ParameterTypes.Direction, startId + "," + endId);
		
		addTolinks2ElementMap(agentId, patternId, startId, id);
		addTolinks2ElementMap(agentId, patternId, endId, id);
		
		return link;
	}
	
	private PatternElement createTextChild(ElementVariable elementVar, String agentId, String patternId, String id, String parentId){	
		
		PatternElement patternElement = new PatternElement(id);
		patternElement.addParameter(ParameterTypes.Type, OntologyXML2ObjReader.ELEMENT_TYPE_TEXT);
		patternElement.addParameter(ParameterTypes.Id, id);
		patternElement.addParameter(ParameterTypes.ElementId, FAOntElementIds.TEXT);
		patternElement.addParameter(ParameterTypes.PatternId, patternId);
		patternElement.addParameter(ParameterTypes.AgentId, agentId);
		patternElement.addParameter(ParameterTypes.Parent, parentId);
		patternElement.addParameter(ParameterTypes.Text, generateText(elementVar));
		
		return patternElement;
	}
	private final int MAX_LINES = 2;
	private final int MAX_CHAR_LINE = 30;
	/**
	 * Generates the text to be displayed in the text element of a box link
	 * the text contains the types constraints.
	 * @param elementVar
	 * @return
	 */
	private String generateText(ElementVariable elementVar){
		StringBuilder value = new StringBuilder();
		List<Comparison> comparisons = elementVar.getComparisons();
		int count = 0;
		//generate text to be displayed
		for(Comparison comparison : comparisons){
			if(comparison.getLeftExpr().getPropId().equals(TypePropDescr.PROP_ID)){
				String tmp = ElementVariableUtil.generatedReadableStrFromComparison(elemMap, comparison);
				if(count < MAX_LINES+1){
					if(count < MAX_LINES){
						value.append(ComparisonUtil.fromOperId2Str(comparison.getOperator().getName()) + " ");
						value.append(tmp.substring(0, (tmp.length() < MAX_CHAR_LINE ? tmp.length() : MAX_CHAR_LINE)));
						if(tmp.length() >= MAX_CHAR_LINE){
							value.append("...\n");
						}else{
							value.append("\n");
						}
					} else{
						value.append("...");
					}
					count++;
				} else{
					break;
				}
			}
		}
		return value.toString();
	}
}
