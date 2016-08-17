package lasad.logging.commonformat.util;

import java.util.List;

import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Preamble;

public interface CFWriter {
	
	public void closeWriter(String sessionID);
	public void writePreamble(String sessionID, Preamble preamble);
	public void writeActionList(String sessionID, List<Action> actionList);
}
