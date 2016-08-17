package lasad.shared.dfki.meta.agents.action.feedback;


/**
 * 
 * @author oliverscheuer
 * 
 */
public class PatternFilterDef_User extends PatternFilterDef{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8195564123585310173L;
	private PatternFilterDef_UserSetting setting;

	public PatternFilterDef_User(){
		
	}
	public PatternFilterDef_User(PatternFilterDef_UserSetting setting) {
		this.setting = setting;
	}

	public PatternFilterDef_UserSetting getSetting() {
		return setting;
	}

	public void setSetting(PatternFilterDef_UserSetting setting) {
		this.setting = setting;
	}

	@Override
	public boolean isUserSpecific() {
		return (setting != PatternFilterDef_UserSetting.NONE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		PatternFilterDef_User other = (PatternFilterDef_User) obj;
		if (setting != other.setting)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FeedbackFilterUser [setting=" + setting + "]";
	}

}
