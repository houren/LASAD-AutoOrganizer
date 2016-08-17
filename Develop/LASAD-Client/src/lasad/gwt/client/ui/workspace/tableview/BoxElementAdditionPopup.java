package lasad.gwt.client.ui.workspace.tableview;

import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.model.ElementInfo;

import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class BoxElementAdditionPopup extends Popup{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private final Label heading;
	private Map<Label, ElementInfo> labels;
	private final Label cancel;
	private ElementInfo selectedLabel;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public BoxElementAdditionPopup() {
		
		setBorders(true);
		setWidth(225);
		setAutoHide(false);
		setLayout(new FillLayout());
		setStyleName("dialog-root");
		
		heading = new Label("Please choose an element to add...", false);
		heading.setStyleName("dialog-heading");
		add(heading);
		
		labels = new HashMap<Label, ElementInfo>();
		
		cancel = new Label("Cancel");
		cancel.setStyleName("dialog-text");
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		add(cancel);
		
	}
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	public void addLabel(String str, ElementInfo info) {
		
		final Label label = new Label(str, false);
		label.setStyleName("dialog-text");
		label.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setSelectedLabel(labels.get(label));
				hide();
			}
		});
		
		labels.put(label, info);

		insert(label, getItemCount() - 1);
	}


	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
	public Map<Label, ElementInfo> getLabels() {
		return labels;
	}


	public void setLabels(Map<Label, ElementInfo> labels) {
		this.labels = labels;
	}


	public ElementInfo getSelectedLabel() {
		return selectedLabel;
	}


	public void setSelectedLabel(ElementInfo selectedLabel) {
		this.selectedLabel = selectedLabel;
	}
	
}
