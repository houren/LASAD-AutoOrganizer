package lasad.gwt.client.ui;

import lasad.gwt.client.helper.connector.AbstractConnector;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Decorator for any UIObject, adds ability to be connectable. Implements
 * {@link Connector} and wraps UIObject.
 */
public class MyUIObjectConnector extends AbstractConnector {

	/**
	 * Wrapped UIObject
	 */
	private UIObject wrapped;
	private Direction[] directions;

	public MyUIObjectConnector(UIObject wrapped) {
		this.wrapped = wrapped;
	}

	public MyUIObjectConnector(UIObject wrapped, Direction[] directions) {
		this.wrapped = wrapped;
		this.directions = directions;
	}

	/**
	 * Finds Connector for UIObject (in static registry)
	 */
	public MyUIObjectConnector getWrapper(UIObject uio) {
		return this;
	}

	/**
	 * Unwrapps specified UIObject (removes wrapper from static registry)
	 * 
	 * @param wrapped
	 */
	public void unwrap(UIObject wrapped) {
		wrapped = null;
	}

	/**
	 * Unwrapps wrapped UIObject (removes wrapper from static registry)
	 */
	public void unwrap() {
		wrapped = null;
	}

	/**
	 * Disconnects from all connections and unwraps associated widget
	 */
	public void remove() {
		disconnect();
		unwrap();
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getHeight()
	 */
	public int getHeight() {
		if (wrapped == null) {
			throw new IllegalStateException("Wrapped object is null.");
		}

		return wrapped.getOffsetHeight();
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getWidth()
	 */
	public int getWidth() {
		if (wrapped == null) {
			throw new IllegalStateException("Wrapped object is null.");
		}

		return wrapped.getOffsetWidth();
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getLeft()
	 */
	public int getLeft() {
		if (wrapped == null) {
			throw new IllegalStateException("Wrapped object is null.");
		}
		return wrapped.getElement().getOffsetLeft();
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getTop()
	 */
	public int getTop() {
		if (wrapped == null) {
			throw new IllegalStateException("Wrapped object is null.");
		}

		return wrapped.getElement().getOffsetTop();
	}

	public Direction[] getDirections() {
		return directions;
	}
	
	@Override
	public int getInvisibleBorderWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInvisibleBorderHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	// public Collection getConnections(Direction dir) {
	// Collection cons = super.getConnections();
	//		
	// ((AbstractConnection)cons.iterator().next()).getCalculator().
	//		
	// return cons;
	// }
}