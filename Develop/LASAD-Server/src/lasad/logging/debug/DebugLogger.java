package lasad.logging.debug;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import lasad.gui.ServerGUI;
import lasad.logging.LoggerInterface;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;

/**
 * Format-independent logger. Should be used for debugging and error reports.
 * Each logger should be a Thread to avoid a slow-down of the whole server.
 * 
 * This logger will buffer all incoming messages and write them to file and gui after 5 seconds.
 * 
 * @author Frank Loll
 *
 */
public class DebugLogger extends Thread implements LoggerInterface {

	private Object lockObject = new Object();
	
	private FileWriter fw;
	private ServerGUI gui;
	
	private volatile StringBuilder sb;
	
	public DebugLogger(ServerGUI gui) {
		this.sb = new StringBuilder();
		this.gui = gui;
		
		try {
			fw = new FileWriter("log/debug.log", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.start();
	}
	
	@Override
	public void log(String text) {
		synchronized(lockObject) {
			Date time = new Date(System.currentTimeMillis());
			
			sb.append(time.toString());
			sb.append(": ");
			sb.append(text);
			sb.append(System.getProperty("line.separator"));
			
			lockObject.notify();
		}	
	}
	
	@Override
	public void logError(String text) {
		this.log(text);
	}
	
	@Override
	public void log(ActionPackage p) {
		this.log(p.toString());
	}

	@Override
	public void log(Action a) {
		this.log(a.toString());
	}

	@Override
	public void endLogging() {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				synchronized(lockObject) {
					lockObject.wait();
					if(sb.length() > 0) {
						String s = sb.toString();
									
						fw.append(s);
						
						if(this.gui != null) {
							gui.logToScreen(s);
						}
						else {
							System.out.print(s);
						}
						
								
						sb = new StringBuilder();
					}
				}
			}
			catch (Exception e) {
				System.err.println(e);
			}
		}
	}
}
