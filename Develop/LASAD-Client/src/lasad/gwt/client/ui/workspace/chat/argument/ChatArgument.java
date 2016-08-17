package lasad.gwt.client.ui.workspace.chat.argument;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.chat.AbstractChat;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;

public class ChatArgument extends AbstractChat {
	
	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public ChatArgument(AbstractGraphMap map) {
		super(map);
	}

	@Override
	protected void sendChatMessage(String mapID, String msg) {
		communicator.sendActionPackage(actionBuilder.sendChatMessage(mapID, msg));
	}

}
