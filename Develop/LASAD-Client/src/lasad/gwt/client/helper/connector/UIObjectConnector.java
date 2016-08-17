/*
 * Copyright 2007 Michał Baliński
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package lasad.gwt.client.helper.connector;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Decorator for any UIObject, adds ability to be connectable. Implements
 * {@link Connector} and wraps UIObject.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class UIObjectConnector extends AbstractConnector {

	/**
	 * Static registry of all wrapped UIObjects
	 */
	private static final Map<UIObject, UIObjectConnector> wrappersMap = new HashMap<UIObject, UIObjectConnector>();

	/**
	 * Wrapped UIObject
	 */
	private UIObject wrapped;
	
	private int invisibleBorderWidth = 0;
	private int invisibleBorderHeight = 0;
	
	/**
	 * Static factory method, decorates UIObject with Connector functionality.
	 */
	public static UIObjectConnector wrap(UIObject wrapped) {
		if (wrappersMap.containsKey(wrapped)) {
			return (UIObjectConnector) wrappersMap.get(wrapped);
		} else {
			UIObjectConnector c = new UIObjectConnector(wrapped);
			wrappersMap.put(wrapped, c);
			return c;
		}
	}

	/**
	 * Static factory method, decorates UIObject with Connector functionality.
	 * 
	 * @param wrapped
	 *            UIObject to be wrapped
	 * @param directions
	 *            directions in which connector allow connections
	 * @return Connector
	 */
	public static UIObjectConnector wrap(UIObject wrapped, final Direction[] directions) {
		if (wrappersMap.containsKey(wrapped)) {
			return (UIObjectConnector) wrappersMap.get(wrapped);
		} else {
			UIObjectConnector c = new UIObjectConnector(wrapped) {
				public Direction[] getDirections() {
					return directions;
				}
			};
			wrappersMap.put(wrapped, c);
			return c;
		}
	}

	public static UIObjectConnector wrap(UIObject wrapped, final int borderWidth, final int borderHeight) {
		if (wrappersMap.containsKey(wrapped)) {
			return (UIObjectConnector) wrappersMap.get(wrapped);
		} else {
			UIObjectConnector c = new UIObjectConnector(wrapped) {
				public int getInvisibleBorderWidth() {
					return borderWidth;
				}
				
				public int getInvisibleBorderHeight() {
					return borderHeight;
				}
			};
			wrappersMap.put(wrapped, c);
			return c;
		}
	}
	
	/**
	 * Finds Connector for UIObject (in static registry)
	 */
	public static UIObjectConnector getWrapper(UIObject uio) {
		return (UIObjectConnector) wrappersMap.get(uio);
	}

	/**
	 * Unwrapps specified UIObject (removes wrapper from static registry)
	 * 
	 * @param wrapped
	 */
	public static void unwrap(UIObject wrapped) {
		if (wrappersMap.containsKey(wrapped)) {
			getWrapper(wrapped).unwrap();
		}
	}

	/**
	 * Unwrapps wrapped UIObject (removes wrapper from static registry)
	 */
	public void unwrap() {
		wrappersMap.remove(wrapped);
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
	 * Private constructor
	 * 
	 * @param wrapped
	 */
	private UIObjectConnector(UIObject wrapped) {
		this.wrapped = wrapped;
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

		// int containerOffset = 0;
		// Element parent = DOM.getParent(wrapped.getElement());
		// while( parent!=null ){
		// if( "relative".equals(DOM.getStyleAttribute(parent, "position")) ){
		// containerOffset = DOM.getAbsoluteLeft(parent);
		// break;
		// }
		// parent = DOM.getParent(parent);
		// }
		// return wrapped.getAbsoluteLeft() - containerOffset;
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getTop()
	 */
	public int getTop() {
		if (wrapped == null) {
			throw new IllegalStateException("Wrapped object is null.");
		}

		return wrapped.getElement().getOffsetTop();

		// int containerOffset = 0;
		// Element parent = DOM.getParent(wrapped.getElement());
		// while( parent!=null ){
		// if( "relative".equals(DOM.getStyleAttribute(parent, "position")) ){
		// containerOffset = DOM.getAbsoluteTop(parent);
		// break;
		// }
		// parent = DOM.getParent(parent);
		// }
		// return wrapped.getAbsoluteTop() - containerOffset;
	}

	@Override
	public int getInvisibleBorderWidth() {
		return invisibleBorderWidth;
	}

	@Override
	public int getInvisibleBorderHeight() {
		return invisibleBorderHeight;
	}

}
