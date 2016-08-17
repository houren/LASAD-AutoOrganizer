package lasad.gwt.client.ui.box.pattern;

import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.box.elements.AbstractBoxHeaderElement;
import lasad.gwt.client.ui.box.helper.BoxConnectorElement;
import lasad.gwt.client.ui.box.pattern.elements.BoxHeaderElementPattern;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.PatternGraphMap;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.CreateLinkDialogPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.manager.PatternController;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FATDebug;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

public class BoxPattern extends AbstractBox {
	
	private PatternController myController;

	public BoxPattern(GraphMap map, ElementInfo info,
			SelectionDetailsPanel sdp, boolean isR) {
		super(map, info, sdp, isR);
	}

	@Override
	protected AbstractBoxHeaderElement createBoxHeaderElement(
			AbstractBox correspondingBox, String title, boolean isReplay) {
		return new BoxHeaderElementPattern(correspondingBox, title, isReplay);
	}

	@Override
	protected void sendUpdateTranscriptLinkToServer(String mapID, int elementID,
			TranscriptLinkData tData) {
		// TODO Auto-generated method stub
		//this is not needed

	}

	@Override
	protected void sendCreateTranscriptLinkToServer(String mapID, String boxID,
			TranscriptLinkData tData) {
		// TODO Auto-generated method stub
		//this is not needed
	}

	@Override
	protected AbstractCreateLinkDialog createLinkDialog(
			GraphMap graphMap, AbstractBox b1, AbstractBox b2, int x,
			int y) {
		return new CreateLinkDialogPattern(graphMap, b1, b2,x, y);
	}

	@Override
	protected BoxConnectorElement createBoxConnectorElement(
			AbstractBox abstractBox, String style) {
		return new BoxConnectorElement(this, style);
	}

	@Override
	protected void onClickSendUpdateBoxSizeToServer(String mapID, int boxID,
			int width, int height) {
		if(myController== null)
			setMyControllerFromLASADClient();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
				updateBoxSize(myController.getMapInfo().getAgentId(), mapID, String.valueOf(boxID), width, height);
		
	}
	
	@Override
	public PatternController getMyController() {
		return myController;
	}

	@Override
	public void setMyController(AbstractMVController myController) {
		this.myController = (PatternController) myController;

	}

	@Override
	public void setMyControllerFromLASADClient() {
		//String agentId = ((PatternGraphMap)myMap).getMyViewSession().getController().getMapInfo().getAgentId();
//		myController = FeedbackAuthoringTabContent.getInstance().getPatternServerManager().getController(myMap.getID());
		myController = ((PatternGraphMap)myMap).getMyViewSession().getController();
		if(myController == null)
			FATDebug.print(FATDebug.ERROR, "ERROR [BOXPattern][setMyControllerFromLASADClient] myController is null");
	}

	@Override
	protected SelectionDetailsPanel createSelectionDetailsPanel(
			GraphMap mymap) {
		return new SelectionDetailsPanel(myMap);
	}

	@Override
	protected void onClickSendUpdatePositionToServer(String mapID, int boxID,
			int x, int y) {
		if(myController== null)
			setMyControllerFromLASADClient();
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
					updateBoxPosition(myController.getMapInfo().getAgentId(), mapID, String.valueOf(boxID), x, y);
		
	}

}
