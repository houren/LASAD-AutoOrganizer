package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager;

import java.util.Vector;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.model.events.LasadEvent;
import lasad.gwt.client.model.pattern.PatternGraphMapInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;
import lasad.shared.dfki.meta.agents.analysis.structure.StructuralAnalysisTypeManipulator;
import lasad.shared.dfki.meta.agents.analysis.structure.model.ElementVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.LinkVariable;
import lasad.shared.dfki.meta.agents.analysis.structure.model.NodeVariable;


/**
 * This class represents the controller in the MVC pattern.
 */
public class PatternController extends AbstractMVController{
	PatternGraphMapInfo graphMapInfo;
	StructuralAnalysisTypeManipulator patManipulator;
	
	//private Vector<PatternMVCViewSession> registeredViewSessions = new Vector<PatternMVCViewSession>();
	private PatternMVCViewSession viewSession;
	
	public PatternController(GraphMapInfo mapInfo) {
		super(mapInfo);
		this.graphMapInfo = (PatternGraphMapInfo)mapInfo;
	}
	
//	public PatternController(String patternId, PatternDrawingAreaInfo drawingAreaInfo) {
//		this.patternId = patternId;
//		this.drawingAreaInfo = drawingAreaInfo;
//	}

	/**
	 * Handles the add element event to the pattern diagramming area.
	 */
	@Override
	public PatternController addElementModel(AbstractUnspecifiedElementModel model) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController][addElementModel]: " + model, Logger.DEBUG);
		
		elementModelMapping.put(model.getId(), model);
		viewSession.registerNewModel(model);
		addElementToPattern(model);
		
		return this;
	}
	
	private void addElementToPattern(AbstractUnspecifiedElementModel model){
		if (model.getType().equalsIgnoreCase("box") || model.getType().equalsIgnoreCase("emptybox")) {
			NodeVariable nodeVar = new NodeVariable(Integer.toString(model.getId()));
			nodeVar.setName(generateElemVarName(model));
			addCoordinates(model, nodeVar);
			addLabel(model, nodeVar);
			if(model.getElementid().equalsIgnoreCase("box")){
				patManipulator.addNodeVar(nodeVar);
			} else if(model.getElementid().equalsIgnoreCase("nobox")){
				patManipulator.addNotNodeVar(nodeVar);
			} else{
				FATDebug.print(FATDebug.ERROR, "[PatternController][addElementToPattern] unknown type:" + model);
			}
		} else if (model.getType().equalsIgnoreCase("relation") || model.getType().equalsIgnoreCase("emptyrelation")) {
			LinkVariable linkVar = new LinkVariable(Integer.toString(model.getId()));
			linkVar.setName(generateElemVarName(model));
//			//Direction : 1,3
//			String direction = model.getValue(ParameterTypes.Direction);
//			String[] stNodes = direction.split(",");
			if(model.getElementid().equalsIgnoreCase("directedlink")
					|| model.getElementid().equalsIgnoreCase("nodirectedlink")){
				linkVar.setDirectionMatters(true);
			}
			
			linkVar.setSource(ElementVariableUtil.getElementVariable(patManipulator, String.valueOf(model.getParents().get(0).getId())));
			linkVar.setTarget(ElementVariableUtil.getElementVariable(patManipulator, String.valueOf(model.getParents().get(1).getId())));
			if(model.getElementid().equalsIgnoreCase("link") 
					|| model.getElementid().equalsIgnoreCase("directedlink")){
				patManipulator.addLinkVar(linkVar);
			} else if(model.getElementid().equalsIgnoreCase("nolink") 
					|| model.getElementid().equalsIgnoreCase("nodirectedlink")){
				patManipulator.addNotLinkVar(linkVar);
			} else{
				FATDebug.print(FATDebug.ERROR, "[PatternController][addElementToPattern] unknown type:" + model);
			}
		}

	}
	
	private void addCoordinates(AbstractUnspecifiedElementModel model, NodeVariable elem){
		String posX = model.getValue(ParameterTypes.PosX);
		String posY = model.getValue(ParameterTypes.PosY);
		if(posX != null){
			elem.addParameter(ParameterTypes.PosX, posX);
		}
		if(posY != null){
			elem.addParameter(ParameterTypes.PosY, posY);
		}
	}
	private void addLabel(AbstractUnspecifiedElementModel model, NodeVariable elem){
		ElementInfo elemInfo = getDrawingAreaInfo().getElementsByType(model.getType()).get(model.getValue(ParameterTypes.ElementId));
		String title = elemInfo.getElementOption(ParameterTypes.Heading);
		if(title != null){
			elem.addParameter(ParameterTypes.Label, title);
		}
	}
	private void updateParameter(NodeVariable elemVar, ParameterTypes paramType, String value){
		elemVar.addParameter(paramType, value);
	}
	
	private String generateElemVarName(AbstractUnspecifiedElementModel model){
		ElementInfo elemInfo = getDrawingAreaInfo().getElementsByType(model.getType()).get(model.getValue(ParameterTypes.ElementId));
		String title = elemInfo.getElementOption(ParameterTypes.Heading);
		return new String(title + "-" + model.getValue(ParameterTypes.RootElementId));
	}
	
	/**
	 * Updates values on the model
	 * 
	 * Note: To ensure proper highlighting its necessary to make sure that the USERNAME parameter is processed first
	 * 		 We don't care about this
	 * 
	 * @param id
	 * @param values
	 * @return Boolean
	 */
	@Override
	public boolean updateElement(int id, Vector<Parameter> values) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController][updateElement]: " + id, Logger.DEBUG);
