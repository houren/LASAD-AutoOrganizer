package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Collection;
import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractCreateBoxDialog extends BoxComponent implements FocusableInterface {

	protected GraphMap myMap;
	private Element rootElement, headerElement;
	private Vector<Element> actions = null;

	private EventListener boxDialogListener;

	public AbstractCreateBoxDialog(GraphMap map, int posX, int posY) {
		this.myMap = map;
		
		//boxDialogListener = new CreateBoxDialogListener(myMap, this);
		boxDialogListener = createCreateBoxDialogListener(myMap, this);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu(false);
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		this.setPosition(posX, posY);

		// Get Focus
		myMap.getFocusHandler().releaseAllFocus();
		myMap.getFocusHandler().setFocus(this);
	}
	protected abstract AbstractCreateBoxDialogListener createCreateBoxDialogListener(GraphMap map, AbstractCreateBoxDialog dialog);
	protected abstract AbstractCreateBoxDialogListener createCreateBoxDialogListener(GraphMap map, AbstractCreateBoxDialog dialog, TranscriptLinkData tData);

	public AbstractCreateBoxDialog(GraphMap map, int posX, int posY, TranscriptLinkData tData) {
		this.myMap = map;

		//boxDialogListener = new CreateBoxDialogListener(myMap, this, tData);
		boxDialogListener = createCreateBoxDialogListener(myMap, this, tData);

		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu(true);
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		this.setPosition(posX, posY);
		myMap.getFocusHandler().releaseAllFocus();
	}

	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Please choose a Contribution...");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	/**
	 * 
	 * @param filterTranscriptSupport Need when only those boxes are supposed to
	 * be created that support transcript linking. Set false to turn off transcript support filtering
	 */
	public void initMenu(boolean filterTranscriptSupport) {
//		for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("box").values()) {
		for (ElementInfo info : getElementsByType("box")) {
				Element tmpElement = DOM.createDiv();
				tmpElement.setClassName("dialog-text");
				tmpElement.setInnerText(info.getElementOption(ParameterTypes.Heading));
				
				if (filterTranscriptSupport) {
					if (info.getChildElements().containsKey("transcriptlink")) {
						this.actions.add(tmpElement);
					}
				}
				else {
					this.actions.add(tmpElement);
				}

				DOM.sinkEvents(tmpElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
				DOM.setEventListener(tmpElement, boxDialogListener);
		}
		Element cancelElement = DOM.createDiv();
		cancelElement.setClassName("dialog-text");
		cancelElement.setInnerText("Cancel");
		this.actions.add(cancelElement);

		DOM.sinkEvents(cancelElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		DOM.setEventListener(cancelElement, boxDialogListener);
	}
	
	protected abstract Collection<ElementInfo> getElementsByType(String type);

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		AbstractCreateBoxDialog.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if (!focus) {
			// Focus got lost, delete Element
			removeFromParent();
		}
	}
}