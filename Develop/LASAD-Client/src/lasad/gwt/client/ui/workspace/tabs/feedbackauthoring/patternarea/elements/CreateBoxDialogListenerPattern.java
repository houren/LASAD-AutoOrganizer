package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialogListener;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

/**
 * Handles events on the create box dialog displayed on right click on the diagramming area
 * @author Anahuac
 *
 */
public class CreateBoxDialogListenerPattern extends
		AbstractCreateBoxDialogListener {

	public CreateBoxDialogListenerPattern(GraphMap map,
			AbstractCreateBoxDialog dialog, TranscriptLinkData tData) {
		super(map, dialog, tData);
	}

	public CreateBoxDialogListenerPattern(GraphMap map,
			AbstractCreateBoxDialog dialog) {
		super(map, dialog);
	}

	@Override
	protected void onClickSendCreateBoxUpdateToServer(
			TranscriptLinkData myTData, boolean createTranscriptLink,
			ElementInfo info, String mapID, int posX, int posY) {
		// TODO Auto-generated method stub
		
		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
			createBoxWithElements(info, agentId,mapID, posX, posY);

//		public ActionPackage createBoxWithElements(ElementInfo currentElement, int mapID, int posX, int posY) {
//			ActionPackage p = new ActionPackage();
//			Action action = createBox(currentElement, mapID, posX, posY);
//			p.addAction(action);
//
//			Vector<Action> vAction = createBoxElementsAction(currentElement, mapID);
//			if (vAction.size() > 0) {
//				for (Action a : vAction) {
//					p.addAction(a);
//				}
//			}
//			return p;
//		}
		
	}

//	public Action createBox(ElementInfo config, int mapID, int posX, int posY) {
//		Action a = new Action(Commands.CreateElement, Categories.Map);
//		a.addParameter(ParameterTypes.Type, "box");
//		a.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
//		a.addParameter(ParameterTypes.ElementId, config.getElementID());
//		a.addParameter(ParameterTypes.PosX, String.valueOf(posX));
//		a.addParameter(ParameterTypes.PosY, String.valueOf(posY));
//
//		return a;
//	}
//	
//	private Vector<Action> createBoxElementsAction(ElementInfo currentElement, int mapID) {
//
//		Vector<Action> allActions = new Vector<Action>();
//
//		for (ElementInfo childElement : currentElement.getChildElements().values()) {
//			Logger.log("Creating action for ChildElement: " + childElement.getElementType(), Logger.DEBUG_DETAILS);
//			for (int i = 0; i < childElement.getQuantity(); i++) {
//				Action a = new Action(Commands.CreateElement, Categories.Map);
//				a.addParameter(ParameterTypes.Type, childElement.getElementType());
//				a.addParameter(ParameterTypes.MapId, mapID + "");
//				a.addParameter(ParameterTypes.Parent, "LAST-ID");
//				a.addParameter(ParameterTypes.ElementId, String.valueOf(childElement.getElementID()));
//				a.addParameter(ParameterTypes.UserName, LASAD_Client.getInstance().getUsername());
//
//				if (childElement.getElementType().equals("rating")) {
//					a.addParameter(ParameterTypes.Score, childElement.getElementOption(ParameterTypes.Score));
//				} else if (childElement.getElementType().equals("awareness")) {
//					a.addParameter(ParameterTypes.Time, "CURRENT-TIME"); // The time will be
//
//					// filled in by the
//					// server
//				}
//
//				if (a != null) {
//					allActions.add(a);
//				}
//			}
//		}
//		return allActions;
//	}
	
	
	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((PatternMVCViewSession)myMap.getMyViewSession()).getController().getDrawingAreaInfo().getElementsByType(type).values();
		
//		PatternDrawingAreaSpace patternDrawingAreaSpace = (PatternDrawingAreaSpace) myMap.getMyArgumentMapSpace();
//		return patternDrawingAreaSpace.getPatternInfo().getElementsByType(type).values();
	}

}
