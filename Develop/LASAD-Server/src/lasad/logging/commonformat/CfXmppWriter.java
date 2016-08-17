package lasad.logging.commonformat;

import java.util.List;

import lasad.logging.commonformat.util.CF2StringUtil;
import lasad.logging.commonformat.util.CFWriter;
import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Preamble;
import de.kuei.metafora.xmpp.XMPPBridge;

public class CfXmppWriter implements CFWriter{

	public static String DEPLOYMENT_VERSION = "false";
//	public static String DEPLOYMENT_VERSION = "true";

	
	static {
		try {
			XMPPBridge.createConnection("LasadLogger", "LasadLoggerTest", "LasadLoggerTest", "logger@conference.metafora.ku-eichstaett.de", "LasadLoggerTest", "LasadLoggerTest");
//			XMPPBridge.createConnection("LasadLogger", "LasadLogger8080", "LasadLogger8080", "logger@conference.metafora.ku-eichstaett.de", "LasadLogger8080", "LasadLogger8080");
//			XMPPBridge.createConnection("LasadLogger", "LasadLogger", "LasadLogger", "logger@conference.metafora.ku-eichstaett.de", "LasadLogger", "LasadLogger");
		}
		catch(Exception e){
			System.out.println("[CfXmppWriter] error creating connection - " + e.getMessage());
		}
	}
	
	private XMPPBridge xmppBridge;
	
	private CF2StringUtil cf2SUtil;
	 
	 public CfXmppWriter(){
		try{
			cf2SUtil = new CF2StringUtil();
			xmppBridge = XMPPBridge.getConnection("LasadLogger");
			xmppBridge.connect(true);
			xmppBridge.sendMessage("LasadLogger connected at " + System.currentTimeMillis());
		} catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void closeWriter(String sessionID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePreamble(String sessionID, Preamble preamble) {
		//not needed for metafora logging
//		try {
//			xmppBridge.sendMessage(CF2StringUtil.getFileHeader());
//			xmppBridge.sendMessage(cf2SUtil.preamble2String(preamble));
//			xmppBridge.sendMessage(CF2StringUtil.getOpenActions());
//		} catch (Exception e) {
//	        e.printStackTrace();
//	    }
		
	}

	@Override
	public void writeActionList(String sessionID, List<Action> actionList) {
		
		for(Action act : actionList){
			try {
				if (isCurrentAction(act)){
					xmppBridge.sendMessage(cf2SUtil.action2String(act));
				}
			} catch (Exception e) {
		        e.printStackTrace();
		    }
			
		}
	}

	private boolean isCurrentAction(Action act) {
		
		long delay = 20000;
		long time = Long.parseLong(act.getTime());
		if (time > System.currentTimeMillis() - delay){
			return true;
		}
		return false;
	}

}
