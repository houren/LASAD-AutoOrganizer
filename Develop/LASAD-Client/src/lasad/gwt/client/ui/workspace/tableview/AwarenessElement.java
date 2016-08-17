package lasad.gwt.client.ui.workspace.tableview;

import java.util.Date;
import java.util.Vector;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.Label;



public class AwarenessElement extends AbstractChildElement{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private Label icon;
	private Label userAndTimeLabel;
	
	protected final ActionFactory actionFactory = ActionFactory.getInstance();
	protected final LASADActionSender actionSender = LASADActionSender.getInstance();

	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AwarenessElement(ElementInfo info) {
		
		super(info);
		
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		icon = new Label();
		icon.setStyleName("box-ee-awareness-icondiv");
		userAndTimeLabel  = new Label();
		userAndTimeLabel.setStyleName("box-ee-awareness-viewModeDiv");
		
		// width and height of icon are 16px.
		add(icon, new RowData(16, 16, new Margins(0, 1, 0, 5)));
		add(userAndTimeLabel);
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	@Override
	public ChildElementTypeEnum getType() {
		
		return ChildElementTypeEnum.AWARENESS;
	}
	
	@Override
	public String getSummary() {
		
		return userAndTimeLabel.getText();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
	}
	
	
	@Override
	protected String getVarValue(ParameterTypes vName) {

		return null;
	}


	@Override
	protected void setVarValue(ParameterTypes vName, String value) {
		
	}


	@Override
	protected void switchToEditMode() {
		
	}


	@Override
	protected void switchToViewMode() {
		
	}
	
	
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************

	@Override
	public void changeValueMVC(AbstractUnspecifiedElementModel model, ParameterTypes vname) {

		String user = model.getAuthor() + " , ";

		String time = "";
		if (model.getValue(ParameterTypes.Time) != null) {
			Date date = new Date(Long.parseLong(model.getValue(ParameterTypes.Time)));
			String timeString = date.toString();
			time = timeString.substring(4, 16);
		}
		
		userAndTimeLabel.setText(user + time);
	}


	@Override
	protected void sendUpdateElementWithMultipleValuesToServer(String mapID,
			int elementID, Vector<Object[]> values) {
		actionSender.sendActionPackage(actionFactory.updateElementWithMultipleValues(mapID, elementID, values));
	}


	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
}
