package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class URLType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210437217458897492L;
	private static URLType instance = new URLType();
	
	private URLType(){
		super();
		lasadElementType = "url";
		jessTemplateName = "elem_url";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID,"LINK", JessDataType.STRING, ComparisonGroup.NONE);
	}
	
	public static URLType getInstance(){
		return instance;
	}
}
