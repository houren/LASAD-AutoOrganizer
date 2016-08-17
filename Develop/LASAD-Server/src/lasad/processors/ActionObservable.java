package lasad.processors;

import java.util.Vector;

import lasad.entity.User;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.commands.Commands;

/**
 * ActionObservable is part of the Observer Pattern to handle the ActionPackages and their processing.
 * 
 * @author SN
 */
public class ActionObservable {
	private Vector<ActionObserver> observers;

	/**
	 * Constructor
	 */
	public ActionObservable() {
		observers = new Vector<ActionObserver>();
	}

	/**
	 * adds an observer. ActionPackages will be sent to all observers.
	 * 
	 * @param observer
	 * @author SN
	 */
	public void addListener(ActionObserver observer) {
		observers.add(observer);
	}

	/**
	 * removes an observer.
	 * 
	 * @param observer
	 * @author SN
	 */
	public void removeListener(ActionObserver observer) {
		observers.remove(observer);
	}

	/**
	 * send an Action to all registered observers. If there is no observer to process the given action, the action is printed out
	 * to the console.
	 * 
	 * @param a
	 * @param u
	 * @param sessionID
	 * @author SN
	 */
	void notifyObservers(Action a, User u, String sessionID) {
		boolean actionHasBeenProcessed = false;
		for (ActionObserver observer : observers) {
			actionHasBeenProcessed = observer.processAction(a, u, sessionID) || actionHasBeenProcessed;
		}
		if (!actionHasBeenProcessed && !a.getCmd().equals(Commands.Heartbeat)) {
			outputUnprocessedAction(a);
		}
	}

	/**
	 * If an action is not processed it is printed to the console
	 * 
	 * @param a
	 * @author SN
	 */
	private void outputUnprocessedAction(Action a) {
		System.err.println("Action has not been processed: " + a.toString());
	}
}
