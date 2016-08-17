package lasad.gwt.client.ui.box.pattern.helper;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;

public class DelElementFromBoxDialogListenerPattern extends
		AbstractDelElementFromBoxDialogListener {

	public DelElementFromBoxDialogListenerPattern(GraphMap map,
			AbstractBox box,
			AbstractDelElementFromBoxDialog DelElementfromBoxDialog) {
		super(map, box, DelElementfromBoxDialog);
	}

	@Override
	protected void onClickSendUpdate2Sever(String mapId, int elementId) {
		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					removeElement(agentId, mapId, String.valueOf(elementId));

//		public ActionPackage removeElement(int mapID, int id) {
//			ActionPackage p = new ActionPackage();
//			Action a = new Action(Commands.DeleteElement, Categories.Map);
//			a.addParameter(ParameterTypes.MapId, String.valueOf(mapID));
//			a.addParameter(ParameterTypes.Id, String.valueOf(id));
//
//			p.addAction(a);
//			return p;
//		}
		
	}

}
