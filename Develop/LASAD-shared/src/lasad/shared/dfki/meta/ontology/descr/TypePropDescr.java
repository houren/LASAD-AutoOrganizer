package lasad.shared.dfki.meta.ontology.descr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class TypePropDescr extends StandardPropDescr {

	private static final long serialVersionUID = 9153838249783802724L;

	public static final String PROP_ID = "type";
	public static final String JESS_SLOT_NAME = "type";

	public TypePropDescr() {
		super(PROP_ID, null, JESS_SLOT_NAME, JessDataType.STRING,
				ComparisonGroup.TYPE);
	}

}
