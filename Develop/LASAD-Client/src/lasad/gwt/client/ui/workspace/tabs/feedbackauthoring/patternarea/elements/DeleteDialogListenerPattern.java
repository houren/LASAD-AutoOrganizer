package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialogListener;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;

/*
 * Handles events on the delete confirmation dialog of a box or link
 */
public class DeleteDialogListenerPattern extends AbstractDeleteDialogListener {

	public DeleteDialogListenerPattern(GraphMap myMap,
			AbstractDeleteDialog dialog, AbstractBox boxTarget) {
		super(myMap, dialog, boxTarget);
	}

	public DeleteDialogListenerPattern(GraphMap myMap,
			AbstractDeleteDialog dialog, AbstractLinkPanel linkTarget) {
		super(myMap, dialog, linkTarget);
	}

	@Override
	protected void onClickSendUpdateToServer(String mapId, int elementId) {
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
