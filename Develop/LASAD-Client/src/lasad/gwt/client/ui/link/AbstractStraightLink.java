package lasad.gwt.client.ui.link;

import java.util.Iterator;

import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connection.calculator.ConnectionDataCalculator;
import lasad.gwt.client.helper.connection.calculator.StraightCenterConnectionDataCalculator;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connection.ending.DrawEnding;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;


public abstract class AbstractStraightLink extends AbstractLink {
	public AbstractStraightLink(GraphMap myMap, ElementInfo config, Connector cn1,
			Connector cn2, boolean isReplay) {
		super(myMap, config, cn1, cn2, isReplay);
	}
	
	/**
	 * @see lasad.gwt.client.helper.connection.BezierTwoEndedConnection#createCalculator()
	 */
	protected ConnectionDataCalculator createCalculator() {
		return new StraightCenterConnectionDataCalculator();
	}
	
	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected Point calculatePosition(ConnectionData data) {
		Point result = null;
		
		Point p1 = (Point) data.getPoints().get(0); // get start and end
		Point p2 = (Point) data.getPoints().get(1);
		
		int X = (p1.getLeft() + p2.getLeft()) / 2;
		int Y = (p1.getTop() + p2.getTop()) / 2;
		result = new Point(X, Y);
		return result;
	}

	@Override
	protected void update(ConnectionData data) {
		if (data.getPoints().size() != 2) {
			throw new IllegalArgumentException("Expected two connection points");
		}
		curve.draw((Point) data.getPoints().get(0), (Point) data.getPoints().get(1), (Point) data.getPoints().get(1), (Point) data.getPoints().get(0));
		
		try {
			position = calculatePosition(data);
			updateLinkPanelPosition();

			// recalculate all connected connections
			if (cn != null) {
				for (Iterator<Connection> i = cn.getConnections().iterator(); i.hasNext();) {
					Connection con = (Connection) i.next();
					con.update();
				}
			}
			
			// Endings
			if (useEndings) {
				if (getEnding(0) != null) {
					
					if (getEnding(0) instanceof DrawEnding)
						((DrawEnding) getEnding(0)).setCurveStyleElement(curve.getCurveStyleElement());
					
					Point p1 = (Point) data.getPoints().get(0);
					Point p2 = (Point) data.getPoints().get(1);
		
					float tan = ((float) (p2.top - p1.top)) / (p2.left - p1.left);

					float angle = (float) Math.toDegrees(Math.atan(tan));

					if (p1.left < p2.left) {
						angle += 180f;
					}
					getEnding(0).update(p1.left, p1.top, angle + 90f);
				}
		
				if (getEnding(1) != null) {
					
					if (getEnding(1) instanceof DrawEnding)
						((DrawEnding) getEnding(1)).setCurveStyleElement(curve.getCurveStyleElement());
					
					Point p1 = (Point) data.getPoints().get(1);
					Point p2 = (Point) data.getPoints().get(0);
		
					float tan = ((float) (p2.top - p1.top)) / (p2.left - p1.left);

					float angle = (float) Math.toDegrees(Math.atan(tan));
					if (p1.left < p2.left) {
						angle += 180f;
					}

					//float newangle = angle + 90f;

					getEnding(1).update(p1.left, p1.top, angle + 90f);
				}
			}
			
		} catch (Exception e) {
			Logger.log("update(ConnectionData) in class AbstractStraightLink.java failed.", Logger.DEBUG_ERRORS);
		}
	} 
}