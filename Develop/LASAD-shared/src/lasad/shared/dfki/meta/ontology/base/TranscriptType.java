package lasad.shared.dfki.meta.ontology.base;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class TranscriptType extends BaseType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1161339575521699807L;
	private static TranscriptType instance = new TranscriptType();

	private TranscriptType() {

		super();
		lasadElementType = "transcript-link";
		jessTemplateName = "elem_transcript-link";
		addComponentSpecification("startrow","STARTROW", JessDataType.NUMBER,
				ComparisonGroup.NONE);
		addComponentSpecification("startpoint","STARTPOINT", JessDataType.NUMBER,
				ComparisonGroup.NONE);
		addComponentSpecification("endrow","ENDROW", JessDataType.NUMBER,
				ComparisonGroup.NONE);
		addComponentSpecification("endpoint","ENDPOINT", JessDataType.NUMBER,
				ComparisonGroup.NONE);
	}

	public static TranscriptType getInstance() {
		return instance;
	}
}
