package lasad.logging.commonformat.util;

import java.util.List;
import java.util.Vector;

import lasad.logging.commonformat.util.jaxb.Action;
import lasad.logging.commonformat.util.jaxb.Preamble;

public class CFTranslationResult {
	private List<Action> actionList = new Vector<Action>();
	private Preamble preamble;
	private String sessionID;
	
	public CFTranslationResult() {
		super();
	}
	
	public CFTranslationResult(List<Action> actionList, Preamble preamble,
			String sessionID) {
		super();
		this.actionList = actionList;
		this.preamble = preamble;
		this.sessionID = sessionID;
	}
	
//	public void addAction2ActionList(Action action ){
//		actionList.add(action);
//	}
//	
//	public void addActionList2ActionList(List<Action> actionL ){
//		actionList.addAll(actionL);
//	}
	
	public List<Action> getActionList() {
		return actionList;
	}
	public void addActionList(List<Action> actionL) {
		this.actionList.addAll(actionL);
	}
	public void addAction(Action action) {
		this.actionList.add(action);
	}
	
	public Preamble getPreamble() {
		return preamble;
	}
	public void setPreamble(Preamble preamble) {
		this.preamble = preamble;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	
}
