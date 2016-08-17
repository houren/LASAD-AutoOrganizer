package lasad.shared.dfki.meta.agents.action.feedback;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class MsgCompDef_LongText extends MsgCompDef{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4975556852195147458L;
	private String text = null;
	private boolean responsePrompt = false;

	// inferred from text
	private List<String> dynamicParameters = new Vector<String>();

	public MsgCompDef_LongText(){
		
	}
	public MsgCompDef_LongText(String text) {
		this.text = text;
		extractDynamicParamters(text);
	}

	public boolean isResponsePrompt() {
		return responsePrompt;
	}

	public void setResponsePrompt(boolean responsePrompt) {
		this.responsePrompt = responsePrompt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTextParametersInserted(Map<String, String> params2values) {
		String newText = text;
		for (String paramName : params2values.keySet()) {
			String paramValue = params2values.get(paramName);
			newText = newText.replace("[##" + paramName + "##]", paramValue);
		}
		return newText;
	}

	public void extractDynamicParamters(String text) {
		int searchStartIndex = 0;
		boolean doSearch = true;
		while (doSearch) {
			int patternStart = text.indexOf("[##", searchStartIndex);
			if (patternStart != -1) {
				int patternEnd = text.indexOf("##]", patternStart + 3);
				if (patternEnd != -1) {
					String parameter = text.substring(patternStart + 3,
							patternEnd);
					dynamicParameters.add(parameter);
					searchStartIndex = patternEnd;
				} else {
					doSearch = false;
				}
			} else {
				doSearch = false;
			}

		}
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
		MsgCompDef_LongText other = (MsgCompDef_LongText) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LongText [text=" + text + "]";
	}

}
