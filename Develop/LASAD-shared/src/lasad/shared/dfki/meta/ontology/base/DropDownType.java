package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class DropDownType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1640223911191484845L;
	private static DropDownType instance = new DropDownType();
	
	private DropDownType(){
		super();
		lasadElementType = "dropdown";
		jessTemplateName = "elem_dropdown";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "SELECTION", JessDataType.STRING, ComparisonGroup.TEXT);
	}
	
	public static DropDownType getInstance(){
		return instance;
	}
}