package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.MapChild;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.TemplateParent;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;


public class CreateAndDeleteSessions extends ContentPanel {
	
	private ContentPanel instructionPanel, sessionPanel, actionPanel;
	
	private static SimpleComboBox<String> templates;
	private static SimpleComboBox<String> sessions;
	
	private static Vector<SimpleComboBox<String>> restrictions = new Vector<SimpleComboBox<String>>();
	private static Vector<String> users = new Vector<String>();
	
	private static HashMap<String, Vector<String>> templateToSessions = null;
	private static TreeStore<ModelData> store = new TreeStore<ModelData>();
	private TreePanel<ModelData> userOverview;
	
	private static TextField<String> sessionName;
	public static void addSessionItem(String mapName, String template, String currentID, String maxID) {		
		if(templateToSessions != null) {
			if(templateToSessions.get(template) == null) {
				templateToSessions.put(template, new Vector<String>());
			}
			if(mapName != null) {
				templateToSessions.get(template).add(mapName);
			}
		}
		
		if(currentID.equals(maxID)) {
			refreshViews();	
		}	
	}
	
	public static void removeSessionItem(String mapName, String template) {
		Vector<String> usersOfRole = templateToSessions.get(template);
		for(String u : usersOfRole) {
			if(u.equals(mapName)) {
				templateToSessions.get(template).remove(u);
				break;
			}
		}
		refreshViews();
	}
	
	public static void createUserList(Vector<String> userlist) {
		users = userlist;
		
		for(SimpleComboBox<String> cb : restrictions) {
			cb.add(userlist);
		}
	}
	
	private static void refreshViews() {
		
		// Clear existing lists
		sessions.removeAll();
		templates.removeAll();
		
		// Refresh tree view
		int numberOfTemplates = templateToSessions.keySet().size();
		
		TemplateParent[] templateFolder = new TemplateParent[numberOfTemplates];
		
		ArrayList<String> Listofsessions=new ArrayList<String>();
		
		for(int i=0; i<templateToSessions.keySet().size(); i++) {
			templateFolder[i] = new TemplateParent();
			
			Iterator<String> keyIterator = templateToSessions.keySet().iterator();
			
			int j = i;
			while(j > 0) {
				if(keyIterator.hasNext()) {
					keyIterator.next();
				}
				j--;
			}
			
			while(keyIterator.hasNext()) {
				String key = keyIterator.next();
				templateFolder[i].set("name", key);
				Vector<String> mapsList = templateToSessions.get(key);
				
				for(String m : mapsList) {
					if(m != null) {
						templateFolder[i].add(new MapChild(m));
					
						// Refresh template drop down menu from del panel
						//sessions.add(m);
						Listofsessions.add(m);
					}
				}
				break;
			}
		}

		Arrays.sort(templateFolder);
		
		TemplateParent root = new TemplateParent("root");
		for (int i = 0; i < templateFolder.length; i++) {
			root.add((TemplateParent) templateFolder[i]);
			
			// Refresh template drop down menu from add panel
			templates.add(templateFolder[i].getName());	
		}
		java.util.Collections.sort(Listofsessions);
		for(String us:Listofsessions)
			sessions.add(us);

		
		store.removeAll();
		store.add(root.getChildren(), true);
		
	}
	
