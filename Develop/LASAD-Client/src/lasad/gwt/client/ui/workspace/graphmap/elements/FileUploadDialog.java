package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Vector;

import lasad.gwt.client.ui.common.FocusableInterface;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
//import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;

public class FileUploadDialog extends BoxComponent implements FocusableInterface {

	private Element rootElement, headerElement;
	private GraphMap myMap;

	private Vector<Element> actions = null;
	//private GXTFileUploadDialog fileUpload = null;

	public FileUploadDialog(GraphMap targetMap, int left, int top) {
		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();
		initMenu();

		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}

		// Create a FormPanel and point it at a service.
		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "fileupload");

		// set the form to use the POST method, and multipart MIME encoding for
		// file upload
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// Create a FileUpload widget.
		final FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		//Element input = DOM.createElement("input");
		/*InputElement inputElement = */Document.get().createFileInputElement(); // GXT
																				// Library
																				// checken

		addToMap(targetMap, left, top);

		// Get Focus
		myMap = targetMap;
		targetMap.getFocusHandler().setFocus(this);
	}

	public FileUploadDialog(GraphMap targetMap, AbstractLinkPanel targetLink, int left, int top) {
		actions = new Vector<Element>();

		rootElement = DOM.createDiv();
		rootElement.setClassName("dialog-root");

		initHeading();

		initMenu();
		for (int i = 0; i < actions.size(); i++) {
			DOM.appendChild(rootElement, actions.get(i));
		}
		// DOM.setIntStyleAttribute(this.getElement(), "zIndex",
		// XDOM.getTopZIndex());
		addToMap(targetMap, left, top);

		// Get Focus
		myMap = targetMap;
		targetMap.getFocusHandler().setFocus(this);

	}

	private void addToMap(GraphMap targetMap, int left, int top) {
		targetMap.add(this);
		// DOM.setIntStyleAttribute(this.getElement(), "zIndex",
		// XDOM.getTopZIndex());
		setPosition(left, top);
		targetMap.layout();
	}

	private void initHeading() {
		this.headerElement = DOM.createDiv();
		this.headerElement.setInnerText("Are you sure?");
		this.headerElement.setClassName("dialog-heading");
		DOM.setStyleAttribute(headerElement, "backgroundColor", "#E6E6E6");
		DOM.appendChild(rootElement, headerElement);
	}

	private void initMenu() {
		Element yesElement = DOM.createDiv();
		yesElement.setClassName("dialog-text");
		yesElement.setInnerText("Yes, delete it");
		this.actions.add(yesElement);

		DOM.sinkEvents(yesElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		// DOM.setEventListener(yesElement, deleteDialogListener);

		Element noElement = DOM.createDiv();
		noElement.setClassName("dialog-text");
		noElement.setInnerText("No, keep it");
		this.actions.add(noElement);

		DOM.sinkEvents(noElement, Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONCLICK);
		// DOM.setEventListener(noElement, deleteDialogListener);

	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		FileUploadDialog.this.el().updateZIndex(1);
	}

	@Override
	public FocusableInterface getFocusParent() {
		return myMap;
	}

	@Override
	public void setElementFocus(boolean focus) {
		if (!focus) {
			// Focus got lost
			removeFromParent();
		}
	}

}