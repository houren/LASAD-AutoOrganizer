package lasad.gwt.client.helper.connection.ending;

import lasad.gwt.client.helper.connection.data.Point;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class DrawEndingCanvas extends DrawEnding {

	private Element styledDiv;

	/**
	 * Private constructor. Do not instantiate directly @see BezierCurve
	 */
	protected DrawEndingCanvas() {
	// Do not instantiate directly
	}

	private Element canvas;

	{
		initCanvas();
	}

	protected void initCanvas() {
		this.canvas = DOM.createElement("canvas");
		String prev = DOM.getElementAttribute(canvas, "class");
		DOM.setElementAttribute(canvas, "class", prev + " gwt-diagrams-canvas");
	}

	public void update(int left, int top, float angle) {

		if (angle < 0.0f || angle >= 360.0f) {
			throw new IllegalArgumentException("Angle must from [0.0f, 360.0f)");
		}

		// Hack for making the arrow heads get drawn in vertical cases
		boolean isVertical = false;
		if (angle == 0.0f || angle == 180.0f)
		{
			isVertical = true;
		}

		int arrowheadWidth = 6;
		int arrowheadHeight = 15;

		Point start = new Point(left, top);

		int size = Math.max(arrowheadWidth, arrowheadHeight);
		Point realStart = new Point(start.left - size, start.top - size);

		DOM.setElementAttribute(canvas, "left", Integer.toString(realStart.left) + "px");
		DOM.setElementAttribute(canvas, "top", Integer.toString(realStart.top) + "px");
		DOM.setStyleAttribute(canvas, "left", Integer.toString(realStart.left) + "px");
		DOM.setStyleAttribute(canvas, "top", Integer.toString(realStart.top) + "px");
		DOM.setElementAttribute(canvas, "width", Integer.toString(arrowheadWidth + size * 2) + "px");
		DOM.setElementAttribute(canvas, "height", Integer.toString(arrowheadHeight + size * 2) + "px");
		DOM.setStyleAttribute(canvas, "width", Integer.toString(arrowheadWidth + size * 2) + "px");
		DOM.setStyleAttribute(canvas, "height", Integer.toString(arrowheadHeight + size * 2) + "px");

		// calculate another end point of this link
		Point end = new Point(
				(int)(start.left - 40*Math.sin(Math.toRadians(angle))), 
				(int)(start.top + 40*Math.cos(Math.toRadians(angle))));

		Point vect = new Point(start.left-end.left, start.top-end.top);
		Point[] tipPoints = getTipPoints(start, vect, arrowheadHeight, arrowheadWidth, isVertical);
		
		// start = start.move(new Point(-2,-1));
		Point p1=tipPoints[0], p2=tipPoints[1], p3=tipPoints[2];

		realStart = realStart.move(new Point(2, 2).negative());

		drawImpl(p1.move(realStart.negative()), p2.move(realStart.negative()), p3.move(realStart.negative()), DOM.getStyleAttribute(styledDiv, "color"),
				DOM.getStyleAttribute(styledDiv, "width").replace("px", ""));

	}
	
	/**
	 * Calculate tip points
	 * @param start
	 * @param vect
	 * @return
	 */
	private Point[] getTipPoints(Point point, Point vect, int arrowheadHeight, int arrowheadWidth, boolean isVertical) {
		//array for tip points
		//{ax, ay, bx, by, cx, cy}
		Point[] tipPoints = new Point[3];
		
		//intersection point is first tip point

		tipPoints[0] = new Point(point.left, point.top);

		//calculate normalization factor
		double norm = Math.sqrt(vect.left*vect.left + vect.top*vect.top);

		//go 'arrowheadHeight' px back on edge
		double hx = point.left - (vect.left/norm)*arrowheadHeight;
		double hy = point.top - (vect.top/norm)*arrowheadHeight;
		
		//go 'arrowheadWidth' px with 90 degree to edge in each direction
		//to get other tip points
		tipPoints[1] = new Point((int)(hx + (-vect.top/norm)*arrowheadWidth), (int)(hy + (vect.left/norm)*arrowheadWidth));
		tipPoints[2] = new Point((int)(hx - (-vect.top/norm)*arrowheadWidth), (int)(hy - (vect.left/norm)*arrowheadWidth));

		// Hack for the bug that LASAD was not drawing arrow heads if and only if the relations were vertical
		if (isVertical)
		{
			Point temp = tipPoints[1].clone();
			tipPoints[1] = tipPoints[2].clone();
			tipPoints[2] = temp;

			if (tipPoints[1].getTop() < tipPoints[0].getTop())
			{
				Point vectorUp = new Point(0, 2 * arrowheadHeight);
				tipPoints[1] = tipPoints[1].move(vectorUp);
				tipPoints[2] = tipPoints[2].move(vectorUp);
			}
			else if (tipPoints[1].getTop() > tipPoints[0].getTop())
			{
				Point vectorDown = new Point(0, -2 * arrowheadHeight);
				tipPoints[1] = tipPoints[1].move(vectorDown);
				tipPoints[2] = tipPoints[2].move(vectorDown);
			}
		} 	
		return tipPoints;
	}

	private native void drawImpl(Point p1, Point p2, Point p3, String color, String linewidth)/*-{
		var canvas = this.@lasad.gwt.client.helper.connection.ending.DrawEndingCanvas::canvas;
		var ctx = canvas.getContext('2d');


		ctx.strokeStyle = color;
		ctx.fillStyle = color;
		ctx.lineWidth = linewidth;
		ctx.beginPath();
		ctx.moveTo(p1.@lasad.gwt.client.helper.connection.data.Point::left,
		p1.@lasad.gwt.client.helper.connection.data.Point::top);
		ctx.lineTo(p2.@lasad.gwt.client.helper.connection.data.Point::left,
		p2.@lasad.gwt.client.helper.connection.data.Point::top);
		ctx.lineTo(p3.@lasad.gwt.client.helper.connection.data.Point::left,
		p3.@lasad.gwt.client.helper.connection.data.Point::top);
		ctx.fill();
	}-*/;

	/**
	 * @see lasad.gwt.client.helper.common.bezier.BezierCurve#getElement()
	 */
	public Element getElement() {
		return canvas;
	}

	public Element getCurveStyleElement() {
		return styledDiv;
	}

	public void setCurveStyleElement(Element style) {
		if (styledDiv == null) {
			styledDiv = style;
		}
	}
	// private native static String getComputedStyle(Element element, String
	// cssRule)/*-{
	// if( $doc.defaultView && $doc.defaultView.getComputedStyle ){
	// return $doc.defaultView.getComputedStyle( element, ''
	// ).getPropertyValue(cssRule);
	// } else {
	// return null;
	// }
	// }-*/;
}