package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.OntologyParent;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.TemplateChild;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.HTML;

public class CreateAndDeleteTemplate extends ContentPanel {

	private ContentPanel instructionPanel, templatePanel, actionPanel;

	private static SimpleComboBox<String> ontologies;
	private static SimpleComboBox<String> availableTemplates;

	private static FieldSet collaborativeSet, transcriptSet, additionalSet;
	// private CheckBox selectionDetails;
	private static CheckBox userList, miniMap, trackCursor, chat, onlyAuthor, commitByEnter, straightLink, autoGrow, allowLinksToLinks;
	private static Slider maxUsers;
	private final static int MAX_USERS_VALUE = 5;
	private SliderField sf;
	private static TextArea transcript, templateDescription;
	private static TextField<String> templateName;
	private TreePanel<ModelData> templateOverview;

	private static HashMap<String, Vector<String>> templateMap = null;
	private static TreeStore<ModelData> store = new TreeStore<ModelData>();

	public static void addTemplateItem(String ontology, String templateName, String currentID, String maxID) {

		if (templateMap != null) {
			if (templateMap.get(ontology) == null) {
				templateMap.put(ontology, new Vector<String>());
			}
			if (templateName != null) {
				templateMap.get(ontology).add(templateName);
			}
		}

		if (currentID.equals(maxID)) {
			refreshViews();
		}

	}

	public static void removeTemplateItem(String ontology, String templateName) {
		Vector<String> templatesOfOntology = templateMap.get(ontology);
		if (templatesOfOntology != null) {
			for (String t : templatesOfOntology) {
				if (t.equals(templateName)) {
					templateMap.get(ontology).remove(t);
					break;
				}
			}
		}
		refreshViews();
	}

	private static void refreshViews() {

		// Clear existing lists
		ontologies.removeAll();
		availableTemplates.removeAll();

		// Refresh tree view
		int numberOfOntologies = templateMap.keySet().size();

		OntologyParent[] ontologyFolder = new OntologyParent[numberOfOntologies];
		ArrayList<String> Listoftemp = new ArrayList<String>();

		for (int i = 0; i < templateMap.keySet().size(); i++) {
			ontologyFolder[i] = new OntologyParent();

			Iterator<String> keyIterator = templateMap.keySet().iterator();

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
				Vector<String> templates = templateMap.get(key);

				for (String t : templates) {
					ontologyFolder[i].add(new TemplateChild(t));

					// Refresh template drop down menu from del panel
					// availableTemplates.add(t);
					Listoftemp.add(t);
				}
				break;
			}
		}

		OntologyParent root = new OntologyParent("root");
		for (int i = 0; i < ontologyFolder.length; i++) {
			root.add((OntologyParent) ontologyFolder[i]);

			// Refresh ontology drop down menu from add panel
			ontologies.add(ontologyFolder[i].getName());

		}
		java.util.Collections.sort(Listoftemp);
		for (String ut : Listoftemp)
			availableTemplates.add(ut);

