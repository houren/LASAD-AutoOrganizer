package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;

/**
 * 
 * @author oliverscheuer
 *
 */
public class RatingType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8876789597594891923L;
	private static RatingType instance = new RatingType();
	
	private RatingType(){
		super();
		lasadElementType = "rating";
		jessTemplateName = "elem_rating";
		addComponentSpecification(PropDescr.DEFAULT_COMPONENT_ID, "SCORE", JessDataType.NUMBER, ComparisonGroup.NUMBER);
	}
	
	public static RatingType getInstance(){
		return instance;
	}
}