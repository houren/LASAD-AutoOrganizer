package lasad.shared.dfki.meta.agents.provision;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ProvisionTimeDef_Periodically extends ProvisionTimeDef {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4802608690876220552L;
	private Long checkInterval = null;

	public ProvisionTimeDef_Periodically() {
	}
	
	public ProvisionTimeDef_Periodically(Long checkInterval) {
		super();
		this.checkInterval = checkInterval;
	}

	public Long getCheckInterval() {
		return checkInterval;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkInterval == null) ? 0 : checkInterval.hashCode());
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
		ProvisionTimeDef_Periodically other = (ProvisionTimeDef_Periodically) obj;
		if (checkInterval == null) {
			if (other.checkInterval != null)
				return false;
		} else if (!checkInterval.equals(other.checkInterval))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProvisionTimePeriodicallyDef [checkInterval=" + checkInterval
				+ "]";
	}

}
