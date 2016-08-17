package lasad.gwt.client.ui.common.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public abstract class AbstractExtendedRadioButtonElement extends AbstractExtendedElement{

	
	private int min_width = 10;
	//private int element_width = 10;
	
	private  String label;
	private String  background_color="";
	private String  font_color="";
	private boolean mode=false;
	private EventListener myEventListener = null;
	private String Check="";
	Element spaceTD = DOM.createTD();
	private Element elementContent = null;
	Element tbody = DOM.createTBody();
	Element row = DOM.createTR();
	Element labelTD = DOM.createTD();
	Element labelE = DOM.createDiv();
	Element checkdiv = DOM.createDiv();
	Element  myradiobutton;
		
	
		Element space = DOM.createDiv();
		ArrayList <Map<String, String>> radiobuttons;
	
	
		ArrayList <String> activeitems;
		ArrayList <Integer> activechildindex;
		
		
		public AbstractExtendedRadioButtonElement(boolean readonly,
			ExtendedElementContainerInterface container, ElementInfo config) {
			super(container, config,readonly);
		// Adding variable to store user selection
			this.elementVars.add(ParameterTypes.MyCheck);
			radiobuttons=new ArrayList <Map<String, String>>();
			activeitems=new ArrayList <String>();
			activechildindex=new ArrayList<Integer>();
			//Getting radiobuttons configuration
			radiobuttons=config.getRadioButtonElements();
			getparameters(config);
		}
		
		

		
		public AbstractExtendedRadioButtonElement(
				ExtendedElementContainerInterface container, ElementInfo config,boolean _mode) {
				super(container, config);
			// Adding variable to store user selection
				this.elementVars.add(ParameterTypes.MyCheck);
				radiobuttons=new ArrayList <Map<String, String>>();
				activeitems=new ArrayList <String>();
				activechildindex=new ArrayList<Integer>();
				//Getting radiobuttons configuration
				radiobuttons=config.getRadioButtonElements();
				getparameters(config);
				mode=_mode;
			}
			
		
		
		  
		void getparameters(ElementInfo myconfig)
		{
			// Getting label string
			   
			if (myconfig.getElementOption(ParameterTypes.Label) != null) {
				label = myconfig.getElementOption(ParameterTypes.Label) + ": ";
			}
			else {
				label = "Rate: ";
			}
			
			// Getting background color
			if (myconfig.getElementOption(ParameterTypes.BackgroundColor) != null || myconfig.getElementOption(ParameterTypes.BackgroundColor)=="") {
				background_color =myconfig.getElementOption(ParameterTypes.BackgroundColor);
			}
			else {
				background_color="#FFFFFF";
			}
			
			//Getting font color
			if (myconfig.getUiOption(ParameterTypes.FontColor) != null || myconfig.getUiOption(ParameterTypes.FontColor)=="") {
				font_color =myconfig.getUiOption(ParameterTypes.FontColor);
			}
			else {
				font_color="#000000";
			}
			
			if (myconfig.getUiOption(ParameterTypes.Width) != null || myconfig.getUiOption(ParameterTypes.Width)=="") {
				//element_width =Integer.parseInt(myconfig.getUiOption(ParameterTypes.Width));
			}
			
		
		}

// Producing unique string for group id of radio button
		   public static String getRandomString() {
		    	
			    double a=	Math.random();
			    	String ak=Double.toString(a).substring(3, 11);
			    	
			        return ak;
			        
			    }
	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

		//Creating table for elements
		elementContent = DOM.createTable();
		DOM.appendChild(elementContent, tbody);
		DOM.appendChild(tbody, row);	
		DOM.appendChild(row, labelTD);
		DOM.appendChild(labelTD, labelE);
		DOM.setStyleAttribute(row,"backgroundColor",background_color);
		

		// Label
		DOM.setStyleAttribute(labelE, "fontSize", 10+"px");
		DOM.setStyleAttribute(labelE, "fontWeight", "bold");
		DOM.setStyleAttribute(labelE, "color",font_color);
		
		DOM.setStyleAttribute(labelE, "align", "left");
		labelE.setInnerHTML(label);
		
		int radiobuttoncount=radiobuttons.size();
		
// limiting maximum number of radio button  to at most 4
		if(radiobuttoncount>=5)
			radiobuttoncount=4;
		if(radiobuttoncount==3)
			min_width=30;
		else if(radiobuttoncount==2)
			min_width=60;
		
		
		String group=getRandomString();
	
		DOM.setStyleAttribute(labelE, "width", 6+min_width+"px");
	
		for(int i=0;i<radiobuttoncount;i++){
			// Rendering radio button elements
			Map<String, String> radiobuttonoptions = new HashMap<String, String>();
			radiobuttonoptions=radiobuttons.get(i);
			Element radiobuttoncell = DOM.createTD();
			Element radiobuttonlabel = DOM.createTD();
			
			String selected=radiobuttonoptions.get("checked");
			String classid=radiobuttonoptions.get("id");
			
		  myradiobutton= DOM.createInputRadio(group);
		  // Checking initial status of the radio button
		if(selected.equalsIgnoreCase("1"))
	   ((InputElement) myradiobutton.cast()).setDefaultChecked(true);
		myradiobutton.setClassName(classid);
		myradiobutton.setId(Integer.toString(i));
		Element myradiobuttonlabel= DOM.createLabel();
		myradiobuttonlabel.setInnerText(radiobuttonoptions.get("label"));
		DOM.appendChild(radiobuttoncell, myradiobutton);	
		DOM.appendChild(row,radiobuttoncell);
		DOM.appendChild(row, radiobuttonlabel);
		DOM.appendChild(radiobuttonlabel, myradiobuttonlabel);
		DOM.setStyleAttribute(myradiobuttonlabel, "fontSize", 10+"px");
		DOM.setStyleAttribute(myradiobuttonlabel, "color", "black");
		DOM.setStyleAttribute(myradiobuttonlabel, "width","auto");
		DOM.setStyleAttribute(myradiobuttonlabel, "align", "left");
		if(isModificationAllowed() && !this.readOnly)
		{
			this.createListener(myradiobutton);
		}
		else
		{
			((InputElement) myradiobutton.cast()).setDisabled(true);
		}
	
		activechildindex.add(DOM.getChildIndex(this.row,radiobuttonlabel));
		activeitems.add(classid);
		}

		//Setting Styles
		DOM.setStyleAttribute(elementContent, "borderCollapse", "collapse");
		setElementSize(new Size(getActualViewModeWidth(),
				getActualViewModeHeight()));
				

	}

	
	
	
	protected String getVarValue(ParameterTypes name) {
		if(name == ParameterTypes.MyCheck){
			return this.Check;
			
		}
		return null;
	}
	// Getting index of the checked radio button
	int getCellIndex(int CheckValue){
		int result=0;
		if(CheckValue==0)
			result=1;
		else if(CheckValue==1)
			result=3;
		else if(CheckValue==2)
			result=5;
		else if(CheckValue==3)
			result=7;		
		return result;
	}
	

	protected void setVarValue(ParameterTypes name, String value, String username) {
		if(name == ParameterTypes.MyCheck){
			this.checkForHighlight(username);
			this.Check=value;
		        if(this.row != null)  {
		        		Element selectedradiobutton =(Element) DOM.getChild(this.row, getCellIndex(Integer.parseInt(this.Check))).getFirstChild();		        	  		
		        		((InputElement) selectedradiobutton.cast()).setChecked(true);
		        	
		        	
		        }
		}
	}
	
	
	
	
	
	// Checking whether there is radiobutton element or not
	Boolean HasItem(String item,ArrayList <String> items){
		Boolean result=false;
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).equalsIgnoreCase(item)){
				result=true;
				break;	
			}}		
		return result;
	}
	
	public void createListener(Element radioButtondiv) {
		this.myEventListener = new EventListener() {
			public void onBrowserEvent(Event be) {
				if(be.getTypeInt()==Events.OnClick.getEventCode()){
					if(AbstractExtendedRadioButtonElement.this.isActiveViewMode()){
						if(HasItem(((Element) be.getEventTarget().cast()).getClassName(),activeitems)){
							
							AbstractExtendedRadioButtonElement.this.Check = ((Element) be.getEventTarget().cast()).getId().toString();
								System.out.println("CheckID:"+Check);
								if(!mode){
									AbstractExtendedRadioButtonElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedRadioButtonElement.this);
									AbstractExtendedRadioButtonElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedRadioButtonElement.this);
								}
						}
				
					}
				} 
				
			
				be.stopPropagation();
			}
		};

		DOM.sinkEvents(radioButtondiv, Events.OnClick.getEventCode());
		DOM.setEventListener(radioButtondiv, myEventListener);
		
	}


	protected void setElementSize(Size size) {
		
		int balanceWidth=4;
		//int balanceHeight=4;
		
		
		if(this.row != null)  {
			int itemcount=activechildindex.size();
			// Calculating appropriate width for the element
			int widthperelement= (size.width)/(itemcount+1);
			for(int i=0;i<itemcount;i++){
			Element item =(Element) DOM.getChild(this.row,activechildindex.get(i));	
			//Setting width of the element
			DOM.setStyleAttribute(item,"width",Math.max(0,widthperelement-balanceWidth)+"px");
    	
			}
			if(this.labelE != null){
				DOM.setStyleAttribute(this.labelE,"width",Math.max(0,widthperelement+balanceWidth)+"px");
			}
    		
		}

	}

	protected void switchToEditMode(Element contentFrame) {

	}

	protected void switchToViewMode(Element contentFrame) {
		
		// Building the element
		buildElement();
		

		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
	

	}

	@Override
	protected void setElementHighlight(boolean highlight) {}

	protected void onEstablishModelConnection() { }

	@Override
	protected void onRemoveModelConnection() { }
}