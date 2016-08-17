package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialogListener;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

public class CreateBoxDialogArgument extends AbstractCreateBoxDialog {

	public CreateBoxDialogArgument(GraphMap map, int posX, int posY) {
		super(map, posX, posY);
	}
	
	public CreateBoxDialogArgument(GraphMap map, int posX, int posY,
			TranscriptLinkData tData) {
		super(map, posX, posY, tData);
	}

	@Override
	protected AbstractCreateBoxDialogListener createCreateBoxDialogListener(
			GraphMap map, AbstractCreateBoxDialog dialog) {
		return new CreateBoxDialogListenerArgument(map, this);
	}

	@Override
	protected AbstractCreateBoxDialogListener createCreateBoxDialogListener(
			GraphMap map, AbstractCreateBoxDialog dialog,
			TranscriptLinkData tData) {
		return new CreateBoxDialogListenerArgument(map, this, tData);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}

}
