package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class FrameType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8079588979302743432L;
	private static FrameType instance = new FrameType();
	
	private FrameType(){
		super();
		lasadElementType = "frame";
		jessTemplateName = "elem_frame";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "LINK", JessDataType.STRING, ComparisonGroup.NONE);
	}
	
	public static FrameType getInstance(){
		return instance;
	}
}
