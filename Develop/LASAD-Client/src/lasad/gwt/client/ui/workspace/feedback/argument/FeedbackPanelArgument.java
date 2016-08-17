package lasad.gwt.client.ui.workspace.feedback.argument;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.AbstractMVController;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackPanel;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackPanelListener;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class FeedbackPanelArgument extends AbstractFeedbackPanel {
	
	MVController controller = null;

	public FeedbackPanelArgument(AbstractGraphMap map) {
		super(map);
		controller = getController();
	}

	@Override
	protected AbstractFeedbackPanelListener getFeedbackPanelListener(
			AbstractFeedbackPanel myReference, int clusterID) {
		return new FeedbackPanelListenerArgument(myReference, clusterID);
	}

	@Override
	public MVController getController() {
		if(controller == null) {
			controller = LASAD_Client.getMVCController(myMap.getID());
		}
		return controller;
	}

	@Override
	public void setController(AbstractMVController controller) {
		this.controller = (MVController)controller;
	}

}
