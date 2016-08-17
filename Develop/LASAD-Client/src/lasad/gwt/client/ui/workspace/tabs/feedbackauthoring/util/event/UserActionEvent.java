package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.event;


/**
 * @author Anahuac
 *
 */
public interface UserActionEvent {
	
	public abstract String getProperty(ActionUserEventProperty name);
	
	public abstract void addProperty(ActionUserEventProperty name, String value);
	
}
