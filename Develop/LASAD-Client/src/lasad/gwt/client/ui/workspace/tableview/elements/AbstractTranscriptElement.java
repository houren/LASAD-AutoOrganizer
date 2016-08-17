package lasad.gwt.client.ui.workspace.tableview.elements;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ArgumentMapSpace;
import lasad.gwt.client.ui.workspace.tableview.AbstractChildElement;
import lasad.gwt.client.ui.workspace.tableview.ChildElementTypeEnum;
import lasad.gwt.client.ui.workspace.transcript.Transcript;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;

public abstract class AbstractTranscriptElement extends AbstractChildElement{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private Label icon;
	private Label transacipt;
	
	private int startRow;
	private int endRow;
	private String line;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractTranscriptElement(ElementInfo info) {
		
		super(info);
		
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		icon = new Label();
		icon.setStyleName("box-ee-transcript-icondiv");
		icon.sinkEvents(Event.ONCLICK);
		icon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {

				if (model != null) {
					
					String mapId = model.getValue(ParameterTypes.MapId);
					
					ArgumentMapSpace space = LASAD_Client.getMapTab(mapId).getMyMapSpace();
			
					Transcript transcript = space.getTranscript();
			
					if (transcript != null) {
						
						int parentId = model.getParents().get(0).getId();
						
						transcript.highlightElementsTranscriptLink(parentId);
					}
					
				} else {
					
				}
			}
		});
		
		transacipt = new Label();
		transacipt.setStyleName("box-ee-transcript-viewModeDiv");

		add(icon, new RowData(16, 16));
		add(transacipt);
		
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	@Override
	public ChildElementTypeEnum getType() {
		
		return ChildElementTypeEnum.TRANSCRIPT;
	}
	
	@Override
	public String getSummary() {
		
		return line;
	}
	
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
	}
	
	@Override
	protected String getVarValue(ParameterTypes vName) {

		return "";
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
		
		startRow = Integer.parseInt(this.model.getValue(ParameterTypes.StartRow));
		endRow = Integer.parseInt(this.model.getValue(ParameterTypes.EndRow));
		
		if (startRow != endRow) {
			
			transacipt.setText(" Transcript: Line " + startRow + " - " + endRow);
			line = startRow + " - " + endRow;
			
		} else {
			
			transacipt.setText(" Transcript: Line " + startRow);
			line = startRow + "";
		}
		
	}


	@Override
	protected abstract void sendUpdateElementWithMultipleValuesToServer(String mapID, int elementID, Vector<Object[]> values);


	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
}
