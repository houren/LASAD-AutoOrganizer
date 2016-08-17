package lasad.gwt.client.ui.link.pattern;

import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractLinkHeaderButtonListener;
import lasad.gwt.client.ui.link.pattern.helper.LinkHeaderButtonListenerPattern;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.CreateLinkDialogPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ElementVariableUtil;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.shared.communication.objects.parameters.ParameterTypes;

/**
 * Documentation!?
 */

public class LinkPanelPattern extends AbstractLinkPanel {
	
	PatternController myController;

	public LinkPanelPattern(AbstractLink myLink, String details, boolean isR) {
		super(myLink, details, isR);
	}

	@Override
	protected AbstractLinkHeaderButtonListener createLinkHeaderButtonListener(
			AbstractLinkPanel linkPanel) {
		return new LinkHeaderButtonListenerPattern(linkPanel);
	}

	@Override
	protected AbstractCreateLinkDialog createCreateLinkDialog(GraphMap map,
			AbstractBox start, AbstractLink end, int posX, int posY) {
		return new CreateLinkDialogPattern(map, start, end, posX, posY);
	}

	@Override
	protected void sendUpdateLinkSize(String mapID, int linkID, int height) {
		if(myController == null){
			myController = (PatternController) getMVCViewSession().getController();
			if(myController == null){
				FATDebug.print(FATDebug.ERROR, "[LinkPanelPattern][sendUpdateLinkSize]myController is null");
				return;
			}
		}
//		if(myController == null){
//			FATDebug.print(FATDebug.ERROR, "[LinkPanelPattern][sendUpdateLinkSize]myController is null");
//			return;
//		}
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					updateLinkSize(myController.getMapInfo().getAgentId(), mapID, linkID, height);
	}
	
	@Override
	protected void sendUpdateLinkPosition(String mapID, int linkID, float per) {
		if(myController == null){
			myController = (PatternController) getMVCViewSession().getController();
			if(myController == null){
				FATDebug.print(FATDebug.ERROR, "[LinkPanelPattern][sendUpdateLinkPosition]myController is null");
				return;
			}
		}
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					updateLinkPosition(myController.getMapInfo().getAgentId(), mapID, linkID, per);
	}

	@Override
	public PatternController getController() {
		return myController;
	}

	@Override
	public void setController(GraphMap myMap) {
//		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
//		myController = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(agentId, myMap.getID());
		myController = ((PatternGraphMap)myMap).getMyViewSession().getController();
		
	}
	
	@Override
	public void setRootElementID(String rootElementID) {
		if (details) {
			//headerText.setInnerHTML(rootElementID + "&nbsp;&middot;&nbsp;<b>" + myLink.getElementInfo().getElementOption(ParameterTypes.Heading) + "</b>");
			String header = ElementVariableUtil.createHeaderForBoxLink(myLink.getElementInfo().getElementOption(ParameterTypes.Heading), rootElementID);
			headerText.setInnerHTML("<b>" + header + "</b>");
			//headerText.setInnerHTML("<b>" + myLink.getElementInfo().getElementOption(ParameterTypes.Heading)  + "-" + rootElementID + "</b>");
		}
	}

}
