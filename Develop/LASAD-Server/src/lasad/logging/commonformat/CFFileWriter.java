package lasad.logging.commonformat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import lasad.logging.Logger;
import lasad.logging.commonformat.util.CF2StringUtil;
import lasad.logging.commonformat.util.CFConstants;
import lasad.logging.commonformat.util.CFWriter;
import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Preamble;

public class CFFileWriter implements CFWriter {

	private HashMap<String, PrintWriter> outputFileMap;
	private CF2StringUtil cf2SUtil;
	private boolean cfLogSubDirOK = false;

	public CFFileWriter() {
		if(!existCFLogSubDirectory()){
			cfLogSubDirOK = createCFLogSubDirectory();
		}else{
			cfLogSubDirOK = true;
		}
		
		if (cfLogSubDirOK){
			outputFileMap = new HashMap<String, PrintWriter>();
			cf2SUtil = new CF2StringUtil();
		}
	}
	
	@Override
	public void closeWriter(String sessionID){
		Logger.debugLog("##Closing writer:" + sessionID);
		if(!isAllOK()){
			return;
		}
		closeFile(sessionID);
	}
	
	@Override
	public void writePreamble(String sessionID, Preamble preamble) {
		if(!isAllOK()){
			return;
		}
		PrintWriter outputFile = getOutputFile(sessionID);
		if(outputFile != null){
			outputFile.println(CF2StringUtil.getFileHeader());
			outputFile.println(cf2SUtil.preamble2String(preamble));
			outputFile.println(CF2StringUtil.getOpenActions());
		}
		try{outputFile.close();}catch(Exception e){}
	}
	
	@Override
	public void writeActionList( String sessionID, List<Action> actionList) {
		if(!isAllOK()){
			return;
		}
		PrintWriter outputFile = getOutputFile(sessionID);
		if(outputFile != null){
			for(Action act : actionList){
				outputFile.println(cf2SUtil.action2String(act));
			}
		}
		try{outputFile.close();}catch(Exception e){}
	}
	
	public void closeFile(String sessionID){
		if(outputFileMap.containsKey(sessionID)){
			PrintWriter outputFile = outputFileMap.remove(sessionID);
			if (outputFile != null){
				Logger.debugLog("Closing file for session:" + sessionID);
				outputFile.println(CF2StringUtil.getFileTail());
				outputFile.flush();
				outputFile.close();
				outputFile = null;
			 }
		}
	}
	
	private PrintWriter getOutputFile(String sessionID){
		PrintWriter outputFile = null;
		if(outputFileMap.containsKey(sessionID)){
			Logger.debugLog("##File for session " + sessionID + "already exists");
			outputFile = outputFileMap.get(sessionID);
		}
		else{
			try {
				Logger.debugLog("##Creating file for session:" + sessionID);
				//we can add a timestamp too.
				String pathName = CFConstants.LOG_FILE_PATH + CFConstants.CF_LOG_FILE_PATH +
									CFConstants.CF_LOG_FILE_PREFIX + sessionID +
									CFConstants.LOG_FILE_EXTENSION;
				outputFile = new PrintWriter(new BufferedWriter(new FileWriter(pathName)), true);
				outputFileMap.put(sessionID, outputFile);
			} catch (IOException e) {
				Logger.logError(e.getMessage());
				e.printStackTrace();
			}
		}
		return outputFile;
	}
	
	private boolean existCFLogSubDirectory(){
		boolean retVal = false;
		String pathName = CFConstants.LOG_FILE_PATH + CFConstants.CF_LOG_FILE_PATH;
		File directory = new File(pathName);
		if(directory.exists()){
			retVal = true;
			Logger.debugLog("##Directory " + pathName + " already exist");
		}
		else{
			Logger.debugLog("##Directory " + pathName + " does not exist");
		}
		//File[] files = directory.listFiles();
		return retVal;
	}
	
	private boolean createCFLogSubDirectory(){
		boolean retVal = false;
		String pathName = CFConstants.LOG_FILE_PATH + CFConstants.CF_LOG_FILE_PATH;
		File directory = new File(pathName);
		if(directory.mkdir()){   //or mkdirs()???
			retVal = true;
			Logger.debugLog("##Directory " + pathName + " created successfully");
		} else{
			Logger.logError("##ERROR while creatind directory " + pathName);
		}
		return retVal;
	}
	
	public boolean isAllOK(){
		boolean retVal = false;
		retVal = cfLogSubDirOK;
		return retVal;
	}
	
}