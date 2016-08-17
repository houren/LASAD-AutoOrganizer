package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.argument.elements.ExtendedAwarenessElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedDropdownElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedRadioButtonElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedRatingElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedTextElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedTranscriptElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedUFrameElementArgument;
import lasad.gwt.client.ui.common.argument.elements.ExtendedURLElementArgument;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.OntologyGenerator;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui.SimpleBox;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.elements.ui.SimpleLink;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.Contribution;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.Element;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.OntologyParent;
import lasad.gwt.client.ui.workspace.transcript.TranscriptLinkData;
import lasad.gwt.client.xml.OntologyReader;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.HTML;

public class CreateModifyAndDeleteOntology extends ContentPanel {

	private static FieldSet cloneOntologySet;

	private LASADActionSender communicator = LASADActionSender.getInstance();
	private ActionFactory builder = ActionFactory.getInstance();

	private Element dropSource = null;

	private ContentPanel step2aInstructionPanel, step2bInstructionPanel, step2aCover, step2bCover, step2aAddDel, step2bEdit;
	private ContentPanel preview;
	private static ContentPanel elementActions;
	//private boolean isradiobuttons = false;
	private SimpleLink link;
	public static String selectedOntology;

	private static SimpleComboBox<String> ontologyDeleteComboBox, existingOntologiesComboBox;
	private static TextField<String> ontologyName;

	private static TreeStore<ModelData> elementStore = new TreeStore<ModelData>();
	private static TreeStore<ModelData> ontologyStore = new TreeStore<ModelData>();
	private static TreePanel<ModelData> elementOverview;
	private TreePanel<ModelData> ontologyOverview;

	private static Vector<String> ontologyList;

	public static HashMap<String, Vector<ElementInfo>> ontologyToContribution;
	public static HashMap<ElementInfo, Vector<ElementInfo>> contributionToElement;

	private static CreateModifyAndDeleteOntology instance;

	public static void clearElementActions() {
		elementActions.removeAll();
	}

	public static CreateModifyAndDeleteOntology getInstance() {
		if (instance == null) {
			instance = new CreateModifyAndDeleteOntology();
		}
		return instance;
	}

	public static void updateOntologyView(Vector<String> ontologyVector) {
		if (ontologyList == null) {
			ontologyList = new Vector<String>();
		}

		// Remove all existing data to avoid duplicates
		ontologyList.clear();

		ontologyList = ontologyVector;
		refreshOntologyList();
	}

	public static void updateOntologyDetailsView(String xml) {

		ontologyToContribution = new HashMap<String, Vector<ElementInfo>>();
		contributionToElement = new HashMap<ElementInfo, Vector<ElementInfo>>();

		OntologyReader.fillHashMapsFromXML(ontologyToContribution, contributionToElement, xml);

		refreshOntologyDetailsView();
	}

	private static void refreshOntologyDetailsView() {
		OntologyParent ontologyFolder = new OntologyParent();
		ontologyFolder.set("name", selectedOntology);

		OntologyParent contributionBoxFolder = new OntologyParent();
		contributionBoxFolder.set("name", "Nodes");
		ontologyFolder.add(contributionBoxFolder);

		OntologyParent contributionRelationFolder = new OntologyParent();
		contributionRelationFolder.set("name", "Relations");
		ontologyFolder.add(contributionRelationFolder);

		for (ElementInfo contribution : ontologyToContribution.get(selectedOntology)) {
			Contribution contributionFolder = new Contribution();
			contributionFolder.set("name", contribution.getElementOption(ParameterTypes.Heading));
			// contributionFolder.set("name", contribution.getElementID() +
			// " ("+contribution.getElementOption(ParameterTypes.Heading) + ")");
			contributionFolder.setElementInfo(contribution);

			if (contribution.getElementType().equalsIgnoreCase("box")) {
				contributionBoxFolder.add(contributionFolder);
			} else if (contribution.getElementType().equalsIgnoreCase("relation")) {
				contributionRelationFolder.add(contributionFolder);
			}

			for (ElementInfo element : contributionToElement.get(contribution)) {
				Element elementFolder = new Element();
				elementFolder.set("name", element.getElementID() + " (" + element.getElementType() + ")");
				elementFolder.setElementInfo(element);
				elementFolder.setParentContribution(contributionFolder);

				contributionFolder.add(elementFolder);
			}
		}
		elementStore.removeAll();
		elementStore.add(ontologyFolder, true);

		elementOverview.expandAll();
	}

