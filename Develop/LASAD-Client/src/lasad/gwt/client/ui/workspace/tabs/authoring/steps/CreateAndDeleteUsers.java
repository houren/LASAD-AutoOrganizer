package lasad.gwt.client.ui.workspace.tabs.authoring.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.RoleParent;
import lasad.gwt.client.ui.workspace.tabs.authoring.helper.lists.UserChild;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.HTML;




public class CreateAndDeleteUsers extends ContentPanel {
	
	private ContentPanel instructionPanel, userPanel, actionPanel, myDeleteContent;
	
	private static SimpleComboBox<String> roles;
	private static SimpleComboBox<String> users; 
		
	private static HashMap<String, Vector<String>> rolesToUsersMap = null;
	private static TreeStore<ModelData> store = new TreeStore<ModelData>();
	private TreePanel<ModelData> userOverview;
	
	private static TextField<String> userName, userPassword;
	
	public static void addUserItem(String username, String role, String currentID, String maxID) {		
		if(rolesToUsersMap != null) {
			if(rolesToUsersMap.get(role) == null) {
				rolesToUsersMap.put(role, new Vector<String>());
			}
			rolesToUsersMap.get(role).add(username);
		}
		
		if(currentID.equals(maxID)) {
			refreshViews();	
		}	
	}
	
	public static void removeUserItem(String role, String username) {
		Vector<String> usersOfRole = rolesToUsersMap.get(role);
		for(String u : usersOfRole) {
			if(u.equals(username)) {
				rolesToUsersMap.get(role).remove(u);
				break;
			}
		}
		refreshViews();
	}
	
	private static void refreshViews() {
		
		// Clear existing lists
		roles.removeAll();
		users.removeAll();
		
		// Refresh tree view
		int numberOfRoles = rolesToUsersMap.keySet().size();
		
		RoleParent[] roleFolder = new RoleParent[numberOfRoles];
		
		 ArrayList<String> Listofusers=new ArrayList<String>();
		for(int i=0; i<rolesToUsersMap.keySet().size(); i++) {
			roleFolder[i] = new RoleParent();
			
			Iterator<String> keyIterator = rolesToUsersMap.keySet().iterator();
			
			int j = i;
			while(j > 0) {
				if(keyIterator.hasNext()) {
					keyIterator.next();
				}
				j--;
			}
			
			while(keyIterator.hasNext()) {
				String key = keyIterator.next();
				roleFolder[i].set("name", key);
				Vector<String> usersList = rolesToUsersMap.get(key);
				java.util.Collections.sort(usersList);
				
				for(String u : usersList) {
					roleFolder[i].add(new UserChild(u));
					
					// Avoid to list the current user himself
					if(u.equals(LASAD_Client.getInstance().getUsername())) {
						continue;
					}
					
					// Refresh template drop down menu from del panel
					
					//users.add(u);
					Listofusers.add(u);
					
				}
				break;
			}
		}
		
		
		
		RoleParent root = new RoleParent("root");
		for (int i = 0; i < roleFolder.length; i++) {
			root.add((RoleParent) roleFolder[i]);
			
			// Refresh ontology drop down menu from add panel
			roles.add(roleFolder[i].getName());	
		}
		java.util.Collections.sort(Listofusers);
		
		for(String us:Listofusers)
			users.add(us);

		store.removeAll();
		store.add(root.getChildren(), true);
	}
	
	public CreateAndDeleteUsers() {
		this.setSize("100%", "100%");
		this.setHeading("Step 1: User definition");
		
		this.setLayout(new RowLayout(Orientation.VERTICAL));
		
		initInstructionPanel();
		
		initUserPanel();
		
		initActionPanel();
		
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		centerPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		centerPanel.add(userPanel, new RowData(0.35, 1, new Margins(5)));
		centerPanel.add(actionPanel, new RowData(0.65, 1, new Margins(5)));
		centerPanel.layout();
		
		this.add(instructionPanel, new RowData(1, 0.15, new Margins(5)));
		this.add(centerPanel, new RowData(1, 0.85, new Margins(5)));

		this.layout();
	}
	
	private void initInstructionPanel() {
		instructionPanel = new ContentPanel();
		instructionPanel.setHeading("Instructions");
		instructionPanel.setLayout(new FitLayout());
		instructionPanel.setScrollMode(Scroll.AUTOY);
		
		HTML instructionText = new HTML();
		instructionText.setStylePrimaryName("instruction-text");
		instructionText.setHTML("<p><b>Description:</b> In this first step, you can define user accounts. These are needed to login to the system. On the left-hand side, there is an overview of existing user accounts sorted by role. On the right-hand side, you have the possibility to add or delete user accounts.<br><br></p>" + 
				"<p><b>Add user:</b> Each user is defined by a unique username, a password (consisting of at least 8 characters), and a role. Connected to each user role, there is a set of rights. A \"Guest\" is only able to participate in an existing map. In addition to a \"Guest\" users that have the role \"Standard\" assigned are able to create new maps from templates. In addition to that the \"Teacher\" is also able to access this authoring tool, whereas the \"Developer\" will see logs that are useful to detect errors in the code basis of LASAD and has access to untested system features.<br><br></p>"+				
				"<p><b>Delete user:</b> If you want to delete a user account, just choose it from the list. If this user is author of a template or contributed to a map, his or her username will be replaced by \"Unknown\".</p>");
		instructionPanel.add(instructionText);
	}

