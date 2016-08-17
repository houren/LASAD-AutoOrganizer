package lasad.gwt.client.ui;

import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

/**
 * Must be implemented by all graphical elements, which are added to an
 * ArgumentMap panel
 * 
 * @author Jan Brenner
 * 
 */
public interface GraphicalElementInterface {

	GraphMap myMap = null;

	public Connector getConnector();

	public Connector getConnector(Direction dir);

	public Direction getDirection();

	public GraphMap getMap();

	public ElementInfo getElementInfo();

}