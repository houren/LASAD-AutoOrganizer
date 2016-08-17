package lasad.gwt.client.ui.workspace.argumentmap.elements;

import java.util.Collection;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.graphmap.GraphMap;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialog;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateBoxDialogListener;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;

public class CreateBoxDialogListenerArgument extends AbstractCreateBoxDialogListener{
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public CreateBoxDialogListenerArgument(GraphMap map, AbstractCreateBoxDialog dialog) {
		super(map, dialog);
	}
	
	public CreateBoxDialogListenerArgument(GraphMap map, AbstractCreateBoxDialog dialog, TranscriptLinkData tData) {
		super(map, dialog, tData);
	}

	@Override
	protected void onClickSendCreateBoxUpdateToServer(
			TranscriptLinkData myTData, boolean createTranscriptLink,
			ElementInfo info, String mapID, int posX, int posY) {
		Action transcriptLinkAction = null;
		if(createTranscriptLink){
			transcriptLinkAction = actionBuilder.createTranscriptLink(mapID, "LAST-ID", myTData);
		}
		// Send Action --> Server
		ActionPackage actionPackage = actionBuilder.createBoxWithElements(info, mapID, posX, posY);
		actionPackage.addAction(transcriptLinkAction);
		communicator.sendActionPackage(actionPackage);
	}

	@Override
	protected Collection<ElementInfo> getElementsByType(String type) {
		return ((MVCViewSession)myMap.getMyViewSession()).getController().getMapInfo().getElementsByType(type).values();
//		return myMap.getMyViewSession().getController().getMapInfo().getElementsByType(type).values();
	}

}