package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea;

/*
 * This class is the pattern workspace
 */
import lasad.gwt.client.model.AbstractMVCViewSession;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.workspace.chat.AbstractChat;
import lasad.gwt.client.ui.workspace.chat.ExtendedChatPanel;
import lasad.gwt.client.ui.workspace.details.SelectionDetailsPanel;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;

import com.extjs.gxt.ui.client.util.Size;

public class PatternGraphMapSpace extends GraphMapSpace {
	protected PatternMVCViewSession session;
	//PatternDrawingAreaInfo patternInfo;

//	public PatternDrawingAreaSpace(PatternDrawingAreaInfo patternInfo, Size size) {
	public PatternGraphMapSpace(PatternMVCViewSession session, Size size) {
		super(session, size);
		this.session = session;
		createDrawingAreaSpace();
	}

	@Override
	protected void createDrawingAreaSpace() {
		menuBar = new PatternGraphMapMenuBar(this);
		workspaceArea.setTopComponent(menuBar);
		myMap = new PatternGraphMap(this);
		myMap.setMyViewSession(session);
		workspaceArea.add(myMap);
		this.layout();
	}

	@Override
	public PatternMVCViewSession getSession() {
		return session;
	}

	@Override
	public void setSession(AbstractMVCViewSession session) {
		this.session = (PatternMVCViewSession) session;
	}

	@Override
	public void changeSelectionDetailsPanelTo(SelectionDetailsPanel sdp) {

	}

	@Override
	public AbstractChat getChatPanel() {
		return null;
	}

	@Override
	public ExtendedChatPanel getExtendedChatPanel() {
		return null;
	}

//	public PatternDrawingAreaInfo getPatternInfo() {
//		return patternInfo;
//	}
	
	

}
