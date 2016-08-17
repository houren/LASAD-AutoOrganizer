package lasad.shared.dfki.meta.ontology;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Description of tools available in a session in the EUE
 * 
 * @author oliverscheuer
 * 
 */
public class ToolContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2840153145821883831L;
	String mapID = null;
	String mapName = null;
	String templateName = null;

	SortedSet<String> transcriptIDs = new TreeSet<String>();
	boolean simpleChat = false;
	boolean soChat = false;

	/**
	 * 
	 */
	
	public ToolContext() {
		super();
	}

	public String getMapID() {
		return mapID;
	}

	public void setMapID(String mapID) {
		this.mapID = mapID;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public SortedSet<String> getTranscriptIDs() {
		return transcriptIDs;
	}

	public void setTranscriptIDs(SortedSet<String> transcriptIDs) {
		this.transcriptIDs = transcriptIDs;
	}

	public void addTranscriptID(String transcriptID) {
		transcriptIDs.add(transcriptID);
	}

	public boolean isSimpleChat() {
		return simpleChat;
	}

	public void setSimpleChat(boolean simpleChat) {
		this.simpleChat = simpleChat;
	}

	public boolean isSoChat() {
		return soChat;
	}

	public void setSoChat(boolean soChat) {
		this.soChat = soChat;
	}

	@Override
	public String toString() {
		return "ToolContext [mapID=" + mapID + ", mapName=" + mapName
				+ ", templateName=" + templateName + ", transcriptIDs="
				+ transcriptIDs + ", simpleChat=" + simpleChat + ", soChat="
				+ soChat + "]";
	}

}
