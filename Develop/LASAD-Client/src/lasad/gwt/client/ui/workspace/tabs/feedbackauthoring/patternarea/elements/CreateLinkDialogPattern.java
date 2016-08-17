package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateLinkDialogListener;

/**
 * Creates the create link dialog displayed on click and drag of a box's knob on the diagramming area
 * @author Anahuac
 *
 */
public class CreateLinkDialogPattern extends AbstractCreateLinkDialog {

	public CreateLinkDialogPattern(GraphMap map, AbstractBox start,
			AbstractBox end, int posX, int posY) {
		super(map, start, end, posX, posY);
	}

	public CreateLinkDialogPattern(GraphMap map, AbstractBox start,
			AbstractBox end) {
		super(map, start, end);
	}

	public CreateLinkDialogPattern(GraphMap map, AbstractBox start,
			AbstractLink end, int posX, int posY) {
		super(map, start, end, posX, posY);
	}

	@Override
	protected AbstractCreateLinkDialogListener createCreateLinkDialogListener(
			GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1,
			AbstractBox b2) {
		return new CreateLinkDialogListenerPattern(map, dialogue, b1, b2);
	}

	@Override
	protected AbstractCreateLinkDialogListener createCreateLinkDialogListener(
			GraphMap map, AbstractCreateLinkDialog dialogue, AbstractBox b1,
			AbstractLink l2) {
		return new CreateLinkDialogListenerPattern(map, dialogue, b1, l2);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((PatternMVCViewSession)myMap.getMyViewSession()).getController().getDrawingAreaInfo().getElementsByType(type).values();
//		PatternDrawingAreaSpace patternDrawingAreaSpace = (PatternDrawingAreaSpace) myMap.getMyArgumentMapSpace();
//		return patternDrawingAreaSpace.getPatternInfo().getElementsByType(type).values();
	}

}
