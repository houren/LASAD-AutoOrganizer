package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialogListener;

/**
 * Creates the create box and link dialog displayed on click and drag of a box's knob on the diagramming area
 * @author Anahuac
 *
 */
public class CreateBoxLinkDialogPattern extends AbstractCreateBoxLinkDialog {

	public CreateBoxLinkDialogPattern(GraphMap map, AbstractBox start,
			int posX, int posY, int step, ElementInfo boxConfig) {
		super(map, start, posX, posY, step, boxConfig);
	}

	public CreateBoxLinkDialogPattern(GraphMap map, AbstractBox start,
			int posX, int posY, int step) {
		super(map, start, posX, posY, step);
	}

	@Override
	protected AbstractCreateBoxLinkDialogListener createAbstractCreateBoxLinkDialogListener(
			GraphMap map, AbstractCreateBoxLinkDialog dialog) {
		return new CreateBoxLinkDialogListenerPattern(map, dialog);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((PatternMVCViewSession)myMap.getMyViewSession()).getController().getDrawingAreaInfo().getElementsByType(type).values();
//		PatternDrawingAreaSpace patternDrawingAreaSpace = (PatternDrawingAreaSpace) myMap.getMyArgumentMapSpace();
//		return patternDrawingAreaSpace.getPatternInfo().getElementsByType(type).values();
	}

}
