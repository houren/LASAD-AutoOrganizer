package lasad.gwt.client.communication.eventservice.events;

import lasad.shared.communication.objects.ActionPackage;
import de.novanic.eventservice.client.event.Event;

public class ActionPackageEvent implements Event {
	private static final long serialVersionUID = -1981410194973389321L;

	private ActionPackage actionPackage;

	public ActionPackageEvent() {}

	public ActionPackageEvent(ActionPackage actionPackage) {
		setActionPackage(actionPackage);
	}

	public ActionPackage getActionPackage() {
		return actionPackage;
	}

	public void setActionPackage(ActionPackage actionPackage) {
		this.actionPackage = actionPackage;
	}
}