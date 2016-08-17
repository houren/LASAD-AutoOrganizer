package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.CreateBoxDialogPattern;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.CreateBoxLinkDialogPattern;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

/**
 * This class is the pattern diagramming area.
 * @author Anahuac
 *
 */
public class PatternGraphMap extends GraphMap {
	
	protected PatternMVCViewSession myViewSession;

	public PatternGraphMap(GraphMapSpace parentElement) {
		super(parentElement);
	}
	
	@Override
	protected void init() {
		this.myViewSession = (PatternMVCViewSession) myArgumentMapSpace.getSession();
		ID = myViewSession.getController().getDrawingAreaInfo().getMapID();
	}

	@Override
	protected void sendHandleEventResultToServer(String mapID, int cursorID,
			String username, int x, int y) {
		//updateMyCursorPositionNonPersistent, no required

	}

	@Override
	protected AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map,
			int posX, int posY, TranscriptLinkData tData) {
		return new CreateBoxDialogPattern(map, posX, posY, tData);
	}

	@Override
	protected AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map,
			int posX, int posY) {
		return new CreateBoxDialogPattern(map, posX, posY);
	}

	@Override
	protected AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(
			GraphMap map, AbstractBox start, int posX, int posY, int step) {
		return new CreateBoxLinkDialogPattern(map, start, posX, posY, step);
	}

	@Override
	public PatternMVCViewSession getMyViewSession() {
		return myViewSession;
	}

	@Override
	public void setMyViewSession(AbstractMVCViewSession myViewSession) {
		this.myViewSession = (PatternMVCViewSession) myViewSession; 
	}

	@Override
	public void deleteAllFeedbackClusters() {
		//not required
	}

}
