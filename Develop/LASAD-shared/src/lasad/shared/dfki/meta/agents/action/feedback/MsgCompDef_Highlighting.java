package lasad.shared.dfki.meta.agents.action.feedback;


/**
 * 
 * @author oliverscheuer
 * 
 */
public class MsgCompDef_Highlighting extends MsgCompDef{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1004210882335457525L;

	@Override
	public String toString() {
		return "MsgCompDef_Highlighting []";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

}
