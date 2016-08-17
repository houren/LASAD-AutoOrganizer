package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class XmppMessageButtonType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3599155163726617180L;
	private static XmppMessageButtonType instance = new XmppMessageButtonType();

	private XmppMessageButtonType() {
		super();
		lasadElementType = "xmppMessageButton";
		jessTemplateName = "elem_xmppMessageButton";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "LINK", JessDataType.STRING, ComparisonGroup.NONE);
	}

	public static XmppMessageButtonType getInstance() {
		return instance;
	}
}
