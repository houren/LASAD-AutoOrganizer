package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.ui.box.AbstractBox;

import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.Event;



/**
 * Extended Version of the original EXT Draggable Class
 * Add support for AfterDrag Position Update without setting directly the box position.
 *  
 * BoxDraggable -> Event: DragEnd -> Create Action -> Send to Server -> Server send to Client -> Model -> Position Update 
 * 
 * @author burner
 *
 */
public class BoxDraggable extends Draggable {

	private boolean isDragging = false;
	
	public BoxDraggable(Component dragComponent) {
		super(dragComponent);
	}

	public BoxDraggable(final Component dragComponent, final Component handle) {
		super(dragComponent,handle);
	}

	
	
	@Override
	protected void startDrag(Event event) {
		isDragging=true;
		// Call the real starDrag End Method to prevent the originally behavior
		super.startDrag(event);
	}

	@Override
	protected void stopDrag(Event event) {
		if (isDragging && isUseProxy() && !isMoveAfterProxyDrag() && super.getDragWidget() instanceof AbstractBox) {
			// Calculate new relative Position from the PageCoordinates of the dragged proxyElement 
			Rectangle rect = proxyEl.getBounds();
			Point cord = ((AbstractBox)getDragWidget()).el().translatePoints(new Point(rect.x,rect.y));

			// Call the Box method to create an Server Action
			((AbstractBox)getDragWidget()).updatePosition(cord.x, cord.y);
		}
		isDragging=false;
		
		// Call the real stopDrag End Method to prevent the originally behavior
		super.stopDrag(event);
	}
		
	
	
	
}