	private static void refreshOntologyList() {
		// Clear existing lists
		ontologyDeleteComboBox.removeAll();
		existingOntologiesComboBox.removeAll();

		// Refresh tree view
		OntologyParent[] ontologyFolder = new OntologyParent[ontologyList.size()];

		for (int i = 0; i < ontologyList.size(); i++) {
			ontologyFolder[i] = new OntologyParent();
			ontologyFolder[i].set("name", ontologyList.get(i));
		}

		Arrays.sort(ontologyFolder);

		OntologyParent root = new OntologyParent("root");
		for (int i = 0; i < ontologyFolder.length; i++) {
			root.add(ontologyFolder[i]);

			// Refresh template drop down menu from add panel
			ontologyDeleteComboBox.add(ontologyFolder[i].getName());
			existingOntologiesComboBox.add(ontologyFolder[i].getName());
		}

		ontologyStore.removeAll();
		ontologyStore.add(root.getChildren(), true);
	}

	public static void removeOntologyItem(String ontology) {

		for (int i = 0; i < ontologyList.size(); i++) {

			if (ontologyList.get(i).equals(ontology)) {
				ontologyList.remove(i);
				break;
			}
		}

		refreshOntologyList();
	}

	private CreateModifyAndDeleteOntology() {
		this.setSize("100%", "100%");
		this.setHeading("Step 2: Ontology definition");

		this.setLayout(new AccordionLayout());

		initInnerAccordeon();

		layout();

	}

	private void initInnerAccordeon() {
		initStep2a();
		initStep2b();
	}

