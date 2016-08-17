package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class RadioButtonsType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5023203951643165775L;
	private static RadioButtonsType instance = new RadioButtonsType();
	
	private RadioButtonsType(){
		super();
		lasadElementType = "radiobtn";
		jessTemplateName = "elem_radiobtn";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID,"Mycheck", JessDataType.STRING, ComparisonGroup.TEXT);
	}
	
	public static RadioButtonsType getInstance(){
		return instance;
	}
}