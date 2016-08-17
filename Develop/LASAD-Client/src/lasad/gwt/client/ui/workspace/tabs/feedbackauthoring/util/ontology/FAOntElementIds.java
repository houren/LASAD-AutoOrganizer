package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.ontology;

import java.util.HashMap;
import java.util.Map;

/**
 * Elements of the Feedback Authoring ontology
 * @author Anahuac
 *
 */
public class FAOntElementIds {
	
	public static final String BOX = "box";
	public static final String NO_BOX = "nobox";
	public static final String DIRECTED_LINK = "directedlink";
	public static final String NO_DIRECTED_LINK = "nodirectedlink";
	public static final String LINK = "link";
	public static final String NO_LINK = "nolink";
	public static final String TEXT = "text";

	public static final String BOX_LABEL = "Box";
	public static final String NO_BOX_LABEL = "No-Box";
	public static final String DIRECTED_LINK_LABEL = "Directed-Link";
	public static final String NO_DIRECTED_LINK_LABEL = "No-Directed-Link";
	public static final String LINK_LABEL = "Link";
	public static final String NO_LINK_LABEL = "No-Link";
	
	static Map<String, String> id2Label = new HashMap<String, String>();
	static{
		id2Label.put(BOX, BOX_LABEL);
		id2Label.put(NO_BOX, NO_BOX_LABEL);
		id2Label.put(DIRECTED_LINK, DIRECTED_LINK_LABEL);
		id2Label.put(NO_DIRECTED_LINK, NO_DIRECTED_LINK_LABEL);
		id2Label.put(LINK, LINK_LABEL);
		id2Label.put(NO_LINK, NO_LINK_LABEL);
	}
	
	public static String getLabelForElemId(String elementId){
		return id2Label.get(elementId);
	}
	
}
