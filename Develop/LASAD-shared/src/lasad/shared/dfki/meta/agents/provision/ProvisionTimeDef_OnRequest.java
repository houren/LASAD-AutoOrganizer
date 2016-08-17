package lasad.shared.dfki.meta.agents.provision;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ProvisionTimeDef_OnRequest extends ProvisionTimeDef {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2959234631423108191L;
	private String displayName = null;

	public ProvisionTimeDef_OnRequest() {
	}
	
	public ProvisionTimeDef_OnRequest(String displayName) {
		super();
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
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
		ProvisionTimeDef_OnRequest other = (ProvisionTimeDef_OnRequest) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProvisionTimeOnRequestDef [displayName=" + displayName + "]";
	}

}
