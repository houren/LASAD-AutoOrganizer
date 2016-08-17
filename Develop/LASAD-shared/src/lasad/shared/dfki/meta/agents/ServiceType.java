package lasad.shared.dfki.meta.agents;

import java.io.Serializable;

/**
 * Specification of an {@IAgent}'s service that can, in principle, be
 * offered in the end-user environment (e.g., analyzing the workspace content,
 * providing a hint or feedback).
 * 
 * @author oliverscheuer
 * 
 */
public abstract class ServiceType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9043306214564340954L;
	protected ServiceID serviceID = null;
	protected String name = null;
	private String description = null;

	public ServiceType() {

	}

	public ServiceType(ServiceID serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceID getServiceID() {
		return serviceID;
	}

	public void setServiceID(ServiceID serviceID) {
		this.serviceID = serviceID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
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
		ServiceType other = (ServiceType) obj;
		if (serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!serviceID.equals(other.serviceID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServiceType [serviceID=" + serviceID + "]";
	}

}
