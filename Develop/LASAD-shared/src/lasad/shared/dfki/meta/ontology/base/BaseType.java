package lasad.shared.dfki.meta.ontology.base;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import lasad.shared.dfki.meta.ontology.descr.ComparisonGroup;
import lasad.shared.dfki.meta.ontology.descr.JessDataType;
import lasad.shared.dfki.meta.ontology.descr.NonStandardPropDescr;
import lasad.shared.dfki.meta.ontology.descr.PropDescr;


/**
 * The {@link BaseType} comprises all the attributes of
 * {@link NonStandardPropDescr}s that are solely based on the corresponding UI
 * widget.
 * 
 * <br/>
 * <br/>
 * (see {@link PropDescr}, {@link NonStandardPropDescr})
 * 
 * @author oliverscheuer
 * 
 */
public abstract class BaseType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3232238969106843554L;

	protected static final Map<String, BaseType> lasadElemType2baseType = new HashMap<String, BaseType>();

	protected String lasadElementType = null;
	protected String jessTemplateName = null;

	private Map<String, String> componentID2jessSlot = new HashMap<String, String>();
	private Map<String, JessDataType> componentID2jessDataTypes = new HashMap<String, JessDataType>();
	private Map<String, ComparisonGroup> componentID2ComparisonGroups = new HashMap<String, ComparisonGroup>();

	protected int numRelevantSlots = 0;

	public String getLASADElementType() {
		return lasadElementType;
	}

	protected void addComponentSpecification(String compName, String jessSlot,
			JessDataType dataType, ComparisonGroup compGroup) {
		if (JessDataType.LIST.equals(dataType)) {
			System.err.println("'JessDataType.LIST' not supported for nonstandard properties.");
			//System.exit(1);   //this is not supoorted by GWT
			return;
		}
		componentID2jessSlot.put(compName, jessSlot);
		componentID2jessDataTypes.put(compName, dataType);
		componentID2ComparisonGroups.put(compName, compGroup);
		if (!ComparisonGroup.NONE.equals(compGroup)) {
			++numRelevantSlots;
		}
	}

	public boolean hasOnlySingleComponent() {
		return componentID2jessDataTypes.size() == 1;
	}

	public List<String> getComponentIDs() {
		Set<String> compSet = componentID2jessSlot.keySet();
		List<String> compList = new Vector<String>(compSet);
		Collections.sort(compList);
		return compList;
	}

	public String getJessSlot(String componentID) {
		return componentID2jessSlot.get(componentID);
	}

	public JessDataType getSlotDataType(String componentID) {
		return componentID2jessDataTypes.get(componentID);
	}

	public ComparisonGroup getComparisonGroup(String componentID) {
		return componentID2ComparisonGroups.get(componentID);
	}

	public static BaseType getBaseType(String lasadElemType) {
		initTypeLookupTable();
		BaseType retVal = lasadElemType2baseType.get(lasadElemType);
		return retVal;
	}

	public String getJessTemplateName() {
		return jessTemplateName;
	}

	private static void initTypeLookupTable() {
		if (lasadElemType2baseType.isEmpty()) {
			AwarenessType awareness = AwarenessType.getInstance();
			lasadElemType2baseType.put(awareness.getLASADElementType(),
					awareness);

			DropDownType dropdown = DropDownType.getInstance();
			lasadElemType2baseType
					.put(dropdown.getLASADElementType(), dropdown);

			RadioButtonsType radiobuttons = RadioButtonsType.getInstance();
			lasadElemType2baseType.put(radiobuttons.getLASADElementType(),
					radiobuttons);

			RatingType rating = RatingType.getInstance();
			lasadElemType2baseType.put(rating.getLASADElementType(), rating);

			TextType text = TextType.getInstance();
			lasadElemType2baseType.put(text.getLASADElementType(), text);

			TranscriptType transcript = TranscriptType.getInstance();
			lasadElemType2baseType.put(transcript.getLASADElementType(),
					transcript);

			URLType url = URLType.getInstance();
			lasadElemType2baseType.put(url.getLASADElementType(), url);

			FrameType frame = FrameType.getInstance();
			lasadElemType2baseType.put(frame.getLASADElementType(), frame);

			XmppMessageButtonType button = XmppMessageButtonType.getInstance();
			lasadElemType2baseType.put(button.getLASADElementType(), button);

		}
	}

	@Override
	public String toString() {
		//return getClass().getSimpleName(); //this is not supoorted by GWT
		return "BaseType";
	}

}