	public CreateAndDeleteSessions() {
		this.setSize("100%", "100%");
		this.setHeading("Step 4: Create and delete maps");
		
		this.setLayout(new RowLayout(Orientation.VERTICAL));
		
		initInstructionPanel();
		
		initMapOverviewPanel();
		
		initActionPanel();
		
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		centerPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		centerPanel.add(sessionPanel, new RowData(0.35, 1, new Margins(5)));
		centerPanel.add(actionPanel, new RowData(0.65, 1, new Margins(5)));
		centerPanel.layout();
		
		this.add(instructionPanel, new RowData(1, 0.15, new Margins(5)));
		this.add(centerPanel, new RowData(1, 0.85, new Margins(5)));

		this.layout();
		
		// This is the first step, which does not need to get expanded before use. Thus, get user data after layout.
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getUsers());
	}
	 
	private void initInstructionPanel() {
		instructionPanel = new ContentPanel();
		instructionPanel.setHeading("Instructions");
		instructionPanel.setLayout(new FitLayout());
		instructionPanel.setScrollMode(Scroll.AUTOY);
		
		HTML instructionText = new HTML();
		instructionText.setStylePrimaryName("instruction-text");
		instructionText.setHTML("<p><b>Description:</b> Once the ontology and template is defined, you have here the possibility to create concrete instances of an argument map. On the left-hand side, there is an overview of all existing maps (pre-defined as well as user-created ones) sorted by template. On the right-hand side, you can choose if you want to add or delete a map.<br><br></p>" + 
								"<p><b>Add map:</b> To add a new map, you have to choose a template on which the map is based on. Further, each map requires a unique name, which will appear in the map overview after login. Optionally, you can restrict a map to a single user (this of course does only makes sense, if you do not plan to argue collaboratively in this map). To restrict a map to a specific user, choose one from the list. Otherwise leave the field emptry to make it accessible for all users of the system. If you do not want to create pre-defined maps, you can skip this step. Users with role \"Standard\" (or higher) have the possibility to create new maps based on templates in the map overview anyway.<br><br></p>"+				
								"<p><b>Delete map:</b> If you want to delete a map, just choose it from the list. However, you will no longer be able to replay or restore the contents of the map.</p>");
		instructionPanel.add(instructionText);
	}

	private void initMapOverviewPanel() {
		sessionPanel = new ContentPanel();
		sessionPanel.setHeading("Existing templates and maps");
		sessionPanel.setLayout(new FitLayout());
		sessionPanel.setScrollMode(Scroll.AUTOY);
		
		templateToSessions = new HashMap<String, Vector<String>>();

		store = new TreeStore<ModelData>();
		
		userOverview = new TreePanel<ModelData>(store);
		userOverview.setDisplayProperty("name"); 
		userOverview.setWidth(300);
		
		sessionPanel.add(userOverview);
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
		FormPanel deleteSessionFormPanel = new FormPanel();
		deleteSessionFormPanel.setHeading("Delete map");
		
		sessions = new SimpleComboBox<String>();
		sessions.setFieldLabel("Map");
		sessions.setEmptyText("Choose map that should be deleted");
		sessions.setAllowBlank(false);
		sessions.setForceSelection(true);
		sessions.setTriggerAction(TriggerAction.ALL);
		
		deleteSessionFormPanel.add(sessions);
		
		Button deleteSessionButton = new Button("Delete") {	
			@Override
			protected void onClick(ComponentEvent ce) {
				if(sessions.validate()) {
					
					final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {  
						public void handleEvent(MessageBoxEvent ce) {  
						   Button btn = ce.getButtonClicked();
						   
						   if(btn.getItemId().equals(Dialog.YES)) {
								ActionPackage p = ActionFactory.getInstance().deleteMap(sessions.getSimpleValue());
								LASADActionSender.getInstance().sendActionPackage(p);
								
								LASADInfo.display("Delete map", "Try to remove map, please wait...");
								sessions.reset();
						   }
						}  
					};  
					MessageBox.confirm("Confirm", "If you delete this map, there will be no replay available. Are you sure you want to do that?", l);

				}
				else {
					LASADInfo.display("Error", "Please choose a map that should be deleted.");
				}
			}
		};
		
		deleteSessionFormPanel.add(deleteSessionButton);
		return deleteSessionFormPanel;
	}

	private FormPanel initAddForm() {
		final FormPanel addMapFormPanel = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(170);
		addMapFormPanel.setLayout(layout);
		addMapFormPanel.setHeading("Add map");
		addMapFormPanel.setScrollMode(Scroll.AUTOY);
		
		sessionName = new TextField<String>();
		sessionName.setFieldLabel("Name");
		sessionName.setEmptyText("Enter a unique map name");
		sessionName.setAllowBlank(false);
		sessionName.setMinLength(3);
		addMapFormPanel.add(sessionName);
		
		templates = new SimpleComboBox<String>();
		templates.setFieldLabel("Template");
		templates.setEmptyText("Choose a template");
		templates.setAllowBlank(false);
		templates.setForceSelection(true);
		templates.setTriggerAction(TriggerAction.ALL);
		addMapFormPanel.add(templates);
		
		restrictions.removeAllElements();
		generateUserRestrictionBox(addMapFormPanel);

		//TODO Zhenyu
		addMapFormPanel.setAction(GWT.getModuleBaseURL() + "ImageUpload");

		// set the form to use the POST method, and multipart MIME
		// encoding for file upload
		addMapFormPanel.setEncoding(FormPanel.Encoding.MULTIPART);
		addMapFormPanel.setMethod(FormPanel.Method.POST);

		Label upname = new Label("BackgroundImage (optional):");
		addMapFormPanel.add(upname);
		// Create a FileUpload widget.
		final FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		upload.setStyleName("x-btn");
		addMapFormPanel.add(upload);
		
		
		
		Button addMapButton = new Button("Add") {	
			@Override
			protected void onClick(ComponentEvent ce) {
				
				if(!templates.validate()) {
					LASADInfo.display("Error", "Please choose a template.");
					return;
				}
				
				if(!sessionName.validate()) {
					LASADInfo.display("Error", "Please define a name for the new map with at least 3 characters.");
					return;
				}

				String useTemplate = templates.getSimpleValue();
				String useSessionName = sessionName.getValue();
				
				Vector<String> allUserRestrictions = new Vector<String>();
				for(SimpleComboBox<String> scb : restrictions) {
					if(!scb.getSimpleValue().equals("")) {
						allUserRestrictions.add(scb.getSimpleValue());
					}
				}
				
				String backgroundImage = "";
				if(upload.getFilename()!= null)
				{
					addMapFormPanel.submit();
					backgroundImage=GWT.getHostPageBaseURL()+"uploads/"+upload.getFilename().substring(upload.getFilename().lastIndexOf('\\')+1);
				}
				ActionPackage p = ActionFactory.getInstance().createMap(useSessionName, useTemplate, allUserRestrictions, true,backgroundImage);
//				ActionPackage p = ActionFactory.getInstance().createMap(useSessionName, useTemplate, allUserRestrictions, true);
				LASADActionSender.getInstance().sendActionPackage(p);
				
				LASADInfo.display("Create map", "Trying to create a new map...");
			}


		};
		
		Button resetButton = new Button("Reset") {	
			@Override
			protected void onClick(ComponentEvent ce) {
				resetSessionData();
			}
		};
		
		addMapFormPanel.setBodyBorder(false);
		addMapFormPanel.setButtonAlign(HorizontalAlignment.LEFT);
		addMapFormPanel.addButton(addMapButton);
		addMapFormPanel.addButton(resetButton);
		return addMapFormPanel;
	}

	private void generateUserRestrictionBox(final FormPanel addMapFormPanel) {
		SimpleComboBox<String> restrictedToUser = new SimpleComboBox<String>() {

			@Override
			protected void onSelect(SimpleComboValue<String> model, int index) {
				super.onSelect(model, index);
				
				int id = 0;
				for(int i=0; i<restrictions.size(); i++) {
					if(restrictions.get(i) == this) {
						id = i;
					}
				}
				
				if(restrictions.size() <= id+1) {
					generateUserRestrictionBox(addMapFormPanel);
				}
			}
			
		};
		restrictedToUser.setFieldLabel("Restricted to user (optional)");
		restrictedToUser.setEmptyText("Choose a user");
		
		if(users.size() > 0) {		
			for(String user : users) {
				boolean ok = true;				
				for(SimpleComboBox<String> alreadyChosen : restrictions) {
					if(user.equals(alreadyChosen.getSimpleValue())) {
						ok = false;
					}
				}
				if(ok) {
					restrictedToUser.add(user);
				}
			}
		}
		
		restrictedToUser.setForceSelection(true);
		restrictedToUser.setTriggerAction(TriggerAction.ALL);
		restrictions.add(restrictedToUser);
		addMapFormPanel.add(restrictedToUser);
		addMapFormPanel.layout();
	}

	public static void resetSessionData() {
		
		if(sessionName != null && templates != null && users != null) {
			sessionName.reset();
			templates.setForceSelection(false);
			templates.reset();
			templates.setForceSelection(true);
			
			SimpleComboBox<String> scb = restrictions.get(0);
			scb.setForceSelection(false);
			scb.reset();
			scb.setForceSelection(true);
			
			for(int i=0; i<restrictions.size(); i++) {
				// The first restriction dropdown menu should stay
				if(i==0) {
					continue;
				}
				else {
					restrictions.get(i).removeFromParent();
				}
			}
			restrictions.clear();
			restrictions.add(scb);
		}
	}
	
	@Override
	protected void onExpand() {
		super.onExpand();
		
		// Remove all existing data to avoid duplicates
		templateToSessions.clear();
		
		// Get existing users and map data from server
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getUserListWithoutRole());
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getMapsAndTemplates());
		this.layout();
	}
}
