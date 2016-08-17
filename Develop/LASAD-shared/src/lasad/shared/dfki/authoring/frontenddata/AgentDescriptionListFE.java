package lasad.shared.dfki.authoring.frontenddata;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * a list of agent descriptions (to be used in the graphical frontend).
 * 
 * @author oliverscheuer
 * 
 */
public class AgentDescriptionListFE implements Serializable, ObjectFE {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8973365884094215879L;
	private List<AgentDescriptionFE> agentDescriptions = new Vector<AgentDescriptionFE>();
	
	public AgentDescriptionListFE(){
		
	}

	public void add(AgentDescriptionFE agentDescr) {
		agentDescriptions.add(agentDescr);
	}
	
	public List<AgentDescriptionFE> getAgentDescriptions() {
		return agentDescriptions;
	}

	@Override
	public String toString() {
		return "AgentDescriptionListFE [agentDescriptions=" + agentDescriptions
				+ "]";
	}

}
