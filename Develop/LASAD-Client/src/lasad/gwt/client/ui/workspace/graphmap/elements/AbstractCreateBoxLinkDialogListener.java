package lasad.gwt.client.ui.workspace.graphmap.elements;

import java.util.Collection;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * This class represents the abstract action handler for the CreateBoxLink dialog box.
 */

public abstract class AbstractCreateBoxLinkDialogListener implements EventListener {

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	protected GraphMap myMap;
	private AbstractCreateBoxLinkDialog myDialog;

	public AbstractCreateBoxLinkDialogListener(GraphMap map, AbstractCreateBoxLinkDialog dialog) {
		this.myMap = map;
		this.myDialog = dialog;
	}

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnMouseOver.getEventCode()) {
			handleMouseOver(be);
		} else if (be.getTypeInt() == Events.OnClick.getEventCode()) {
			handleOnClick(be);
		} else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()) {
			handleMouseOut(be);

		}
		be.stopPropagation();
	}

	private void handleOnClick(Event be) {
		if (myDialog.getStep() == 1) {
			// Choose a BoxType
//			for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("box").values()) {
			for (ElementInfo info : getElementsByType("box")) {
				if (((Element) be.getEventTarget().cast()).getInnerText().equals(info.getElementOption(ParameterTypes.Heading))) {
					// Call Step 2
					//myMap.add(new CreateBoxLinkDialog(myMap, myDialog.getStartBox(), myDialog.getPosition(true).x, myDialog.getPosition(true).y, 2, info));
					myMap.add(createCreateBoxLinkDialog(myMap, myDialog.getStartBox(), myDialog.getPosition(true).x, myDialog.getPosition(true).y, 2, info));
					myMap.layout();
					break;
				}
			}
		} else if (myDialog.getStep() == 2) {
			// Box and Link was choosen
//			for (ElementInfo info : myMap.getMyViewSession().getController().getMapInfo().getElementsByType("relation").values()) {
			for (ElementInfo info : getElementsByType("relation")) {
				if (((Element) be.getEventTarget().cast()).getInnerText().equals(info.getElementOption(ParameterTypes.Heading))) {
					// Send Action --> Server
					
					onClickSendUpdateToServer(myDialog.getBoxConfig(), info, myMap.getID(), myDialog.getPosition(true).x, myDialog.getPosition(true).y, String.valueOf(myDialog.getStartBox().getConnectedModel().getId()), "LAST-ID");
				}

					//communicator.sendActionPackage(actionBuilder.createBoxAndLink(myDialog.getBoxConfig(), info, myMap.getID(), myDialog.getPosition(true).x, myDialog.getPosition(true).y, String.valueOf(myDialog.getStartBox().getConnectedModel().getId()), "LAST-ID"));
					
						
					// ActionSet actionSet = new ActionSet(myMap.getID());
					//					
					// // Add CreateBoxAction
					// Vector<LASADActionSetEntryInterface> actions =
					// LASADActionFactory.createBoxWithElements(myDialog.getBoxConfig(),myDialog.getPosition(true).x,myDialog.getPosition(true).y);
					// for (LASADActionSetEntryInterface action : actions){
					// actionSet.addAction(action);
					// }
					//					
					// for(LASADActionSetEntryInterface action :
					// LASADActionFactory.createLinkWithElements(info,myDialog.getStartBox().getConnectedModel().getId(),0)){
					// actionSet.addAction(action);
					// }
					//					

			}
		}
		myDialog.removeFromParent();
	}
	protected abstract Collection<ElementInfo> getElementsByType(String type);
	protected abstract AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(GraphMap map, AbstractBox start, int posX, int posY, int step, ElementInfo boxConfig);
	protected abstract void onClickSendUpdateToServer(ElementInfo boxInfo, ElementInfo linkInfo, String mapID, int x, int y, String start, String end);

	private void handleMouseOut(Event be) {
		// End hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text-highlighted")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text");
		}
	}

	private void handleMouseOver(Event be) {
		// Start hover effect
		if (((Element) be.getEventTarget().cast()).getClassName().equals("dialog-text")) {
			((Element) be.getEventTarget().cast()).setClassName("dialog-text-highlighted");
		}
	}
}