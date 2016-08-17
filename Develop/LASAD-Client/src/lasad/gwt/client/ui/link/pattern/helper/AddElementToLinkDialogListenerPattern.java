package lasad.gwt.client.ui.link.pattern.helper;

import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialog;
import lasad.gwt.client.ui.link.helper.AbstractAddElementToLinkDialogListener;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;

public class AddElementToLinkDialogListenerPattern extends AbstractAddElementToLinkDialogListener {

	public AddElementToLinkDialogListenerPattern(GraphMap map,
			AbstractLinkPanel linkPanelReference,
			AbstractAddElementToLinkDialog dialog) {
		super(map, linkPanelReference, dialog);
	}

	@Override
	protected void onClickSendUpdateToServer(String mapId, int linkId,
			String elementType, String elementId) {
		String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					addElement(agentId, mapId, String.valueOf(linkId), elementType, elementId);
		
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
