package lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class SimpleBoxConnectorElement extends BoxComponent {

	private Element domElement;
	private String myStyle;

	public SimpleBoxConnectorElement(String style) {
		super();
		this.myStyle = style;
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		ComponentHelper.doAttach(this);
	}

	@Override
	protected void onRender(Element target, int index) {
		domElement = DOM.createDiv();
		domElement.setClassName(myStyle);

		setElement(domElement, target, index);
	}
}