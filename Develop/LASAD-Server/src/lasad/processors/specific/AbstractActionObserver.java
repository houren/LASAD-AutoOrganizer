package lasad.processors.specific;

import lasad.Server;
import lasad.processors.ActionObserver;
import lasad.processors.ActionProcessor;

/**
 * AbstractActionObserver is part of the Observer Pattern to handle the ActionPackages and their processing.
 * 
 * @author SN
 */
public abstract class AbstractActionObserver implements ActionObserver {
	Server myServer;
	ActionProcessor aproc;

	/**
	 * Constructor
	 */
	public AbstractActionObserver() {
		this.myServer = Server.getInstance();
		this.aproc = ActionProcessor.getInstance();
		aproc.addListener(this);
	}

}
