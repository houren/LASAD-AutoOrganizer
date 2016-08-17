package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class AwarenessType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4185166797008038415L;
	private static AwarenessType instance = new AwarenessType();
	
	private AwarenessType(){
		super();
		lasadElementType = "awareness";
		jessTemplateName = "elem_awareness";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "TEXT", JessDataType.STRING, ComparisonGroup.NONE);
	}
	
	public static AwarenessType getInstance(){
		return instance;
	}
}
