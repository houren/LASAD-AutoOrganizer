package lasad.gwt.client.ui.box;

import lasad.gwt.client.helper.connector.Connector;
import lasad.gwt.client.helper.connector.UIObjectConnector;

import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/** 
 * Used to have a target connector for drag-n-drop during link creation process
 * 
 * @author Frank Loll
 *
 */
public class SimpleConnector extends BoxComponent {

	private Element rootElement;
	private Connector cn = null;
	
	public SimpleConnector(int left, int top) {
		
		rootElement = DOM.createDiv();
		DOM.setStyleAttribute(rootElement, "width", "1px");
		DOM.setStyleAttribute(rootElement, "height", "1px");
		DOM.setStyleAttribute(rootElement, "background", "white");
		setPosition(left, top);
		
		this.cn = UIObjectConnector.wrap(this);
	}
	
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}
	
	public Connector getConnector() {
		return cn;
	}
}