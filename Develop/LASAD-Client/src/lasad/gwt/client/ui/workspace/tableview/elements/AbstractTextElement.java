package lasad.gwt.client.ui.workspace.tableview.elements;

import java.util.Vector;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.model.MVCViewRecipient;
import lasad.gwt.client.model.argument.MVCViewSession;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.tableview.AbstractChildElement;
import lasad.gwt.client.ui.workspace.tableview.ChildElementTypeEnum;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;

public abstract class AbstractTextElement extends AbstractChildElement{

	//*************************************************************************************
	//	Fields
	//*************************************************************************************
	
	private Label label;
	private TextBox textBox;
	private TextArea textArea;
	private TextBoxBase boxOrArea;
	
	private String texttype;
	
	private HandlerRegistration registration;
	private ClickHandler activeHandler;
	private ClickHandler inactiveHandler;

	private boolean editable;
	
	//*************************************************************************************
	//	Constructor
	//*************************************************************************************
	
	public AbstractTextElement(ElementInfo info) {
		
		super(info);
		
		elementVars.add(ParameterTypes.Text);
		elementVars.add(ParameterTypes.Status);
		
		editable = true;
		
		setStyleName("extendedTextElement");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		String labelString = info.getElementOption(ParameterTypes.Label);
		String textType = info.getElementOption(ParameterTypes.TextType);
		
		RowData labelData = null;
		RowData fieldData = null;
		RowData areaData = null;
		
		if (labelString != null) {
			
			labelData = new RowData(0.3, 1);
			fieldData = new RowData(0.7, 1);
			areaData = new RowData(0.7, 1);
			
		} else {
			
			fieldData = new RowData(1, 1);
			areaData = new RowData(1, 1);
		}
		
		
		if (labelString != null) {
			label = new Label(labelString);
			label.setStyleName("extendedTextElement-TextLabel-Left");
			add(label, labelData);
		}
		
		
		if ("textfield".equalsIgnoreCase(textType)) {
			
			texttype = "textfield";
			
			textBox = new ExtendedTextBox();
			textBox.setStyleName("extendedTextElement-TextField-ViewMode");
			
			add(textBox, fieldData);
			boxOrArea = textBox;
			
		} else {
			
			textArea = new ExtendedTextArea();
			textArea.setStyleName("extendedTextElement-TextArea-ViewMode");
			
			add(textArea, areaData);
			boxOrArea = textArea;
		}
		
		
		activeHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(activeViewMode){
					
					argumentMapTable.getFocusHandler().releaseAllFocus();
					
					argumentMapTable.getFocusHandler().setFocus(AbstractTextElement.this);
				}
				
			}
		};
		
		
		inactiveHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if (editable) {
					
					LASADInfo.display("Error", "This element is locked because another user is working on it currently...");
				} else {
					
					LASADInfo.display("Error", "This element is read only with this view mode...");
				}
				
			}
		};
		
		
		registration = boxOrArea.addClickHandler(activeHandler);
		
		setViewMode(true);
		
