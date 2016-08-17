package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.pattern.UnspecifiedElementModelPattern;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * Received the requests from {@link PatternServerManager}
 * @author Anahuac
 *
 */
public class PatternReceiver {
	
	private static PatternReceiver myInstance = null;

	public static PatternReceiver getInstance() {
		if (myInstance == null) {
			myInstance = new PatternReceiver();
		}
		return myInstance;
	}

	private PatternReceiver() {
	}

	public void processCreateElementAction(PatternElement action){
		
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.patternReceiver][processCreateElementAction]", Logger.DEBUG);
		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
		
		String agentId = action.getParameterValue(ParameterTypes.AgentId);
		String patternId = action.getParameterValue(ParameterTypes.PatternId);
		PatternController controller = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, patternId);
		
		
		String elementType = action.getParameterValue(ParameterTypes.Type);
		String elementIDString = action.getParameterValue(ParameterTypes.Id);
		int id = -1;
		if (elementIDString != null) {
			id = Integer.parseInt(elementIDString);
		}
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver] Create Model: " + id + ", Type: " + elementType,
				Logger.DEBUG);
		AbstractUnspecifiedElementModel elementModel = new UnspecifiedElementModelPattern(id, elementType);
		if (action.getParameterValue(ParameterTypes.ReplayTime) != null) {
			elementModel.setIsReplay(true);
		}
		// Needed to enable the add and del buttons in box header
		if (action.getParameterValue(ParameterTypes.ElementId) != null) {
			elementModel.setElementId(action.getParameterValue(ParameterTypes.ElementId));
		}

		// Add more specific data to the model
		for (Parameter param : action.getParameters()) {
			if (param.getType() != null && !param.getType().equals(ParameterTypes.Parent)
					&& !param.getType().equals(ParameterTypes.HighlightElementId.toString())) {
				elementModel.setValue(param.getType(), param.getValue());
			}
		}

		// Work on parent relations
		if (action.getParameterValues(ParameterTypes.Parent) != null) {
			for (String parentID : action.getParameterValues(ParameterTypes.Parent)) {
				controller.setParent(elementModel, controller.getElement(Integer.parseInt(parentID)));

				Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver] Added ParentElement: " + parentID, Logger.DEBUG);
			}
		}

		// Now Register new Element to the Model
		controller.addElementModel(elementModel);
		
	}
	public void processUpdateElementAction(PatternElement action){
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processUpdateElementAction]", Logger.DEBUG);
		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
		
		String agentId = action.getParameterValue(ParameterTypes.AgentId);
		String patternId = action.getParameterValue(ParameterTypes.PatternId);
		PatternController controller = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, patternId);
		controller.updateElement(Integer.parseInt(action.getParameterValue(ParameterTypes.Id)), action.getParameters());
	}
	public void processUpdateCursorPositionAction(PatternElement action){
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processUpdateCursorPositionAction]", Logger.DEBUG);
		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
		
		String agentId = action.getParameterValue(ParameterTypes.AgentId);
		String patternId = action.getParameterValue(ParameterTypes.PatternId);
		PatternController controller = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, patternId);
		controller.updateElement(Integer.parseInt(action.getParameterValue(ParameterTypes.Id)), action.getParameters());
	}
	
	public void processDeleteElementAction(PatternElement action){
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processDeleteElementAction]", Logger.DEBUG);
		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
		
		String agentId = action.getParameterValue(ParameterTypes.AgentId);
		String patternId = action.getParameterValue(ParameterTypes.PatternId);
		PatternController controller = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, patternId);
		int id = Integer.parseInt(action.getParameterValue(ParameterTypes.Id));
		if (controller.getElement(id) != null) {
			try {
				controller.deleteElement(id,
						action.getParameterValue(ParameterTypes.UserName));
			} catch (Exception e) {
				e.printStackTrace();
				Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processDeleteElementAction] Can not delete element, because ID is no int!", Logger.DEBUG_ERRORS);
			}

		} else {
			// This occurs when another user deletes his/her feedback
			Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processDeleteElementAction] Cannot delete, because element ID is unknown.", Logger.DEBUG_ERRORS);
		}
	}
	public void processErrorAction(PatternElement action){
		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternReceiver][processErrorAction]", Logger.DEBUG);
		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
		
		String patternId = action.getParameterValue(ParameterTypes.PatternId);
		LASADInfo.display("Error", action.getParameterValue(ParameterTypes.Message) + patternId);
	}
	
//	public void processDrawingAreaAction(PatternElement action){
//		Logger.log("[lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.patternReceiver][processDrawingAreaAction]", Logger.DEBUG);
//		Logger.log("[Details] " + action.toString(), Logger.DEBUG_DETAILS);
//		String patternId = action.getParameterValue(ParameterTypes.PatternId);
//	}
}
