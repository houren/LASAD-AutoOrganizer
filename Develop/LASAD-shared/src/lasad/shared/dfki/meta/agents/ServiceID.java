package lasad.shared.dfki.meta.agents;

import java.io.Serializable;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ServiceID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 468209200679626985L;
	private String agentID;
	private String typeID;
	private ServiceClass serviceClass;
	
	public ServiceID(){
		
	}

	public ServiceID(String agentID, String typeID, ServiceClass serviceClass) {
		this.agentID = agentID;
		this.typeID = typeID;
		this.serviceClass = serviceClass;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public String getAgentID() {
		return agentID;
	}

	public String getTypeID() {
		return typeID;
	}

	public ServiceClass getServiceClass() {
		return serviceClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentID == null) ? 0 : agentID.hashCode());
		result = prime * result
				+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
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
		ServiceID other = (ServiceID) obj;
		if (agentID == null) {
			if (other.agentID != null)
				return false;
		} else if (!agentID.equals(other.agentID))
			return false;
		if (serviceClass != other.serviceClass)
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
		return "[agentID=" + agentID + ", typeID=" + typeID + ", serviceClass="
				+ serviceClass + "]";
	}

}
