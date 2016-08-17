package lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui;

import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.helper.connection.BezierTwoEndedConnection;
import lasad.gwt.client.helper.connection.Connection;
import lasad.gwt.client.helper.connection.data.BezierConnectionData;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.helper.connector.UIObjectConnector;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;

/**
 * No documentation
 * @author ???
 */

public class SimpleLink extends BezierTwoEndedConnection implements ExtendedElementContainerInterface {

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	protected SimpleLinkPanel linkPanel;

	private ContentPanel myMap;
	private Connector cn, startConnector, endConnector;
	private Point middle;

	protected boolean editMode = false;
	protected boolean principles = false;

	public SimpleLink(ContentPanel myMap, ElementInfo config, Connector cn1, Connector cn2) {
		super(cn1, cn2, config.getUiOption(ParameterTypes.LineWidth), config.getUiOption(ParameterTypes.LineColor), (config.getElementOption(ParameterTypes.Endings).equals("true")) ? true : false);
		this.config = config;
		this.myMap = myMap;
		this.setStartConnector(cn1);
		this.setEndConnector(cn2);
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", 0);
		initLinkPanel();
	}

	private void initLinkPanel() {

		if (config.getElementOption(ParameterTypes.Details) != null) {
			linkPanel = new SimpleLinkPanel(this, config.getElementOption(ParameterTypes.Details));
		} else {
			linkPanel = new SimpleLinkPanel(this, "true");
		}
		update();
		myMap.add(linkPanel);
		myMap.layout();
	}

	/**
	 * Calculate middlepoint of a beziercurve
	 * 
	 * @param p1
	 *            Point 1
	 * @param p2
	 *            Controlpoint 1
	 * @param p3
	 *            Controlpoint 2
	 * @param p4
	 *            Point 2
	 * @return Middlepoint
	 */
	private Point calculateMiddle(Point p1, Point p2, Point p3, Point p4) {
		float v = 0.5f;
		float a = 1 - v;
		float b = a * a * a;
		float c = v * v * v;
		float resultX = b * p1.getLeft() + 3 * v * a * a * p2.getLeft() + 3 * v * v * a * p3.getLeft() + c * p4.getLeft();
		int X = Math.round(resultX);
		float resultY = b * p1.getTop() + 3 * v * a * a * p2.getTop() + 3 * v * v * a * p3.getTop() + c * p4.getTop();
		int Y = Math.round(resultY);
		Point result = new Point(X, Y);
		return result;
	}

	public Connector getConnector() {
		if (cn == null) {
			cn = UIObjectConnector.wrap(linkPanel);
		}
		return cn;
	}

	public ContentPanel getMap() {
		return myMap;
	}

	@Override
	public void remove() {
		linkPanel.removeFromParent();
		super.remove();
	}

	public void setLineColor(String color) {
		DOM.setStyleAttribute(this.getCurveStyleElement(), "color", color);
	}

	public void setLineWidth(String width) {
		DOM.setStyleAttribute(this.getCurveStyleElement(), "width", width);
	}

	@Override
	protected void update(ConnectionData data) {

		BezierConnectionData bdata = (BezierConnectionData) data;

		Point p1 = (Point) bdata.getPoints().get(0); // get start and end points
		Point p2 = (Point) bdata.getPoints().get(1);
		Point p3 = (Point) bdata.getControlPoints().get(0);
		Point p4 = (Point) bdata.getControlPoints().get(1);
		/*
		 * recalculate positions source: the center of the connection
		 */

		middle = calculateMiddle(p1, p3, p4, p2);

		updateLinkPanelPosition();

		// recalculate all connected connections
		if (cn != null) {
			for (Iterator<Connection> i = cn.getConnections().iterator(); i.hasNext();) {
				Connection con = (Connection) i.next();
				con.update();
			}
		}
		super.update(data);
	}

	protected void updateLinkPanelPosition() {
		if (middle != null) {
			int x = middle.getLeft();
			int y = middle.getTop();

			int posX = (x - 2) - (linkPanel.getWidth() / 2);
			int posY = (y - 2) - (linkPanel.getHeight() / 2);

			if (this.connectedModel != null) {
				this.connectedModel.setValue(ParameterTypes.PosX, posX + "");
				this.connectedModel.setValue(ParameterTypes.PosY, posY + "");
			}

			linkPanel.setPosition(posX, posY);
		}
	}

	public Connector getConnector(Direction dir) {
		return getConnector();
	}

	public Direction getDirection() {
		return null;
	}

	public SimpleLinkPanel getLinkPanel() {
		return linkPanel;
	}

	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vName) {
		if (vName == ParameterTypes.Height) {
			this.linkPanel.setHeight(Integer.parseInt(model.getValue(ParameterTypes.Height)) + 2);
		}

		else if (vName == ParameterTypes.RootElementId) {
			this.linkPanel.setRootElementID(model.getValue(ParameterTypes.RootElementId));
		}

		else if (vName == ParameterTypes.Direction) {
			this.setEndings(this.getEnding(1), this.getEnding(0));
			this.update();
			this.getConnectedModel().setValue(ParameterTypes.Direction, "unchanged");
		}
	}

	private AbstractUnspecifiedElementModel connectedModel = null;

	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}

	public void setConnectedModel(AbstractUnspecifiedElementModel connectedModel) {
		this.connectedModel = connectedModel;
	}

	private ElementInfo config = null;

	public ElementInfo getElementInfo() {
		return config;
	}

	public void addExtendedElement(AbstractExtendedElement element) {
		linkPanel.addExtendedElement(element);
	}

	public void addExtendedElement(AbstractExtendedElement element, int pos) {
		linkPanel.addExtendedElement(element, pos);
	}

	public void removeExtendedElement(AbstractExtendedElement element) {
		linkPanel.removeExtendedElement(element);
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return linkPanel.getExtendedElements();
	}

	public void setHighlight(boolean highlight) {
		linkPanel.setHighlight(highlight);
	}

	public Connector getStartConnector() {
		return startConnector;
	}

	public void setStartConnector(Connector startConnector) {
		this.startConnector = startConnector;
	}

	public Connector getEndConnector() {
		return endConnector;
	}

	public void setEndConnector(Connector endConnector) {
		this.endConnector = endConnector;
	}

	@Override
	public GenericFocusHandler getFocusHandler() {
		return null;
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return null;
	}

	@Override
	public MVCViewSession getMVCViewSession() {
		return null;
	}

	@Override
	public FocusableInterface getFocusParent() {
		return null;
	}

	@Override
	public void setElementFocus(boolean focus) {
	}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		return null;
	}

	@Override
	public void textAreaCallNewHeightgrow(int height) {
		// TODO Auto-generated method stub
		
	}
}