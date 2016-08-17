package lasad.gwt.client.ui.workspace.tableview;

import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.model.ElementInfo;

import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;

public class BoxClosePopup extends Popup{

	
	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private final Label heading;
	private Map<Label, ElementInfo> labels;
	private CheckBox conform;
	
	private Boolean close;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public BoxClosePopup() {
		
		setBorders(true);
		setWidth(225);
		setAutoHide(false);
		setLayout(new FillLayout());
		setStyleName("dialog-root");
		
		heading = new Label("Please choose an element to delete...", false);
		heading.setStyleName("dialog-heading");
		add(heading);
		
		
		
		labels = new HashMap<Label, ElementInfo>();
		Label yesLabel = new Label("Yes, delete it", false);
		yesLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				close = Boolean.TRUE;
				hide();
			}
		});
		yesLabel.setStyleName("dialog-text");
		add(yesLabel);
		
		
		Label noLabel = new Label("No, keep it", false);
		noLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				close = Boolean.FALSE;
				hide();
			}
		});
		noLabel.setStyleName("dialog-text");
		add(noLabel);
		
		labels.put(yesLabel, new ElementInfo());
		labels.put(noLabel, new ElementInfo());
		
		
		
		conform = new CheckBox("Do not ask me again");
		conform.setStyleName("dialog-text-nohighlighting delete-dialog-checkbox-label");
//		conform.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				hide();
//			}
//		});
		add(conform);
		

	}
	
	
	//*************************************************************************************
	// getter & setter
	//*************************************************************************************

	public CheckBox getConform() {
		return conform;
	}

	public void setConform(CheckBox conform) {
		this.conform = conform;
	}

	public Boolean isClose() {
		return close;
	}

	public void setClose(Boolean close) {
		this.close = close;
	}
	
}
