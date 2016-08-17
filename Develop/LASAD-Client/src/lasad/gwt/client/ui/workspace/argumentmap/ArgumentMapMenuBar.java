package lasad.gwt.client.ui.workspace.argumentmap;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.helper.connector.Direction;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.GraphMapInfo;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.model.argument.MVController;
import lasad.gwt.client.settings.DebugSettings;
import lasad.gwt.client.ui.box.AbstractBox;
import lasad.gwt.client.ui.link.AbstractLink;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.argumentmap.elements.CreateSpecialLinkDialogArgument;
import lasad.gwt.client.ui.workspace.graphmap.AbstractGraphMap;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapMenuBar;
import lasad.gwt.client.ui.workspace.graphmap.GraphMapSpace;
import lasad.gwt.client.ui.workspace.graphmap.elements.AbstractCreateSpecialLinkDialog;
import lasad.gwt.client.ui.workspace.loaddialogues.ExportScreenShotDialogue;
import lasad.gwt.client.ui.workspace.tableview.ArgumentEditionStyleEnum;
import lasad.gwt.client.ui.workspace.tableview.TableZoomEnum;
import lasad.gwt.client.ui.workspace.tableview.argument.MapTableArgument;
import lasad.gwt.client.xml.MapToXMLConverter;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
//import lasad.gwt.client.ui.workspace.argumentmap.ImportMapDialog;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;

/**
 *	Finishes the implementation of the map's menu bar.  It has been redone by Kevin Loughlin to fall in line with that of a standard
 *	application menu
 *	@author Unknown
 *	@since Unknown, Last Updated 28 August 2015 by Kevin Loughlin
 */
public class ArgumentMapMenuBar extends GraphMapMenuBar {

	private final LASADActionSender communicator = LASADActionSender.getInstance();
	private final ActionFactory actionBuilder = ActionFactory.getInstance();

	public ArgumentMapMenuBar(GraphMapSpace mySpace) {
		super(mySpace);
	}

	@Override
	protected GraphMapInfo getDrawingAreaInformation() {
		MVCViewSession viewSession = (MVCViewSession) myMapSpace.getSession();
		return viewSession.getController().getMapInfo();
		// return myMapSpace.getSession().getController().getMapInfo();
	}

	@Override
	public void removeFeedbackEngine(String agentID, String typeID) {
		if (itemFeedback != null) {
			for (int i = 0; i < itemFeedback.getMenu().getItemCount(); i++) {
				MenuItem x = (MenuItem) itemFeedback.getMenu().getItem(i);
				Logger.log("i = " + i, Logger.DEBUG);
				Logger.log("MENUITEM TEXT: " + x.getText(), Logger.DEBUG);
				Logger.log("AGENTID: " + agentID, Logger.DEBUG);
				Logger.log("TYPEID:" + typeID, Logger.DEBUG);
				if (x.getText().equals(agentID + ": " + typeID)) {
					itemFeedback.getMenu().remove(x);
					break;
				}
			}
		}
	}

	@Override
	public void addFeedbackEngine(final String agentName, final String feedbackName) {
		if (itemFeedback != null) {
			MenuItem newFeedbackAgentItem = new MenuItem(agentName + ": " + feedbackName);
			newFeedbackAgentItem.addSelectionListener(new SelectionListener<MenuEvent>() {
				@Override
				public void componentSelected(MenuEvent me) {
					// Send Action --> Server
					ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().deleteAllFeedbackClusters();
					communicator.sendActionPackage(actionBuilder.requestFeedback(
							ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getID(), LASAD_Client.getInstance().getUsername(),
							agentName, feedbackName));
				}
			});
			itemFeedback.getMenu().add(newFeedbackAgentItem);
		} else
			Logger.log("FEEDBACK MENU IS NULL", Logger.DEBUG_ERRORS);
	}

	@Override
	public void addFeedbackEngine(final String agentName, final String feedbackName, final String agentType) {
		if (itemFeedback != null) {
			MenuItem newFeedbackAgentItem = new MenuItem(agentName + ": " + feedbackName);
			newFeedbackAgentItem.addSelectionListener(new SelectionListener<MenuEvent>() {
				@Override
				public void componentSelected(MenuEvent me) {
					// Send Action --> Server
					ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().deleteAllFeedbackClusters();
					communicator.sendActionPackage(actionBuilder.requestFeedback(
							ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getID(), LASAD_Client.getInstance().getUsername(),
							agentName, feedbackName, agentType));
				}
			});
			itemFeedback.getMenu().add(newFeedbackAgentItem);
		} else
			Logger.log("FEEDBACK MENU IS NULL", Logger.DEBUG_ERRORS);
	}

