package lasad.shared.dfki.meta.agents.action.feedback;


/**
 * 
 * @author oliverscheuer
 * 
 */
public class PatternFilterDef_LastModTime extends PatternFilterDef{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5214391509183972249L;
	private PatternFilterDef_LastModTimeSetting setting;
	private Integer referenceValue;

	public PatternFilterDef_LastModTime(){
		
	}
	public PatternFilterDef_LastModTime(PatternFilterDef_LastModTimeSetting setting,
			Integer referenceValue) {
		this.setting = setting;
		this.referenceValue = referenceValue;
	}

	public PatternFilterDef_LastModTimeSetting getSetting() {
		return setting;
	}

	public void setSetting(PatternFilterDef_LastModTimeSetting setting) {
		this.setting = setting;
	}

	public Integer getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(Integer referenceValue) {
		this.referenceValue = referenceValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((referenceValue == null) ? 0 : referenceValue.hashCode());
		result = prime * result + ((setting == null) ? 0 : setting.hashCode());
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
		PatternFilterDef_LastModTime other = (PatternFilterDef_LastModTime) obj;
		if (referenceValue == null) {
			if (other.referenceValue != null)
				return false;
		} else if (!referenceValue.equals(other.referenceValue))
			return false;
		if (setting != other.setting)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FeedbackFilterLastModTime [setting=" + setting
				+ ", referenceValue=" + referenceValue + "]";
	}

}
