package lasad.gwt.client.ui.workspace.feedback.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackCluster;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;

public class FeedbackClusterArgument extends AbstractFeedbackCluster {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public FeedbackClusterArgument(GraphMap correspondingMap, int elementID,
			String feedbackMsg, String posX, String posY, boolean responseReq) {
		super(correspondingMap, elementID, feedbackMsg, posX, posY, responseReq);
	}

	public FeedbackClusterArgument(GraphMap correspondingMap, int elementID,
			String feedbackMsg, String detailedFeedbackMsg, String posX,
			String posY, boolean responseReq) {
		super(correspondingMap, elementID, feedbackMsg, detailedFeedbackMsg, posX,
				posY, responseReq);
	}

	@Override
	protected void sendRemoveElementToServer(String mapID, int elementID) {
		communicator.sendActionPackage(actionBuilder.removeElement(mapID, elementID));
	}

}
