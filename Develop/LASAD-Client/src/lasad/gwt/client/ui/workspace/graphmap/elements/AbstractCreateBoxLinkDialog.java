package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Collection;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * This class creates the dialog for adding both (and corresponding link) via click and drag from another box.
 * If using the drop down argument map menu to add a link, see AbstractCreateSpecialLinkDialog.
 * If creating only a link between two existing boxes via drag, see AbstractCreateLinkDialog.
 * Documentation added by Kevin Loughlin, 16 June 2015
 * @author Unknown
 */

public abstract class AbstractCreateBoxLinkDialog extends BoxComponent implements FocusableInterface {

	protected GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;

	private EventListener boxLinkDialogListener;

	private int step;
	private ElementInfo boxConfig = null;
	private AbstractBox startBox = null;

	public AbstractCreateBoxLinkDialog(GraphMap map, AbstractBox start, int posX, int posY, int step, ElementInfo boxConfig) {
		this(map, start, posX, posY, step);

		this.boxConfig = boxConfig;
	}

	public AbstractCreateBoxLinkDialog(GraphMap map, AbstractBox start, int posX, int posY, int step) {
		this.myMap = map;
		this.startBox = start;
		this.step = step;

		//boxLinkDialogListener = new CreateBoxLinkDialogListener(myMap, this);
		boxLinkDialogListener = createAbstractCreateBoxLinkDialogListener(myMap, this);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		this.setPosition(posX, posY);

		// Get Focus
		myMap.getFocusHandler().releaseAllFocus();
		myMap.getFocusHandler().setFocus(this);

	}
	protected abstract AbstractCreateBoxLinkDialogListener createAbstractCreateBoxLinkDialogListener(GraphMap map, AbstractCreateBoxLinkDialog dialog);

	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);

		if (step == 1) {
			this.headerElement.setInnerText("Please choose a Contribution...");
		} else if (step == 2) {
			this.headerElement.setInnerText("Please choose a Relation...");
		}

	}
	
	protected abstract Collection<ElementInfo> getElementsByType(String type);

	public void initMenu() {
		if (step == 1) {
//			for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("box").values()) {
			for (ElementInfo info : getElementsByType("box")) {
				Element tmpElement = DOM.createDiv();
				tmpElement.setClassName("dialog-text");
				tmpElement.setInnerText(info.getElementOption(ParameterTypes.Heading));
				this.actions.add(tmpElement);

				DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
				DOM.setEventListener(tmpElement, boxLinkDialogListener);
			}
		} else if (step == 2) {
//			for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("relation").values()) {
			for (ElementInfo info :getElementsByType("relation")) {
				Element tmpElement = DOM.createDiv();
				tmpElement.setClassName("dialog-text");
				tmpElement.setInnerText(info.getElementOption(ParameterTypes.Heading));
				this.actions.add(tmpElement);

				DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
				DOM.setEventListener(tmpElement, boxLinkDialogListener);
			}
		}

		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");
		cancelElement.setInnerText("Cancel");
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(cancelElement, boxLinkDialogListener);
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractCreateBoxLinkDialog.this.el().updateZIndex(1);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public ElementInfo getBoxConfig() {
		return boxConfig;
	}

	public void setBoxConfig(ElementInfo boxConfig) {
		this.boxConfig = boxConfig;
	}

	public AbstractBox getStartBox() {
		return startBox;
	}

	public void setStartBox(AbstractBox startBox) {
		this.startBox = startBox;
	}

	public FocusableInterface getFocusParent() {
		return myMap;
	}

	public void setElementFocus(boolean focus) {
		if (!focus) {
			// focus got lost
			removeFromParent();
		}
	}
}