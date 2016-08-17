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

package lasad.gwt.client.helper.connection.calculator;

import java.util.List;

import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;

/**
 * Full rectilinear connections data calculator.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class FullRectilinearTwoEndedCalculator implements ConnectionDataCalculator {

	/**
	 * @see lasad.gwt.client.helper.connection.calculator.ConnectionDataCalculator#calculateConnectionData(java.util.List)
	 */
	public ConnectionData calculateConnectionData(List<Connector> connectors) {
		if (connectors.size() != 2) {
			throw new IllegalArgumentException("Unsupported connectors count");
		}

		Connector c1 = connectors.get(0);
		Connector c2 = connectors.get(1);

		ConnectionData data = new ConnectionData();

		Direction[] directions = Direction.computeDirections(c1, c2);

		Point s1 = c1.pointOnBorder(directions[0]);
		Point s11 = s1.move(directions[0], 10); // TODO Use generic parameters

		Point s2 = c2.pointOnBorder(directions[1]);
		Point s21 = s2.move(directions[1], 10); // TODO Use generic parameters

		int horizontal = s21.left - s11.left;
		int vertical = s21.top - s11.top;

		Point n1 = null;
		Point n2 = null;

		if (equalDirection(directions[0], 0, horizontal, true) && equalDirection(directions[1], 1, horizontal, true)) {
			n1 = s11.move(directions[0], Math.abs(horizontal / 2));
			n2 = new Point(n1.left, s21.top);
		} else if (equalDirection(directions[0], 0, vertical, false) && equalDirection(directions[1], 1, vertical, false)) {
			n1 = s11.move(directions[0], Math.abs(vertical / 2));
			n2 = new Point(s21.left, n1.top);
		} else if (equalDirection(directions[0], 0, horizontal, true) && equalDirection(directions[1], 1, vertical, false)) {
			n1 = s11.move(directions[0], Math.abs(horizontal));
			n2 = s21.move(directions[1], Math.abs(vertical));
		} else if (equalDirection(directions[0], 0, vertical, false) && equalDirection(directions[1], 1, horizontal, true)) {
			n1 = s11.move(directions[0], Math.abs(vertical));
			n2 = s21.move(directions[1], Math.abs(horizontal));
		} else if (equalDirection(directions[0], 0, horizontal, true) && canMove(directions[1], 1, horizontal, true)) {
			n1 = s11.move(directions[0], Math.abs(horizontal / 2));
			n2 = new Point(n1.left, s21.top);
		} else if (canMove(directions[0], 0, horizontal, true) && equalDirection(directions[1], 1, horizontal, true)) {
			n2 = s21.move(directions[1], Math.abs(horizontal / 2));
			n1 = new Point(n2.left, s11.top);
		} else if (equalDirection(directions[0], 0, vertical, false) && canMove(directions[1], 1, vertical, false)) {
			n1 = s11.move(directions[0], Math.abs(vertical / 2));
			n2 = new Point(s21.left, n1.top);
		} else if (canMove(directions[0], 0, vertical, false) && equalDirection(directions[1], 1, vertical, false)) {
			n2 = s21.move(directions[1], Math.abs(vertical / 2));
			n1 = new Point(s11.left, n2.top);
		} else if (canMove(directions[0], 0, horizontal, true) && canMove(directions[1], 1, horizontal, true)) {
			n1 = s11.move(Direction.RIGHT, horizontal / 2);
			n2 = new Point(n1.left, s21.top);
		} else if (canMove(directions[0], 0, vertical, false) && canMove(directions[1], 1, vertical, false)) {
			n1 = s11.move(Direction.DOWN, vertical / 2);
			n2 = new Point(s21.left, n1.top);
		} else if (canMove(directions[0], 0, horizontal, true) && canMove(directions[1], 1, vertical, false)) {
			n1 = s11.move(Direction.RIGHT, horizontal);
		} else if (canMove(directions[0], 0, vertical, false) && canMove(directions[1], 1, horizontal, true)) {
			n1 = s11.move(Direction.DOWN, vertical);
		}

		data.getPoints().add(s1);
		data.getPoints().add(s11);
		if (n1 != null) {
			data.getPoints().add(n1);
		}
		if (n2 != null) {
			data.getPoints().add(n2);
		}
		data.getPoints().add(s21);
		data.getPoints().add(s2);

		return data;
	}

	private boolean canMove(Direction defined, int index, int sign, boolean horizontal) {
		if (horizontal) {
			if (index == 0 && sign > 0) {
				return defined != Direction.LEFT;
			}
			if (index == 1 && sign > 0) {
				return defined != Direction.RIGHT;
			}
			if (index == 0 && sign < 0) {
				return defined != Direction.RIGHT;
			}
			if (index == 1 && sign < 0) {
				return defined != Direction.LEFT;
			}
		} else {
			if (index == 0 && sign > 0) {
				return defined != Direction.UP;
			}
			if (index == 1 && sign > 0) {
				return defined != Direction.DOWN;
			}
			if (index == 0 && sign < 0) {
				return defined != Direction.DOWN;
			}
			if (index == 1 && sign < 0) {
				return defined != Direction.UP;
			}
		}
		return false; // sign == 0 ??
	}

	private boolean equalDirection(Direction defined, int index, int sign, boolean horizontal) {
		if (horizontal) {
			if (index == 0 && sign > 0) {
				return defined == Direction.RIGHT;
			}
			if (index == 1 && sign > 0) {
				return defined == Direction.LEFT;
			}
			if (index == 0 && sign < 0) {
				return defined == Direction.LEFT;
			}
			if (index == 1 && sign < 0) {
				return defined == Direction.RIGHT;
			}
		} else {
			if (index == 0 && sign > 0) {
				return defined == Direction.DOWN;
			}
			if (index == 1 && sign > 0) {
				return defined == Direction.UP;
			}
			if (index == 0 && sign < 0) {
				return defined == Direction.UP;
			}
			if (index == 1 && sign < 0) {
				return defined == Direction.DOWN;
			}
		}
		return false; // sign == 0 ??
	}
}