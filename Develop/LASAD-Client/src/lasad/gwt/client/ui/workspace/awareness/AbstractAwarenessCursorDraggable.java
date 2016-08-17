package lasad.gwt.client.ui.workspace.awareness;

import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.util.Point;
import com.google.gwt.user.client.Event;

/**
 * Extended Version of the original EXT Draggable Class Add support for
 * AfterDrag Position Update without setting directly the box position.
 * 
 * BoxDraggable -> Event: DragEnd -> Create Action -> Send to Server -> Server
 * send to Client -> Model -> Position Update
 * 
 * @author burner
 * 
 */
public abstract class AbstractAwarenessCursorDraggable extends Draggable {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private String mapID;
	private int elementID;
	private AbstractAwarenessCursor myRef;

	public AbstractAwarenessCursorDraggable(AbstractAwarenessCursor dragComponent, String mapID, int elementID) {
		super(dragComponent);
		this.myRef = dragComponent;
		this.mapID = mapID;
		this.elementID = elementID;
	}

	@Override
	protected void startDrag(Event event) {
		// Call the real starDrag End Method to prevent the originally behavior
		super.startDrag(event);
	}

	@Override
	protected void stopDrag(Event event) {
		// Calculate new relative Position from the PageCoordinates of the
		// dragged proxyElement
		Point cord = myRef.getPosition(true);
//		communicator.sendActionPackage(actionBuilder.updateGroupCursorPositionPersistent(mapID, elementID, cord.x, cord.y));
		sendUpdateGroupCursorPositionPersistentToServer(mapID, elementID, cord.x, cord.y);
		super.stopDrag(event);
	}
	
	protected abstract void sendUpdateGroupCursorPositionPersistentToServer(String mapID, int elementID, int x, int y);
	
}