	private void initUserPanel() {
		userPanel = new ContentPanel();
		userPanel.setHeading("Existing users");
		userPanel.setLayout(new FitLayout());
		userPanel.setScrollMode(Scroll.AUTOY);
		
		rolesToUsersMap = new HashMap<String, Vector<String>>();

		store = new TreeStore<ModelData>();
		
		userOverview = new TreePanel<ModelData>(store);
		userOverview.setDisplayProperty("name"); 
		userOverview.setWidth(300);
		
		userPanel.add(userOverview);
	}

	private void initActionPanel() {
		actionPanel = new ContentPanel();
		actionPanel.setHeading("Actions");
		actionPanel.setLayout(new AccordionLayout());
		
		actionPanel.add(initAddForm());
		actionPanel.add(initDeleteForm());
	}

	private FormPanel initDeleteForm() {
		
		myDeleteContent = new ContentPanel();
		myDeleteContent.setHeading("Delete User");
		
		FormPanel deleteUserFormPanel = new FormPanel();
		deleteUserFormPanel.setButtonAlign(HorizontalAlignment.LEFT);
		deleteUserFormPanel.setHeading("Delete User");		
		
		users = new SimpleComboBox<String>();
		users.setFieldLabel("User");
		users.setEmptyText("Choose user");
		users.setAllowBlank(false);
		users.setForceSelection(true);
		users.setTriggerAction(TriggerAction.ALL);

		deleteUserFormPanel.add(users);
		
		Button deleteMapButton = new Button("Delete") {	
			@Override
			protected void onClick(ComponentEvent ce) {
				if(users.validate()) {

							   ActionPackage p = ActionFactory.getInstance().deleteUser(users.getSimpleValue());
								LASADActionSender.getInstance().sendActionPackage(p);
								
								LASADInfo.display("Delete user", "Try to remove user, please wait...");
								
								users.setForceSelection(false);
								users.reset();
								users.setForceSelection(true);
				}
				else {
					LASADInfo.display("Error", "Please choose a user that should be deleted.");
				}
			}
		};
		
		deleteUserFormPanel.add(deleteMapButton);
		deleteUserFormPanel.layout();
		
		return deleteUserFormPanel;
	}

	private FormPanel initAddForm() {
		
		FormPanel addUserFormPanel = new FormPanel();
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(120);
		addUserFormPanel.setLayout(layout);
		addUserFormPanel.setHeading("Add User");
		addUserFormPanel.setScrollMode(Scroll.AUTOY);
		
		roles = new SimpleComboBox<String>();
		roles.setFieldLabel("Role");
		roles.setEmptyText("Choose a role");
		roles.setAllowBlank(false);
		roles.setForceSelection(true);
		roles.setTriggerAction(TriggerAction.ALL);
		
		userName = new TextField<String>();
		userName.setFieldLabel("Name");
		userName.setEmptyText("Enter username");
		userName.setAllowBlank(false);
		userName.setMinLength(1);
		
		userPassword = new TextField<String>();
		userPassword.setFieldLabel("Password");
		userPassword.setPassword(true);
		userPassword.setEmptyText("Enter password");
		userPassword.setAllowBlank(false);
		userPassword.setMinLength(1);
		
		addUserFormPanel.add(userName);
		addUserFormPanel.add(userPassword);
		addUserFormPanel.add(roles);
		
		Button addMapButton = new Button("Add") {	
			@Override
			protected void onClick(ComponentEvent ce) {
				
				if(!roles.validate()) {
					LASADInfo.display("Error", "Please choose a role.");
					return;
				}
				
				if(!userName.validate()) {
					LASADInfo.display("Error", "Please define a name for the new user with at least 1 character.");
					return;
				}
				
				if(!userPassword.validate()) {
					LASADInfo.display("Error", "Please define a password for the new user with at least 1 character.");
					return;
				}
				
				String useRole = roles.getSimpleValue();
				String useNickname = userName.getValue();
				String usePassword = userPassword.getValue();
				
				ActionPackage p = ActionFactory.getInstance().createUser(useNickname, usePassword, useRole);
				LASADActionSender.getInstance().sendActionPackage(p);
				
				LASADInfo.display("Create user", "Trying to create new user...");				
			}

	
		};
		
		addUserFormPanel.add(addMapButton);
		return addUserFormPanel;
	}

	@Override
	protected void onExpand() {
		super.onExpand();
		
		// Remove all existing data to avoid duplicates
		rolesToUsersMap.clear();
		
		// Get existing template data from server
		LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().getUsers());
		
		this.layout();
	}
	
	public static void resetUserData() {
		CreateAndDeleteUsers.userName.reset();
		CreateAndDeleteUsers.userPassword.reset();
		CreateAndDeleteUsers.roles.reset();
	}
}
