package lasad.gwt.client.ui.workspace.awareness.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.awareness.AbstractAwarenessCursor;
import lasad.gwt.client.ui.workspace.awareness.AbstractAwarenessCursorDraggable;

public class AwarenessCursorDraggableArgument extends AbstractAwarenessCursorDraggable {

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();
	
	public AwarenessCursorDraggableArgument(
			AbstractAwarenessCursor dragComponent, String mapID, int elementID) {
		super(dragComponent, mapID, elementID);
	}

	@Override
	protected void sendUpdateGroupCursorPositionPersistentToServer(
			String mapID, int elementID, int x, int y) {
		communicator.sendActionPackage(actionBuilder.updateGroupCursorPositionPersistent(mapID, elementID, x, y));
	}

}
