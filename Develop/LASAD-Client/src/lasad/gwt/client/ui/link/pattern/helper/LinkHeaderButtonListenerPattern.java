package lasad.gwt.client.ui.link.pattern.helper;

import lasad.gwt.client.ui.common.helper.AbstractConfigWindow;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractDelElementFromLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractLinkHeaderButtonListener;
import lasad.gwt.client.ui.link.pattern.LinkPanelPattern;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractDeleteDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.DeleteDialogPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window.ElementConstraintsWindow;

public class LinkHeaderButtonListenerPattern extends
		AbstractLinkHeaderButtonListener {

	public LinkHeaderButtonListenerPattern(AbstractLinkPanel linkPanelReference) {
		super(linkPanelReference);
	}

	@Override
	protected AbstractDeleteDialog createDeleteDialog(GraphMap targetMap,
			AbstractLinkPanel targetBox, int left, int top) {
		return new DeleteDialogPattern(targetMap, targetBox, left, top);
	}

	@Override
	protected AbstractAddElementToLinkDialog createAddElementToLinkDialog(
			GraphMap map, AbstractLinkPanel linkPanelReference, int posX,
			int posY) {
		return new AddElementToLinkDialogPattern(map, linkPanelReference, posX, posY);
	}

	@Override
	protected AbstractDelElementFromLinkDialog createDelElementFromLinkDialog(
			GraphMap map, AbstractLinkPanel linkPanelReference, int posX,
			int posY) {
		return new DelElementFromLinkDialogPattern(map, linkPanelReference, posX, posY);
	}

	@Override
	protected void onClickSendRemoveUpdate2Sever(String mapId, int linkId) {
		if(((LinkPanelPattern)linkPanelReference).getController() == null){
			((LinkPanelPattern)linkPanelReference).setController(linkPanelReference.getMyLink().getMap());
		}
		String agentId = ((LinkPanelPattern)linkPanelReference).getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
				removeElement(agentId, mapId, String.valueOf(linkId));
	}

	@Override
	protected void onClickSendLinKDirUpdate2Sever(String mapId, int linkId,
			String start, String end) {
		if(((LinkPanelPattern)linkPanelReference).getController() == null){
			((LinkPanelPattern)linkPanelReference).setController(linkPanelReference.getMyLink().getMap());
		}
		String agentId = ((LinkPanelPattern)linkPanelReference).getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
				setLinkDirection(agentId, mapId, String.valueOf(linkId), String.valueOf(start), String.valueOf(end));
		
	}

	@Override
	protected AbstractConfigWindow createConfigLinkWindow(GraphMap map,
			AbstractLinkPanel linkPanelReference, int posX, int posY) {
		return new ElementConstraintsWindow(map, linkPanelReference);
	}

}
