package lasad.gwt.client.ui.workspace.feedback.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackPanel;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackPanelListener;

public class FeedbackPanelListenerArgument extends AbstractFeedbackPanelListener {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public FeedbackPanelListenerArgument(AbstractFeedbackPanel myReference,
			int clusterID) {
		super(myReference, clusterID);
	}

	@Override
	protected void sendRemoveElement(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, elementID));
	}

}
