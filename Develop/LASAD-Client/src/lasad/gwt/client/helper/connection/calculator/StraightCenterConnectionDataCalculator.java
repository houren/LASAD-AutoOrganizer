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

/**
 * Calculates data for straight centered connection.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class StraightCenterConnectionDataCalculator implements ConnectionDataCalculator {

	/**
	 * @see lasad.gwt.client.helper.connection.calculator.ConnectionDataCalculator#calculateConnectionData(java.util.List)
	 */
	public ConnectionData calculateConnectionData(List<Connector> connectors) {
		if (connectors.size() != 2) {
			throw new IllegalArgumentException("Unsupported connectors count");
		}

		Connector c1 = (Connector) connectors.get(0);
		Connector c2 = (Connector) connectors.get(1);
		
		// for some UIObjects there are invisible borders
		// subtract their values from both sides of the widget
		int c1Width = c1.getWidth() - c1.getInvisibleBorderWidth()*2;
		int c1Height = c1.getHeight() - c1.getInvisibleBorderHeight()*2;
		int c2Width = c2.getWidth() - c2.getInvisibleBorderWidth()*2;
		int c2Height = c2.getHeight() - c2.getInvisibleBorderHeight()*2;
		
		ConnectionData data = new ConnectionData();

		Point center1 = new Point(c1.getLeft() + c1.getWidth() / 2, c1.getTop() + c1.getHeight() / 2);
		Point center2 = new Point(c2.getLeft() + c2.getWidth() / 2, c2.getTop() + c2.getHeight() / 2);

		Point diff = new Point(center2.left - center1.left, center2.top - center1.top);

		
		int leftSign = diff.left >= 0 ? -1 : 1;
		int topSign = diff.top >= 0 ? -1 : 1;
		
		// c1 vertical
		int top = c1Height / 2;
		int left = diff.top != 0 ? top * diff.left / Math.abs(diff.top) : Integer.MAX_VALUE;
		top *= -topSign;

		if (Math.abs(left) > c1Width / 2) {
			// c1 horizontal
			left = c1Width / 2;
			top = diff.left != 0 ? left * diff.top / Math.abs(diff.left) : Integer.MAX_VALUE;
			left *= -leftSign;
		}
		data.getPoints().add(new Point(center1.left + left, center1.top + top));

		// c2 vertical
		top = c2Height / 2;
		left = diff.top != 0 ? top * diff.left / Math.abs(diff.top) : Integer.MAX_VALUE;
		top *= topSign;
		left = -left;

		if (Math.abs(left) > c2Width / 2) {
			// c2 horizontal
			left = c2Width / 2;
			top = diff.left != 0 ? left * diff.top / Math.abs(diff.left) : Integer.MAX_VALUE;
			left *= leftSign;
			top = -top;
		}
		data.getPoints().add(new Point(center2.left + left, center2.top + top));
			
		return data;
	}
}