	@Override
	public void createMenuBar()
	{
		final Button itemLASAD = new Button("LASAD");
		final Button itemFile = new Button("File");
		final Button itemEdit = new Button(myConstants.EditMenu());
		final Button itemView = new Button("View");
		final Button itemHelp = new Button("Help")
		{
			@Override
			protected void onClick(ComponentEvent ce)
			{
				LASAD_Client.getInstance().createTutorialVideosTab();
			}
		};

		Menu lasadMenu = createLASADmenu();
		Menu fileMenu = createFileMenu();
		Menu editMenu = createEditMenu();
		Menu viewMenu = createViewMenu();

		itemLASAD.setMenu(lasadMenu);
		itemFile.setMenu(fileMenu);
		itemEdit.setMenu(editMenu);
		itemView.setMenu(viewMenu);

		/*	Menu was becoming permanently frozen open on double click, so this is a hack to close it instead.  Users should just
			use a single click. */
		itemLASAD.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				itemLASAD.hideMenu();
			}
		});

		itemFile.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				itemFile.hideMenu();
			}
		});

		itemEdit.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				itemEdit.hideMenu();
			}
		});

		itemView.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				itemView.hideMenu();
			}
		});

		this.add(itemLASAD);
		this.add(itemFile);
		this.add(itemEdit);
		this.add(itemView);
		this.add(itemHelp);

		final Button itemGroup = new Button(myConstants.GroupTools());
		Menu groupMenu = createGroupMenu();
		itemGroup.setMenu(groupMenu);

		itemGroup.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				itemGroup.hideMenu();
			}
		});

		if (groupMenu.getItemCount() > 0)
		{
			this.add(itemGroup);
		}

		if (myMapInfo.isFeedback())
		{
			Menu feedbackMenu = createFeedbackMenu();
			itemFeedback.setMenu(feedbackMenu);
			itemFeedback.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
			{
				@Override
				public void handleEvent(BaseEvent be)
				{
					itemFeedback.hideMenu();
				}
			});
			this.add(itemFeedback);
		}

		if (editionStyle == ArgumentEditionStyleEnum.TABLE)
		{
			createZoomMenu();
		}

		if (LASAD_Client.getInstance().getRole().equalsIgnoreCase("developer"))
		{
			final Button itemDebug = new Button("Debug");
			Menu debugMenu = createDebugMenu();
			itemDebug.setMenu(debugMenu);
			itemDebug.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
			{
				@Override
				public void handleEvent(BaseEvent be)
				{
					itemDebug.hideMenu();
				}
			});
			this.add(itemDebug);
		}
	}

	public void setFontSizeSelection(int fontSize){
		if(sizeOptions == null) return;
		for (Component menuComponent : sizeOptions.getItems())
		{
			if (menuComponent instanceof CheckMenuItem)
			{
				if(!((CheckMenuItem) menuComponent).getText().equals(Integer.toString(fontSize)))
					((CheckMenuItem) menuComponent).setChecked(false);
				else 
					((CheckMenuItem) menuComponent).setChecked(true);
			}
		}
	}

	/*	An attempt originally by Kevin Loughlin to downsize area captured by screen shot, but that now works
		thanks to Darlan Sanatana Farias.  Perhaps we should change the method name ;) */
	protected MenuItem kevinCreateScreenshotItem() {
		final MenuItem screenshot = new MenuItem("Create a screenshot");
		screenshot.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				// extend the size of map to adjust itself to the windows
				((ArgumentMap) myMapSpace.getMyMap()).extendMapDimension(Direction.RIGHT,
						(myMapSpace.getMyMap().getMapDimensionSize().width / myMapSpace.getMyMap().getOffsetWidth() + 1)
								* myMapSpace.getMyMap().getOffsetWidth() - myMapSpace.getMyMap().getMapDimensionSize().width);
				((ArgumentMap) myMapSpace.getMyMap()).extendMapDimension(Direction.DOWN,
						(myMapSpace.getMyMap().getMapDimensionSize().height / myMapSpace.getMyMap().getOffsetHeight() + 1)
								* myMapSpace.getMyMap().getOffsetHeight() - myMapSpace.getMyMap().getMapDimensionSize().height);

				// save position in order to return to this position after screenshot
				final int leftBefore = myMapSpace.getMyMap().getBody().getScrollLeft();
				final int topBefore = myMapSpace.getMyMap().getBody().getScrollTop();

				ExportScreenShotDialogue.getInstance().showLoadingScreen();
				Vector<AbstractBox> boxes = LASAD_Client.getMapTab(myMapInfo.getMapID()).getMyMapSpace().getMyMap().getBoxes();

				int top = Integer.MAX_VALUE;
				int left = Integer.MAX_VALUE;

				int bottom = Integer.MIN_VALUE;
				int right = Integer.MIN_VALUE;

				for (AbstractBox box : boxes)
				{
					int yTop = box.getPosition(true).y;
					int yBottom = yTop + box.getHeight();

					int xLeft = box.getPosition(true).x;
					int xRight = xLeft + box.getWidth();

					if (yTop < top)
					{
						top = yTop;
					}

					if (yBottom > bottom)
					{
						bottom = yBottom;
					}

					if (xLeft < left)
					{
						left = xLeft;
					}

					if (xRight > right)
					{
						right = xRight;
					}
				}
				final int LEFT = left;
				final int TOP = top;
				final int WIDTH = right - LEFT;
				final int HEIGHT = bottom - TOP;
				final int interval_H = myMapSpace.getMyMap().getOffsetHeight();
				final int interval_W = myMapSpace.getMyMap().getOffsetWidth();
				
				// roll map to the beginning
				myMapSpace.getMyMap().getBody().scrollTo("top", TOP);
				myMapSpace.getMyMap().getBody().scrollTo("left", LEFT);

				// make a screen shot
				captureMap(myMapSpace.getMyMap().getBody().getId());

				Timer t = new Timer() {
					// the size of the windows
					int position_H = TOP;
					int position_W = LEFT;
					int sum = 0;
					boolean isFinished = false;
					int numberOfColumns = (int) Math.ceil(WIDTH * 1.0 / interval_W);
					int numberOfRows = (int) Math.ceil(HEIGHT * 1.0 / interval_H);
					int numOfAllImages = numberOfColumns*numberOfRows;
					int i = 1; int j = 1;
					
					public void run() {
						
						if (!isFinished) {
							RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GWT.getModuleBaseURL()
									+ "ScreenShot");
							RequestCallback handler = new RequestCallback() {

								@Override
								public void onError(Request request, Throwable e) {
									if (DebugSettings.debug_errors)
										Logger.log(e.toString(), Logger.DEBUG_ERRORS);
								}

								@Override
								public void onResponseReceived(Request request, Response response) {
									// Browser will open a save file dialog box
									// System.err.println("ScreenShot sendet!");
								}
							};

							// update the step of the process
							ExportScreenShotDialogue.getInstance().updateProgress(((float) sum ) / numOfAllImages);

							try {
								// get the screenshot and send it to the servlet
								String map_temp = showMap();
								map_temp = map_temp.substring(map_temp.indexOf(",") + 1);
								builder.sendRequest(LASAD_Client.getInstance().getUsername() + "_" + myMapInfo.getMapID() + sum
										+ ":" + map_temp, handler);
							} catch (RequestException e) {
								e.printStackTrace();
							}
							// if not end, roll the map to next window
							if (i < numberOfRows+1) {
								if (j < numberOfColumns) {
									position_W += interval_W;
									j++;
								} else {
									position_H += interval_H;
									position_W = LEFT;
									i++;
									j = 1;
								}

								myMapSpace.getMyMap().getBody().scrollTo("top", position_H);
								myMapSpace.getMyMap().getBody().scrollTo("left", position_W);
								captureMap(myMapSpace.getMyMap().getBody().getId());
								sum++;
							} else {
								ExportScreenShotDialogue.getInstance().updateProgress((float) 1);
								// return to previous position
								myMapSpace.getMyMap().getBody().scrollTo("top", topBefore);
								myMapSpace.getMyMap().getBody().scrollTo("left", leftBefore);
								isFinished = true;
								RequestBuilder builder_end = new RequestBuilder(RequestBuilder.POST, GWT.getModuleBaseURL()
										+ "ScreenShotMerge");
								try {
									// calculate the cols and rows of the image
									int cols = numberOfColumns;
									int rows = numberOfRows;
									String format = LASAD_Client.getInstance().getUsername() + "_" + myMapInfo.getMapID() + ","
											+ rows + ":" + cols;

									Logger.log(format, Logger.DEBUG);

									builder_end.sendRequest(format, new RequestCallback() {
										@Override
										public void onError(Request request, Throwable e) {
											if (DebugSettings.debug_errors)
												Logger.log(e.toString(), Logger.DEBUG_ERRORS);
										}

										@Override
										public void onResponseReceived(Request request, Response response) {
											// Browser will open a save file dialog box
											ExportScreenShotDialogue.getInstance().closeLoadingScreen();
											com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "ScreenShotMerge",
													"_blank", "enabled");
											// System.err.println("Screenshot mergen!");
										}
									});
									this.cancel();
								} catch (RequestException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				};

				// delay running for 3 seconds
				t.scheduleRepeating(3000);
			}
		});

		return screenshot;
	}
	
	// TODO Zhenyu, currently unused
	private MenuItem createSearchBox() {
		final MenuItem searchbox = new MenuItem("search the Text of Boxes.....");
		searchbox.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO Auto-generated method stub
				final Window w = new Window();
				w.setHeading("Searching Boxes");
				FormData formData = new FormData("-20");

				com.extjs.gxt.ui.client.widget.form.FormPanel simple = new com.extjs.gxt.ui.client.widget.form.FormPanel();
				simple.setBorders(false);
				simple.setBodyBorder(false);
				simple.setLabelWidth(55);
				simple.setPadding(5);
				simple.setHeaderVisible(false);

				final TextField<String> keyword = new TextField<String>();
				keyword.setFieldLabel("Keywords");
				keyword.setEmptyText("Please input the keywords....");
				keyword.setAllowBlank(false);
				simple.add(keyword, formData);

				final SimpleComboBox<String> combo = new SimpleComboBox<String>();
				combo.setFieldLabel("where");
				combo.add("all maps");
				combo.add("this map");
				combo.setSimpleValue("all maps");
				simple.add(combo, formData);

				Button b = new Button("Submit");
				b.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						if (keyword.getValue() != null) {
							// communicator.sendActionPackage(actionBuilder.searchForBoxes(keyword.getValue(),combo.getSelectedIndex()+"",""+myMapInfo.getMapID()));
							w.setVisible(false);
						}
					}
				});
				simple.addButton(b);

				w.add(simple);
				w.show();

			}

		});

		return searchbox;
	}

	// TODO Zhenyu
	public static native void captureMap(String id) /*-{
		$wnd.captureScreenShot(id);
	}-*/;

	// TODO Zhenyu
	public static native String showMap() /*-{
		return $wnd.getImage();
	}-*/;

	@Override
	protected void handleCreateNewBoxItemSelectionEvent(MenuEvent me, final ElementInfo currentElement) {
		int tempPosX = ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getHScrollPosition()
				+ ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getInnerWidth() / 2
				- (Integer.parseInt(currentElement.getUiOption(ParameterTypes.Width)) / 2);
		int tempPosY = ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getVScrollPosition()
				+ ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getInnerHeight() / 2
				- (Integer.parseInt(currentElement.getUiOption(ParameterTypes.Height)) / 2);
		ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
		communicator.sendActionPackage(actionBuilder.createBoxWithElements(currentElement, ArgumentMapMenuBar.this.getMyMapSpace()
				.getMyMap().getID(), tempPosX, tempPosY));
	}

	@Override
	protected AbstractCreateSpecialLinkDialog createSpecialLinkDialog(ElementInfo config, String mapId, TreeMap<String, AbstractBox> boxes,
			TreeMap<String, AbstractLink> links) {
		return new CreateSpecialLinkDialogArgument(config, mapId, boxes, links);
	}

	@Override
	protected void handleGroupPointerItemSelectionEvent(MenuEvent me, int tempPosX, int tempPosY) {
		communicator.sendActionPackage(actionBuilder.createGroupCursor(ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getID(),
				LASAD_Client.getInstance().getUsername(), tempPosX, tempPosY));
	}

	private Menu createGroupMenu() {
		Menu groupMenu = new Menu();

		if (myMapInfo.isGroupPointer()) {
			MenuItem groupPointerItem = createGroupPointerItem();
			groupMenu.add(groupPointerItem);
		}
		return groupMenu;
	}

	private Menu createDebugMenu() {
		Menu debugMenu = new Menu();

		// Creates the sub menu for box types
		MenuItem setDebugSettings = createSetDebugSettingsItem();
		debugMenu.add(setDebugSettings);

		MenuItem editSpots = createEditAttributeItem();
		debugMenu.add(editSpots);

		MenuItem getBoxPairsOfDifferentUsers = getBoxPairsOfDifferentUsersItem();
		debugMenu.add(getBoxPairsOfDifferentUsers);

		return debugMenu;
	}

	private Menu createFeedbackMenu() {
		Menu menu = new Menu();

		MenuItem deleteFeedback = deleteFeedbackItem();
		menu.add(deleteFeedback);

		return menu;
	}

	private void createZoomMenu() {
		final Button zoom = new Button("Zoom");
		Menu zoomMenu = new Menu();

		for (final TableZoomEnum e : TableZoomEnum.values()) {
			MenuItem item = new MenuItem(e.toString());
			zoomMenu.add(item);

			item.addSelectionListener(new SelectionListener<MenuEvent>() {

				@Override
				public void componentSelected(MenuEvent ce) {

					AbstractGraphMap argumentMap = myMapSpace.getMyMap();
					if (argumentMap instanceof MapTableArgument) {

						MapTableArgument argumentMapTable = (MapTableArgument) argumentMap;

						argumentMapTable.resize(e);

					}
				}
			});
		}

		zoom.setMenu(zoomMenu);
		zoom.addListener(Events.OnDoubleClick, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{
				zoom.hideMenu();
			}
		});
		add(zoom);
	}

	private MenuItem createEditAttributeItem() {
		final MenuItem editAttributeItem = new MenuItem("Edit Spots");
		editAttributeItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				editAttributeItem.getParentMenu().hide();
				final MessageBox box = MessageBox.prompt("Edit Attribute", "Code-Pattern: Element-ID,Attribute,Value");
				box.addCallback(new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getText().equalsIgnoreCase("OK")) {
							if (be.getValue() == null) {
								LASADInfo.display("Error", "No valid value entered.");
							} 
							// TODO Sending the action was commented out, so I commented out the rest of the code.  Why was this so?
							/*else {
								String[] input = be.getValue().split(",");
								String id = input[0];
								String attribute = input[1];
								String value = "";
								if (input.length == 3) {
									value = input[2];
								}
								communicator.sendActionPackage(actionBuilder.editAttribute(GraphMapMenuBar.this.getMyMapSpace().getMyMap().getID(),
								id, attribute, value)); 
							} */
						}
					}
				});
			}
		});
		return editAttributeItem;
	}

	private MenuItem getBoxPairsOfDifferentUsersItem() {
		final MenuItem editAttributeItem = new MenuItem("Count number of connected box pairs with different authors");
		editAttributeItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {

				MVController myController = LASAD_Client.getMVCController(myMapSpace.getMyMap().getID());

				int number = 0;
				// If object is a box, protect all links that are connected to
				// the box as well as all boxes that are connected to these
				// links
				Iterator<AbstractUnspecifiedElementModel> myProtectionIterator = myController.getAllElements().iterator();
				while (myProtectionIterator.hasNext()) {
					AbstractUnspecifiedElementModel item = myProtectionIterator.next();
					if (item.getParents().size() >= 2) {
						if (!item.getParents().get(0).getAuthor().equalsIgnoreCase(item.getParents().get(1).getAuthor())) {
							number++;
						}
					}
				}

				LASADInfo.display("Result", number + "");

			}
		});

		return editAttributeItem;
	}

	private MenuItem deleteFeedbackItem() {
		final MenuItem deleteFeedbackItem = new MenuItem(myConstants.DeleteAllFeedbackMenuItem());
		deleteFeedbackItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me) {
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().deleteAllFeedbackClusters();
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
			}
		});
		return deleteFeedbackItem;
	}

	/*
	 *	Create the menu for logging out
	 */
	protected Menu createLASADmenu()
	{
		Menu menu = new Menu();

		MenuItem about = createAboutItem();
		menu.add(about);

		MenuItem logOut = createLogOutItem();
		menu.add(logOut);

		return menu;
	}

	/*
	 *	Create menu for file operations (closing, exporting, etc)
	 */
	protected Menu createFileMenu()
	{
		Menu menu = new Menu();

		MenuItem importItem = createImportItem();
		menu.add(importItem);

		MenuItem exportItem = createExportItem();
		menu.add(exportItem);

		MenuItem screenshot = kevinCreateScreenshotItem();
		menu.add(screenshot);

		MenuItem closeMap = createCloseMapItem();
		menu.add(closeMap);

		return menu;
	}

	/*
	 *	Create the menu for editing (add, delete, autoOrganize, maybe undo if we implement that, etc.)
	 */
	protected Menu createEditMenu() {
		Menu menu = new Menu();

		// TODO Re-implement the undo function (untouched by CMU staff in summer of 2015, to be developed still)
		// MenuItem editUndoItem = createEditUndoItem();
		// menu.add(editUndoItem);

		MenuItem addItem = createAddItem();
		menu.add(addItem);

		MenuItem fontSizeItem = createFontSizeItem();
		menu.add(fontSizeItem);

		MenuItem autoOrganizeItem = createAutoOrganizeItem();
		menu.add(autoOrganizeItem);

		return menu;
	}

	/*
	 *	Create the menu for viewing (all there is right now is centering a contribution)
	 */
	protected Menu createViewMenu()
	{
		Menu menu = new Menu();
		MenuItem centerContribution = createCenterContributionItem();
		menu.add(centerContribution);
		return menu;
	}

	/*
	 *	Creates the about subitem of the LASAD menu that provides general info about what LASAD is, version, etc.
	 */
	protected MenuItem createAboutItem()
	{
		final MenuItem aboutItem = new MenuItem("About LASAD");
		aboutItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me)
			{
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				LASAD_Client.getInstance().createAboutTab();
			}
		});
		return aboutItem;
	}

	/*
	 *	Create the subitem of the LASAD menu that allows a user to logout when clicked
	 */
	protected MenuItem createLogOutItem()
	{
		final MenuItem logOutItem = new MenuItem("Logout");
		logOutItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent me)
			{
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				LASAD_Client.getInstance().logOut();
			}
		});
		return logOutItem;
	}

	protected MenuItem createImportItem()
	{
		final MenuItem loadItem = new MenuItem("Import map");
		loadItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce)
			{
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				ImportMapDialog importDialog = new ImportMapDialog();
				importDialog.show();
			}
		});
		return loadItem;
	}

	/*
	 *	Subitem of the file menu that allows the user to export the map
	 */
	protected MenuItem createExportItem()
	{
		final MenuItem saveItem = new MenuItem("Export map");
		saveItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				MapToXMLConverter conv = new MapToXMLConverter(ArgumentMapMenuBar.this.getMyMapSpace().getMyMap(),
						ArgumentMapMenuBar.this.myMapInfo.getXmlOntology(), ArgumentMapMenuBar.this.myMapInfo.getXmlTemplate());

				// Send the xml-string as a post request to servlet
				// Get the file name via http-response, then open file location
				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GWT.getModuleBaseURL() + "saveToXmlServlet");
				RequestCallback handler = new RequestCallback() {

					@Override
					public void onError(Request request, Throwable e) {
						if (DebugSettings.debug_errors)
							Logger.log(e.toString(), Logger.DEBUG_ERRORS);
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						// Browser will open a save file dialog box
						com.google.gwt.user.client.Window.open(GWT.getModuleBaseURL() + "saveToXmlServlet", "_blank", "enabled");
					}
				};
				try {
					builder.sendRequest(conv.getXmlString(), handler);
				} catch (RequestException e) {
					e.printStackTrace();
				}
			}
		});
		return saveItem;
	}

	/*
	 *	Subitem of the file menu that lets the user close the current map and return to the map overview screen when clicked
	 */
	protected MenuItem createCloseMapItem()
	{
		final MenuItem closeItem = new MenuItem("Close");
		closeItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				communicator.sendActionPackage(actionBuilder.leaveMap(ArgumentMapMenuBar.this.getMyMapInfo().getMapID()));
			}
		});
		return closeItem;
	}

	/*
	 *	Subitem of the edit menu that allows an alternative way to click and drag for adding an item to the map
	 */
	protected MenuItem createAddItem()
	{
		MenuItem addItem = new MenuItem("Add");
		addItem.setSubMenu(createBoxLinkMenu());
		return addItem;
	}

	/*
	 *	Provides "Contribution" and "relation" as options
	 */
	protected Menu createBoxLinkMenu()
	{
		Menu boxLinkMenu = new Menu();
		boxLinkMenu.add(createContributionItem());
		boxLinkMenu.add(createRelationItem());
		return boxLinkMenu;
	}

	protected MenuItem createFontSizeItem()
	{
		MenuItem fontSizeItem = new MenuItem("Select font size");
		fontSizeItem.setSubMenu(createSizeSelector());
		return fontSizeItem;
	}

	Menu sizeOptions = null;
	protected Menu createSizeSelector()
	{
		sizeOptions = new Menu();
		for (int i = 8; i < 37; i += 2)
		{
			sizeOptions.add(createNextFontSize(i));
		}
		return sizeOptions;
	}

	protected CheckMenuItem createNextFontSize(final int i)
	{
		final CheckMenuItem fontSize = new CheckMenuItem(String.valueOf(i));
		if (i == 10)
		{
			fontSize.setChecked(true);
		}
			
		fontSize.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent me)
			{
				for (Component menuComponent : fontSize.getParentMenu().getItems())
				{
					if (menuComponent instanceof CheckMenuItem)
					{
						((CheckMenuItem) menuComponent).setChecked(false);
					}
				}

				fontSize.setChecked(true);
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				communicator.sendActionPackage(actionBuilder.changeFontSize(ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getID(), i));
			}
		});
		return fontSize;
	}

	/*
	 *	Create the item listed as contribution for the add subitem.
	 */
	protected MenuItem createContributionItem()
	{
		final MenuItem boxMenu = new MenuItem(myConstants.ContributionMenuItem());
		Menu subBoxes = new Menu();

		// Collect box types
		
		Map<String, ElementInfo> boxes = myMapInfo.getElementsByType("box");
		if(boxes != null) {
			for (ElementInfo info : boxes.values()) {
				subBoxes.add(createNewBoxItem(info));
			}
		}

		boxMenu.setSubMenu(subBoxes);
		return boxMenu;
	}

	/*
	 *	Create the item listed as relation for the add subitem.
	 */
	protected MenuItem createRelationItem()
	{
		// Creates the sub menu for link types
		final MenuItem linkMenu = new MenuItem(myConstants.RelationMenuItem());
		Menu subLinks = new Menu();

		// Collect link types
		Map<String, ElementInfo> relations = myMapInfo.getElementsByType("relation");
		if(relations != null) {
			for (ElementInfo info : relations.values()) {
				subLinks.add(createNewLinkItem(info));
			}
		}
		
		linkMenu.setSubMenu(subLinks);
		
		return linkMenu;
	}

	/*
	 *	Subitem of the edit menu that autoOrganizes the map when subitem (specifying orientation) is pressed
	 */
	protected MenuItem createAutoOrganizeItem()
	{
		final MenuItem autoOrganizeItem = new MenuItem("Map Organization");
		autoOrganizeItem.setSubMenu(chooseOrganizationStyle());
		return autoOrganizeItem;
	}

	protected Menu chooseOrganizationStyle()
	{
		Menu runOrAdjust = new Menu();
		runOrAdjust.add(createRunOrganizer());
		runOrAdjust.add(createOrganizerSettings());
		return runOrAdjust;
	}

	protected MenuItem createRunOrganizer()
	{
		final MenuItem runOrganizer = new MenuItem("Organize Map");
		runOrganizer.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				AbstractGraphMap myMap = ArgumentMapMenuBar.this.getMyMapSpace().getMyMap();
				myMap.getFocusHandler().releaseAllFocus();
				communicator.sendActionPackage(actionBuilder.autoOrganize(myMap.getID(), myMap.getOrgOrientation(), myMap.getOrgBoxWidth(), myMap.getOrgBoxHeight()));
			}
		});
		return runOrganizer;
	}

	protected MenuItem createOrganizerSettings()
	{
		final MenuItem organizerPreferences = new MenuItem("Preferences");
		organizerPreferences.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			@Override
			public void componentSelected(MenuEvent ce)
			{
				ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getFocusHandler().releaseAllFocus();
				CreatePreferencesDialog preferencesDialog = new CreatePreferencesDialog(ArgumentMapMenuBar.this.getMyMapSpace().getMyMap().getID());
				preferencesDialog.show();
			}
		});
		return organizerPreferences;
	}
}