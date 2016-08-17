package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.elements;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternElement;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * This class represents a pattern, it stores the boxes and links created in the pattern workspace or
 * diagramming area.
 * 
 * @author anahuacv
 * 
 */
public class PatternModel {
	private String id;
	private PatternController controller;
	private Map<String, PatternElement> elementsMap = new LinkedHashMap<String, PatternElement>();
	//box|link -> list of children
	private Map<String, Vector<String>> child2ParentMap = new LinkedHashMap<String, Vector<String>>();
	//box -> list of links. Used to track the links related to a box
	private Map<String, Vector<String>> links2ElementMap = new LinkedHashMap<String, Vector<String>>();

	public PatternModel(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public PatternController getController() {
		return controller;
	}
	public void setController(PatternController controller) {
		this.controller = controller;
	}
	
	public void addPatternElement(PatternElement patternElement){
		if(!elementsMap.containsKey(patternElement.getId())){
			elementsMap.put(patternElement.getId(), patternElement);
		}
		
		if(patternElement.getParameterValue(ParameterTypes.RootElementId) != null){
			child2ParentMap.put(patternElement.getId(), new Vector<String>());
		} else{
			String parentId = patternElement.getParameterValue(ParameterTypes.Parent);
			Vector<String> childrenList = child2ParentMap.get(parentId);
			childrenList.add(patternElement.getId());
		}
		
	}
	
	public PatternElement deletePatternElement(String patternElementId){
		return elementsMap.remove(patternElementId);
	}
	/**
	 * Deletes the children of an element (box/link)
	 * @param patternElementId
	 * @return Vector<PatternElement> containing the children of the deleted PatternElement
	 */
	public Vector<PatternElement> deletePatternElementChildren(String patternElementId){
		Vector<PatternElement> childrenDeleted = new Vector<PatternElement>();
		Vector<String> childrenList = child2ParentMap.remove(patternElementId);
		for(String childId: childrenList){
			PatternElement patternElement = deletePatternElement(childId);
			if(patternElement != null)
				childrenDeleted.add(patternElement);
		}
		return childrenDeleted;
	}
	/**
	 * Updates patternElementId with the given value
	 * @param patternElementId
	 * @param type
	 * @param newValue
	 * @return
	 */
	public PatternElement updatePatternElement(String patternElementId, String type, String newValue){
		PatternElement patternElement = null;
		if(elementsMap.containsKey(patternElementId)){
			patternElement = elementsMap.get(patternElementId);
			if(!patternElement.replaceParameter(ParameterTypes.valueOf(type), newValue)){
				//if nothing was update it does not exist, add it!
				patternElement.addParameter(ParameterTypes.valueOf(type), newValue);
			}
		}
		return patternElement;
	}
	
	public PatternElement getPatternElement(String patternElementId){
		return elementsMap.get(patternElementId);
	}
	public Vector<PatternElement> getPatternElements(){
		return new Vector<PatternElement>(elementsMap.values());
	}
	public void addTolinks2ElementMap(String elementId, String linkId){
		Vector<String> linkList = links2ElementMap.get(elementId);
		if (linkList == null){
			linkList = new Vector<String>();
			links2ElementMap.put(elementId, linkList);
		}
		if(!linkList.contains(linkId)){
			linkList.add(linkId);
		}
	}

	public void removeFromlinks2ElementMap(String elementId, String linkId){
		Vector<String> linkList = links2ElementMap.get(elementId);
		if (linkList != null){
			linkList.remove(linkId);
		}
	}
	public Vector<String> getlinks2ElementMap(String elementId){
		 return links2ElementMap.get(elementId);
	}
	public List<String> getParentList(){
		 return new Vector<String>(child2ParentMap.keySet());
	}
	public List<String> getChildrenOfParent(String elementId){
		 return new Vector<String>(child2ParentMap.get(elementId));
	}
	
	
}
