package lasad.gwt.client.ui.workspace.chat;

import java.util.Vector;

public interface CommonChatInterface {
	public void addChatMessage(ChatMessage msg);
	public Vector<ChatMessage> getChatMessages();
}
