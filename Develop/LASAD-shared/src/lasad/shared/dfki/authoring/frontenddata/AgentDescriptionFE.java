package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;

import lasad.shared.dfki.meta.agents.ActionAgentConfigData;
import lasad.shared.dfki.meta.agents.SupportedOntologiesDef;


/**
 * an agent description (to be used in the graphical frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class AgentDescriptionFE implements Serializable, ObjectFE{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1826330979600182245L;
	private String agentID;
	private String displayName = null;
	private String description = null;

	private boolean configCompleted = false;
	private boolean confReadable = false;
	private boolean confWritable = false;

	private SupportedOntologiesDef supportedOntology = null;
	private ActionAgentConfigData confData = null;
	
	public AgentDescriptionFE(){
		
	}

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isConfigCompleted() {
		return configCompleted;
	}

	public void setConfigCompleted(boolean configCompleted) {
		this.configCompleted = configCompleted;
	}

	public boolean isConfReadable() {
		return confReadable;
	}

	public void setConfReadable(boolean confReadable) {
		this.confReadable = confReadable;
	}

	public boolean isConfWritable() {
		return confWritable;
	}

	public void setConfWritable(boolean confWritable) {
		this.confWritable = confWritable;
	}

	public SupportedOntologiesDef getSupportedOntology() {
		return supportedOntology;
//		return null;
	}
	
	public void setSupportedOntology(SupportedOntologiesDef supportedOntology) {
		this.supportedOntology = supportedOntology;
	}
	
	public ActionAgentConfigData getConfData() {
		return confData;
//		return null;
	}
	
	public void setConfData(ActionAgentConfigData confData) {
		this.confData = confData;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentDescriptionFE other = (AgentDescriptionFE) obj;
		if (agentID == null) {
			if (other.agentID != null)
				return false;
		} else if (!agentID.equals(other.agentID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AgentDescriptionFE [agentID=" + agentID + ", displayName="
				+ displayName + ", configCompleted=" + configCompleted
				+ ", confReadable=" + confReadable + ", confWritable="
				+ confWritable + ", supportedOntology=" + supportedOntology
				+ ", confData=" + confData + "]";
	}

}
