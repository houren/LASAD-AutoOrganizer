package lasad.gwt.client.ui.box.pattern.helper;

import lasad.gwt.client.model.pattern.PatternGraphMapInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.helper.AbstractAddElementToBoxDialog;
import lasad.gwt.client.ui.box.helper.AbstractBoxHeaderButtonListener;
import lasad.gwt.client.ui.box.helper.AbstractDelElementFromBoxDialog;
import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.DeleteDialogPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.ElementConstraintsWindow;

public class BoxHeaderButtonListenerPattern extends
		AbstractBoxHeaderButtonListener {

	public BoxHeaderButtonListenerPattern(AbstractBox boxReference) {
		super(boxReference);
	}

	@Override
	protected AbstractDeleteDialog createDeleteDialog(GraphMap targetMap,
			AbstractBox targetBox, int left, int top) {
		return new DeleteDialogPattern(targetMap, targetBox, left, top);
	}

	@Override
	protected AbstractAddElementToBoxDialog createAddElementToBoxDialog(
			GraphMap map, AbstractBox box, int posX, int posY) {
		return new AddElementToBoxDialogPattern(map, box, posX, posY);
	}

	@Override
	protected AbstractDelElementFromBoxDialog createDelElementFromBoxDialog(
			GraphMap map, AbstractBox box, int posX, int posY) {
		return new DelElementFromBoxDialogPattern(map, box, posX, posY);
	}

	@Override
	protected void onClickServerUpdate(String mapID, int boxId) {
//		String agentId = ((PatternGraphMap)map).getMyViewSession().getController().getMapInfo().getAgentId();
//		((PatternGraphMapInfo)myMapInfo).getAgentId();
		String agentId = ((PatternGraphMapInfo)boxReference.getMyController().getMapInfo()).getAgentId();
		
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					removeElement(agentId, mapID, String.valueOf(boxId));

	}

	@Override
	protected AbstractConfigWindow createConfigBoxWindow(GraphMap map,
			AbstractBox box, int posX, int posY) {
		return new ElementConstraintsWindow(map, box);
	}

}
