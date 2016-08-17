package lasad;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
// deprecated import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import javax.jws.WebMethod;

import lasad.database.DatabaseConnectionHandler;
import lasad.gui.ServerGUI;
import lasad.logging.Logger;
import lasad.logging.commonformat.CfXmppWriter;
import lasad.logging.commonformat.CommonFormatLogger;
import lasad.logging.commonformat.util.CFConstants;
import lasad.logging.debug.DebugLogger;
import lasad.processors.ActionProcessor;
import lasad.shared.ServerInterface;
import lasad.shared.communication.objects.ActionPackage;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = -7503356520419489180L;

	public Config conf;
	public State currentState;
	private ServerGUI gui;

	private static Server singletonInstance = null;

	private List<ActionPackage> apList = new Vector<ActionPackage>();
	private Object apListLock = new Object();

	public ActionProcessor ap;

	public static Server getInstance() {
		return singletonInstance;
	}

	protected Server(String[] args) throws RemoteException {
		super();

		singletonInstance = this;

		this.conf = new Config(this);

		if ("TRUE".equalsIgnoreCase(conf.parameters.get("Server GUI"))) {
			this.gui = new ServerGUI(this);
		} else {
			this.gui = null;
		}

		conf.loadConfigFile(args[0]);
		DatabaseConnectionHandler.initDatabase(this);
		this.currentState = State.getInstance();

		if (Boolean.parseBoolean(conf.parameters.get("Logging"))) {
			// Add all relevant loggers here
			Logger.addLogger(new DebugLogger(this.gui));
			if (Config.xmppLogging) {
				CommonFormatLogger xmppLogger = new CommonFormatLogger(new CfXmppWriter());
				xmppLogger.setLogEnable(CFConstants.LOGGING_ON);
				Logger.addLogger(xmppLogger);
			} else {
				Logger.addLogger(new CommonFormatLogger());
			}
		}

		this.ap = ActionProcessor.getInstance();

		conf.initServerConfiguration();
		startServer();

		System.out.println("Server successfully started!");

		Logger.debugLog("Database initialized!");

	}

	/**
	 * @param args[0] = Config file
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("To run the server provide additional parameters. Use the following syntax:");
			System.err.println("java Server PathToConfigFile");
		}

		else {
			try {
				new Server(args);
			} catch (RemoteException re) {
				re.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void startServer() {

		// TODO Implement RMI Security Manager + Policy-File...
		if (System.getSecurityManager() == null) {
			// RMISecurityManager secure = new RMISecurityManager();
			//
			// PropertyPermission pp = new PropertyPermission("java.rmi.server.useCodebaseOnly", "true");
			//
			// java.rmi.server.useCodebaseOnly

			System.setSecurityManager(new SecurityManager());
		}

		try {

			// TODO Check if there is already an RMI registry open --> Use it, if it is there.

			int registryPort = Integer.parseInt(conf.parameters.get("RMI-Registry Port"));
			LocateRegistry.createRegistry(registryPort);
			Registry rmiReg = LocateRegistry.getRegistry("localhost", registryPort);

			rmiReg.rebind(conf.parameters.get("Servername"), this);
			execute();
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Logger.log("Server successfully started!\n");
	}

	public void closeServer() {
		Logger.debugLog("Server terminated.");
		Logger.endLogging();

		try {
			UnicastRemoteObject.unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			e.printStackTrace();
		}
	}

	@Override
	@WebMethod
	public void doActionOnServer(ActionPackage p) throws RemoteException {

		synchronized (apListLock) {
			int apListSize = apList.size();
			boolean notifyFlag = false;
			if (apListSize == 0) {
				notifyFlag = true;
			}
			if (p != null) {
				apList.add(p);
			}
			if (notifyFlag && apList.size() >= 1) {
				apListLock.notify();
			}
		}

	}

	private ActionPackage getAndRemoveFirstActionPackage() {
		ActionPackage ap = null;
		synchronized (apListLock) {
			while (apList.size() == 0) {
				try {
					Logger.debugLog("Waiting ...size of ActionPackage list is 0 ");
					apListLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// debugLog("Here we go!!!");
			ap = apList.remove(0);
		}
		return ap;
	}

	private void execute() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					ActionPackage p = getAndRemoveFirstActionPackage();
					if (ap != null) {
						Logger.debugLog("About to process AP");
						ap.processActionPackage(p);
						Logger.debugLog("AP processed successfully");
					}
				}
			}
		}.start();
	}

}