	private void initStep2a() {
		step2aCover = new ContentPanel();
		step2aCover.setHeading("Step 2a: Add / Delete Ontology");
		step2aCover.setLayout(new RowLayout(Orientation.VERTICAL));

		step2aAddDel = new ContentPanel();
		step2aAddDel.setHeaderVisible(false);
		step2aAddDel.setLayout(new RowLayout(Orientation.HORIZONTAL));

		// Left-hand side list
		ContentPanel list = new ContentPanel();
		list.setLayout(new FitLayout());
		list.setHeading("Overview of existing ontologies");

		elementStore = new TreeStore<ModelData>();
		ontologyOverview = new TreePanel<ModelData>(ontologyStore) {

			@SuppressWarnings("rawtypes")
			@Override
			protected void onClick(TreePanelEvent tpe) {
				super.onClick(tpe);
				if (tpe.getItem() != null) {
					selectedOntology = tpe.getItem().get("name").toString();
					step2bCover.setEnabled(true);
				}
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void onDoubleClick(TreePanelEvent tpe) {
				super.onDoubleClick(tpe);
				if (tpe.getItem() != null) {
					selectedOntology = tpe.getItem().get("name").toString();
					step2bCover.setEnabled(true);
					step2bCover.setExpanded(true);
				}

			}

		};
		ontologyOverview.setDisplayProperty("name");

		list.add(ontologyOverview);

		// Right-hand side actions
		ContentPanel actions = new ContentPanel();
		actions.setLayout(new AccordionLayout());
		actions.setHeading("Actions");

		// Add form panel
		FormPanel addForm = new FormPanel();
		addForm.setHeaderVisible(true);
		addForm.setHeading("Add Ontology");

		cloneOntologySet = new FieldSet();
		FormLayout collaborativeLayout = new FormLayout();
		collaborativeLayout.setLabelWidth(65);
		collaborativeLayout.setDefaultWidth(300);
		cloneOntologySet.setLayout(collaborativeLayout);
		cloneOntologySet.setHeading("Clone existing ontology");
		cloneOntologySet.setCheckboxToggle(true);
		cloneOntologySet.setExpanded(false);

		existingOntologiesComboBox = new SimpleComboBox<String>();
		existingOntologiesComboBox.setFieldLabel("Ontology");
		existingOntologiesComboBox.setEmptyText("Choose ontology");
		existingOntologiesComboBox.setForceSelection(true);
		existingOntologiesComboBox.setAllowBlank(false);
		existingOntologiesComboBox.setTriggerAction(TriggerAction.ALL);

		cloneOntologySet.add(existingOntologiesComboBox);

		ontologyName = new TextField<String>();
		ontologyName.setFieldLabel("Name");
		ontologyName.setEmptyText("Enter a unique ontology name");
		ontologyName.setAllowBlank(false);
		ontologyName.setMinLength(3);

		Button addButton = new Button("Create") {
			@Override
			protected void onClick(ComponentEvent ce) {

				boolean clone = false;
				if (cloneOntologySet.isExpanded()) {
					if (!existingOntologiesComboBox.validate()) {
						LASADInfo.display("Error", "Please choose an ontology that should be cloned");
						return;
					} else {
						clone = true;
					}

				}

				if (ontologyName.validate()) {
					if (clone) {
						LASADActionSender.getInstance().sendActionPackage(
								ActionFactory.getInstance().createOntology(ontologyName.getValue(),
										OntologyGenerator.createEmptyOntology(ontologyName.getValue()),
										existingOntologiesComboBox.getSimpleValue()));
					} else {
						LASADActionSender.getInstance().sendActionPackage(
								ActionFactory.getInstance().createOntology(ontologyName.getValue(),
										OntologyGenerator.createEmptyOntology(ontologyName.getValue()), null));
					}
				} else {
					LASADInfo.display("Error", "Please define a name for the new ontology");
				}
			}
		};

		addForm.add(cloneOntologySet);
		addForm.add(ontologyName);
		addForm.add(addButton);
		actions.add(addForm);

		// Add delete form panel
		initDelPanel(actions);

		step2aAddDel.add(list, new RowData(0.5, 1, new Margins(5)));
		step2aAddDel.add(actions, new RowData(0.5, 1, new Margins(5)));

		step2aInstructionPanel = new ContentPanel();
		step2aInstructionPanel.setHeading("Instructions");
		step2aInstructionPanel.setLayout(new FitLayout());
		step2aInstructionPanel.setScrollMode(Scroll.AUTOY);

		HTML instructionText = new HTML();
		instructionText.setStylePrimaryName("instruction-text");
		instructionText
				.setHTML("<p><b>Description:</b> In this second step, you can create and delete ontologies. An ontology is a definition of elements that are available for argument modeling, i.e. boxes and relations in the usual graph-based visualization of LASAD. user accounts. On the left-hand side, there is an overview of existing ontologies. To modify the element definitions of these ontologies, you can click on one of them, which will enable step 2b.<br><br></p>"
						+ "<p><b>Add ontology:</b> Here, you need to provide a unique name for the new ontology. The new ontology will not contain any elements. To add elements create an ontology, choose it from the overview on the left-hand side and open <i>Step 2b: Edit ontology</i>.<br><br></p>"
						+ "<p><b>Delete ontology:</b> If you want to delete an existing ontology, just choose it from the list. However, if you delete an ontology which is used by one or multiple templates, these templates and all maps that are based on these templates will be deleted as well. Use this option with care!</p>");
		step2aInstructionPanel.add(instructionText);

		step2aCover.add(step2aInstructionPanel, new RowData(1, 0.15, new Margins(5)));
		step2aCover.add(step2aAddDel, new RowData(1, 0.85, new Margins(5)));

		this.add(step2aCover);
	}

	private void initDelPanel(ContentPanel actions) {
		FormPanel delForm = new FormPanel();
		delForm.setHeaderVisible(true);
		delForm.setHeading("Delete Ontology");

		ontologyDeleteComboBox = new SimpleComboBox<String>();
		ontologyDeleteComboBox.setFieldLabel("Ontology");
		ontologyDeleteComboBox.setEmptyText("Choose ontology");
		ontologyDeleteComboBox.setForceSelection(true);
		ontologyDeleteComboBox.setAllowBlank(false);
		ontologyDeleteComboBox.setTriggerAction(TriggerAction.ALL);

		Button delButton = new Button("Delete") {
			@Override
			protected void onClick(ComponentEvent ce) {
				if (ontologyDeleteComboBox.validate()) {

					if (ontologyDeleteComboBox.getSimpleValue().equals(selectedOntology)) {
						selectedOntology = null;
						step2bCover.setEnabled(false);
					}

					ActionPackage p = ActionFactory.getInstance().deleteOntology(ontologyDeleteComboBox.getSimpleValue());
					LASADActionSender.getInstance().sendActionPackage(p);

					LASADInfo.display("Delete ontology", "Trying to remove ontology, please wait...");

					ontologyDeleteComboBox.setForceSelection(false);
					ontologyDeleteComboBox.reset();
					ontologyDeleteComboBox.setForceSelection(true);
				} else {
					LASADInfo.display("Error", "Please select an ontology");
				}

			}
		};

		delForm.add(ontologyDeleteComboBox);
		delForm.add(delButton);
		actions.add(delForm);
	}

	private void initStep2b() {

		step2bCover = new ContentPanel() {

			@Override
			protected void onExpand() {
				if (preview != null) {
					if (link != null) {
						link.remove();
						link = null;
					}
					preview.removeAll();
				}

				ActionPackage p = ActionFactory.getInstance().getOntologyDetails(selectedOntology);
				LASADActionSender.getInstance().sendActionPackage(p);
				super.onExpand();
			}

		};
		step2bCover.setHeading("Step 2b: Edit Ontology");
		step2bCover.setEnabled(false);
		step2bCover.setLayout(new RowLayout(Orientation.VERTICAL));

		step2bEdit = new ContentPanel();
		step2bEdit.setHeaderVisible(false);

		step2bEdit.setLayout(new RowLayout(Orientation.VERTICAL));

		// Top half
		ContentPanel step2bTop = new ContentPanel();
		step2bTop.setHeaderVisible(false);
		step2bTop.setLayout(new RowLayout(Orientation.HORIZONTAL));
		step2bTop.setBorders(false);

		// Top Left
		ContentPanel elementPanel = new ContentPanel();
		elementPanel.setHeading("Elements of this ontology");
		elementPanel.setLayout(new FitLayout());
		elementPanel.setScrollMode(Scroll.AUTOY);

		elementStore = new TreeStore<ModelData>();
		elementOverview = new TreePanel<ModelData>(elementStore) {

			@SuppressWarnings("rawtypes")
			@Override
			protected void onClick(TreePanelEvent tpe) {
				super.onClick(tpe);

				elementActions.removeAll();

				if (tpe.getItem() instanceof Contribution) {

					Contribution currentItem = ((Contribution) tpe.getItem());

					Contribution selectedContribution = (Contribution) tpe.getItem();
					createPreviewForElement(selectedContribution.getElementInfo());

					// Avoid the possibility to add elements to relations that
					// do only consist of plain lines
					if (!"false".equalsIgnoreCase(selectedContribution.getElementInfo().getElementOption(ParameterTypes.Details))) {

						StepCreateAndDeleteElement editStep = new StepCreateAndDeleteElement(selectedContribution.getElementInfo());
						editStep.resetElementList();

						for (ModelData md : currentItem.getChildren()) {
							editStep.addElementToList((String) md.get("name"));
						}

						elementActions.add(editStep);
					}

				} else if (tpe.getItem() instanceof OntologyParent) {

					OntologyParent currentItem = ((OntologyParent) tpe.getItem());

					if (currentItem.get("name").equals("Nodes")) {

						// Fill all existing nodes into the list to choose a
						// node from for deletion
						StepCreateModifyAndDeleteNode stepModifyNode = new StepCreateModifyAndDeleteNode();
						stepModifyNode.resetNodeList();
						for (ModelData md : currentItem.getChildren()) {
							Contribution currentNode = (Contribution) md;

							stepModifyNode.addNodeToList((String) currentNode.get("name"), currentNode.getElementInfo());
						}

						elementActions.add(stepModifyNode);
					} else if (currentItem.get("name").equals("Relations")) {
						// Fill all existing nodes into the list to choose a
						// node from for deletion
						StepCreateModifyAndDeleteRelation stepModifyRelation = new StepCreateModifyAndDeleteRelation();
						stepModifyRelation.resetRelationList();
						for (ModelData md : currentItem.getChildren()) {

							Contribution currentNode = (Contribution) md;

							stepModifyRelation.addRelationToList((String) currentNode.get("name"), currentNode.getElementInfo());
						}

						elementActions.add(stepModifyRelation);
					}
				} else if (tpe.getItem() instanceof Element) {
					Element el = (Element) tpe.getItem();

					createPreviewForElement(el.getParentContribution().getElementInfo());

					StepModifyElement stepModifyEl = new StepModifyElement(el.getParentContribution().getElementInfo(), el.getElementInfo());
					elementActions.add(stepModifyEl);
				}

				elementActions.layout();
				step2bEdit.layout();
			}
		};

		elementOverview.setDisplayProperty("name");

		// Enable DnD
		TreePanelDragSource source = new TreePanelDragSource(elementOverview);
		source.addDNDListener(new DNDListener() {

			@Override
			public void dragStart(DNDEvent e) {

				// Allow only Elements to be moved, not nodes and relations...
				if (!(elementOverview.getSelectionModel().getSelectedItem() instanceof Element)) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				} else {
					dropSource = (Element) elementOverview.getSelectionModel().getSelectedItem();
					super.dragStart(e);
				}
			}
		});

		source.setTreeSource(DND.TreeSource.LEAF);

		TreePanelDropTarget target = new TreePanelDropTarget(elementOverview) {

			// Used to avoid drag and drop to other nodes
			// See http://www.sencha.com/forum/showthread.php?111017-Sibling-Reordering-DND-Tree for details
			@Override
			protected void showFeedback(DNDEvent event) {
				final ModelData source = tree.getSelectionModel().getSelectedItem();

				final TreePanel<ModelData>.TreeNode target = tree.findNode(event.getTarget());
				if (target == null || !tree.getStore().getParent(source).equals(tree.getStore().getParent(target.getModel()))) {
					event.getStatus().setStatus(false);
					event.setCancelled(true);
					Insert.get().hide();
					return;
				}

				super.showFeedback(event);
			}

			@Override
			protected void onDragDrop(DNDEvent event) {
				super.onDragDrop(event);

				Contribution parent = dropSource.getParentContribution();

				parent.getElementInfo().getChildElements().clear();

				// It is essential to use a LinkedHashMap instead of a normal
				// one, because a HashMap does not keep the order of the keys on
				// insert, but a LinkedHashMap does!
				LinkedHashMap<String, ElementInfo> newOrder = new LinkedHashMap<String, ElementInfo>();

				for (int i = 0; i < elementStore.getChildren(elementStore.findModel(parent)).size(); i++) {
					Element child = (Element) elementStore.getChildren(elementStore.findModel(parent)).get(i);
					newOrder.put(child.getElementInfo().getElementID(), child.getElementInfo());
				}

				parent.getElementInfo().setChildElements(newOrder);

				String xml = OntologyGenerator.createOntology(CreateModifyAndDeleteOntology.selectedOntology,
						CreateModifyAndDeleteOntology.contributionToElement.keySet());
				communicator.sendActionPackage(builder.updateOntology(CreateModifyAndDeleteOntology.selectedOntology, xml));
				LASADInfo.display("Info", "Ontology will be updated on server...");

				createPreviewForElement(parent.getElementInfo());
			}

		};
		target.setAllowSelfAsSource(true);
		target.setAllowDropOnLeaf(false);
		target.setFeedback(Feedback.INSERT);

		elementPanel.add(elementOverview);

		step2bTop.add(elementPanel, new RowData(0.5, 1, new Margins(5, 0, 5, 5)));

		// Top Right
		initPreview(step2bTop);

		step2bEdit.add(step2bTop, new RowData(1, 0.35, new Margins(0)));

		elementActions = new ContentPanel();
		elementActions.setLayout(new FitLayout());
		elementActions.setHeading("Actions");

		step2bEdit.add(elementActions, new RowData(1, 0.65, new Margins(5)));

		step2bInstructionPanel = new ContentPanel();
		step2bInstructionPanel.setHeading("Instructions");
		step2bInstructionPanel.setLayout(new FitLayout());
		step2bInstructionPanel.setScrollMode(Scroll.AUTOY);

		HTML instructionText = new HTML();
		instructionText.setStylePrimaryName("instruction-text");
		instructionText
				.setHTML("<p><b>Description:</b> In this step, you will be able to edit the elements of the chosen ontology. There is an overview of nodes and relations of the ontology on the left-hand side (\"Elements of this ontology\"), whereas a preview of the currently selected element is shown on the right-hand side. The actions that can be done are shown in the bottom area in this step. Please consult the manual for further instructions.<br><br></p>");

		step2bInstructionPanel.add(instructionText);

		step2bCover.add(step2bInstructionPanel, new RowData(1, 0.15, new Margins(5)));
		step2bCover.add(step2bEdit, new RowData(1, 0.85, new Margins(5)));

		this.add(step2bCover);
	}

