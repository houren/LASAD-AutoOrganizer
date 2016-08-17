package lasad.gwt.client.ui.workspace.details;

import java.util.Vector;

import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainer;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.gwt.client.ui.common.ExtendedElementContainerMasterInterface;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.common.GenericFocusHandler;
import lasad.gwt.client.ui.common.highlight.GenericHighlightHandler;
import lasad.gwt.client.ui.common.highlight.HighlightableElementInterface;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;


/**
 * Visualization of the details on the right-hand side of the screen as part of the east panel of the argument map space
 * 
 * @author Frank Loll
 * 
 */

public class SelectionDetailsPanel extends ContentPanel implements MVCViewRecipient, ExtendedElementContainerMasterInterface, ExtendedElementContainerInterface {

	private AbstractGraphMap myMap;
	private ExtendedElementContainer extendedElementContainer = null;
	private SimplifiedBoxHeaderElement header;
	private TranscriptLinkData tData = null;
	
	private ElementInfo info = null;
	private Element root;
	
	private String title = "Error", color;
	private int width = 0, height = 0;

	public TranscriptLinkData getTData() {
		return tData;
	}

	public String getColor() {
		return color;
	}

	public void setTData(TranscriptLinkData tData) {
		this.tData = tData;
	}

	public void setTitle(String t) {
		this.title = t;
		this.header.setTitle(t);
	}
	
	public void setColor(String c) {
		this.color = c;
		DOM.setStyleAttribute(header.getContent(), "backgroundColor", color);
	}

	public SelectionDetailsPanel(AbstractGraphMap map) {
		this.myMap = map;
		this.setHeading("Selection Details");
		this.setBodyBorder(false);

		this.root = DOM.createDiv();

		initExtendedElementContainer();
	}

	public SelectionDetailsPanel(AbstractGraphMap map, ElementInfo info) {
		this(map);
		this.info = info;
		
		header = new SimplifiedBoxHeaderElement(this.title);
		DOM.insertChild(this.root, header.getContent(), 0);
	}

	protected void afterRender() {
		super.afterRender();

		this.getBody().appendChild(root);
		DOM.setStyleAttribute(this.getBody().dom, "position", "relative");
	}

	public void addContent(Element content) {
		DOM.appendChild(this.getElement(), content);
		this.layout();
	}

	private void initExtendedElementContainer() {
		this.extendedElementContainer = new ExtendedElementContainer(this);
		this.extendedElementContainer.setElementSpacing(1);
		this.extendedElementContainer.setPaddingTop(1);
		this.extendedElementContainer.setPaddingBottom(1);
		this.extendedElementContainer.setPaddingLeft(1);
		this.extendedElementContainer.setPaddingRight(1);
		// add Container Element to DIV
		DOM.appendChild(this.root, extendedElementContainer.getContainerElement());

		extendedElementContainer.setWidth(296);
		extendedElementContainer.setHeight(myMap.getHeight() - 50);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		if(width > 0 && this.width != width) {
			extendedElementContainer.setWidth(width);
			this.width = width;
		}
		if(height-16 > 0 && this.height != height) { // -16 is for header and space between header and container
			extendedElementContainer.setHeight(this.getInnerHeight()-16);
			this.height = height;
		}
	}

	@Override
	public void extendedElementContainerCallNewHeight(int height) {	}

	@Override
	public void extendedElementContainerPublishedNewMaxHeight(int height) { 
		// Ignore, not needed
	}

	@Override
	public void extendedElementContainerPublishedNewMinHeight(int height) {
		// Ignore, not needed
	}

	@Override
	public GenericFocusHandler getFocusHandler() {
		return myMap.getFocusHandler();
	}

	@Override
	public GenericHighlightHandler getHighlightHandler() {
		// Ignore, not needed
		return null;
	}

	@Override
	public AbstractMVCViewSession getMVCViewSession() {
		return myMap.getMyViewSession();
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element) {
		extendedElementContainer.addExtendedElement(element);
	}

	@Override
	public void addExtendedElement(AbstractExtendedElement element, int pos) {
			extendedElementContainer.addExtendedElement(element, pos);
	}

	@Override
	public ElementInfo getElementInfo() {
		return info;
	}

	@Override
	public Vector<AbstractExtendedElement> getExtendedElements() {
		return extendedElementContainer.getExtendedElements();
	}

	@Override
	public void removeExtendedElement(AbstractExtendedElement element) {
		extendedElementContainer.removeExtendedElement(element);
	}

	@Override
	public void setElementFocus(boolean focus) {}

	@Override
	public HighlightableElementInterface getHighlightParent() {
		// Ignore, not needed
		return null;
	}

	@Override
	public void setHighlight(boolean highlight) {}

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {
		// Ignore, not needed
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		myMap.getMyArgumentMapSpace().changeSelectionDetailsPanelTo(new SelectionDetailsPanel(myMap));
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		if (connectedModel == null) {
			connectedModel = model;
			return true;
		} else {
			return false;
		}
	}

	private AbstractUnspecifiedElementModel connectedModel;

	@Override
	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}

	public void setConnectedModel(AbstractUnspecifiedElementModel connectedModel) {
		this.connectedModel = connectedModel;
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void textAreaCallNewHeightgrow(int height) {
		// TODO Auto-generated method stub
		
	}
}
