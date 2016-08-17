package lasad.gwt.client.helper.connection;

import lasad.gwt.client.helper.connection.calculator.ConnectionDataCalculator;
import lasad.gwt.client.helper.connection.calculator.StraightCenterConnectionDataCalculator;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.google.gwt.user.client.DOM;

/**
 * Straight line connection widget.
 * 
 * @author Michał Baliński (michal.balinski@gmail.com)
 */
public class GenericStraightTwoEndedConnection extends BezierTwoEndedConnection {

	private GraphMap myMap;

	/**
	 * Constructor
	 * 
	 * @param c1
	 * @param c2
	 */
	public GenericStraightTwoEndedConnection(GraphMap myMap, Connector c1, Connector c2, String width, String color) {
		super(c1, c2);
		this.myMap = myMap;
		DOM.setStyleAttribute(this.getCurveStyleElement(), "width", width);
		DOM.setStyleAttribute(this.getCurveStyleElement(), "color", color);
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", 0);
	}

	/**
	 * Constructor
	 * 
	 * @param toConnect
	 */
	public GenericStraightTwoEndedConnection(Connector[] toConnect) {
		super(toConnect);
	}

	/**
	 * @see lasad.gwt.client.helper.connection.BezierTwoEndedConnection#createCalculator()
	 */
	protected ConnectionDataCalculator createCalculator() {
		return new StraightCenterConnectionDataCalculator();
	}

	/**
	 * @see lasad.gwt.client.helper.connection.BezierTwoEndedConnection#update(lasad.gwt.client.helper.connection.data.ConnectionData)
	 */
	protected void update(ConnectionData data) {
		if (data.getPoints().size() != 2) {
			throw new IllegalArgumentException("Expected two connection points");
		}

		curve.draw((Point) data.getPoints().get(0), (Point) data.getPoints().get(1), (Point) data.getPoints().get(1), (Point) data.getPoints().get(0));

		// // Endings
		// if( getEnding(0)!=null ){
		// Point p1 = (Point)data.getPoints().get(0);
		// Point p2 = (Point)data.getPoints().get(1);
		//		
		// float tan = ((float)(p2.top-p1.top))/(p2.left - p1.left);
		// float angle = (float)Math.toDegrees(Math.atan(tan));
		// if( p1.left < p2.left ) {
		// angle += 180f;
		// }
		// getEnding(0).update(p1.left, p1.top, angle+90f);
		// }
		//		
		// if( getEnding(1)!=null ){
		// Point p1 = (Point)data.getPoints().get(1);
		// Point p2 = (Point)data.getPoints().get(0);
		//
		// float tan = ((float)(p2.top-p1.top))/(p2.left - p1.left);
		// float angle = (float)Math.toDegrees(Math.atan(tan));
		// if( p1.left < p2.left ) {
		// angle += 180f;
		// }
		// getEnding(1).update(p1.left, p1.top, angle+90f);
		// }

	}

	@Override
	public void remove() {
		super.remove();
		this.myMap.remove(this);
	}
}
