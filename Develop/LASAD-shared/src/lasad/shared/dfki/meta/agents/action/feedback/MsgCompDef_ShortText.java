package lasad.shared.dfki.meta.agents.action.feedback;


/**
 * 
 * @author oliverscheuer
 * 
 */
public class MsgCompDef_ShortText extends MsgCompDef{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3345451524746103417L;
	private String text = null;

	public MsgCompDef_ShortText(){
		
	}
	public MsgCompDef_ShortText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MsgCompDef_ShortText other = (MsgCompDef_ShortText) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShortText [text=" + text + "]";
	}

}
