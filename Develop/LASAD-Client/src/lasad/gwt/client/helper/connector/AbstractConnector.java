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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connection.data.Point;

/**
 * Base implementation of connector.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public abstract class AbstractConnector implements Connector {

	/**
	 * Connections to which this connector is connected
	 */
	private Set<Connection> connections = new HashSet<Connection>();

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#disconnect(lasad.gwt.client.helper.connection.Connection)
	 */
	public void disconnect(Connection c) {
		if (connections.remove(c)) {
			c.disconnect(this);
		}
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#disconnect()
	 */
	public void disconnect() {
		Iterator<Connection> i = connections.iterator();
		
		while(i.hasNext()) {
			Connection c = (Connection) i.next();
			c.disconnect(this);
			i.remove();
		}
		
//		for (Iterator<Connection> i = connections.iterator(); i.hasNext();) {
//			disconnect((Connection) i.next());
//		}
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#connect(lasad.gwt.client.helper.connection.Connection)
	 */
	public void connect(Connection c) {
		connections.add(c);
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getDirections()
	 */
	public Direction[] getDirections() {
		return Direction.getAll();
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#update()
	 */
	public void update() {
		for (Iterator<Connection> i = connections.iterator(); i.hasNext();) {
			Connection c = (Connection) i.next();
			c.update();
		}
	}

	public Collection<Connection> getConnections() {
		return connections;
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#getConnectionPoint(lasad.gwt.client.helper.connector.Direction)
	 */
	public Point getConnectionPoint(Direction direction) {
		// if( direction == Direction.UP ) {
		// return new Point(getLeft() + getWidth()/2, getTop());
		// } else if( direction == Direction.DOWN ) {
		// return new Point(getLeft() + getWidth()/2, getTop() + getHeight());
		// } else if( direction == Direction.LEFT ) {
		// return new Point(getLeft(), getTop() + getHeight()/2);
		// } else if( direction == Direction.RIGHT ) {
		// return new Point(getLeft() + getWidth(), getTop() + getHeight()/2);
		// }
		// throw new IllegalStateException("Imposible " + direction);
		return pointOnBorder(direction);
	}

	/**
	 * @see lasad.gwt.client.helper.connector.Connector#pointOnBorder(lasad.gwt.client.helper.connector.Direction)
	 */
	public Point pointOnBorder(Direction d) {
		return new Point(getLeft() + getWidth() / 2, getTop() + getHeight() / 2).move(d, ((d.isHorizontal()) ? getWidth() / 2 : getHeight() / 2));
	}

}
