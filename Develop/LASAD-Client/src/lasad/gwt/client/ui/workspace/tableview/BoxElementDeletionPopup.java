package lasad.gwt.client.ui.workspace.tableview;

import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.model.AbstractUnspecifiedElementModel;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCHelper;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;



public class BoxElementDeletionPopup extends Popup{
	
	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private final Label heading;
	private final Label cancel;
	private final String separator;
	
	private Map<Label, AbstractUnspecifiedElementModel> labels;
	private AbstractUnspecifiedElementModel selectedModel;
	
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public BoxElementDeletionPopup() {
		
		setBorders(true);
		setWidth(225);
		setAutoHide(false);
		setLayout(new FillLayout());
		setStyleName("dialog-root");
		
		heading = new Label("Please choose an element to delete...", false);
		heading.setStyleName("dialog-heading");
		add(heading);
		
		labels = new HashMap<Label, AbstractUnspecifiedElementModel>();
		
		cancel = new Label("Cancel");
		cancel.setStyleName("dialog-text");
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		add(cancel);
		
		separator = ":";
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	public void addLabel(AbstractUnspecifiedElementModel model, AbstractChildElement childElement) {
		
		ElementInfo childInfo = childElement.info;
		
		int count = MVCHelper.getChildModelsByElementID(model, childInfo.getElementID()).size();
		
		if (count > childInfo.getMinQuantity()) {
			
			String text = childElement.model.getValue(ParameterTypes.Text);
			String suffix = new String();
			
			if (text != null) {
				
				if (text.length() > 5) {
					suffix = text.substring(0, 5);
				} else {
					suffix = text;
				}
			}
			
			
			String str = new String();
			if (childInfo.getElementOption(ParameterTypes.LongLabel) != null) {
			
				str = childInfo.getElementOption(ParameterTypes.LongLabel) + separator + suffix;
			
			} else if (childInfo.getElementOption(ParameterTypes.Label) != null) {
			
				str = childInfo.getElementOption(ParameterTypes.Label) + separator + suffix;
			
			} else {
			
				str = childInfo.getElementID() + separator + suffix;
			}
		
			insetLabel(str, childElement.model);
		}
		
	}
	
	
	private void insetLabel(String str, final AbstractUnspecifiedElementModel model) {
		
		final Label label = new Label(str, false);
		label.setStyleName("dialog-text");
		labels.put(label, model);
		label.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				selectedModel = model;
				hide();
			}
		});

		insert(label, getItemCount() - 1);
	}

	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************
	
	public AbstractUnspecifiedElementModel getSelectedModel() {
		
		return selectedModel;
	}
	
}
