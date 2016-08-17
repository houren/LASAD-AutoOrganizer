package lasad.gwt.client.ui.workspace.tableview;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;

public class ElementLayoutContainer extends LayoutContainer{

	private Button button;
	

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setBorders(true);
		setSize(100, 100);
		
		button = new Button("blank");
		add(button);
		
	}

}
