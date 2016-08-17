package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AbstractExtendedUFrameElement extends AbstractExtendedElement {
	
	public AbstractExtendedUFrameElement(ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config);

		// Set possible Element Vars
		// Only this Elements would be updates to the model
	
		getConfigs(config);
		this.elementVars.add(ParameterTypes.Link);
	}
	
	
	
	public AbstractExtendedUFrameElement(ExtendedElementContainerInterface container, ElementInfo config,boolean readonly) {
		super(container, config);

		
		// Set possible Element Vars
		// Only this Elements would be updates to the model
	
		getConfigs(config);
		this.readOnly=readonly;
		readOnly=readonly;
		this.elementVars.add(ParameterTypes.Link);
	}
	
	
	public AbstractExtendedUFrameElement(ExtendedElementContainerInterface container, ElementInfo config, boolean readOnly, boolean dummy) {
		super(container, config, readOnly);
		this.elementVars.add(ParameterTypes.Link);
		getConfigs(config);
		dummy=false;
		if(dummy) {
			buildElement();
			this.setVarValue(ParameterTypes.Link, "http://google.com", LASAD_Client.getInstance().getUsername());
		}
	}

	Element elementContent = null;

	Element logoDiv = null;
	Element editButtonDiv = null;

	Element frameElement = null;
	Element textElement = null;
	
	Element textFrameDiv = null;
	Element _contentFrame=null;
	String defaulttxt="Please enter URL:";
	private boolean editable=false;
	//whether URL is in DB or not
	private boolean urlSetted=false;
	//whether image is loaded or not
	private boolean firstTime=false;
	private boolean isImage=false;
	Element image=null;
	private Image imagewidget=null;
	private Window imagewindow=null;
	private boolean isOpened=false;
	private Size activeSize=null;
	private Size activeimageSize=null;
	private int window_height=400;
	private int window_width=400;
	private String defaultURL="";
	private boolean hasdefaultURL=false;
	private boolean noneditableinitialized=false;
	private boolean readOnly=false;
	
	
