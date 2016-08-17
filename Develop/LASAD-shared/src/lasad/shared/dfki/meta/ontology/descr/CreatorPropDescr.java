package lasad.shared.dfki.meta.ontology.descr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class CreatorPropDescr extends StandardPropDescr {

	private static final long serialVersionUID = 3768330610866452355L;

	public static final String PROP_ID = "creator";
	public static final String JESS_SLOT_NAME = "creator";

	public CreatorPropDescr() {
		super(PROP_ID, null, JESS_SLOT_NAME, JessDataType.STRING,
				ComparisonGroup.USER);
	}
}
