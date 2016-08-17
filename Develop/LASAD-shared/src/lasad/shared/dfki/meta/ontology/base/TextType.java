package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class TextType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2396817321498328438L;
	private static TextType instance = new TextType();
	
	private TextType(){
		super();
		lasadElementType = "text";
		jessTemplateName = "elem_text";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "TEXT", JessDataType.STRING, ComparisonGroup.TEXT);
	}
	
	public static TextType getInstance(){
		return instance;
	}
}