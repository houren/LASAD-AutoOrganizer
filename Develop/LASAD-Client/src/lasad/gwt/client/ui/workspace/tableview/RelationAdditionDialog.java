package lasad.gwt.client.ui.workspace.tableview;

import java.util.List;
import java.util.Set;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/*
 * While this class is intended for creating an "Add relation dialog" for when the user selects add relation from the Argument Map menu,
 * it appears that this class is not in use and is replaced by AbstractCreateSpecialLinkDialog, unless I'm missing something.
 * - Noted by Kevin Loughlin, 16 June 2015
 */

public class RelationAdditionDialog extends Dialog {

	// *************************************************************************************
	// Fields
	// *************************************************************************************

	private List<RelationInfo> relationInfos;
	private RelationInfo selectedInfo;

	private Label label;
	private LayoutContainer container;
	
	private ListBox fromBox;
	private ListBox toBox;
	

	// *************************************************************************************
	// Constructor
	// *************************************************************************************

	public RelationAdditionDialog(String name, List<RelationInfo> relationInfos) {

		this.relationInfos = relationInfos;

		setHeading("Add new Relation ...");
		setBorders(false);
		setBodyBorder(false);
		setPlain(true);
		setModal(true);
		setClosable(false);

		setLayout(new FitLayout());
		setSize(250, 150);

		setButtons(Dialog.OKCANCEL);
		setHideOnButtonClick(true);

		
		label = new Label(name);
		
		TableData data1 = new TableData();
		data1.setHorizontalAlign(HorizontalAlignment.CENTER);
		data1.setWidth("50");
		
		TableData data2 = new TableData();
		data2.setHorizontalAlign(HorizontalAlignment.LEFT);
		data2.setWidth("200");
		
		
		HorizontalPanel fromPanel = new HorizontalPanel();
		fromPanel.setTableWidth("100%");
		fromPanel.add(new Label("From"), data1);
		
		fromBox = new ListBox();
		fromBox.setWidth("150px");
		fromPanel.add(fromBox, data2);
		
		HorizontalPanel toPanel = new HorizontalPanel();
		toPanel.setTableWidth("100%");
		toPanel.add(new Label("To"), data1);
		
		toBox = new ListBox();
		toBox.setWidth("150px");
		toPanel.add(toBox, data2);

		initBox();
		
		
		container = new LayoutContainer(new AnchorLayout());
		AnchorData anchorData = new AnchorData("100% 30%");
		container.add(label, anchorData);
		container.add(fromPanel, anchorData);
		container.add(toPanel, anchorData);
		add(container);

	}

	private void initBox() {

		
		if (relationInfos.size() > 0) {
			
			for (RelationInfo info : relationInfos) {

				fromBox.addItem(info.getFromModel().getValue(ParameterTypes.RootElementId));
					
			}
				
			selectedInfo = relationInfos.get(0);
			changeToBoxItems();
			

			fromBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {

					String str = fromBox.getValue(fromBox.getSelectedIndex());

					for (RelationInfo info : relationInfos) {

						if (str.equalsIgnoreCase(info.getFromModel().getValue(ParameterTypes.RootElementId))) {

							selectedInfo = info;
							changeToBoxItems();

							break;
						}
					}

				}
			});

			toBox.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {

					String id = toBox.getValue(toBox.getSelectedIndex());
					selectedInfo.selectToModel(id);
				}
			});
		}
		
	}
	
	
	
	private void changeToBoxItems() {
		
		toBox.clear();
		
		Set<AbstractUnspecifiedElementModel> toModels = selectedInfo.getToModels();

		if (toModels.size() > 0) {
			
			for (AbstractUnspecifiedElementModel model : toModels) {
				String text = model.getValue(ParameterTypes.RootElementId);
				toBox.addItem(text);
			}
			
			String id = toBox.getValue(0);
			selectedInfo.selectToModel(id);
		}
	}
	
	

	// *************************************************************************************
	// getter & setter
	// *************************************************************************************

	public List<RelationInfo> getRelationInfos() {
		return relationInfos;
	}

	public void setRelationInfos(List<RelationInfo> relationInfos) {
		this.relationInfos = relationInfos;
	}

	public RelationInfo getSelectedInfo() {
		return selectedInfo;
	}

	public void setSelectedInfo(RelationInfo selectedInfo) {
		this.selectedInfo = selectedInfo;
	}

}
