package lasad.gwt.client.ui.workspace.awareness.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.awareness.AbstractAwarenessCursor;
import lasad.gwt.client.ui.workspace.awareness.AbstractAwarenessCursorDraggable;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class AwarenessCursorArgument extends AbstractAwarenessCursor {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public AwarenessCursorArgument(AbstractGraphMap correspondingMap,
			String elementID, boolean group) {
		super(correspondingMap, elementID, group);
	}

	public AwarenessCursorArgument(AbstractGraphMap correspondingMap,
			String elementID, String username) {
		super(correspondingMap, elementID, username);
	}

	@Override
	protected void sendRemoveElementToServer(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, elementID));
	}

	@Override
	protected AbstractAwarenessCursorDraggable createAwarenessCursorDraggable(
			AbstractAwarenessCursor dragComponent, String mapID, int elementID) {
		return new AwarenessCursorDraggableArgument(dragComponent, mapID, elementID);
	}

}
