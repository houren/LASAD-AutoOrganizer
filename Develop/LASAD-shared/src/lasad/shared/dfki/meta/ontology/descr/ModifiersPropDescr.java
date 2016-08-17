package lasad.shared.dfki.meta.ontology.descr;

/**
 * 
 * @author oliverscheuer
 * 
 */
public class ModifiersPropDescr extends StandardPropDescr {

	private static final long serialVersionUID = -7375473255048024837L;

	public static final String PROP_ID = "modifiers";
	public static final String JESS_SLOT_NAME = "modifiers";

	public ModifiersPropDescr() {
		super(PROP_ID, null, JESS_SLOT_NAME, JessDataType.LIST,
				ComparisonGroup.USER);
	}
}
