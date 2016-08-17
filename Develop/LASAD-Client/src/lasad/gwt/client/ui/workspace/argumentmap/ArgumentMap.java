package lasad.gwt.client.ui.workspace.argumentmap;

import java.util.List;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.workspace.argumentmap.elements.CreateBoxDialogArgument;
import lasad.gwt.client.ui.workspace.argumentmap.elements.CreateBoxLinkDialogArgument;
import lasad.gwt.client.ui.workspace.feedback.AbstractFeedbackCluster;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxLinkDialog;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.GWT;

/**
 * Documentation?
 */

public class ArgumentMap extends GraphMap {
	
	protected LASADActionSender communicator = LASADActionSender.getInstance();
	protected ActionFactory actionBuilder = ActionFactory.getInstance();

	protected MVCViewSession myViewSession;

	lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	public ArgumentMap(GraphMapSpace parentElement) {
		super(parentElement);
	}
	
	@Override
	protected void init() {
		this.myViewSession = (MVCViewSession) myArgumentMapSpace.getSession();
		ID = myViewSession.getController().getMapInfo().getMapID();
	}

	@Override
	protected void sendHandleEventResultToServer(String mapID, int cursorID,
			String username, int x, int y) {
		communicator.sendActionPackage(actionBuilder.updateMyCursorPositionNonPersistent(ID, myAwarenessCursorID, LASAD_Client.getInstance().getUsername(), x, y));
	}
	
	@Override
	protected AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map,
			int posX, int posY, TranscriptLinkData tData) {
		return new CreateBoxDialogArgument(map, posX, posY, tData);
	}

	@Override
	protected AbstractCreateBoxDialog createCreateBoxDialog(GraphMap map,
			int posX, int posY) {
		return new CreateBoxDialogArgument(map, posX, posY);
	}

	@Override
	protected AbstractCreateBoxLinkDialog createCreateBoxLinkDialog(
			GraphMap map, AbstractBox start, int posX, int posY, int step) {
		return new CreateBoxLinkDialogArgument(map, start, posX, posY, step);
	}

	@Override
	public MVCViewSession getMyViewSession() {
		return myViewSession;
	}

	@Override
	public void setMyViewSession(AbstractMVCViewSession myViewSession) {
		this.myViewSession = (MVCViewSession) myViewSession;
	}

	@Override
	public void deleteAllFeedbackClusters() {
		MVController controller = LASAD_Client.getMVCController(this.ID);
		if (controller != null) {
			List<Component> mapComponents = this.getItems();

			for (Component c : mapComponents) {
				if (c instanceof AbstractFeedbackCluster) {
					AbstractFeedbackCluster fc = (AbstractFeedbackCluster) c;
					communicator.sendActionPackage(actionBuilder.removeElement(this.ID, fc.getID()));
				}
			}
		}
	}

}
