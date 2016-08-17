package lasad.gwt.client.ui.workspace.awareness;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DragEvent;
import com.extjs.gxt.ui.client.event.DragListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * This provides the functionality to track the cursor of users that are working
 * on the same map In addition, there is support to create a common group
 * pointer
 */
public abstract class AbstractAwarenessCursor extends BoxComponent implements MVCViewRecipient {

	private final lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);
//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	private AbstractGraphMap myMap;

	private int id;
	private final String username;
	private String status = "";
	private boolean group = false; // true if it is a group cursor

	private Element rootElement, cursorIconElement, textElement;

	private Listener<ComponentEvent> componentListener = null;
	
	public AbstractAwarenessCursor(AbstractGraphMap correspondingMap, String elementID, String username) {
		this.myMap = correspondingMap;
		this.id = Integer.parseInt(elementID);
		this.username = username;

		initElements();
	}

	public AbstractAwarenessCursor(AbstractGraphMap correspondingMap, String elementID, boolean group) {
		this.myMap = correspondingMap;
		this.id = Integer.parseInt(elementID);
		this.group = group;
		this.username = "";

		this.myMap.setMyAwarenessCursorID(Integer.parseInt(elementID));

		initElements();
	}

	/**
	 * Handle the whole set of the BoxComponentEvent handling Don't call this
	 * method directly, it will be done afterRender
	 */
	private void setComponentHandling() {
		componentListener = new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if (be.getEventTypeInt() == Events.OnDoubleClick.getEventCode()) {
					onDoubleClick(be);
				}
			}

			private void onDoubleClick(ComponentEvent be) {
				MVController controller = LASAD_Client.getMVCController(myMap.getID());

				if (controller != null) {
//					communicator.sendActionPackage(actionBuilder.removeElement(myMap.getID(), id));
					sendRemoveElementToServer(myMap.getID(), id);
				}
				be.cancelBubble();
			}
		};
		this.addListener(Events.OnDoubleClick, componentListener);
		this.sinkEvents(Events.OnDoubleClick.getEventCode());
	}
	
	protected abstract void sendRemoveElementToServer(String mapID, int elementID);

	private void initElements() {

		rootElement = DOM.createDiv();
		rootElement.setClassName("awareness_cursor_root");

		if (group) {
			cursorIconElement = DOM.createDiv();
			cursorIconElement.setClassName("awareness_groupcursor_icon");

			textElement = DOM.createDiv();
			textElement.setClassName("awareness_groupcursor_text");

			textElement.setInnerHTML("<b>" + myConstants.GroupPointer() + "</b><br>" + status);
		} else {
			cursorIconElement = DOM.createDiv();
			cursorIconElement.setClassName("awareness_cursor_icon");

			textElement = DOM.createDiv();
			textElement.setClassName("awareness_cursor_text");

			textElement.setInnerHTML("<b>" + username + "</b><br>" + status);
		}

		DOM.appendChild(rootElement, cursorIconElement);
		DOM.appendChild(rootElement, textElement);
	}

	/**
	 * Init the DragListener to move the box via drag & drop
	 * 
	 * @param myMap
	 */
	protected void makeDraggable(final AbstractGraphMap myMap) {
//		AwarenessCursorDraggable d = new AwarenessCursorDraggable(this, myMap.getID(), id);
		AbstractAwarenessCursorDraggable d = createAwarenessCursorDraggable(this, myMap.getID(), id);
		d.setUseProxy(false);
		d.setMoveAfterProxyDrag(false);
		d.setContainer(myMap);

		// Movement Listener
		DragListener dListener = new DragListener() {

			@Override
			public void dragStart(DragEvent de) {
//				MapElementSelection.unselectAllElements(myMap);
				super.dragStart(de);
			}

			@Override
			public void dragMove(DragEvent de) {
				super.dragMove(de);
			}

			public void dragEnd(DragEvent dev) {
				super.dragEnd(dev);
			}
		};
		d.addDragListener(dListener);
	}
	
	protected abstract AbstractAwarenessCursorDraggable createAwarenessCursorDraggable(AbstractAwarenessCursor dragComponent, String mapID, int elementID);

	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		setElement(this.rootElement, target, index);
	}

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vName) {
		Logger.log("[lasad.gwt.client.ui.workspace.awareness.AwarenessCursor] changeValueMVC: name: " + vName + " value: " + model.getValue(vName), Logger.DEBUG);
		if (vName == ParameterTypes.PosX || vName == ParameterTypes.PosY) {
			this.setPosition(Integer.parseInt(model.getValue(ParameterTypes.PosX)), Integer.parseInt(model.getValue(ParameterTypes.PosY)));
			this.setZIndex(XDOM.getTopZIndex());
		}
	}

	private AbstractUnspecifiedElementModel connectedModel;

	public AbstractUnspecifiedElementModel getConnectedModel() {
		return connectedModel;
	}

	public void setConnectedModel(AbstractUnspecifiedElementModel connectedModel) {
		this.connectedModel = connectedModel;
	}

	@Override
	public void deleteModelConnection(AbstractUnspecifiedElementModel model) {
		if (model.getId() == myMap.getMyAwarenessCursorID()) {
			myMap.setRefreshMyCursor(false);
			myMap.setMyAwarenessCursorID(-1);			
		}
		this.removeFromParent();
	}

	@Override
	public boolean establishModelConnection(AbstractUnspecifiedElementModel model) {
		return false;
	}

	protected void afterRender() {
		super.afterRender();

		if (group) {
			makeDraggable(myMap);
			setComponentHandling();
		}

	}
}