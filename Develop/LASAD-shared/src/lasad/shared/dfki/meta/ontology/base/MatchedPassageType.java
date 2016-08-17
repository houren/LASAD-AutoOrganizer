package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * Attribute derived from transcript type 
 * @author oliverscheuer
 *
 */
public class MatchedPassageType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1068979304247168725L;
	private static MatchedPassageType instance = new MatchedPassageType();
	
	private MatchedPassageType(){
		super();
		lasadElementType = null;
		jessTemplateName = "matched-passage";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "type_actual", JessDataType.STRING, ComparisonGroup.TEXT);
	}
	
	public static MatchedPassageType getInstance(){
		return instance;
	}
}
