package lasad.logging.commonformat.util;

import java.util.List;
import java.util.Vector;

import lasad.logging.commonformat.util.jaxb.Object;

/**
 * <p>
 * A {@link CFGUIAction} represents exactly one manipulation that a user made in
 * the application's GUI (i.e., creating, modifying, or deleting
 * {@link EUEObject}s). A {@link CFGUIAction} can subsume multiple {@link Action}s
 * for instance, when the user creates an object that is itself composed of
 * multiple components. In this case, one user interface action will be
 * represented as a sequence of {@link Action}s (one action per
 * (sub-)component). In this case each {@link CFGUIAction} corresponds to one
 * {@link SessionEvent}.
 * </p>
 * <p>
 * Replayed {@link CFGUIAction}s (cf. {@link #replayed}), on the other hand, can
 * be based on {@link Action}s that solely aim at reconstructing the latest
 * state of {@link EUEObject}s, disregarding the process of how these
 * {@link EUEObject}s have been constructed and manipulated. In this case, some
 * process characteristics might still be reconstructable through timestamp
 * information (cf. {@link #creationDate}, {@link #firstModDate}, and
 * {@link #modificationDate}), i.e., multiple {@link SessionEvent}s can be
 * produced to at least indicate important temporal "landmarks" in the life
 * cycle of the respective {@link EUEObject}s.
 * </p>
 * 
 * @author Anahuac Valero, Oliver Scheuer
 * 
 */
public class CFGUIAction {

	private String guiActionID;

	private String sessionID;
	private String srcCompId;
	private String userID;
	
	List<Object> createEueObjectList = new Vector<Object>();
	List<Object> updateEueObjectList = new Vector<Object>();
	List<Object> deleteEueObjectList = new Vector<Object>();
	
	private String atClassification; //actiontype classification   userEventType
	private String atType; //actiontype type
	private long time;

	private boolean replayed;
	private long creationDate;
	private long firstModDate;
	private long modificationDate;

	private int numActions;
	private int currentActions;

	public CFGUIAction(String guiActionID) {
		this.guiActionID = guiActionID;

		numActions = 0;
		currentActions = 0;
		replayed = false;
		creationDate = 0L;
		modificationDate = 0L;
	}

	public boolean isComplete() {
		boolean retVal = false;

		if (numActions == currentActions) {
			retVal = true;
		}
		return retVal;
	}

	public boolean isReplayedAction() {
		return replayed;
	}

	public void setIsReplayedAction(boolean fromGraph) {
		this.replayed = fromGraph;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(long modificationDate) {
		this.modificationDate = modificationDate;
	}

	public long getFirstModDate() {
		return firstModDate;
	}

	public void setFirstModDate(long firstModDate) {
		this.firstModDate = firstModDate;
	}

	public String getGuiActionID() {
		return guiActionID;
	}

	public void setGuiActionID(String guiActionID) {
		this.guiActionID = guiActionID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSrcCompId() {
		return srcCompId;
	}

	public void setSrcCompId(String srcCompId) {
		this.srcCompId = srcCompId;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getNumActions() {
		return numActions;
	}

	public void setNumActions(int numActions) {
		this.numActions = numActions;
	}

	public void decreaseNumActionsByOne() {
		numActions--;
	}

	public int getCurrentActions() {
		return currentActions;
	}

	public void setCurrentActions(int currentActions) {
		this.currentActions = currentActions;
	}

	public void increaseCurrentActionsByOne() {
		currentActions++;
	}

	public String getAtClassification() {
		return atClassification;
	}

	public void setAtClassification(String atClassification) {
		this.atClassification = atClassification;
	}
	
	public List<Object> getCreateEueObjectList() {
		return createEueObjectList;
	}

	public void addEueObject2CreateList(Object eueObject) {
		createEueObjectList.add(eueObject);
	}

	public List<Object> getUpdateEueObjectList() {
		return updateEueObjectList;
	}

	public void addEueObject2UpdateList(Object eueObject) {
		updateEueObjectList.add(eueObject);
	}

	public List<Object> getDeleteEueObjectList() {
		return deleteEueObjectList;
	}
	
	public void addEueObject2DeleteList(Object eueObject) {
		deleteEueObjectList.add(eueObject);
	}

	public String getAtType() {
		return atType;
	}

	public void setAtType(String atType) {
		this.atType = atType;
	}

}