//		layout();
	}
	
	
	//*************************************************************************************
	//	Methods
	//*************************************************************************************
	
	@Override
	public ChildElementTypeEnum getType() {
		
		return ChildElementTypeEnum.TEXT;
	}
	
	@Override
	public String getSummary() {
		
		return boxOrArea.getText();
	}
	
	
	@Override
	public void setReadOnly(boolean readOnly) {
		
		editable = !readOnly;
		
		boxOrArea.setReadOnly(readOnly);
		
		if (!editable) {
			
			registration.removeHandler();
			registration = boxOrArea.addClickHandler(inactiveHandler);
		
		} else {
			
			registration.removeHandler();
			registration = boxOrArea.addClickHandler(activeHandler);
		}
		
	}
	
	
	@Override
	public void setBlocked(boolean blocked) {
		
		super.setBlocked(blocked);
		
			if(blocked) {
				
				if (!activeViewMode) {
					
					setViewMode(true);
				}
				
				if (boxOrArea instanceof TextBox) {
				
					boxOrArea.setStyleName("extendedTextElement-TextField-LockedMode");
				}
			
				if (boxOrArea instanceof TextArea) {
				
					boxOrArea.setStyleName("extendedTextElement-TextArea-LockedMode");
				}
				
				
				if (editable) {
					
					boxOrArea.setReadOnly(true);
					registration.removeHandler();
					
					registration = boxOrArea.addClickHandler(inactiveHandler);
				}
				
				setElementFocus(false);
			
			} else {
				
				if(activeViewMode) {
					
					if (boxOrArea instanceof TextBox) {
						
						boxOrArea.setStyleName("extendedTextElement-TextField-ViewMode");
					}
				
					if (boxOrArea instanceof TextArea) {
					
						boxOrArea.setStyleName("extendedTextElement-TextArea-ViewMode");
					}

					if (editable) {
						
						boxOrArea.setReadOnly(false);
						registration.removeHandler();
						
						registration = boxOrArea.addClickHandler(activeHandler);
					}
					
				}	
			}

	}
	
	
	@Override
	public void setElementFocus(boolean focus) {
		super.setElementFocus(focus);
		
		if(focus) {
//			actionSender.sendActionPackage(actionFactory.lockElement(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId()));
			sendLockElementToServer(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId());
		}
		else {
//			actionSender.sendActionPackage(actionFactory.unlockElement(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId()));
			sendUnlockElementToServer(this.getConnectedModel().getValue(ParameterTypes.MapId), this.getConnectedModel().getId());
		}
	}
	protected abstract void sendLockElementToServer(String mapID, int elementID);
	protected abstract void sendUnlockElementToServer(String mapID, int elementID);
	
	protected String getVarValue(ParameterTypes name) {
		
		if (name == ParameterTypes.Text) {
			
			return boxOrArea.getText();	
		}
		
		return null;
	}
	
	protected void setVarValue(ParameterTypes name, String value) {
		
		if (name == ParameterTypes.Text) {
			
			boxOrArea.setText(value);
			
		}
		
		if (name == ParameterTypes.Status) {
			
			if(value.equalsIgnoreCase("LOCK")) {
				setBlocked(true);
			}
			
			if(value.equalsIgnoreCase("UNLOCK")) {
				setBlocked(false);
			}
		}
	}


	@Override
	protected void switchToEditMode() {

		if(!blocked) {

			if (argumentMapTable != null) {
				
				MVCViewSession session = (MVCViewSession) argumentMapTable.getMyViewSession();
				Vector<MVCViewRecipient> recipients = session.getMVCViewRecipientsByModel(model);
				
				for (MVCViewRecipient recipient: recipients) {
					
					if (recipient instanceof AbstractTextElement) {
						
						AbstractTextElement textElement = (AbstractTextElement)recipient;
						TextBoxBase base = textElement.getBoxOrArea();
						
//						textElement.setActiveViewMode(false);
						
						if (base instanceof TextBox) {
							
							base.setStyleName("extendedTextElement-TextField-EditMode");
						}
						
						if (base instanceof TextArea) {
							
							base.setStyleName("extendedTextElement-TextArea-EditMode");
						}
					}
				}
				
			} else {
				
				if (boxOrArea instanceof TextBox) {
					
					boxOrArea.setStyleName("extendedTextElement-TextField-EditMode");
				}
				
				if (boxOrArea instanceof TextArea) {
					
					boxOrArea.setStyleName("extendedTextElement-TextArea-EditMode");
				}
			}
		}
		
	}


	@Override
	protected void switchToViewMode() {
		
		if (argumentMapTable != null) {
			
			MVCViewSession session = (MVCViewSession) argumentMapTable.getMyViewSession();
			Vector<MVCViewRecipient> recipients = session.getMVCViewRecipientsByModel(model);
			
			for (MVCViewRecipient recipient: recipients) {
				
				if (recipient instanceof AbstractTextElement) {
					
					AbstractTextElement textElement = (AbstractTextElement)recipient;
					TextBoxBase base = textElement.getBoxOrArea();
					
//					textElement.setActiveViewMode(true);
					
					if (base instanceof TextBox) {
						
						base.setStyleName("extendedTextElement-TextField-ViewMode");
					}
				
					if (base instanceof TextArea) {
					
						base.setStyleName("extendedTextElement-TextArea-ViewMode");
					}
				}
			}

		} else {
			
			if (boxOrArea instanceof TextBox) {
				
				boxOrArea.setStyleName("extendedTextElement-TextField-ViewMode");
			}
		
			if (boxOrArea instanceof TextArea) {
			
				boxOrArea.setStyleName("extendedTextElement-TextArea-ViewMode");
			}
		}
			
	}
	
	
	
	//*************************************************************************************
	// methods of Inteface MVCViewRecipient
	//*************************************************************************************

	
	
	//*************************************************************************************
	//	getter & setter
	//*************************************************************************************

	public String getLabelText() {
		
		if (label != null) {
			
			return label.getText();
		} else {

			return "CONTENT";
		}
	}
	
	public String getTexttype() {
		return texttype;
	}

	public void setTexttype(String texttype) {
		this.texttype = texttype;
	}


	/**
	 * @return the boxOrArea
	 */
	public TextBoxBase getBoxOrArea() {
		return boxOrArea;
	}


	/**
	 * @param boxOrArea the boxOrArea to set
	 */
	public void setBoxOrArea(TextBoxBase boxOrArea) {
		this.boxOrArea = boxOrArea;
	}


	@Override
	protected abstract void sendUpdateElementWithMultipleValuesToServer(String mapID, int elementID, Vector<Object[]> values);


}