package lasad.gwt.client.ui.workspace.feedback;

import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Yet again, little to no documentation/commenting of any sort provided.
 * @author ???
 */

public class ShowFeedbackTextWindow extends BoxComponent implements FocusableInterface {

	private GraphMap myMap;
	private Element rootElement, headerElement;
	private String text;

	public ShowFeedbackTextWindow(GraphMap map, String text, int posX, int posY) {
		this.myMap = map;
		this.text = text;

		rootElement = DOM.createDiv();
		rootElement.setClassName("feedback-root");

		initHeading();

		initContent();

		this.setPosition(posX, posY);

		// Get Focus
		myMap.getFocusHandler().setFocus(this);
	}

	public void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Feedback: ");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	public void initContent() {
		Element tmpElement = DOM.createDiv();
		tmpElement.setClassName("dialog-text");
		tmpElement.setInnerText(text);
		DOM.appendChild(rootElement, tmpElement);
	}

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		ShowFeedbackTextWindow.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if (!focus) {
			removeFromParent();
		}
	}
}