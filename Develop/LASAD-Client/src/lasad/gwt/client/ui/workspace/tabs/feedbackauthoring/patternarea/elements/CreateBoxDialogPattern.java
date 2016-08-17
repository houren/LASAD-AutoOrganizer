package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialogListener;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

/**
 * Creates the create box dialog displayed on right click on the diagramming area
 * @author Anahuac
 *
 */
public class CreateBoxDialogPattern extends AbstractCreateBoxDialog {


	public CreateBoxDialogPattern(GraphMap map, int posX, int posY,
			TranscriptLinkData tData) {
		super(map, posX, posY, tData);
	}

	public CreateBoxDialogPattern(GraphMap map, int posX, int posY) {
		super(map, posX, posY);
	}

	@Override
	protected AbstractCreateBoxDialogListener createCreateBoxDialogListener(
			GraphMap map, AbstractCreateBoxDialog dialog) {
		return new CreateBoxDialogListenerPattern(map, this);
	}

	@Override
	protected AbstractCreateBoxDialogListener createCreateBoxDialogListener(
			GraphMap map, AbstractCreateBoxDialog dialog,
			TranscriptLinkData tData) {
		return new CreateBoxDialogListenerPattern(map, this, tData);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((PatternMVCViewSession)myMap.getMyViewSession()).getController().getDrawingAreaInfo().getElementsByType(type).values();
//		PatternDrawingAreaSpace patternDrawingAreaSpace = (PatternDrawingAreaSpace) myMap.getMyArgumentMapSpace();
//		return patternDrawingAreaSpace.getPatternInfo().getElementsByType(type).values();
		
	}

}
