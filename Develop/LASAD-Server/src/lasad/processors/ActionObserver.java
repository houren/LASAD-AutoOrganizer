package lasad.processors;

import lasad.entity.User;
import lasad.shared.communication.objects.Action;

public interface ActionObserver {
	public boolean processAction(Action a, User u, String sessionID);
	

}