//		for(Parameter param:values){
//			Logger.log("param: " + param.getType() + " : " + param.getValue() , Logger.DEBUG);
//		}
		
		// Check if Model exists
		if (elementModelMapping.containsKey(id)) {
			// Model exists
			AbstractUnspecifiedElementModel model = elementModelMapping.get(id);
			ElementVariable elemVar = ElementVariableUtil.getElementVariable(patManipulator, String.valueOf(id));
			
			// normal parameter update
			for (Parameter param : values) {

				if (model.setValue(param.getType(), param.getValue())) {
					// returns true if the model has been changed
					// Value has changed, updated subscribed Sessions
					viewSession.changeValue(model, param.getType());
					if(param.getType() == ParameterTypes.Direction
							&& (model.getElementid().equalsIgnoreCase("directedlink")
									|| model.getElementid().equalsIgnoreCase("nodirectedlink"))) {
						//Direction : 1,3
						//String direction = param.getValue();
						//String[] stNodes = direction.split(",");
						LinkVariable linkVar = (LinkVariable) ElementVariableUtil.getElementVariable(patManipulator, Integer.toString(id));
						//Do the swap
						ElementVariable sourceVar = linkVar.getSource();
				    	linkVar.setSource(linkVar.getTarget());
				    	linkVar.setTarget(sourceVar);
					}
					//update coordinate only if it is a node
					if((param.getType() == ParameterTypes.PosX
							|| param.getType() == ParameterTypes.PosY) 
							&& elemVar instanceof NodeVariable){
						updateParameter((NodeVariable)elemVar, param.getType(), param.getValue());
					}
				}
				// Else, nothing to do
			}
			return true;
			
		} else {
			Logger.log("Tried to update non existing element with ID "+id, Logger.DEBUG_ERRORS);
			return false;
		}
	}
	
	/**
	 * Handles the delete element event on the pattern diagramming area.
	 */
	public void deleteElement(int id, String username) {
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController][deleteElement]: " + id + " username:" + username, Logger.DEBUG);
		// Get Model
		AbstractUnspecifiedElementModel model = elementModelMapping.get(id);
		deleteElementFromPattern(model);
		deleteElement(model);
	}
	
	private void deleteElementFromPattern(AbstractUnspecifiedElementModel model){
		if (model.getType().equalsIgnoreCase("box") || model.getType().equalsIgnoreCase("emptybox")) {
			NodeVariable nodeVar = new NodeVariable(Integer.toString(model.getId()));
			if(model.getElementid().equalsIgnoreCase("box")){
				patManipulator.removeNodeVar(nodeVar);
			} else if(model.getElementid().equalsIgnoreCase("nobox")){
				patManipulator.removeNotNodeVar(nodeVar);
			} else{
				FATDebug.print(FATDebug.ERROR, "[PatternController][deleteElementFromoPattern] unknown type:" + model);
			}
		} else if (model.getType().equalsIgnoreCase("relation") || model.getType().equalsIgnoreCase("emptyrelation")) {
			LinkVariable linkVar = new LinkVariable(Integer.toString(model.getId()));
			if(model.getElementid().equalsIgnoreCase("link") 
					|| model.getElementid().equalsIgnoreCase("directedlink")){
				patManipulator.removeLinkVar(linkVar);
			} else if(model.getElementid().equalsIgnoreCase("nolink") 
					|| model.getElementid().equalsIgnoreCase("nodirectedlink")){
				patManipulator.removeNotLinkVar(linkVar);
			} else{
				FATDebug.print(FATDebug.ERROR, "[PatternController][deleteElementFromoPattern] unknown type:" + model);
			}
		}

	}

	/**
	 * Handles the delete element event on the pattern diagramming area.
	 */
	@Override
	public void deleteElement(AbstractUnspecifiedElementModel model) {
		viewSession.unregisterModel(model);

			// Remove from Childs
			while (model.getChildren().size() > 0) {
				removeParent(model, model.getChildren().firstElement());

			}
			// Remove from Parents
			while (model.getParents().size() > 0) {
				model.getParents().firstElement().removeChild(model);
			}

			elementModelMapping.remove(model.getId());
			// Delete the model
//			modelMapped2ViewSession.remove(model);

			model = null;
//		} else {
//			// Nothing to Do, the model was already deleted in the past
//		}
	}
	
	public void setParent(AbstractUnspecifiedElementModel child, AbstractUnspecifiedElementModel parent) {
		
		parent.addChild(child);
	}

	public void removeParent(AbstractUnspecifiedElementModel parent, AbstractUnspecifiedElementModel child) {
		// Publish this action to the sessions
//		for (MVCViewSession session : registeredViewSessions) {
//			Vector<AbstractUnspecifiedElementModel> unsubscribedModels = session.unregisterParent(parent, child);
//			if (unsubscribedModels.size() > 0) {
//				for (AbstractUnspecifiedElementModel model : unsubscribedModels) {
//					modelMapped2ViewSession.get(model).remove(session);
//				}
//			}
//		}
		parent.removeChild(child);
	}

	public PatternGraphMapInfo getDrawingAreaInfo() {
		return graphMapInfo;
	}
	
	public void registerViewSession(AbstractMVCViewSession session) {
		// Add Session
		//this.registeredViewSessions.add(session);
		viewSession = (PatternMVCViewSession)session;
	}
//	public PatternDrawingAreaMVCViewSession getViewSession(){
//		return (PatternDrawingAreaMVCViewSession) viewSession;
//	}

	@Override
	public String getMapID() {
		return graphMapInfo.getMapID();
	}

	@Override
	public PatternGraphMapInfo getMapInfo() {
		return graphMapInfo;
	}

	@Override
	public void setMapInfo(GraphMapInfo mapInfo) {
		this.graphMapInfo = (PatternGraphMapInfo)mapInfo;
	}

	@Override
	public void fireLasadEvent(LasadEvent event) {
		
	}

	public StructuralAnalysisTypeManipulator getPatManipulator() {
		return patManipulator;
	}

	public void setPatManipulator(StructuralAnalysisTypeManipulator patManipulator) {
		this.patManipulator = patManipulator;
	}
	
	
}
