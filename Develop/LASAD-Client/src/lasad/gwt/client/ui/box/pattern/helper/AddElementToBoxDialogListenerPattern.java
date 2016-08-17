package lasad.gwt.client.ui.box.pattern.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;

public class AddElementToBoxDialogListenerPattern extends
		AbstractAddElementToBoxDialogListener {

	public AddElementToBoxDialogListenerPattern(GraphMap map,
			AbstractBox box, AbstractAddElementToBoxDialog dialog) {
		super(map, box, dialog);
	}

	@Override
	protected void onclickSendUpdate2Sever(String mapID, int boxID,
			String elementType, String elementID) {
		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					addElement(agentId, mapID, String.valueOf(boxID), elementType, elementID);
		
//		public ActionPackage addElement(int mapID, int boxID, String elementType, String elementID) {
//			ActionPackage p = new ActionPackage();
//			Action a = new Action(Commands.CreateElement, Categories.Map);
//			a.addParameter(ParameterTypes.MapId, mapID + "");
//			a.addParameter(ParameterTypes.Type, elementType);
//			a.addParameter(ParameterTypes.Parent, String.valueOf(boxID));
//			a.addParameter(ParameterTypes.ElementId, elementID);
//
//			p.addAction(a);
//			return p;
//		}

	}

}
