package lasad.gwt.client.ui.link;

import java.util.Iterator;

import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connection.data.BezierConnectionData;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;



public abstract class AbstractCurvedLink extends AbstractLink {

	public AbstractCurvedLink(GraphMap myMap, ElementInfo config, Connector cn1,
			Connector cn2, boolean isReplay) {
		super(myMap, config, cn1, cn2, isReplay);
	}

	@Override
	protected void update(ConnectionData data) {
		try {

			bdata = (BezierConnectionData) data;
			/*
			 * recalculate positions source: the center of the connection
			 */			
			position = calculatePosition(data);
			updateLinkPanelPosition();
			
			// recalculate all connected connections
			if (cn != null) {
				for (Iterator<Connection> i = cn.getConnections().iterator(); i.hasNext();) {
					Connection con = (Connection) i.next();
					con.update();
				}
			}
			
			super.update(data);
		} catch (Exception e) {
			Logger.log("update(ConnectionData) in class CurvedLink.java failed.", Logger.DEBUG_ERRORS);
		}
	}
}
