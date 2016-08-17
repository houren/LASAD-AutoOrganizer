package lasad.shared.dfki.meta.agents.analysis.counter;

/**
 * (see {@link InstanceTypeSpecific})
 * 
 * @author oliverscheuer
 * 
 */
public class InstanceTypeSpecific_Pattern extends InstanceTypeSpecific {

	/**
	 * 
	 */
	private static final long serialVersionUID = 391993837054866894L;
	private String agentID;
	private String typeID;

	public InstanceTypeSpecific_Pattern(){
		
	}
	public InstanceTypeSpecific_Pattern(String agentID, String typeID) {
		this.agentID = agentID;
		this.typeID = typeID;
	}

	public String getAgentID() {
		return agentID;
	}

	public String getTypeID() {
		return typeID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentID == null) ? 0 : agentID.hashCode());
		result = prime * result + ((typeID == null) ? 0 : typeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceTypeSpecific_Pattern other = (InstanceTypeSpecific_Pattern) obj;
		if (agentID == null) {
			if (other.agentID != null)
				return false;
		} else if (!agentID.equals(other.agentID))
			return false;
		if (typeID == null) {
			if (other.typeID != null)
				return false;
		} else if (!typeID.equals(other.typeID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[agentID=" + agentID + ", typeID=" + typeID + "]";
	}

}
