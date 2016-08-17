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

import lasad.gwt.client.helper.common.bezier.BezierCurve;
import lasad.gwt.client.helper.connection.calculator.BezierConnectionCalculator;
import lasad.gwt.client.helper.connection.calculator.ConnectionDataCalculator;
import lasad.gwt.client.helper.connection.data.BezierConnectionData;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connection.ending.DrawEnding;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Bezier connection widget.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class BezierTwoEndedConnection extends AbstractTwoEndedConnection {
	/**
	 * Represents bezier curve. Implementation depends on browser type.
	 * Instantiated through deferred binding
	 */
	protected BezierCurve curve = (BezierCurve) GWT.create(BezierCurve.class);

	protected DrawEnding endingFirst;
	protected DrawEnding endingSecond;

	protected String lineWidth = "2px", color = "#000000";
	protected boolean useEndings = true;

	/**
	 * Constructs bezier connection
	 */
	public BezierTwoEndedConnection(Connector[] toConnect) {
		super(toConnect);
		setElement(curve.getElement());
		addStyleName("gwt-diagrams-connection");
	}

	public Element getElement() {
		return curve.getElement();
	}

	public Element getCurveStyleElement() {
		return curve.getCurveStyleElement();
	}

	/**
	 * Constructs bezier connection
	 */
	public BezierTwoEndedConnection(Connector c1, Connector c2) {
		this(new Connector[] { c1, c2 });
	}

	public BezierTwoEndedConnection(Connector c1, Connector c2, boolean useEndings) {
		this(new Connector[] { c1, c2 });
		this.setUseEndings(useEndings);
	}

	/**
	 * Constructs bezier connection
	 * 
	 * @param myMap
	 *            Map to which the curve is added
	 * @param c1
	 *            First connector
	 * @param c2
	 *            Second connector
	 * @param title
	 *            Title of the link
	 * @param lineWidth
	 *            Line width in px
	 * @param color
	 *            Color code, e.g. #000000
	 * @param useEndings
	 *            Enable or disable arrows at one end of the connection
	 */
	public BezierTwoEndedConnection(Connector c1, Connector c2, String lineWidth, String color, boolean useEndings) {
		this(new Connector[] { c1, c2 });
		this.setColor(color);
		this.setLineWidth(lineWidth);
		this.setUseEndings(useEndings);
	}

	/**
	 * @see lasad.gwt.client.helper.connection.AbstractConnection#createCalculator()
	 */
	protected ConnectionDataCalculator createCalculator() {
		return new BezierConnectionCalculator();
	}

	/**
	 * @see lasad.gwt.client.helper.connection.AbstractConnection#update(lasad.gwt.client.helper.connection.data.ConnectionData)
	 */
	protected void update(ConnectionData data) {
		if (!(data instanceof BezierConnectionData)) {
			throw new IllegalArgumentException("Expected BezierConnectionData");
		}
		BezierConnectionData bdata = (BezierConnectionData) data;
		if (bdata.getPoints().size() != 2) {
			throw new IllegalArgumentException("Expected two connection points");
		}
		if (bdata.getControlPoints().size() != 2) {
			throw new IllegalArgumentException("Expected two control points");
		}

		curve.draw((Point) bdata.getPoints().get(0), (Point) bdata.getPoints().get(1), (Point) bdata.getControlPoints().get(0), (Point) bdata.getControlPoints().get(1));

		// Endings
		if (useEndings) {
			if (getEnding(0) != null) {
				if (getEnding(0) instanceof DrawEnding) {
					((DrawEnding) getEnding(0)).setCurveStyleElement(curve.getCurveStyleElement());
				}
				Point p = (Point) bdata.getPoints().get(0);
				Point c = (Point) bdata.getControlPoints().get(0);

				if (p.left > c.left) { // RIGHT
					getEnding(0).update(p.left, p.top, Direction.RIGHT.getAngle());
				} else if (p.left < c.left) { // LEFT
					getEnding(0).update(p.left, p.top, Direction.LEFT.getAngle());
				} else if (p.top > c.top) { // DOWN
					getEnding(0).update(p.left, p.top, Direction.DOWN.getAngle());
				} else if (p.top < c.top) { // UP
					getEnding(0).update(p.left, p.top, Direction.UP.getAngle());
				}

			}

			if (getEnding(1) != null) {
				if (getEnding(1) instanceof DrawEnding) {
					((DrawEnding) getEnding(1)).setCurveStyleElement(curve.getCurveStyleElement());
				}
				Point p = (Point) bdata.getPoints().get(1);
				Point c = (Point) bdata.getControlPoints().get(1);

				if (p.left > c.left) { // RIGHT
					getEnding(1).update(p.left, p.top, Direction.RIGHT.getAngle());
				} else if (p.left < c.left) { // LEFT
					getEnding(1).update(p.left, p.top, Direction.LEFT.getAngle());
				} else if (p.top > c.top) { // DOWN
					getEnding(1).update(p.left, p.top, Direction.DOWN.getAngle());
				} else if (p.top < c.top) { // UP
					getEnding(1).update(p.left, p.top, Direction.UP.getAngle());
				}
			}
		}
	}

	/**
	 * @see lasad.gwt.client.helper.connection.AbstractConnection#remove()
	 */
	public void remove() {
		super.remove();
		curve.remove();
		// this.removeFromParent();
	}

	public void setEndings(boolean first, boolean second) {
		if (first && endingFirst == null) {
			endingFirst = (DrawEnding) GWT.create(DrawEnding.class);
		} else if (!first && endingFirst != null) {
			// remove Ending
		}

		if (second && endingSecond == null) {
			endingSecond = (DrawEnding) GWT.create(DrawEnding.class);
		} else if (!second && endingSecond != null) {
			// remove Ending
		}
		super.setEndings(endingFirst, endingSecond);
		update();
	}

	public String getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(String lineWidth) {
		this.lineWidth = lineWidth;
		DOM.setStyleAttribute(this.getCurveStyleElement(), "width", lineWidth);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
		DOM.setStyleAttribute(this.getCurveStyleElement(), "color", color);
	}

	public boolean getUseEndings() {
		return useEndings;
	}

	public void setUseEndings(boolean useEndings) {
		this.useEndings = useEndings;
	}
}