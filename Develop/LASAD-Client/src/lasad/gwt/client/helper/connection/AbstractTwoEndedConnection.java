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

package lasad.gwt.client.helper.connection;

import lasad.gwt.client.helper.connection.ending.ConnectionEnding;
import lasad.gwt.client.helper.connector.Connector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public abstract class AbstractTwoEndedConnection extends AbstractConnection {

	private final ConnectionEnding endings[] = new ConnectionEnding[] { null, null };

	public AbstractTwoEndedConnection(Connector[] toConnect) {
		super(toConnect);
		if (toConnect.length != 2) {
			throw new IllegalArgumentException("Need exactly two connectors to connect");
		}
	}

	public void setEndings(ConnectionEnding first, ConnectionEnding second) {
		endings[0] = first;
		endings[1] = second;

		Element parent = DOM.getParent(getElement());
		if (parent != null && first != null) {
			DOM.appendChild(parent, first.getElement());
		}
		if (parent != null && second != null) {
			DOM.appendChild(parent, second.getElement());
		}
	}

	public ConnectionEnding getEnding(int i) {
		return endings[i];
	}

	/**
	 * Removes the connection endings 3.2.09
	 * 
	 * @see lasad.gwt.client.helper.connection.AbstractConnection#remove()
	 */
	public void remove() {
		removeEndings();
		super.remove();
	}

	public void removeEndings() {
		if (endings[0] != null) {
			DOM.removeChild(DOM.getParent(endings[0].getElement()), endings[0].getElement());

		}
		if (endings[1] != null) {
			DOM.removeChild(DOM.getParent(endings[1].getElement()), endings[1].getElement());
		}
	}
}