		store.removeAll();
		store.add(root.getChildren(), true);
	}

	public CreateAndDeleteTemplate() {
		this.setSize("100%", "100%");
		this.setHeading("Step 3: Template definition");

		this.setLayout(new RowLayout(Orientation.VERTICAL));

		initInstructionPanel();

		initTemplatePanel();

		initActionPanel();

		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		centerPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		centerPanel.add(templatePanel, new RowData(0.35, 1, new Margins(5)));
		centerPanel.add(actionPanel, new RowData(0.65, 1, new Margins(5)));
		centerPanel.layout();

		this.add(instructionPanel, new RowData(1, 0.15, new Margins(5)));
		this.add(centerPanel, new RowData(1, 0.85, new Margins(5)));

		this.layout(true);
	}

	private void initInstructionPanel() {
		instructionPanel = new ContentPanel();
		instructionPanel.setHeading("Instructions");
		instructionPanel.setLayout(new FitLayout());
		instructionPanel.setScrollMode(Scroll.AUTOY);

		HTML instructionText = new HTML();
		instructionText.setStylePrimaryName("instruction-text");
		instructionText
				.setHTML("<p><b>Description:</b> Once, the ontology is defined, there is a need for a template. Whereas the ontology defines the elements available for modeling the argument, the template is used to configure the interface of the system. On the left-hand side of this step, there is an overview of existing templates sorted by ontologies. Similar to the other steps, there is - on the right-hand side - a set of possible actions: (1) add template, and (2) delete template.<br><br></p>"
						+ "<p><b>Add template:</b> As a first step, you have to decide if you want to support individual or collaborative argumentation. If you want to support argumentation in groups, you have to enable the checkbox \"Collaboration\". Once, you enable collaboration, you have a set of options that needs to be defined:<br><br></p>"
						+ "<ul>"
						+ "<li><i>Maximum number of users</i><br></li>"
						+ "<li><i>List of active users:</i> Shows a list of users that are actively working on a map instance based on the template<br></li>"
						+ "<li><i>Use chat:</i> Provide a simple text chat in the interface<br></li>"
						+ "<li><i>Cursor tracking(not recommended for standard use):</i> The users' cursors will be visible to all other users that participate at an argument map (next step) which is based on this template. <i>Important:</i> This option should be used with care! It will cause a lot of traffic which may lead to problems with slow connections or multiple / larger groups of users.<br><br></li>"
						+ "</ul>"
						+ "<p>The next step to decide is if there should be a given internally text that can be used to create relations between the text and the argument elements. To enable linking between the text and the argument element, it is important to know that it is only possible to relate an element to a complete line (or multiple lines) but not to single words or parts of a transcript line.</p>"
						+ "<p>In addition to these specific options, there is a set of general options. Here, you have to choose on which ontology the template is based as well as a unique and a (optional) description which appears during login in the details of the map.<br><br></p>"
						+ "<p><b>Delete template:</b> If you want to delete an existing template, just choose it from the list. If this template is used by one or more map, these maps will be deleted as well and not restoreable!</p>");

		instructionPanel.add(instructionText);
	}

	private void initTemplatePanel() {
		templatePanel = new ContentPanel();
		templatePanel.setHeading("Available Map Templates");
		templatePanel.setLayout(new FitLayout());
		templatePanel.setScrollMode(Scroll.AUTOY);

		templateMap = new HashMap<String, Vector<String>>();

		store = new TreeStore<ModelData>();

		templateOverview = new TreePanel<ModelData>(store);
		templateOverview.setDisplayProperty("name");
		templateOverview.setWidth(300);

		templatePanel.add(templateOverview);
	}

	private void initActionPanel() {
		actionPanel = new ContentPanel();
		actionPanel.setHeading("Actions");
		actionPanel.setLayout(new AccordionLayout());

		FormPanel addTemplateFormPanel = initAddForm();
		FormPanel deleteTemplateFormPanel = initDeleteForm();

		actionPanel.add(addTemplateFormPanel);
		actionPanel.add(deleteTemplateFormPanel);
	}

	private FormPanel initDeleteForm() {
		FormPanel deleteMapFormPanel = new FormPanel();
		deleteMapFormPanel.setHeading("Delete Template");

		availableTemplates = new SimpleComboBox<String>();
		availableTemplates.setFieldLabel("Template");
		availableTemplates.setEmptyText("Choose template that should be deleted");
		availableTemplates.setForceSelection(true);
		availableTemplates.setTriggerAction(TriggerAction.ALL);
		availableTemplates.setAllowBlank(false);
		deleteMapFormPanel.add(availableTemplates);

		Button deleteMapButton = new Button("Delete") {
			@Override
			protected void onClick(ComponentEvent ce) {
				if (availableTemplates.validate()) {

					ActionPackage p = ActionFactory.getInstance().deleteTemplate(availableTemplates.getSimpleValue());
					LASADActionSender.getInstance().sendActionPackage(p);

					LASADInfo.display("Delete template", "Try to remove template, please wait...");

					availableTemplates.setForceSelection(false);
					availableTemplates.reset();
					availableTemplates.setForceSelection(true);

				} else {
					LASADInfo.display("Error", "Please choose a template that should be deleted.");
				}

			}
		};

		deleteMapFormPanel.setBodyBorder(false);
		deleteMapFormPanel.setButtonAlign(HorizontalAlignment.LEFT);
		deleteMapFormPanel.addButton(deleteMapButton);

		return deleteMapFormPanel;
	}

	private FormPanel initAddForm() {
		FormPanel addTemplateFormPanel = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(185);
		addTemplateFormPanel.setLayout(layout);
		addTemplateFormPanel.setHeading("Add Template");
		addTemplateFormPanel.setScrollMode(Scroll.AUTOY);

		// ************ Start - Collaboration (Group argumentation) *************/
		collaborativeSet = new FieldSet();
		FormLayout collaborativeLayout = new FormLayout();
		collaborativeLayout.setLabelWidth(175);
		collaborativeSet.setLayout(collaborativeLayout);
		collaborativeSet.setHeading("Collaboration");
		collaborativeSet.setCheckboxToggle(true);
		collaborativeSet.setExpanded(false);

		maxUsers = new Slider() {
			@Override
			protected void onValueChange(int value) {
				System.out.println("onValueChange value: " + value);
				super.onValueChange(value);
				sf.setFieldLabel("Maximum number of users [" + value + "]");
			}
		};
		maxUsers.setMinValue(2);
		maxUsers.setMaxValue(30);
		maxUsers.setIncrement(1);
		maxUsers.setValue(MAX_USERS_VALUE);
		maxUsers.setMessage("{0} users maximum");

		sf = new SliderField(maxUsers);
		sf.setFieldLabel("Maximum number of users [" + MAX_USERS_VALUE + "]");
		collaborativeSet.add(sf);

		userList = new CheckBox();
		userList.setFieldLabel("List of active users");
		collaborativeSet.add(userList);

		chat = new CheckBox();
		chat.setFieldLabel("Use chat");
		chat.setValue(true);
		collaborativeSet.add(chat);

		trackCursor = new CheckBox();

		trackCursor.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if (trackCursor.getValue()) {
					MessageBox.alert("Cursor tracking causes delays",
							"Caution: Activating cursor tracking causes delays, when using the system remotely.", null);
				}
			}
		});
		trackCursor.setFieldLabel("Cursor tracking");
		collaborativeSet.add(trackCursor);

		addTemplateFormPanel.add(collaborativeSet);
		// ************ End - Collaboration *************/

		// ************ Start - Use a given text *************/
		transcriptSet = new FieldSet();
		FormLayout transcriptSetLayout = new FormLayout();
		transcriptSetLayout.setLabelWidth(160);
		transcriptSetLayout.setDefaultWidth(300);
		transcriptSet.setLayout(transcriptSetLayout);
		transcriptSet.setHeading("Use a given text");
		transcriptSet.setCheckboxToggle(true);
		transcriptSet.setExpanded(false);

		transcript = new TextArea();
		transcript.setFieldLabel("Given text");
		transcript.setHeight(300);
		transcriptSet.add(transcript);

		addTemplateFormPanel.add(transcriptSet);
		// ************ End - Use a given text *************/

		// ************ Start - Additional options *************/
		additionalSet = new FieldSet();
		FormLayout additionalLayout = new FormLayout();
		additionalLayout.setLabelWidth(320);
		additionalSet.setLayout(additionalLayout);
		additionalSet.setHeading("Additional options");
		additionalSet.setCheckboxToggle(true);
		additionalSet.setExpanded(false);

		straightLink = new CheckBox();
		straightLink.setFieldLabel("Use straight-line arrows");
		additionalSet.add(straightLink);

		onlyAuthor = new CheckBox();
		onlyAuthor.setFieldLabel("Only author can edit their own boxes");
		additionalSet.add(onlyAuthor);

		commitByEnter = new CheckBox();
		commitByEnter.setFieldLabel("\"Enter\" key commits text (\"Shift\" + \"Enter\" for new line)");
		additionalSet.add(commitByEnter);

		allowLinksToLinks = new CheckBox();
		allowLinksToLinks.setFieldLabel("Allow links to be drawn from boxes to other links");
		additionalSet.add(allowLinksToLinks);


		// Added by Kevin Loughlin for auto-organize support
		/*
		autoOrganize = new CheckBox();
		autoOrganize.setFieldLabel("Automatically organize argument map");
		additionalSet.add(autoOrganize);
		*/

		addTemplateFormPanel.add(additionalSet);
		// ************ End - Additional options *************/

		// ************ Start - General options *************/
		FieldSet generalSet = new FieldSet();
		FormLayout generalSetLayout = new FormLayout();
		generalSetLayout.setLabelWidth(160);
		generalSet.setLayout(generalSetLayout);
		generalSet.setHeading("General options");
		generalSet.setCheckboxToggle(false);
		generalSet.setExpanded(true);
		generalSet.setCollapsible(false);

		ontologies = new SimpleComboBox<String>();
		ontologies.setFieldLabel("Ontology");
		ontologies.setEmptyText("Choose an ontology");
		ontologies.setForceSelection(true);
		ontologies.setTriggerAction(TriggerAction.ALL);
		generalSet.add(ontologies);

		templateName = new TextField<String>();
		templateName.setFieldLabel("Name");
		templateName.setEmptyText("Enter the name of the new template");
		templateName.setAllowBlank(false);
		templateName.setMinLength(3);
		generalSet.add(templateName);

		templateDescription = new TextArea();
		templateDescription.setFieldLabel("Description (optional)");
		generalSet.add(templateDescription);

		// MODIFICATION BY BM-------------------------------------------------------------------
		autoGrow = new CheckBox();
		autoGrow.setFieldLabel("Auto resize TextArea");

		ToolTipConfig config = new ToolTipConfig();
		config.setDismissDelay(10000);
		config.setText("Activate if you want to use TextAreas, which grow automatically if the text gets longer.");
		autoGrow.setToolTip(config);

		generalSet.add(autoGrow);
		// MODIFICATION BY BM-------------------------------------------------------------------

		miniMap = new CheckBox();
		miniMap.setFieldLabel("Show MiniMap");
		generalSet.add(miniMap);

		// selectionDetails = new CheckBox();
		// selectionDetails.setFieldLabel("Separate view for details");
		// generalSet.add(selectionDetails);
		addTemplateFormPanel.add(generalSet);
		// ************ End - General options *************/

		Button addMapButton = new Button("Add") {
			@Override
			protected void onClick(ComponentEvent ce) {

				if ("".equals(ontologies.getSimpleValue())) {
					LASADInfo.display("Error", "Please choose an ontology.");
					return;
				}

				if (templateName == null || templateName.equals("") || !templateName.validate()) {
					LASADInfo.display("Error", "Please define a name for the new template.");
					return;
				}

				String useOntologyWithName = ontologies.getSimpleValue();
				String useTemplateName = templateName.getValue();
				String useTemplateDescription = templateDescription.getValue();

				// boolean useSeperateViewForDetails =
				// selectionDetails.getValue();

				int maxUserCount = 1;
				boolean useUserList = false, useMiniMap = false, useCursorTracking = false, useChat = false;
				if (collaborativeSet.isExpanded()) {
					maxUserCount = maxUsers.getValue();
					useChat = chat.getValue();
					useUserList = userList.getValue();
					useCursorTracking = trackCursor.getValue();
				}

				// start - additional options

				// Kevin Loughlin added autoOrganize 8 June 2015
				boolean useOnlyAuthorCanModify = false, useCommitTextByEnter = false, useStraightLink = false, useAllowLinksToLinks = false; //useAutoOrganize = false
				if (additionalSet.isExpanded()) {
					useOnlyAuthorCanModify = onlyAuthor.getValue();
					useCommitTextByEnter = commitByEnter.getValue();
					useStraightLink = straightLink.getValue();
					useAllowLinksToLinks = allowLinksToLinks.getValue();
					//useAutoOrganize = autoOrganize.getValue();
				}
				// end - additional options

				useMiniMap = miniMap.getValue();

				boolean useTranscript = false;
				String transcriptText = "";
				if (transcriptSet.isExpanded()) {
					useTranscript = true;
					transcriptText = transcript.getValue();
				}
				// MODIFIED BY BM. ADDED THE autogrowstatement
				boolean autoGrowTextArea = autoGrow.getValue();

				ActionPackage p = ActionFactory.getInstance().createTemplate(useTemplateName, useTemplateDescription, useOntologyWithName,
						false, maxUserCount, useChat, useUserList, useMiniMap, useCursorTracking, useTranscript, transcriptText,
						useOnlyAuthorCanModify, useCommitTextByEnter, useStraightLink, autoGrowTextArea, useAllowLinksToLinks); //useAutoOrganize, 
				// ActionPackage p =
				// ActionFactory.getInstance().createTemplate(useTemplateName,
				// useTemplateDescription, useOntologyWithName,
				// useSeperateViewForDetails, maxUserCount, useChat,
				// useUserList, useCursorTracking, useTranscript,
				// transcriptText);
				LASADActionSender.getInstance().sendActionPackage(p);

				LASADInfo.display("Create template", "Trying to create a new template...");

			}
		};

		addTemplateFormPanel.setBodyBorder(false);
		addTemplateFormPanel.setButtonAlign(HorizontalAlignment.LEFT);
		addTemplateFormPanel.addButton(addMapButton);

		return addTemplateFormPanel;
	}

	@Override
	protected void onExpand() {
		super.onExpand();

		// Remove all existing data to avoid duplicates
		templateMap.clear();

		// Get existing template data from server
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getTemplates());
		this.layout();
	}

	public static void resetTemplateData() {
		templateName.reset();
		ontologies.reset();

		transcriptSet.collapse();
		collaborativeSet.collapse();

		transcript.reset();
		chat.reset();
		userList.reset();
		miniMap.reset();
		trackCursor.reset();
		// selectionDetails.reset();
		templateDescription.reset();

		maxUsers.setValue(MAX_USERS_VALUE);
	}

}