String sourceURL="";
	
	Element tbody = DOM.createTBody();
	Element framerow = DOM.createTR();
	Element editbuttonrow = DOM.createTR();
	Element componentTable=DOM.createTable();
	protected void buildElement() {
		if (elementContent != null) {
			// Already builded		
			if(textElement!=null && frameElement!=null ){
				
				String url=DOM.getElementProperty(textElement, "value");
				if (!url.equals("") && !url.equals(defaulttxt)) {
					if(!url.startsWith("http://") && !url.startsWith("HTTP://") )
						{
						
						url="http://"+url;	
							}
					
					if(isSourceImage(url)){
						
						 frameElement=null;
						 frameElement=DOM.createImg();
						 
						 isImage=true;

					}
					else{
						frameElement=null;
						frameElement=DOM.createIFrame();
						isImage=false;
					}
				
				 frameElement.setPropertyString("src", url);
				 this.sourceURL=url;
					if(isImage && frameElement!=null && activeSize!=null && elementContent!=null){
				
						adjustElementSize(url,activeSize,frameElement,false);
						createImageEvents(frameElement,url);
						
						DOM.setElementProperty(elementContent, "align", "center");
						
					}
					else if(!isImage && frameElement!=null && activeSize!=null ){
						
						if(activeSize.height>19)
						 DOM.setStyleAttribute(frameElement, "height",activeSize.height-19 + "px");
						else
						DOM.setStyleAttribute(frameElement, "height",activeSize.height + "px");	
						DOM.setStyleAttribute(frameElement, "width",activeSize.width + "px");					
					}
				}
							
			}
			return;
			
		}

		EventListener listener= new EventListener() {
			public void onBrowserEvent(Event be) {
				int code = be.getTypeInt();
				if (code == Events.OnClick.getEventCode() && be.getCurrentEventTarget().cast() != frameElement && be.getCurrentEventTarget().cast()!=textFrameDiv) {
					if (AbstractExtendedUFrameElement.this.isActiveViewMode()) {
						// Ask for Focus
						if(editable&&!readOnly){
						AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
						}
						}
				} else if (code == Events.OnFocus.getEventCode()) {
					
					String txtvalue=DOM.getElementProperty(textElement, "value");
					if(txtvalue.equals(defaulttxt)){
						
						DOM.setElementAttribute(textElement, "value", "");
						textElement.setPropertyString("value","");
						
					}

					if (AbstractExtendedUFrameElement.this.isActiveViewMode()) {
						if(editable)
							{							
					AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
							}
						else{
					AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this);
						}}} 
				
				else if (code == Events.OnBlur.getEventCode()) {
					
				 if (AbstractExtendedUFrameElement.this.isActiveViewMode()) {
						// Focus was lost by TABULATOR
					 if(editable)
						{	
						AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this);
						}
					 else{
						 AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
						 
						}}}
				be.stopPropagation();
			}
		};

		elementContent = DOM.createDiv();
		elementContent.setClassName("ExtendenUFrameElement");

		// TEXTFIELD
		textFrameDiv = DOM.createDiv();
		textFrameDiv.setClassName("TextFrameDiv");
		DOM.appendChild(elementContent, textFrameDiv);
		textElement=DOM.createInputText();
		textElement.setClassName("extendedUFrame-TextField-EditMode");
		DOM.setElementAttribute(textElement, "value", defaulttxt);		
		DOM.appendChild(textFrameDiv,textElement);
		DOM.sinkEvents( textElement, Events.Focus.getEventCode());
		DOM.setEventListener(textElement, listener);
		//FrameElement
		 frameElement= DOM.createIFrame();
		 frameElement.setClassName("extendedFrameElement");
		 
		 if(hasdefaultURL){
			 frameElement.setPropertyString("src",defaultURL);	 
		 }
		// Edit Button
		editButtonDiv = DOM.createDiv();
		editButtonDiv.setClassName("AbstractExtendedUFrameElement-EditButton");
		DOM.sinkEvents(this.editButtonDiv, Events.OnClick.getEventCode());
		DOM.setEventListener(this.editButtonDiv, listener);
		DOM.setStyleAttribute(editButtonDiv, "width",  30+ "px");
		if(editable)
		DOM.appendChild(elementContent,editButtonDiv);
		//if(!editable)
		//DOM.setStyleAttribute(editButtonDiv, "visibility", "hidden");
		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));
	}

	
	// Getting configuration from ML
	void getConfigs(ElementInfo config){
		if (config.getUiOption(ParameterTypes.Editable) != null && config.getUiOption(ParameterTypes.Editable)!="" && !config.getUiOption(ParameterTypes.Editable).isEmpty()) {
			editable = Boolean.parseBoolean(config.getUiOption(ParameterTypes.Editable));
		}
		else {
			editable =true;
		}
		
		if (config.getUiOption(ParameterTypes.DefaultURL) != null && config.getUiOption(ParameterTypes.DefaultURL)!="" && !config.getUiOption(ParameterTypes.DefaultURL).isEmpty()) {
			defaultURL= config.getUiOption(ParameterTypes.DefaultURL);
			hasdefaultURL=true;
		}
		else {
			
			hasdefaultURL=false;
		}
		
		if (config.getUiOption(ParameterTypes.WindowHeight) != null && config.getUiOption(ParameterTypes.WindowHeight)!="") {
			window_height= Integer.parseInt(config.getUiOption(ParameterTypes.WindowHeight));
			
		}
		else {
			window_height=400;
		}
		if (config.getUiOption(ParameterTypes.WindowWidth) != null && config.getUiOption(ParameterTypes.WindowWidth)!="") {
			window_width= Integer.parseInt(config.getUiOption(ParameterTypes.WindowWidth));	
		}
		else {
			
			window_width=400;
		}
	}
	
	protected void setElementSize(Size size) {
		int balanceHeight = 4; // 1px distance to the
		activeSize=size;
		if (textFrameDiv != null) {
			DOM.setStyleAttribute(textFrameDiv, "width", size.width + "px");
			DOM.setStyleAttribute(textFrameDiv, "height", Math.max(0, size.height - balanceHeight)-12 + "px");
		}
		if ( frameElement != null) {
			DOM.setStyleAttribute(frameElement, "width", size.width + "px");
			DOM.setStyleAttribute(frameElement, "height", Math.max(0, size.height - balanceHeight)-19 + "px");
		}
	
		if (textElement != null ) {
			
			if(size.width>10)
			DOM.setStyleAttribute(textElement, "width",  size.width-10 + "px");
			else
			DOM.setStyleAttribute(textElement, "width",  size.width + "px");
		}
		
		if(activeSize!=null && sourceURL!=null&& !sourceURL.equalsIgnoreCase("")){
			adjustElementSize(sourceURL,activeSize,frameElement,true);
			
		}
		
		if(editButtonDiv!=null){	
			DOM.setStyleAttribute(editButtonDiv, "width",  size.width+ "px");
		}
	}

	protected void switchToEditMode(Element contentFrame) {
		if(editable)
		{	
			if (!contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);
			}
			if (frameElement!= null && DOM.isOrHasChild(textFrameDiv,frameElement)) {
				DOM.removeChild(textFrameDiv,frameElement);
			}
			if (textElement != null && !DOM.isOrHasChild(textFrameDiv, textElement)) {
				DOM.appendChild(textFrameDiv, textElement);
			}
		}
		else{
		String url="";
		if(textElement!=null)
		url=DOM.getElementProperty(textElement, "value");
		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
			
		}
		if (!url.equals("") && !url.equals(defaulttxt)) {
		
			if(isSourceImage(url) && !noneditableinitialized){
				 frameElement=null;
				 frameElement=DOM.createImg();
				 if(!url.startsWith("http://") && !url.startsWith("HTTP://") )
					{
					url="http://"+url;
						}
				 frameElement.setPropertyString("src", url);		 
					AbstractExtendedUFrameElement.this.sourceURL=url;
					AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
					AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this);
					if(frameElement!=null && activeSize!=null && elementContent!=null){
				
						adjustElementSize(url,activeSize,frameElement,false);
						createImageEvents(frameElement,url);
						
						DOM.setElementProperty(elementContent, "align", "center");
						noneditableinitialized=true;
						
						AbstractExtendedUFrameElement.this.sourceURL=url;
						AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
						AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this);	
					}
					if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {
						DOM.appendChild(textFrameDiv, frameElement);
						}
					 this.setElementFocus(false);
					 
					 AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
					AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this);
					

			}else if(!noneditableinitialized){
			
				noneditableinitialized=true;
				
				 frameElement=null;
				 frameElement=DOM.createIFrame();
				 
				 if(!url.startsWith("http://") && !url.startsWith("HTTP://") )
					{
					
					url="http://"+url;
			
						}
				 frameElement.setPropertyString("src", url);
				 
					AbstractExtendedUFrameElement.this.sourceURL=url;
					
				 if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement) && activeSize!=null) {
					 
					 DOM.setStyleAttribute(frameElement, "width",  activeSize.width-4+ "px");
					 DOM.setStyleAttribute(frameElement, "height",  activeSize.height-4+ "px");
					 DOM.appendChild(textFrameDiv, frameElement);
						}
				 this.setElementFocus(true);
				 this.setElementFocus(false);				 
			}
		
	
		if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {
			DOM.removeChild(textFrameDiv,textElement);
		}
		}
		else{		
		textElement.setPropertyString("value", defaulttxt);				
				MessageBox box = new MessageBox();
			    box.setButtons(MessageBox.OK);
			    box.setIcon(MessageBox.WARNING);
			    box.setTitle("URL");			  
			    box.setMessage("You did not enter any url to display!");
			    box.show();
		}}}
	
	Size adjustImageSize(Size windowsize, Size newimagesize, int minheight, int minwidth, String Type) {
		double image_height = newimagesize.height;
		double image_width = newimagesize.width;
		double aspectratio=0;
		if(image_width>0)
		{aspectratio= image_height / image_width;}
		else
			{aspectratio=1;}

		int cal_heigth = 0;
		int cal_width = 0;
		if (newimagesize.height >= newimagesize.width) {
			cal_heigth = windowsize.height;
			cal_width = (int) (cal_heigth / aspectratio);
			if (cal_width > windowsize.width) {
				cal_width = windowsize.width;
				cal_heigth = (int) (cal_width * aspectratio);}}
		else if (newimagesize.height < newimagesize.width) {
			cal_width = windowsize.width;
			cal_heigth = (int) (cal_width * aspectratio);
			if (cal_heigth > windowsize.height) {
				cal_heigth = windowsize.height;
				cal_width = (int) (cal_heigth / aspectratio);}}
		if (Type.equalsIgnoreCase("window")) {
			if (cal_heigth < minheight) {
				cal_heigth = minheight;}
			if (cal_width < minwidth) {
				cal_width = minwidth;}}
		if (cal_width < 0) {
			cal_width = 5;}
		if (cal_heigth < 0) {
			cal_heigth = 5;}
		Size mysize = new Size(cal_width, cal_heigth);
		return mysize;}
	
	void adjustElementSize(String imagesource,final Size _activeSize,final Element _frameElement,final boolean _allowresize){
		final Image tempimage = new Image();
		tempimage.setUrl(imagesource);
		tempimage.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				Size newimageSize = new Size(tempimage.getWidth(), tempimage.getHeight());
				activeimageSize= newimageSize;
				System.out.println("Result:" + RootPanel.get().remove(tempimage));
			Size adjustedSize=adjustImageSize(_activeSize,newimageSize,0,0,"");
			if(_allowresize){
				if(frameElement!=null){
					if(adjustedSize.height>17){
				DOM.setStyleAttribute(frameElement, "height", adjustedSize.height-17 + "px");
					}
					else{
				DOM.setStyleAttribute(frameElement, "height", adjustedSize.height+ "px");}
				DOM.setStyleAttribute(frameElement, "width", adjustedSize.width + "px");}}
			else{
			if(_frameElement!=null){
				if(adjustedSize.height>17){
			DOM.setStyleAttribute(_frameElement, "height", adjustedSize.height-17 + "px");
				}
				else{
			DOM.setStyleAttribute(_frameElement, "height", adjustedSize.height+ "px");}
			DOM.setStyleAttribute(_frameElement, "width", adjustedSize.width + "px");}}}});
		RootPanel.get().add(tempimage);
	}
	
	boolean isSourceImage(String Url){	
		if(Url.endsWith(".jpg") || Url.endsWith(".jpeg") || Url.endsWith(".gif") || Url.endsWith(".png") || Url.endsWith(".bmp") || Url.endsWith(".svg") )
		{return true;	}
		else{
		return false;}
	}

	protected void switchToViewMode(Element contentFrame) {
		buildElement();
		if(editable)
		{	
			if(!firstTime){	
				if(hasdefaultURL && isSourceImage(defaultURL)){
					// Editable with Defualt Image
				 frameElement=null;
			     frameElement=DOM.createImg();
				 frameElement.setPropertyString("src", defaultURL);
				 this.sourceURL=defaultURL;
				 if(frameElement!=null && activeSize!=null && elementContent!=null){
				adjustElementSize(defaultURL,activeSize,frameElement,false);
				createImageEvents(frameElement,defaultURL);		
				DOM.setElementProperty(elementContent, "align", "center");
					}
				if (!contentFrame.hasChildNodes()) {
					DOM.appendChild(contentFrame, elementContent);
						}
					if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {
						DOM.appendChild(textFrameDiv, frameElement);
					}
					if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {
						DOM.removeChild(textFrameDiv,textElement);
					}
					firstTime=true;
				}
				else{	
			if (!contentFrame.hasChildNodes()) {DOM.appendChild(contentFrame, elementContent);}
			if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {DOM.appendChild(textFrameDiv, frameElement);}
			if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {DOM.removeChild(textFrameDiv,textElement);}
			firstTime=true;}}
				else {
					String url="";
					if(textElement!=null)
					url=DOM.getElementProperty(textElement, "value");
					if (url.equals("") && !url.equals(defaulttxt)) {
					textElement.setPropertyString("value", defaulttxt);
						MessageBox box = new MessageBox();
					    box.setButtons(MessageBox.OK);
					    box.setIcon(MessageBox.WARNING);
					    box.setTitle("URL");			  
					    box.setMessage("You did not enter any url to display!");
					    box.show();
					}
					else{
						if (!contentFrame.hasChildNodes()) {
							DOM.appendChild(contentFrame, elementContent);	
						}
						if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {
							DOM.appendChild(textFrameDiv, frameElement);
						}
						if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {
							DOM.removeChild(textFrameDiv,textElement);
						}
						
					}
				}
		}
		else{
			
	if(!urlSetted && !editable && !hasdefaultURL){
		if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
		if (frameElement!= null && DOM.isOrHasChild(textFrameDiv,frameElement)) {
			DOM.removeChild(textFrameDiv,frameElement);
		}
		if (textElement != null && !DOM.isOrHasChild(textFrameDiv, textElement)) {
			DOM.appendChild(textFrameDiv, textElement);
		}}
	else if(hasdefaultURL){
		if(isSourceImage(defaultURL)){	
		 frameElement=null;
		 frameElement=DOM.createImg();
		 frameElement.setPropertyString("src", defaultURL);
		 this.sourceURL=defaultURL;
		 if(frameElement!=null && activeSize!=null && elementContent!=null){
		 adjustElementSize(defaultURL,activeSize,frameElement,false);
		 createImageEvents(frameElement,defaultURL);
		 DOM.setElementProperty(elementContent, "align", "center");	
			}
			if (!contentFrame.hasChildNodes()) {
				DOM.appendChild(contentFrame, elementContent);	
			}
			if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {
				DOM.appendChild(textFrameDiv, frameElement);
			}
			if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {
				DOM.removeChild(textFrameDiv,textElement);
			}}
		
		else{
			if (!contentFrame.hasChildNodes()) {
			DOM.appendChild(contentFrame, elementContent);
		}
		if (frameElement != null && !DOM.isOrHasChild(textFrameDiv, frameElement)) {
			DOM.appendChild(textFrameDiv, frameElement);
		}
		if (textElement != null && DOM.isOrHasChild(textFrameDiv, textElement)) {
			DOM.removeChild(textFrameDiv,textElement);
		}}}}
	}
	


	
	void showMessage(){
		String txtvalue=DOM.getElementProperty(textElement, "value");
		if(textElement!=null && txtvalue.equalsIgnoreCase("") ){
			MessageBox box = new MessageBox();
		    box.setButtons(MessageBox.OK);
		    box.setIcon(MessageBox.WARNING);
		    box.setTitle("Source URL");
		    box.setMessage("You did not enter any url to display!");
		    box.show();
		    return;
		}
	}
	
	void createImageEvents(Element image,final String _url){
		EventListener textboxlistener = new EventListener() {
			public void onBrowserEvent(Event be) {
				int code = be.getTypeInt();
	 if (code == Events.OnClick.getEventCode()) {
		 if(!isOpened)
			{
		 imagewidget = new Image();
		 imagewidget.setUrl(_url);
		 Size adjustedSize=new Size(0,0);
		 if(activeimageSize!=null){
			 Size tempSize=new Size(window_width,window_height);
			 adjustedSize= adjustImageSize(tempSize,activeimageSize,0,0,"");
		 }
		 imagewidget.setWidth(adjustedSize.width + "px");
		 imagewidget.setHeight(adjustedSize.height + "px");
		 imagewindow = new Window();
		
		if(adjustedSize!=null){
		imagewindow.setWidth(adjustedSize.width + "px");
		imagewindow.setHeight(adjustedSize.height+ "px");
		}
		imagewindow.add(imagewidget);
		imagewindow.setTitle("Image");
		imagewindow.setClosable(false);
		//
		imagewindow.setResizable(false);		
		imagewindow.setDraggable(true);
		DOM.setElementProperty(imagewindow.getElement(), "align", "center");
		ToolButton closeBtn = new ToolButton("x-tool-close");
		closeBtn.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				imagewindow.hide();
				isOpened = false;}});
		closeBtn.setTitle("Close");
		imagewindow.getHeader().addTool(closeBtn);
			imagewindow.show();
			isOpened=true;}}
				be.stopPropagation();
			}
		};
		
		DOM.sinkEvents(image, Events.OnClick.getEventCode());
		DOM.setEventListener(image, textboxlistener);
	}
	protected String getVarValue(ParameterTypes name) {
		if (name == ParameterTypes.Link) {
			if (frameElement != null) {
							
				return this.sourceURL;
			}
		}
		return null;
	}

	protected void setVarValue(ParameterTypes name, String value, String username) {
		this.checkForHighlight(username);

		if (name == ParameterTypes.Link) {
			if (value.startsWith("http://") || value.startsWith("HTTP://")) {
				if (frameElement != null) {
					frameElement.setPropertyString("src", value);
					this.sourceURL = value;
				    urlSetted=true;
				}
				if(textElement!=null){
					
					DOM.setElementAttribute(textElement, "value", value);
					this.sourceURL = value;	
				AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedUFrameElement.this);
			AbstractExtendedUFrameElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedUFrameElement.this); 
			}}} 
		}
	

	@Override
	protected void setElementHighlight(boolean highlight) {
	}

	@Override
	protected void onEstablishModelConnection() {
	}

	@Override
	protected void onRemoveModelConnection() {
	}
}