	protected void createPreviewForElement(ElementInfo elementInfo) {
		if (preview != null) {
			if (link != null) {
				link.remove();
				link = null;
			}
			preview.removeAll();
		}

		if (elementInfo.getElementType().equals("box")) {
			SimpleBox b = new SimpleBox(preview, elementInfo);

			for (String child : elementInfo.getChildElements().keySet()) {
				ElementInfo el = elementInfo.getChildElements().get(child);
				AbstractExtendedElement e = null;

				if (el.getElementType().equalsIgnoreCase("text")) {
					e = new ExtendedTextElementArgument(true, b, el);
				} else if (el.getElementType().equalsIgnoreCase("awareness")) {
					e = new ExtendedAwarenessElementArgument(b, el);
				} else if (el.getElementType().equalsIgnoreCase("rating")) {
					e = new ExtendedRatingElementArgument(b, el, true);
				} else if (el.getElementType().equalsIgnoreCase("transcript-link")) {
					e = new ExtendedTranscriptElementArgument(b, el, new TranscriptLinkData(null, 1, 0, 1, 0));
				} else if (el.getElementType().equalsIgnoreCase("dropdown")) {
					e = new ExtendedDropdownElementArgument(b, el, true);
				} else if (el.getElementType().equalsIgnoreCase("url")) {
					e = new ExtendedURLElementArgument(b, el, true, true);
				}

				else if (el.getElementType().equalsIgnoreCase("frame")) {
					e = new ExtendedUFrameElementArgument(b, el, true);
				}

				else if (el.getElementType().equalsIgnoreCase("radiobtn")) {
					e = new ExtendedRadioButtonElementArgument(b, el, true);
				}

				if (e != null) {
					b.addExtendedElement(e, b.getExtendedElements().size());
				}
			}

			preview.add(b);
			b.setPosition(5, 5);

		} else if (elementInfo.getElementType().equals("relation")) {
			SimpleBox b1 = new SimpleBox(preview, "Dummy", "standard", 140, 25, "#E6E6E6", 1);
			b1.setPosition(5, 5);
			preview.add(b1);

			SimpleBox b2 = new SimpleBox(preview, "Dummy", "standard", 140, 25, "#E6E6E6", 2);
			b2.setPosition(405, 85);
			preview.add(b2);

			preview.layout();

			link = new SimpleLink(preview, elementInfo, b1.getConnector(), b2.getConnector());
			for (String child : elementInfo.getChildElements().keySet()) {
				ElementInfo el = elementInfo.getChildElements().get(child);

				AbstractExtendedElement e = null;

				if (el.getElementType().equalsIgnoreCase("text")) {
					e = new ExtendedTextElementArgument(link, el);
				} else if (el.getElementType().equalsIgnoreCase("awareness")) {
					e = new ExtendedAwarenessElementArgument(link, el);
				} else if (el.getElementType().equalsIgnoreCase("rating")) {
					e = new ExtendedRatingElementArgument(link, el, true);
				} else if (el.getElementType().equalsIgnoreCase("dropdown")) {
					e = new ExtendedDropdownElementArgument(link, el, true);
				} else if (el.getElementType().equalsIgnoreCase("url")) {
					e = new ExtendedURLElementArgument(link, el, true, true);
				}

				else if (el.getElementType().equalsIgnoreCase("frame")) {
					e = new ExtendedUFrameElementArgument(link, el, true);
				}

				else if (el.getElementType().equalsIgnoreCase("radiobtn")) {
					e = new ExtendedRadioButtonElementArgument(link, el, true);
				}

				if (e != null) {
					link.addExtendedElement(e, link.getExtendedElements().size());
				}
			}

			preview.add(link);
			preview.layout();
			link.setEndings(false, true);
		}
		preview.layout();
	}

	private void initPreview(ContentPanel step2bTop) {
		preview = new ContentPanel();
		preview.setScrollMode(Scroll.AUTO);
		preview.setHeading("Preview");
		preview.setLayout(new AbsoluteLayout());
		preview.layout();

		step2bTop.add(preview, new RowData(0.5, 1, new Margins(5)));
	}

	@Override
	protected void onExpand() {
		super.onExpand();

		// Get existing users and map data from server
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getOntologyList());
		this.layout();
	}

	public static void resetOntologyData() {
		cloneOntologySet.setExpanded(false);

		existingOntologiesComboBox.setAllowBlank(true);
		existingOntologiesComboBox.reset();
		existingOntologiesComboBox.setAllowBlank(false);

		ontologyName.setAllowBlank(true);
		ontologyName.reset();
		ontologyName.setAllowBlank(false);
	}
}