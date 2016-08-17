package lasad.shared.dfki.meta.ontology.descr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class FirstTsPropDescr extends StandardPropDescr {

	private static final long serialVersionUID = -885916141712330490L;

	public static final String PROP_ID = "first-ts";
	public static final String JESS_SLOT_NAME = "creation_ts";

	public FirstTsPropDescr() {
		super(PROP_ID, null, JESS_SLOT_NAME, JessDataType.NUMBER,
				ComparisonGroup.TS);
	}

}
