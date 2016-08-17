package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.window;

import java.util.List;

import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.util.FeedbackAuthoringStrings;

/**
 * Class to customize the {@link AgentWindow}
 * @author Anahuac
 *
 */
public class AgentWindowConf {
	private String title = FeedbackAuthoringStrings.HEADING_ADD_NEW_AGENT;
	private int mode = AgentWindow.EDIT_MODE;
	private List<String> ontologyList;
	
	public AgentWindowConf(String title, int mode, List<String> ontologyList) {
		super();
		this.title = title;
		this.mode = mode;
		this.ontologyList = ontologyList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public List<String> getOntologyList() {
		return ontologyList;
	}

	public void setOntologyList(List<String> ontologyList) {
		this.ontologyList = ontologyList;
	}
	
}
