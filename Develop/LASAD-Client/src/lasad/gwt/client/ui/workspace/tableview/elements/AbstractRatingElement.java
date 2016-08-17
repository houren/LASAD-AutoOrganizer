package lasad.gwt.client.ui.workspace.tableview.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tableview.AbstractChildElement;
import lasad.gwt.client.ui.workspace.tableview.ChildElementTypeEnum;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;

public abstract class AbstractRatingElement extends AbstractChildElement{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************

	private Label name;
	private Label addIcon;
	private Label subIcon;
	
	private String text;
	private int score;
	private final int MAX_SCORE;
	private final int MIN_SCORE;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractRatingElement(ElementInfo info) {
		
		super(info);
		
		elementVars.add(ParameterTypes.Score);
		
		MIN_SCORE = Integer.parseInt(info.getElementOption(ParameterTypes.MinScore));
		MAX_SCORE = Integer.parseInt(info.getElementOption(ParameterTypes.MaxScore));
		
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		if (info.getElementOption(ParameterTypes.Label) != null) {
			
			text = new String(info.getElementOption(ParameterTypes.Label) + ": ");
			
		} else {
			
			text = new String("Score: ");
		}
		
		
		score = 0;
		name = new Label(text + score);
		name.setStyleName("rating-Text");
		
		
		addIcon = new Label();
		addIcon.setStyleName("myButton_plus");
		addIcon.sinkEvents(Event.ONCLICK);
		addIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if (activeViewMode) {
					
					if (score < MAX_SCORE){
						score = score + 1;
						
						argumentMapTable.getFocusHandler().setFocus(AbstractRatingElement.this);
						
						argumentMapTable.getFocusHandler().releaseFocus(AbstractRatingElement.this);
							
					} else {
							
						LASADInfo.display("Error", "Maximalwert erreicht");
					}
				}
			}
		});
		
		
		subIcon = new Label();
		subIcon.setStyleName("myButton_minus");
		subIcon.sinkEvents(Event.ONCLICK);
		subIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if (activeViewMode) {
					
					if (score > MIN_SCORE) {
						score = score - 1;
						
						argumentMapTable.getFocusHandler().setFocus(AbstractRatingElement.this);
						argumentMapTable.getFocusHandler().releaseFocus(AbstractRatingElement.this);
							
					} else {
					
						LASADInfo.display("Error", "Minimalwert erreicht");
					}
				}
				
			}
		});
		
		add(name);
		add(subIcon);
		add(addIcon);
		
		setViewMode(true);
		
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	@Override
	public ChildElementTypeEnum getType() {
		
		return ChildElementTypeEnum.RATING;
	}
	
	@Override
	public String getSummary() {
		
		return String.valueOf(score);
	}
	
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
		addIcon.setVisible(!readOnly);
		subIcon.setVisible(!readOnly);
	}
	
	
	@Override
	protected String getVarValue(ParameterTypes vName) {
		
		if(vName == ParameterTypes.Score){
			
			return String.valueOf(score);
		}
		
		return null;
	}


	@Override
	protected void setVarValue(ParameterTypes vName, String value) {

		if (vName == ParameterTypes.Score) {
				
			if (score <= MAX_SCORE && score >= MIN_SCORE) {
				
				name.setText(text + value);
				score = Integer.parseInt(value);
			}
		}
	}


	@Override
	protected void switchToEditMode() {
		
	}


	@Override
	protected void switchToViewMode() {
		
	}


	protected abstract void sendUpdateElementWithMultipleValuesToServer(String mapID, int elementID, Vector<Object[]> values);
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************
	

	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************

}
