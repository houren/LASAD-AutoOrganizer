package lasad.gwt.client.ui.workspace.tabs.map;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingOverviewDialogue;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.OntologyParent;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.TemplateChild;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.commands.Commands;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreeGridEvent;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

public class MapLoginTab extends ContentPanel {

	private lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);
	private Status statusIcon = new Status();
	private ContentPanel right;
	private ContentPanel left, overviewPanel;
	public TreePanel<ModelData> templateOverview;
	private static TreeStore<ModelData> templateStore, activeSessionsStore, priorSessionsStore;

	private static HashMap<String, Vector<String>> ontologyToTemplate = null;

	private boolean updateAllowed = true;
	
	public boolean isUpdateAllowed() {
		return updateAllowed;
	}

	public void setUpdateAllowed(boolean updateAllowed) {
		this.updateAllowed = updateAllowed;
	}

	public int status = 0;

	private static MapLoginTab instance = null;

	public static MapLoginTab getInstance() {
		if (instance == null) {
			instance = new MapLoginTab();
		}
		return instance;
	}

	public void addTemplateItem(String ontology, String templateName, String currentID, String maxID) {
		if (ontologyToTemplate != null) {
			if (ontologyToTemplate.get(ontology) == null) {
				ontologyToTemplate.put(ontology, new Vector<String>());
			}
			ontologyToTemplate.get(ontology).add(templateName);
		}

		if (currentID.equals(maxID)) {
			refreshTemplateView();

			status++;
			Logger.log("STATUS: " + status, Logger.DEBUG);
			updateLoadingOverviewDialogue();
		}
	}

	public void removeTemplateItem(String ontology, String templateName) {
		Vector<String> templatesOfOntology = ontologyToTemplate.get(ontology);
		for (String t : templatesOfOntology) {
			if (t.equals(templateName)) {
				ontologyToTemplate.get(ontology).remove(t);
				break;
			}
		}

		refreshTemplateView();
	}

	private void refreshTemplateView() {

		// Refresh tree view
		int numberOfOntologies = ontologyToTemplate.keySet().size();

		OntologyParent[] ontologyFolder = new OntologyParent[numberOfOntologies];

		for (int i = 0; i < ontologyToTemplate.keySet().size(); i++) {
			ontologyFolder[i] = new OntologyParent();

			Iterator<String> keyIterator = ontologyToTemplate.keySet().iterator();

			int j = i;
			while (j > 0) {
				if (keyIterator.hasNext()) {
					keyIterator.next();
				}
				j--;
			}

			while (keyIterator.hasNext()) {
				String key = keyIterator.next();
				ontologyFolder[i].set("name", key);
				Vector<String> templates = ontologyToTemplate.get(key);

				for (String t : templates) {
					ontologyFolder[i].add(new TemplateChild(t));
				}
				break;
			}
		}

		OntologyParent root = new OntologyParent("root");
		for (int i = 0; i < ontologyFolder.length; i++) {
			root.add((OntologyParent) ontologyFolder[i]);
		}

		templateStore.removeAll();
		templateStore.add(root.getChildren(), true);
	}

	public void addListOfMaps(Action a, boolean activeOnMap) {

		if (!activeOnMap) {
			if (a.getParameters().size() > 1) {
				ModelData m = new BaseModelData();
				m.set("mapid", a.getParameterValue(ParameterTypes.MapId));
				m.set("template", a.getParameterValue(ParameterTypes.TemplateName));
				m.set("templatetitle", a.getParameterValue(ParameterTypes.TemplateTitle));
				m.set("ontology", a.getParameterValue(ParameterTypes.OntologyName));
				m.set("name", a.getParameterValue(ParameterTypes.MapName));
				m.set("createtime", new Date(Long.parseLong(a.getParameterValue(ParameterTypes.Created))).toString());
				m.set("modifiedtime", new Date(Long.parseLong(a.getParameterValue(ParameterTypes.Modified))).toString());

				if (!a.getParameterValue(ParameterTypes.CreatorId).equals("")) {
					m.set("creatorname", a.getParameterValue(ParameterTypes.CreatorName));
					m.set("creatorid", a.getParameterValue(ParameterTypes.CreatorId));
				}

				m.set("usercount", a.getParameterValue(ParameterTypes.ActiveUsers));

				int activeUsers = Integer.parseInt(a.getParameterValue(ParameterTypes.ActiveUsers));

				if (activeUsers == 0) {
					priorSessionsStore.add(m, false);
				} else {
					activeSessionsStore.add(m, false);
				}
			}
		}
		if ("END".equals(a.getParameterValue(ParameterTypes.Status))) {
			status++;
			updateLoadingOverviewDialogue();
		}
	}

	private MapLoginTab() {

		setHeaderVisible(false);
		setBodyBorder(false);
		ontologyToTemplate = new HashMap<String, Vector<String>>();

		setLayout(new RowLayout(Orientation.HORIZONTAL));

		left = new ContentPanel();
		left.setLayout(new AccordionLayout());
		left.setHeaderVisible(false);

		initOverviewPanel();
		left.add(new ImportMapFormPanel(null));

		right = new ContentPanel();
		right.setLayout(new FitLayout());
		right.setHeaderVisible(false);

		add(left, new RowData(0.5, 1.0, new Margins(5)));
		add(right, new RowData(0.5, 1.0, new Margins(5)));

		layout();
	}

	public void updateOverviews() {
		
		if(updateAllowed) {
			status = 0;
			LoadingOverviewDialogue.getInstance().showLoadingScreen();
	
			right.removeAll();
			right.layout();
	
			ontologyToTemplate.clear();
	
			templateStore.removeAll();
			activeSessionsStore.removeAll();
			priorSessionsStore.removeAll();
	
			LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getTemplatesForOverview());
			LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getMaps());
		}
	}

	public void updateLoadingOverviewDialogue() {
		Logger.log("updateLoadingOverviewDialogue", Logger.DEBUG);
		if (status >= 2) {
			LoadingOverviewDialogue.getInstance().closeLoadingScreen();
		}
	}

	private void initOverviewPanel() {
		overviewPanel = new ContentPanel();
		overviewPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		overviewPanel.setHeading("Join map");

		// Build Header Refresh Button
		ToolButton refreshHeaderButton = new ToolButton("x-tool-refresh") {
			protected void onClick(ComponentEvent ce) {
				updateOverviews();
				ce.cancelBubble();
			}
		};

		overviewPanel.getHeader().addTool(refreshHeaderButton);

		ContentPanel templateOverviewContent = new ContentPanel();

		templateOverviewContent.setHeading("Create a new map from a template...");
		templateOverviewContent.setScrollMode(Scroll.AUTOY);
		templateStore = new TreeStore<ModelData>();
		templateOverview = new TreePanel<ModelData>(templateStore) {

			@SuppressWarnings("rawtypes")
			@Override
			protected void onClick(TreePanelEvent tpe) {
				super.onClick(tpe);

				if (tpe.getItem() instanceof TemplateChild) {
					loadTemplateDetails(tpe.getItem().get("name").toString());
				}

			}
		};
		templateOverview.setDisplayProperty("name");
		templateOverviewContent.add(templateOverview);

		ContentPanel activeSessionsOverviewContent = new ContentPanel();
		activeSessionsOverviewContent.setLayout(new FitLayout());

		ColumnConfig sessionName = new ColumnConfig("name", "<div style=\"color:#000000;\">Map name</div>", 150);
		sessionName.setRenderer(new TreeGridCellRenderer<ModelData>());
		ColumnConfig templateName = new ColumnConfig("templatetitle", "<div style=\"color:#000000;\">Template</div>", 150);
		ColumnConfig activeUsers = new ColumnConfig("usercount", "<div style=\"color:#000000;\">Active users</div>", 80);
		ColumnConfig created = new ColumnConfig("createtime", "<div style=\"color:#000000;\">Created on</div>", 165);
		ColumnConfig lastChange = new ColumnConfig("modifiedtime", "<div style=\"color:#000000;\">Last modified</div>", 165);
		ColumnModel activeSessionsCM = new ColumnModel(Arrays.asList(sessionName, templateName, activeUsers, created));

		activeSessionsStore = new TreeStore<ModelData>();
		final TreeGrid<ModelData> activeSessionsTree = new TreeGrid<ModelData>(activeSessionsStore, activeSessionsCM);
		activeSessionsTree.setBorders(true);
		activeSessionsTree.setAutoExpand(true);
		activeSessionsTree.setAutoExpandColumn("name");
		activeSessionsTree.setAutoExpandMax(1000);
		activeSessionsTree.setTrackMouseOver(false);
		activeSessionsTree.addListener(Events.OnClick, new Listener<TreeGridEvent<ModelData>>() {
			public void handleEvent(TreeGridEvent<ModelData> be) {
				ModelData m = be.getModel();
				if (m != null) {
					if (m.get("mapid") != null) {
						loadMapDetails((String) m.get("mapid"));
					}
				}
			}
		});

		activeSessionsOverviewContent.setHeading("Join an active map...");
		activeSessionsOverviewContent.setScrollMode(Scroll.AUTOY);
		activeSessionsOverviewContent.add(activeSessionsTree);

		ContentPanel priorSessionsOverviewContent = new ContentPanel();
		priorSessionsOverviewContent.setLayout(new FitLayout());

		ColumnModel priorSessionsCM = new ColumnModel(Arrays.asList(sessionName, templateName, lastChange));

		priorSessionsStore = new TreeStore<ModelData>();
		final TreeGrid<ModelData> priorSessionsTree = new TreeGrid<ModelData>(priorSessionsStore, priorSessionsCM);
		priorSessionsTree.setBorders(true);
		priorSessionsTree.setAutoExpand(true);
		priorSessionsTree.setAutoExpandColumn("name");
		priorSessionsTree.setAutoExpandMax(1000);
		priorSessionsTree.setTrackMouseOver(false);
		priorSessionsTree.addListener(Events.OnClick, new Listener<TreeGridEvent<ModelData>>() {
			public void handleEvent(TreeGridEvent<ModelData> be) {
				ModelData m = be.getModel();
				if (m != null) {
					if (m.get("mapid") != null) {
						loadMapDetails((String) m.get("mapid"));
					}
				}
			}
		});

		priorSessionsOverviewContent.setHeading("Join a prior map...");
		priorSessionsOverviewContent.setScrollMode(Scroll.AUTOY);
		priorSessionsOverviewContent.add(priorSessionsTree);

		overviewPanel.add(templateOverviewContent, new RowData(1.0, 0.33, new Margins(3)));
		overviewPanel.add(activeSessionsOverviewContent, new RowData(1.0, 0.33, new Margins(3)));
		overviewPanel.add(priorSessionsOverviewContent, new RowData(1.0, 0.33, new Margins(3)));

		this.left.add(overviewPanel);
	}

	public void loadMapDetails(String mapID) {
		right.removeAll();
		right.setLayout(new CenterLayout());

		statusIcon.setAutoHeight(true);
		statusIcon.setWidth(200);
		statusIcon.setBusy("Loading map details...");

		right.add(statusIcon);
		right.layout();

		// Ask server for MapDetails
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getMapDetails(mapID));
	}

	public void loadTemplateDetails(String templateName) {
		right.removeAll();
		right.setLayout(new CenterLayout());

		statusIcon.setAutoHeight(true);
		statusIcon.setWidth(200);
		statusIcon.setBusy("Loading template details...");

		right.add(statusIcon);
		right.layout();

		// Ask server for MapDetails
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getTemplateDetails(templateName));
	}

	public void openMapDetails(final Action a) {
		right.removeAll();
		right.setLayout(new FitLayout());

		JoinSessionContentPanel jscp = JoinSessionContentPanel.getInstance();
		jscp.openMapDetails(a);

		right.add(jscp);
		right.layout();
		layout();
	}

	public void openTemplateDetails(final Action a) {
		right.removeAll();
		right.setLayout(new FitLayout());

		CreateSessionFromTemplateContentPanel csft = CreateSessionFromTemplateContentPanel.getInstance();
		csft.openTemplateDetails(a);

		right.add(csft);
		right.layout();
		layout();
	}

	/**
	 * Displays an error during start of replay-client
	 * 
	 * @param errorcommand
	 *            type of error
	 */
	public void infoReplayError(Commands errorcommand) {
		String errorTitle;
		String errorText;
		if (errorcommand.equals("EMPTY-MAP")) {
			errorTitle = myConstants.MapInformationPanelReplayEmptyMapTitle();
			errorText = myConstants.MapInformationPanelReplayEmptyMapText();
		} else if (errorcommand.equals("NO-MAP")) {
			errorTitle = myConstants.MapInformationPanelReplayNoMapTitle();
			errorText = myConstants.MapInformationPanelReplayNoMapText();
		} else if (errorcommand.equals("NO-USER")) {
			errorTitle = myConstants.MapInformationPanelReplayNoMapTitle();
			errorText = myConstants.MapInformationPanelReplayNoMapText();
		} else if (errorcommand.equals("NO-ACTION")) {
			errorTitle = myConstants.MapInformationPanelReplayNoActionTitle();
			errorText = myConstants.MapInformationPanelReplayNoActionText();
		} else {
			errorTitle = myConstants.MapInformationPanelReplayErrorTitle();
			errorText = myConstants.MapInformationPanelReplayErrorText();
		}
		LASADInfo.display(errorTitle, errorText);
	}

	public void clearRightHandSide() {
		right.removeAll();
	}
}
