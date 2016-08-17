package lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea;

import java.util.TreeMap;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.model.pattern.PatternGraphMapInfo;
import lasad.gwt.client.model.pattern.PatternMVCViewSession;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapMenuBar;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateSpecialLinkDialog;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.FeedbackAuthoringTabContent;
import lasad.gwt.client.ui.workspace.tabs.feedbackauthoring.patternarea.elements.CreateSpecialLinkDialogPattern;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/*
 * This class is the menu bar of the pattern workspace
 */
public class PatternGraphMapMenuBar extends GraphMapMenuBar {

	public PatternGraphMapMenuBar(GraphMapSpace mySpace) {
		super(mySpace);
	}

	@Override
	protected GraphMapInfo getDrawingAreaInformation() {
		// TODO Auto-generated method stub
		
		PatternMVCViewSession viewSession = (PatternMVCViewSession) myMapSpace.getSession();
		return viewSession.getController().getDrawingAreaInfo();
		//return myMapSpace.getSession().getController().getMapInfo();
		//return ((PatternDrawingAreaSpace)this.getMyMapSpace()).getPatternInfo();
	}

	@Override
	public void removeFeedbackEngine(String agentID, String typeID) {

	}

	@Override
	public void addFeedbackEngine(String agentName, String feedbackName) {

	}

	@Override
	protected void addFeedbackEngine(String agentName, String feedbackName,
			String agentType) {

	}

	@Override
	public void createMenuBar() {
		Button itemAdd = new Button(myConstants.AddMenu());

		Menu addMenu = createAddMenu();
		//Menu debugMenu = createDebugMenu();

		itemAdd.setMenu(addMenu);
		this.add(itemAdd);
		 
		if (myMapInfo.isFeedback()) {
			Menu feedbackMenu = createFeedbackMenu();
			itemFeedback.setMenu(feedbackMenu);
			this.add(itemFeedback);
		}

//		if (LASAD_Client.getInstance().getRole().equalsIgnoreCase("developer")) {
//			Button itemDebug = new Button("Debug");
//			itemDebug.setMenu(debugMenu);
//			this.add(itemDebug);
//		}
	}

	@Override
	protected void handleCreateNewBoxItemSelectionEvent(MenuEvent me,
			ElementInfo currentElement) {
		int tempPosX = PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getHScrollPosition() + PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getInnerWidth() / 2 - (Integer.parseInt(currentElement.getUiOption(ParameterTypes.Width)) / 2);
		int tempPosY = PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getVScrollPosition() + PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getInnerHeight() / 2 - (Integer.parseInt(currentElement.getUiOption(ParameterTypes.Height)) / 2);
		PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
		// TODO Auto-generated method stub
		FeedbackAuthoringTabContent.getInstance().getPatternServerManager().
			createBoxWithElements(currentElement, ((PatternGraphMapInfo)myMapInfo).getAgentId(), String.valueOf(myMapInfo.getMapID()), tempPosX, tempPosY);
		//communicator.sendActionPackage(actionBuilder.createBoxWithElements(currentElement, ArgumentMapMenuBarNew.this.getMyMapSpace().getMyMap().getID(), tempPosX, tempPosY));
	}

	@Override
	protected AbstractCreateSpecialLinkDialog createSpecialLinkDialog(
			ElementInfo config, String mapId, TreeMap<String, AbstractBox> boxes,
			TreeMap<String, AbstractLink> links) {
		return new CreateSpecialLinkDialogPattern(config, mapId, boxes, links);
	}

	@Override
	protected void handleGroupPointerItemSelectionEvent(MenuEvent me,
			int tempPosX, int tempPosY) {
	}
	
	private Menu createFeedbackMenu() {
		Menu menu = new Menu();
		MenuItem deleteFeedback = deleteFeedbackItem();
		menu.add(deleteFeedback);
		return menu;
	}
	
	private MenuItem deleteFeedbackItem() {
		final MenuItem deleteFeedbackItem = new MenuItem(myConstants.DeleteAllFeedbackMenuItem());
		deleteFeedbackItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				// TODO Auto-generated method stub
				PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().deleteAllFeedbackClusters();
				PatternGraphMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
			}
		});
		return deleteFeedbackItem;
	}

}
