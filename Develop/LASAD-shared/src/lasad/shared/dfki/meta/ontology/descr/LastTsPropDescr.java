package lasad.shared.dfki.meta.ontology.descr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class LastTsPropDescr extends StandardPropDescr {

	private static final long serialVersionUID = 8573511293528630994L;

	public static final String PROP_ID = "last-ts";
	public static final String JESS_SLOT_NAME = "last_mod_ts";

	public LastTsPropDescr() {
		super(PROP_ID, null, JESS_SLOT_NAME, JessDataType.NUMBER,
				ComparisonGroup.TS);
	}

}
