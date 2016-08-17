package lasad.gwt.client.ui.workspace.tableview.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.tableview.AbstractChildElement;
import lasad.gwt.client.ui.workspace.tableview.ChildElementTypeEnum;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AbstractUrlElement extends AbstractChildElement{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private Label urlIcon;
	private TextBox urlField;
	private Anchor url;

	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractUrlElement(ElementInfo info) {
		
		super(info);
		
		elementVars.add(ParameterTypes.Link);
		
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		urlIcon = new Label();
		urlIcon.setStyleName("extendedURLElement-Logo");
		
		url = new Anchor();
		url.setStyleName("extendedURLElement-TextField-ViewMode");
		
		urlField = new TextBox();
		urlField.setStyleName("extendedURLElement-TextField-EditMode");


		urlIcon.sinkEvents(Event.ONCLICK);
		urlIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
//				remove(url);
//				urlField.setText(url.getText());
//				add(urlField, new RowData(0.9, -1));
//				layout();
//				urlField.setCursorPos(0);
				
				if (activeViewMode) {
					
					argumentMapTable.getFocusHandler().setFocus(AbstractUrlElement.this);
				}
			}
			
		});
		

		urlField.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				
//				Vector<String[]> values = new Vector<String[]>();
//				
//				String oldValue = model.getValue(ParameterTypes.Link);
//				String newValue = urlField.getText();
//				if (!newValue.equals(oldValue)) {
//					
//					String[] valuePair = {ParameterTypes.Link, newValue};
//					values.add(valuePair);
//					
//					int mapId = Integer.valueOf(model.getValue("MAP-ID"));
//					
//					ActionPackage actionPackage = actionFactory.updateElementWithMultipleValues(mapId, model.getId(), values);
//					actionSender.sendActionPackage(actionPackage);
//				}
//				
//				
//				//TODO By Erkang, can not open a window for the url
//				url.setText(urlField.getText());
//				url.setHref(urlField.getText());
//				
//				remove(urlField);
//				add(url, new RowData(0.9, -1));
//				layout();
				
				if (!activeViewMode) {
					
					argumentMapTable.getFocusHandler().releaseFocus(AbstractUrlElement.this);
				}
				
			}
		});
		

		
		add(urlIcon, new RowData(0.1, -1));
		add(url, new RowData(0.9, -1));
		
		activeViewMode = true;
		
//		add(urlIcon, new RowData(0.1, -1));
//		add(urlLabel, new RowData(0.9, -1));
//		add(urlField, new RowData(0.9, -1));
		
	}
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	@Override
	public ChildElementTypeEnum getType() {
		
		return ChildElementTypeEnum.URL;
	}
	
	
	@Override
	public String getSummary() {
		
		return url.getText();
	}
	
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
		setEnabled(!readOnly);
	}
	
	
	@Override
	protected String getVarValue(ParameterTypes vName) {
		
		if(vName == ParameterTypes.Link){
			
			return urlField.getText();
		}
		
		return null;
	}

	@Override
	protected void setVarValue(ParameterTypes vName, String value) {

		if(vName == ParameterTypes.Link) {
			
			if (url.isVisible()) {
				
				url.setText(value);
				url.setHref(value);
				
			} else {
				
				urlField.setText(value);
			}
		}
			
	}

	@Override
	protected void switchToEditMode() {

		remove(url);
		urlField.setText(url.getText());
		add(urlField, new RowData(0.9, -1));
		layout();
		urlField.setCursorPos(0);
		
	}

	@Override
	protected void switchToViewMode() {

		//TODO By Erkang, can not open a window for the url
		url.setText(urlField.getText());
		url.setHref(urlField.getText());
		
		remove(urlField);
		add(url, new RowData(0.9, -1));
		layout();
		
	}

	@Override
	protected abstract void sendUpdateElementWithMultipleValuesToServer(String mapID,
			int elementID, Vector<Object[]> values);
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************
	
//	@Override
//	public void changeValueMVC(UnspecifiedElementModel model, String vname) {
//
//		if(vname.equals(ParameterTypes.Link)) {
//			
//			String newValue = model.getValue(vname);
//			
//			if (url.isVisible()) {
//				
//				url.setText(newValue);
//				url.setHref(newValue);
//				
//			} else {
//				
//				urlField.setText(newValue);
//			}
//			
//		}
//	}

	
	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
}
