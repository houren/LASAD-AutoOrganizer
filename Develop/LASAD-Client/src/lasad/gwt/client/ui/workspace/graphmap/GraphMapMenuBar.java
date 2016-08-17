package lasad.gwt.client.ui.workspace.graphmap;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.settings.DebugSettings;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.link.AbstractLinkPanel;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateSpecialLinkDialog;
import lasad.gwt.client.ui.workspace.tableview.ArgumentEditionStyleEnum;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;

/**
 *	The beginning implementation for the map's menu bar.  Continued in subclass ArgumentMapMenuBar.
 *	@author Unknown
 *	@since Unknown, Last Updated by Kevin Loughlin 21 July 2015
 */
public abstract class GraphMapMenuBar extends ToolBar
{
	protected lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);

	protected GraphMapSpace myMapSpace;
	protected GraphMapInfo myMapInfo;
	protected ArgumentEditionStyleEnum editionStyle;
	protected final Button itemFeedback;

	/**
	 * Creates menu for a map
	 * 
	 * @param myMap
	 */
	public GraphMapMenuBar(GraphMapSpace mySpace) {
		this.myMapSpace = mySpace;
		this.myMapInfo = getDrawingAreaInformation() ;  //myMapSpace.getSession().getController().getMapInfo();
		this.editionStyle = LASAD_Client.getMapEditionStyle(String.valueOf(myMapInfo.getMapID()));

		// This button must be created before createMenuBar
		itemFeedback = new Button("Feedback");
		createMenuBar();

		this.setSpacing(5);
		// To make the menu appear on top, even on top of a box
		DOM.setIntStyleAttribute(this.getElement(), "zIndex", XDOM.getTopZIndex() + 1);
		
	}

	protected abstract GraphMapInfo getDrawingAreaInformation();
	public abstract void removeFeedbackEngine(String agentID, String typeID);
	public abstract void addFeedbackEngine(final String agentName, final String feedbackName);
	protected abstract void addFeedbackEngine(final String agentName, final String feedbackName, final String agentType);
	
	public abstract void createMenuBar();
	
	protected Menu createAddMenu() {
		Menu addMenu = new Menu();

		// Creates the sub menu for box types
		MenuItem addBoxMenu = new MenuItem(myConstants.ContributionMenuItem());
		addMenu.add(addBoxMenu);
		Menu subBoxes = new Menu();
		
		Map<String, ElementInfo> boxes = myMapInfo.getElementsByType("box");
		if(boxes != null) {
			for (ElementInfo info : boxes.values()) {
				subBoxes.add(createNewBoxItem(info));
			}
		}

		addBoxMenu.setSubMenu(subBoxes);

		// Creates the sub menu for link types
		MenuItem addLinkMenu = new MenuItem(myConstants.RelationMenuItem());
		addMenu.add(addLinkMenu);
		Menu subLinks = new Menu();

		// Collect link types
		Map<String, ElementInfo> relations = myMapInfo.getElementsByType("relation");
		if(relations != null) {
			for (ElementInfo info : relations.values()) {
				subLinks.add(createNewLinkItem(info));
			}
		}
		
		addLinkMenu.setSubMenu(subLinks);

		return addMenu;
	}
	
	
	protected MenuItem createNewBoxItem(final ElementInfo currentElement) {

		MenuItem boxItem = new MenuItem(currentElement.getElementOption(ParameterTypes.Heading));
		boxItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				handleCreateNewBoxItemSelectionEvent(me, currentElement);
			}
		});
		return boxItem;
	}
	
	/*
	 * method that handles last action of MenuItem boxItem's componentSelected event
	 */
	protected abstract void handleCreateNewBoxItemSelectionEvent(MenuEvent me, final ElementInfo currentElement);

	protected MenuItem createNewLinkItem(final ElementInfo currentElement) {
		MenuItem linkItem = new MenuItem(currentElement.getElementOption(ParameterTypes.Heading));
		linkItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				TreeMap<String, AbstractBox> boxes = new TreeMap<String, AbstractBox>();
				TreeMap<String, AbstractLink> links = new TreeMap<String, AbstractLink>();
				List<Component> mapComponents = GraphMapMenuBar.this.getMyMapSpace().getMyMap().getItems();
				if (mapComponents.size() > 0) {
					for (Component component : mapComponents) {
						if (component instanceof AbstractBox) {
							boxes.put(((AbstractBox) component).getConnectedModel().getValue(ParameterTypes.RootElementId), (AbstractBox) component);
						} else if (component instanceof AbstractLinkPanel) {
							links.put(((AbstractLinkPanel) component).getMyLink().getConnectedModel().getValue(ParameterTypes.RootElementId), ((AbstractLinkPanel) component).getMyLink());
						}
					}
					if (boxes.size() < 2) {
						LASADInfo.display("Error", "There are not enough elements to connect on the map.");
					} else {
						//CreateSpecialLinkDialog linkDialog = new CreateSpecialLinkDialog(currentElement, getMyMapSpace().getMyMap().getID(), boxes, links);
						AbstractCreateSpecialLinkDialog linkDialog = createSpecialLinkDialog(currentElement, getMyMapSpace().getMyMap().getID(), boxes, links);
						linkDialog.show();
					}
				}

			}

		});

		return linkItem;
	}
	protected abstract AbstractCreateSpecialLinkDialog createSpecialLinkDialog(ElementInfo config, String mapId, TreeMap<String, AbstractBox> boxes, TreeMap<String, AbstractLink> links);
	
	protected MenuItem createGroupPointerItem() {
		final MenuItem groupPointerItem = new MenuItem("Create group pointer");
		groupPointerItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				GraphMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				if (GraphMapMenuBar.this.getMyMapSpace().getMyMap().getMyAwarenessCursorID() == -1) {
					int tempPosX = GraphMapMenuBar.this.getMyMapSpace().getMyMap().getHScrollPosition() + GraphMapMenuBar.this.getMyMapSpace().getMyMap().getInnerWidth() / 2;
					int tempPosY = GraphMapMenuBar.this.getMyMapSpace().getMyMap().getVScrollPosition() + GraphMapMenuBar.this.getMyMapSpace().getMyMap().getInnerHeight() / 2;
					handleGroupPointerItemSelectionEvent(me,tempPosX, tempPosY);
					//communicator.sendActionPackage(actionBuilder.createGroupCursor(GraphMapMenuBar.this.getMyMapSpace().getMyMap().getID(), LASAD_Client.getInstance().getUsername(), tempPosX, tempPosY));
				} else {
					LASADInfo.display("Error", "There is already a group pointer on the map.");
				}
			}
		});
		return groupPointerItem;
	}

	/*
	 * method that handles last action of MenuItem groupPointerItem's componentSelected event
	 */
	protected abstract void handleGroupPointerItemSelectionEvent(MenuEvent me, int tempPosX, int tempPosY);
	
	protected MenuItem createSetDebugSettingsItem() {
		MenuItem setDebugSettingsItem = new MenuItem("Set Debug Settings");
		
		String mode = "";
		Menu settings = new Menu();
		
		//DEBUG
		if (DebugSettings.isDebug())
			mode = "Disable";
		else
			mode = "Enable";
		MenuItem settings_debug = new MenuItem(mode + " standard debugging");
		settings_debug.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				if (DebugSettings.isDebug()) 
					((MenuItem)ce.getItem()).setText("Enable standard debugging");
				else
					((MenuItem)ce.getItem()).setText("Disable standard debugging");
				DebugSettings.setDebug(!DebugSettings.isDebug());
			}
		});
		settings.add(settings_debug);
		
		
		//DEBUG_DETAILS
		if (DebugSettings.isDebug_details())
			mode = "Disable";
		else
			mode = "Enable";
		MenuItem settings_debug_details = new MenuItem(mode + " detailed debugging");
		settings_debug_details.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				if (DebugSettings.isDebug_details()) 
					((MenuItem)ce.getItem()).setText("Enable detailed debugging");
				else
					((MenuItem)ce.getItem()).setText("Disable detailed debugging");
				DebugSettings.setDebug_details(!DebugSettings.isDebug_details());
			}
		});
		settings.add(settings_debug_details);
		
		//DEBUG_ERRORS
		if (DebugSettings.isDebug_errors())
			mode = "Disable";
		else
			mode = "Enable";
		MenuItem settings_debug_errors = new MenuItem(mode + " error debugging");
		settings_debug_errors.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				if (DebugSettings.isDebug_errors()) 
					((MenuItem)ce.getItem()).setText("Enable error debugging");
				else
					((MenuItem)ce.getItem()).setText("Disable error debugging");
				DebugSettings.setDebug_errors(!DebugSettings.isDebug_errors());
			}
		});
		settings.add(settings_debug_errors);
		
		
		//DEBUG_POLLING
		if (DebugSettings.isDebug_polling())
			mode = "Disable";
		else
			mode = "Enable";
		MenuItem settings_debug_polling = new MenuItem(mode + " polling debugging");
		settings_debug_polling.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				if (DebugSettings.isDebug_polling()) 
					((MenuItem)ce.getItem()).setText("Enable polling debugging");
				else
					((MenuItem)ce.getItem()).setText("Disable polling debugging");
				DebugSettings.setDebug_polling(!DebugSettings.isDebug_polling());
			}
		});
		settings.add(settings_debug_polling);

		
		setDebugSettingsItem.setSubMenu(settings);
		return setDebugSettingsItem; 
	}
	
	public GraphMapSpace getMyMapSpace() {
		return myMapSpace;
	}

	public void setMyMapSpace(GraphMapSpace myMapSpace) {
		this.myMapSpace = myMapSpace;
	}

	public GraphMapInfo getMyMapInfo()
	{
		return this.myMapInfo;
	}

	protected MenuItem createCenterContributionItem() {
		final MenuItem centerContributionItem = new MenuItem(myConstants.SearchMenuItem());
		centerContributionItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				GraphMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				centerContributionItem.getParentMenu().hide();
				final MessageBox box = MessageBox.prompt(myConstants.CenterContributionTitle(), myConstants.CenterContributionText());
				box.addCallback(new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getText().equalsIgnoreCase("OK")) {
							if (be.getValue() == null) {
								LASADInfo.display("Error", "No valid value entered.");
							} else {
								List<Component> mapComponents = GraphMapMenuBar.this.getMyMapSpace().getMyMap().getItems();
								String searchValue = be.getValue();
								boolean elementFound = false;
								int i = 0;

								while (elementFound == false && i < mapComponents.size()) {
									if (mapComponents.get(i) instanceof AbstractBox) {
										AbstractBox foundBox = (AbstractBox) mapComponents.get(i);
										if (foundBox.getConnectedModel().getValue(ParameterTypes.RootElementId).equals(searchValue)) {
											foundBox.getElement().scrollIntoView();
											foundBox.getMap().getLayoutTarget().dom.setScrollLeft(foundBox.getPosition(true).x - foundBox.getMap().getInnerWidth() / 2 + foundBox.getWidth() / 2);
											foundBox.getMap().getLayoutTarget().dom.setScrollTop(foundBox.getPosition(true).y - foundBox.getMap().getInnerHeight() / 2 + foundBox.getHeight() / 2);
											elementFound = true;
										}
									} else if (mapComponents.get(i) instanceof AbstractLinkPanel) {
										AbstractLinkPanel foundLinkPanel = ((AbstractLinkPanel) mapComponents.get(i));
										if (foundLinkPanel.getMyLink().getConnectedModel().getValue(ParameterTypes.RootElementId).equals(searchValue)) {
											foundLinkPanel.getMyLink().getMap().getLayoutTarget().dom.setScrollLeft(foundLinkPanel.getPosition(true).x - foundLinkPanel.getMyLink().getMap().getInnerWidth() / 2 + foundLinkPanel.getWidth() / 2);
											foundLinkPanel.getMyLink().getMap().getLayoutTarget().dom.setScrollTop(foundLinkPanel.getPosition(true).y - foundLinkPanel.getMyLink().getMap().getInnerHeight() / 2 + foundLinkPanel.getHeight() / 2);
											elementFound = true;
										}
									}
									i++;
								}
								if (!elementFound) {
									LASADInfo.display("Error", "There is no such contribution");
								}
							}
						}
					}
				});
			}
		});
		return centerContributionItem;
	}
}