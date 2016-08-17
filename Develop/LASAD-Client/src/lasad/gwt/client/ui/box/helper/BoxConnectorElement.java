package lasad.gwt.client.ui.box.helper;

import lasad.gwt.client.ui.box.AbstractBox;

import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class BoxConnectorElement extends BoxComponent {

	private AbstractBox myBox;

	private Element domElement;
	private String myStyle;

	// public int startedX;
	// public int startedY;

	public BoxConnectorElement(AbstractBox box, String style) {
		super();
		this.myBox = box;
		this.myStyle = style;

		makeDraggable();

	}

	@Override
	protected void afterRender() {
		super.afterRender();
		ComponentHelper.doAttach(this);
	}

	protected void onRender(Element target, int index) {
		domElement = DOM.createDiv();
		domElement.setClassName(myStyle);

		setElement(domElement, target, index);
	}

	private void makeDraggable() {
		/*final DragSource dragSource = */ new DragSource(this) {
			@Override
			public void onDragStart(DNDEvent e) {
				e.setData(myBox);
//				super.onDragStart(e);
			}
		};
	}
}