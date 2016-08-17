package lasad.logging;

import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;

/**
 * Each logger should be a Thread to avoid a slow-down of the whole server.
 * @author Frank Loll
 *
 */
public interface LoggerInterface {
	
	/**
	 * Direct log of specific text
	 * @param text
	 */
	public void log(String text);
	
	/**
	 * Direct error logging
	 * @param text
	 */
	public void logError(String text);
	
	/**
	 * Logging of an ActionPackage
	 * @param p
	 */
	public void log(ActionPackage p);
	
	/**
	 * Logging of a specific Action
	 * @param a
	 */
	public void log(Action a);
	
	public void endLogging();
}
