package lasad.logging.commonformat;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import lasad.logging.LoggerInterface;
import lasad.logging.commonformat.util.CFConstants;
import lasad.logging.commonformat.util.CFTranslationResult;
import lasad.logging.commonformat.util.CFWriter;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.Parameter;

/**
 * Each logger should be a Thread to avoid a slow-down of the whole server.
 *
 */
public class CommonFormatLogger extends Thread implements LoggerInterface {
	
	private List<ActionPackage>apList = new Vector<ActionPackage>();
	private Object apListLock = new Object();
	private int logEnable = CFConstants.LOGGING_ON;
	private Object logEnableLock = new Object(); 
	private Action2CFTranslator action2CFTrans;
	private CFWriter cfWriter;
	private Hashtable<Integer, Integer> sessionIDTracker;
	

	public CommonFormatLogger(){
		this (new CFFileWriter());
	}
	
	public CommonFormatLogger(CFWriter cfWriterIn) {
		super(CFConstants.CF_THREAD_NAME);
		sessionIDTracker = new Hashtable<Integer, Integer>();
		action2CFTrans = new Action2CFTranslator();
		cfWriter = cfWriterIn;
		
		this.start();
	}

	@Override
	public void log(String text) {
		//Not implemented
	}

	@Override
	public void logError(String text) {
		//Not implemented
	}

	@Override
	public void log(ActionPackage p) {
		addActionPackage(p);
	}

	@Override
	public void log(Action a) {
		//Not implemented
	}

	@Override
	public void endLogging() {
		closeWriters();
	}
	
	private ActionPackage copyActionPackage(ActionPackage actionPackage){
		ActionPackage newAP = new ActionPackage();
		for (Parameter param : actionPackage.getParameters()){
			//TODO: Warum nicht addParameter(param) ???
			newAP.addParameter(param.getType(), new String(param.getValue()));
//			newAP.addParameter(new String(param.getType()), new String(param.getValue()));
		}
		for (Action oldAction : actionPackage.getActions()){
			Action newAction = new Action(oldAction.getCmd(), oldAction.getCategory());
			
			for (Parameter param : oldAction.getParameters()){
				newAction.addParameter(param.getType(), new String(param.getValue()));
//				newAction.addParameter(new String(param.getType()), new String(param.getValue()));
			}
			newAP.addAction(newAction);
		}
		return newAP;
	}
	
	private void addActionPackage(ActionPackage actionPackage){
		if(!isLogEnable()){
			return;
		}
		synchronized(apListLock) {
			apList.add(copyActionPackage(actionPackage));
			if (apList.size() == 1){
				apListLock.notify();
			}
		}
	}

	public void addActionPackage(List<ActionPackage> actionPackageList){
		if(!isLogEnable()){
			return;
		}
		synchronized(apListLock) {
			boolean flagNotify = false;
			if (apList.size() == 0){
				flagNotify = true;
			}
			for(ActionPackage ap : actionPackageList){
				addActionPackage(ap);
			}
			//apList.addAll(actionPackageList);
			if (flagNotify && apList.size() > 0){
				apListLock.notify();
			}
		}
	}
	
	public ActionPackage getAndRemoveFirstActionPackage(){
		ActionPackage ap = null;
		synchronized(apListLock) {
			while (apList.size() == 0){
				try {
					System.out.println("Waiting...size of ActionPackage list is 0 ");
					apListLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//System.out.println("Here we go!!!");
			ap = apList.remove(0);
        }
		return ap;
	}
	
	public boolean isLogEnable() {
		boolean retVal = false;
		synchronized(logEnableLock) {
			retVal = (logEnable == CFConstants.LOGGING_ON);
        }
		return retVal;
	}

	public void setLogEnable(int value) {
		synchronized(logEnableLock) {
			logEnable = value;
        }
	}
	
	@Override
	public void run() {
		while(true){
			ActionPackage ap = getAndRemoveFirstActionPackage();
			if (ap != null){
				CFTranslationResult result = action2CFTrans.translateAP2CF(ap);
				if(registerSession(result.getSessionID())){
					writeResult(result);
				}
			}
		}
	}
	
	private void writeResult(CFTranslationResult result){
		String sessionID = result.getSessionID();
		if (result.getActionList().size() > 0){
			cfWriter.writeActionList(sessionID, result.getActionList());
		}
		if (result.getPreamble() != null){
			cfWriter.writePreamble(sessionID, result.getPreamble());
		}
	}
	
	private boolean registerSession(String sessionID){
		boolean retVal = false;
		if(sessionID == null || sessionID.equals("")){
			return retVal;
		}
		Integer sID = new Integer(sessionID);
		if(!sessionIDTracker.containsKey(sID)){
			sessionIDTracker.put(sID, new Integer(0));
		}
		retVal = true;
		return retVal;
	}
	
	private void unregisterSession(String sessionID){
		Integer sID = new Integer(sessionID);
		sessionIDTracker.remove(sID);
	}
	
	private List<String> getAllSessions(){
		List<String> sessionList = new Vector<String>();
		Set<Integer> sessionSet = sessionIDTracker.keySet();
		
		for(Integer session1 : sessionSet){
			sessionList.add(session1.toString());
		}
		return sessionList;
	}
	
	private void closeWriter(String sessionID){
		unregisterSession(sessionID);
		cfWriter.closeWriter(sessionID);
	}
	
	private void closeWriters(){
		List<String> sessionList = getAllSessions();
		for(String session : sessionList){
			closeWriter(session);
		}
		
	}

}
