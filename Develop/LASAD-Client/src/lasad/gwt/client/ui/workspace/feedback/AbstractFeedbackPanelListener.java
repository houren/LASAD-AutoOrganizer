package lasad.gwt.client.ui.workspace.feedback;

import java.util.Vector;

import lasad.shared.communication.objects.Parameter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractFeedbackPanelListener implements EventListener {

	private AbstractFeedbackPanel myReference;
	private int clusterID;

//	private final LASADActionSender communicator = LASADActionSender.getInstance();
//	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public AbstractFeedbackPanelListener(AbstractFeedbackPanel myReference, int clusterID) {
		this.myReference = myReference;
		this.clusterID = clusterID;
	}

	public void onBrowserEvent(Event be) {
		if (be.getTypeInt() == Events.OnClick.getEventCode()) {
			// if (((Element)
			// be.getEventTarget().cast()).getClassName().equals("close-button-highlighted"))
			// {}
			// else if (((Element)
			// be.getEventTarget().cast()).getClassName().equals("details-button-highlighted"))
			// {}

			if (myReference.getController().getElement(clusterID).getValue(ParameterTypes.Details) != null) {
				// detailsVisible = true;

				Vector<Parameter> param = new Vector<Parameter>();
				param.add(new Parameter(ParameterTypes.Highlight, "TRUE"));
				myReference.getController().updateElement(clusterID, param);

				myReference.getMyMap().layout();

				final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent ce) {
						// detailsVisible = false;
						Vector<Parameter> param = new Vector<Parameter>();
						param.add(new Parameter(ParameterTypes.Highlight, "FALSE"));
						myReference.getController().updateElement(clusterID, param);

						myReference.getMyMap().layout();
					}
				};

				if (myReference.getController().getElement(clusterID).getValue(ParameterTypes.ResponseRequired).equalsIgnoreCase("TRUE")) {
					MessageBox box = MessageBox.prompt("Detailed feedback", myReference.getController().getElement(clusterID).getValue(ParameterTypes.Details) + "<br><br>Please explain your thoughts:", true);
					box.addCallback(new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							//String v = be.getValue();

							// detailsVisible = false;
							Vector<Parameter> param = new Vector<Parameter>();
							param.add(new Parameter(ParameterTypes.Highlight, "FALSE"));
							myReference.getController().updateElement(clusterID, param);

							myReference.getMyMap().layout();
							// LASADInfo.display("MessageBox", "You entered '{0}'",
							// new Params(v));

							// TODO Send to server and connected clients...
							// actionBuilder.updateElementWithMultipleValues(mapID,
							// id, values)
						}
					});
				} else {
					MessageBox.alert("Detailed feedback", myReference.getController().getElement(clusterID).getValue(ParameterTypes.Details), l);
				}

			}
		} else if (be.getTypeInt() == Events.OnContextMenu.getEventCode()) {
			be.preventDefault();
//			communicator.sendActionPackage(actionBuilder.removeElement(myReference.getMyMap().getID(), clusterID));
			sendRemoveElement(myReference.getMyMap().getID(), clusterID);
		} else if (be.getTypeInt() == Events.OnMouseOver.getEventCode()) {
			Vector<Parameter> param = new Vector<Parameter>();
			param.add(new Parameter(ParameterTypes.Highlight, "TRUE"));
			myReference.getController().updateElement(clusterID, param);

		} else if (be.getTypeInt() == Events.OnMouseOut.getEventCode()) {
			Vector<Parameter> param = new Vector<Parameter>();
			param.add(new Parameter(ParameterTypes.Highlight, "FALSE"));
			myReference.getController().updateElement(clusterID, param);
		}
		be.stopPropagation();
	}
	
	protected abstract void sendRemoveElement(String mapID, int elementID);
}