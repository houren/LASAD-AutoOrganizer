package lasad.gwt.client.ui.link;

import java.util.Vector;

import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.helper.connection.BezierTwoEndedConnection;
import lasad.gwt.client.helper.connection.data.BezierConnectionData;
import lasad.gwt.client.helper.connection.data.ConnectionData;
import lasad.gwt.client.helper.connection.data.Point;
import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.helper.connector.UIObjectConnector;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.GraphicalElementInterface;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;

public abstract class AbstractLink extends BezierTwoEndedConnection implements MVCViewRecipient,
ExtendedElementContainerInterface,
// SelectableElementInterface,
GraphicalElementInterface {

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	protected AbstractLinkPanel linkPanel;

	private GraphMap myMap;
	protected Connector cn;

	private Connector startConnector;

	private Connector endConnector;
	protected Point position;
	private float v = 0.5f;

	protected boolean editMode = false;
	protected boolean principles = false;

	public AbstractLink(GraphMap myMap, ElementInfo config, Connector cn1,
			Connector cn2, boolean isReplay) {
		super(cn1, cn2, config.getUiOption(ParameterTypes.LineWidth), config
				.getUiOption(ParameterTypes.LineColor), (config.getElementOption(ParameterTypes.Endings)
						.equals("true")) ? true : false);
		this.config = config;
		this.myMap = myMap;
		this.setStartConnector(cn1);
		this.setEndConnector(cn2);
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", 0);
		initLinkPanel(isReplay);

	}

	private void initLinkPanel(boolean isReplay) {
		// TODO ParameterTypes.Details should be read from elementUIOptions

		if (config.getElementOption(ParameterTypes.Details) != null) {
			//linkPanel = new LinkPanel(this, config.getElementOption(ParameterTypes.Details),isReplay);
			linkPanel = createAbstractLinkPanel(this, config.getElementOption(ParameterTypes.Details),isReplay);
		} else {
			//linkPanel = new LinkPanel(this, "true",isReplay);
			linkPanel = createAbstractLinkPanel(this, "true",isReplay);
		}
		update();
		myMap.add(linkPanel);
		myMap.layout();
	}
	protected abstract AbstractLinkPanel createAbstractLinkPanel(AbstractLink myLink, String details, boolean isR);

	/**
	 * Calculate the point of a beziercurve
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
//	protected Point calculatePosition(Point p1, Point p2, Point p3, Point p4,float v) {
	protected Point calculatePosition(ConnectionData data) {
		Point result = null;
		if (data instanceof BezierConnectionData) {
			BezierConnectionData bdata = (BezierConnectionData) data;
			Point p1 = (Point) bdata.getPoints().get(0); // get start and end
														// points
			Point p4 = (Point) bdata.getPoints().get(1);
			Point p2 = (Point) bdata.getControlPoints().get(0);
			Point p3 = (Point) bdata.getControlPoints().get(1);
			// p1, p3, p4, p2
			
			float a = 1 - v;
			float b = a * a * a;
			float c = v * v * v;
			float resultX = b * p1.getLeft() + 3 * v * a * a * p2.getLeft() + 3 * v
					* v * a * p3.getLeft() + c * p4.getLeft();
			int X = Math.round(resultX);
			float resultY = b * p1.getTop() + 3 * v * a * a * p2.getTop() + 3 * v
					* v * a * p3.getTop() + c * p4.getTop();
			int Y = Math.round(resultY);
			result = new Point(X, Y);
		}
		return result;
	}

	public Connector getConnector() {
		if (cn == null) {
			cn = UIObjectConnector.wrap(linkPanel);
		}
		return cn;
	}

	public GraphMap getMap() {
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
	//TODO ZyG,19.05.2011 
	BezierConnectionData bdata;
	public BezierConnectionData getBezierConnectionData()
	{
		return bdata;
	}

	@Override
	protected void update(ConnectionData data) {
		try {
			super.update(data);
		} catch (Exception e) {
			Logger.log("update(ConnectionData) in class Link.java failed.", Logger.DEBUG_ERRORS);
		}
	} 

	protected void updateLinkPanelPosition() {
		if (position != null) {
			int x = position.getLeft();
			int y = position.getTop();

			int posX = (x - 2) - (linkPanel.getWidth() / 2);
			int posY = (y - 2) - (linkPanel.getHeight() / 2);

			if (this.connectedModel != null) {
				this.connectedModel.setValue(ParameterTypes.PosX, posX + "");
				this.connectedModel.setValue(ParameterTypes.PosY, posY + "");
			}

			linkPanel.setPosition(posX, posY);

		}
	}
	
	public void setOnlyAuthorCanModify(boolean onlyAuthorCanModify) {
		linkPanel.setOnlyAuthorCanModify(onlyAuthorCanModify);
	}
	
	public void setCommitTextByEnter(boolean commitTextByEnter) {
		linkPanel.setCommitTextByEnter(commitTextByEnter);
	}

	public Connector getConnector(Direction dir) {
		return getConnector();
	}

	public Direction getDirection() {
		return null;
	}

	public AbstractLinkPanel getLinkPanel() {
		return linkPanel;
	}
	
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vName) {
		
		if (vName == ParameterTypes.Percent) {
//			Point p1 = (Point) bdata.getPoints().get(0); // get start and end
//			Point p2 = (Point) bdata.getPoints().get(1);
//			Point p3 = (Point) bdata.getControlPoints().get(0);
//			Point p4 = (Point) bdata.getControlPoints().get(1);
			v = Float.parseFloat(model.getValue(ParameterTypes.Percent));
//			position = calculatePosition(p1, p3, p4, p2,v);
			position = calculatePosition(bdata);
			updateLinkPanelPosition();
		} else if (vName == ParameterTypes.Height) {
			this.linkPanel.setHeight(Integer.parseInt(model.getValue(ParameterTypes.Height)) + 2);
		}
		else if (vName == ParameterTypes.RootElementId) {
			this.linkPanel.setRootElementID(model.getValue(ParameterTypes.RootElementId));
		}
		else if (vName == ParameterTypes.Highlighted) {
			if (model.getValue(ParameterTypes.Highlighted).equalsIgnoreCase("TRUE")) {
				this.setHighlight(true);
			} else if (model.getValue(ParameterTypes.Highlighted).equalsIgnoreCase("FALSE")) {
				this.setHighlight(false);
			}
		}
		else if (vName == ParameterTypes.Faded) {
			if (model.getValue(ParameterTypes.Faded).equalsIgnoreCase("TRUE")) {
				this.linkPanel.setFadedOut(true);
				return;
			} else if (model.getValue(ParameterTypes.Faded).equalsIgnoreCase("FALSE")) {
				this.linkPanel.setFadedOut(false);
				return;
			}
		}

		else if (vName == ParameterTypes.Direction) {
			this.setEndings(this.getEnding(1), this.getEnding(0));
			this.update();
//			this.getConnectedModel().setValue(ParameterTypes.Direction, "unchanged");
		}

		// check if update results in an highlight
		this.getLinkPanel().checkForHighlight(model.getValue(ParameterTypes.UserName));
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		try {
			if (cn != null) {
				cn.disconnect();
			}
		} catch (Exception e) {
			Logger.log("Could not disconnect connector of element: "
					+ model.getId(), Logger.DEBUG_ERRORS);
		} finally {
			myMap.remove(this);
			this.remove();
		}
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		linkPanel.establishModelConnection(model);
		if (connectedModel == null) {
			connectedModel = model;
			return true;
		} else {
			return false;
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
	public GenericFocusHandler getFocusHandler() {
		return getMap().getFocusHandler();
	}

	@Override
	public FocusableInterface getFocusParent() {
		return getMap();
	}

	@Override
	public void setElementFocus(boolean focus) {
	}

	@Override
	public AbstractMVCViewSession getMVCViewSession() {
		return this.getMap().getMyViewSession();
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return linkPanel.getExtendedElements();
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		return getMap().getHighlightHandler();
	}

	public HighlightableElementInterface getHighlightParent() {
		return getMap();
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

	public void setPercentage(float v)
	{
		this.v = v;
	}
	@Override
	public void textAreaCallNewHeightgrow(int height) {
	}
}