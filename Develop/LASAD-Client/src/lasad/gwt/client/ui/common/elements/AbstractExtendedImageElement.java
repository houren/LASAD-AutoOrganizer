package lasad.gwt.client.ui.common.elements;

import lasad.gwt.client.model.ElementInfo;
import lasad.gwt.client.ui.common.AbstractExtendedElement;
import lasad.gwt.client.ui.common.ExtendedElementContainerInterface;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
//import com.google.gwt.event.dom.client.MouseOutEvent;
//import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
//import com.google.gwt.user.client.ui.DisclosureEvent;
//import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class AbstractExtendedImageElement extends AbstractExtendedElement {

	private int image_height = 10;

	private int window_width = 10;
	private int window_height = 10;
	private String i_adjusted_heigth = "0";
	private String i_adjusted_width = "0";
	private String small_image_heigth = "0";
	private String small_image_width = "0";



	private boolean dragable = false;
	private int sizedHight = 0;
	private int sizedWidth = 0;
	private String imagetext = "";
	private String imagesource = "";
	private String background_color = "";
	private String font_color = "";
	private boolean isopened = false;
	private boolean textonimage = true;

	private EventListener myEventListener = null;
	private EventListener myOnloadEventListener = null;
	//private EventListener myFocusLostEventListener = null;

	Element spaceTD = DOM.createTD();
	private Element elementContent = null;
	Element tbody = DOM.createTBody();
	Element imagerow = DOM.createTR();
	Element labelrow = DOM.createTR();
	Element labelTD = DOM.createTD();
	Element labelDiv = DOM.createDiv();

	Window imagewindow = null;
	Image widget = null;
	VerticalPanel vpanel = null;
	DisclosurePanel dp = null;

	Element imageTD = DOM.createTD();
	Element imageDiv = DOM.createDiv();
	Element image = DOM.createImg();

	Element myradiobutton;

	Element container = DOM.createDiv();

	Element space = DOM.createDiv();
	
	public AbstractExtendedImageElement(boolean readonly, ExtendedElementContainerInterface container, ElementInfo config) {
		super(container, config,readonly);
		// Adding variable to store user selection
				
		getparameters(config);
		this.elementVars.add(ParameterTypes.ImageURL);
		this.elementVars.add(ParameterTypes.ImageAdjustedWidth);
		this.elementVars.add(ParameterTypes.ImageAdjustedHeight);
		this.elementVars.add(ParameterTypes.SmallImageHeight);
		this.elementVars.add(ParameterTypes.SmallImageWidth);

		// this.elementVars.add("small_image_adjusted_width");

	}

	void getparameters(ElementInfo myconfig) {
		// Getting label string

		if (myconfig.getElementOption(ParameterTypes.Text) != null) {
			imagetext = myconfig.getElementOption(ParameterTypes.Text);

		} else {
			imagetext = "Image";
		}

		if (myconfig.getElementOption(ParameterTypes.TextOnImage) != null || myconfig.getElementOption(ParameterTypes.Text) != "") {
			textonimage = Boolean.parseBoolean(myconfig.getElementOption(ParameterTypes.TextOnImage));

		} else {
			textonimage = true;
		}

		if (myconfig.getElementOption(ParameterTypes.Source) != null || myconfig.getElementOption(ParameterTypes.Source) != "") {

			imagesource = myconfig.getElementOption(ParameterTypes.Source);
		} else {
			imagesource = "http://www.concertforcancer.org/photo_not_available_poster-p228239333753012519tdcp_400.jpg";
		}

		if (myconfig.getElementOption(ParameterTypes.Dragable) != null || myconfig.getElementOption(ParameterTypes.Dragable) != "") {
			dragable = Boolean.parseBoolean(myconfig.getElementOption(ParameterTypes.Dragable));
		} else {
			dragable = true;
		}

		// Getting background color
		if (myconfig.getUiOption(ParameterTypes.BackgroundColor) != null || myconfig.getUiOption(ParameterTypes.BackgroundColor) != "") {
			background_color = myconfig.getUiOption(ParameterTypes.BackgroundColor);
		} else {
			background_color = "#FFFFFF";
		}

		if (myconfig.getUiOption(ParameterTypes.FontColor) != null || myconfig.getUiOption(ParameterTypes.FontColor) != "") {
			font_color = myconfig.getUiOption(ParameterTypes.FontColor);
		} else {
			font_color = "#FFFFFF";
		}

		if (myconfig.getUiOption(ParameterTypes.MinHeight) != null || myconfig.getUiOption(ParameterTypes.MinHeight) != "") {
			image_height = Integer.parseInt(myconfig.getUiOption(ParameterTypes.MinHeight)) - 15;
		} else {

			image_height = 25;
		}

		if (myconfig.getUiOption(ParameterTypes.WindowHeight) != null || myconfig.getUiOption(ParameterTypes.WindowHeight) != "") {
			window_height = Integer.parseInt(myconfig.getUiOption(ParameterTypes.WindowHeight));
			i_adjusted_heigth = myconfig.getUiOption(ParameterTypes.WindowHeight);
		} else {

			window_height = 50;
			i_adjusted_heigth = "50";
		}

		if (myconfig.getUiOption(ParameterTypes.WindowWidth) != null || myconfig.getUiOption(ParameterTypes.WindowWidth) != "") {
			window_width = Integer.parseInt(myconfig.getUiOption(ParameterTypes.WindowWidth));
			i_adjusted_width = myconfig.getUiOption(ParameterTypes.WindowWidth);
		} else {

			window_width = 50;
			i_adjusted_width = "50";
		}

	}

	Size adjustsize(Size currentsize, double win_width, double win_height) {

		double newwidth = 0;
		double newheight = 0;

		if (currentsize.height >= currentsize.width) {

			newwidth = (double) currentsize.height * (win_width / win_height);
			newheight = (double) currentsize.height;

			if (newwidth > currentsize.width) {
				newheight = (double) currentsize.width * (win_height / win_width);
				newwidth = (double) currentsize.width;

			}
		} else if (currentsize.height < currentsize.width) {

			newheight = (double) currentsize.width * (win_height / win_width);
			newwidth = currentsize.width;

			if (newheight > currentsize.height) {

				newwidth = currentsize.height * (win_width / win_height);
				newheight = currentsize.height;
			}

		}

		Size itemsize = new Size((int) newwidth, (int) newheight);
		return itemsize;
	}

	protected void buildElement() {
		if (elementContent != null) {
			// Already built
			return;
		}

		// imagesource="http://www.unionview.com/media/loading.gif";

		elementContent = DOM.createTable();
		DOM.appendChild(elementContent, tbody);
		DOM.appendChild(elementContent, imageDiv);
		if (textonimage) {
			DOM.appendChild(tbody, labelrow);
			DOM.appendChild(tbody, imagerow);
		} else {
			DOM.appendChild(tbody, imagerow);
			DOM.appendChild(tbody, labelrow);

		}

		DOM.appendChild(imagerow, imageTD);
		DOM.appendChild(labelrow, labelTD);
		DOM.appendChild(imageTD, imageDiv);
		DOM.appendChild(labelTD, labelDiv);

		// Label aa=new Label();
		// DOM.appendChild(labelTD,(Element)aa);
		imagetext = "";
		DOM.appendChild(imageDiv, image);

		if (!imagetext.equals("")) {
			labelDiv.setInnerHTML(imagetext);
			DOM.setStyleAttribute(labelrow, "backgroundColor", background_color);
		}

		// Image
		image.setClassName("my_image");
		image.setPropertyString("src", imagesource);

		// DOM.setStyleAttribute(imageDiv, "width", image_width+"px");
		// DOM.setStyleAttribute(imageTD, "width", image_width+"px");
		DOM.setStyleAttribute(imageDiv, "align", "center");

		DOM.setStyleAttribute(image, "height", image_height + "px");
		DOM.setStyleAttribute(imageDiv, "height", image_height + "px");
		DOM.setStyleAttribute(imageTD, "height", image_height + "px");
		//
		// DOM.setStyleAttribute(labelrow,"height",15+"px");

		// Label

		DOM.setStyleAttribute(labelDiv, "fontSize", 12 + "px");
		DOM.setStyleAttribute(labelDiv, "fontWeight", "bold");
		DOM.setStyleAttribute(labelDiv, "color", font_color);
		DOM.setElementProperty(imageTD, "align", "center");
		DOM.setElementProperty(labelDiv, "align", "center");

		// DOM.setStyleAttribute(labelDiv, "width", "30"+"px");

		if(isModificationAllowed() && !this.readOnly)
		{
			this.createOnloadListener(image);
		}
		
		// this.createResizeEvent(imageDiv);
		// Setting Styles

		// labelrow=null;
		DOM.setStyleAttribute(elementContent, "borderCollapse", "collapse");
		setElementSize(new Size(getActualViewModeWidth(), getActualViewModeHeight()));

	}

	protected String getVarValue(ParameterTypes name) {
		int i=0;
		while(i<2000)
			i++;
		if (name == ParameterTypes.ImageURL) {
			if (image != null)
				image.setPropertyString("src", this.imagesource);
			return this.imagesource;

		}

		else if (name == ParameterTypes.ImageAdjustedHeight) {

			return this.i_adjusted_heigth;

		}

		else if (name == ParameterTypes.ImageAdjustedWidth) {

			return this.i_adjusted_width;

		} 
		else if (name == ParameterTypes.SmallImageHeight) {
			if (!this.small_image_heigth.equalsIgnoreCase("") && !this.small_image_heigth.equalsIgnoreCase("0")){ 
			System.out.println("GVVsmall_image_heigth:"+this.small_image_heigth);

			return this.small_image_heigth;
			}
		}
		else if (name == ParameterTypes.SmallImageWidth) {

			if (!this.small_image_width.equalsIgnoreCase("") && !this.small_image_width.equalsIgnoreCase("0")){ 
			System.out.println("GVVsmall_image_width:"+this.small_image_width);
			return this.small_image_width;
			}
		}

		return null;
	}

	// Getting index of the check radio button

	protected void setVarValue(ParameterTypes name, String value, String username) {
		int i=0;
		while(i<2000)
			i++;
		if (name == ParameterTypes.ImageURL) {
			this.checkForHighlight(username);
			this.imagesource = value;
			if (image != null)
				image.setPropertyString("src", value);

		} else if (name == ParameterTypes.ImageAdjustedHeight) {

			this.checkForHighlight(username);
			this.i_adjusted_heigth = value;
		}

		else if (name == ParameterTypes.ImageAdjustedWidth) {
			this.checkForHighlight(username);
			this.i_adjusted_width = value;
		} else if (name == ParameterTypes.SmallImageHeight) {
			this.checkForHighlight(username);
			this.small_image_heigth = value;
			if (!this.small_image_heigth.equalsIgnoreCase("") && !this.small_image_heigth.equalsIgnoreCase("0")) {
				if (image != null && imageDiv != null && imageTD != null) {

					DOM.setStyleAttribute(image, "height", value + "px");
					DOM.setStyleAttribute(imageDiv, "height", value + "px");
					DOM.setStyleAttribute(imageTD, "height", value + "px");
					DOM.setStyleAttribute(imageDiv, "verticalAlign", "top");
					DOM.setStyleAttribute(imageTD, "verticalAlign", "top");
					DOM.setStyleAttribute(image, "verticalAlign", "top");
					
					System.out.println("small_image_heigth:"+value);
				}

			}

		}

		else if (name == ParameterTypes.SmallImageWidth) {
			this.checkForHighlight(username);
			this.small_image_width = value;

			if (!this.small_image_width.equalsIgnoreCase("") && !this.small_image_width.equalsIgnoreCase("0")) {
				if (image != null && imageDiv != null && imageTD != null) {

					DOM.setStyleAttribute(image, "width", value + "px");
					DOM.setStyleAttribute(imageDiv, "width", value + "px");
					DOM.setStyleAttribute(imageTD, "width", value + "px");
					DOM.setStyleAttribute(imageDiv, "align", "center");
					DOM.setStyleAttribute(imageTD, "align", "center");
					DOM.setStyleAttribute(image, "align", "center");
					System.out.println("small_image_width:"+value);
				}

			}
		}

	}

	Size adjustImageSize(Size windowsize, Size newimagesize, int minheight, int minwidth, String Type) {
		double image_height = newimagesize.height;
		double image_width = newimagesize.width;
		double aspectratio = image_height / image_width;

		int cal_heigth = 0;
		int cal_width = 0;

		if (newimagesize.height >= newimagesize.width) {
			cal_heigth = windowsize.height;
			cal_width = (int) (cal_heigth / aspectratio);

			if (cal_width > windowsize.width) {

				cal_width = windowsize.width;
				cal_heigth = (int) (cal_width * aspectratio);

			}
		}

		else if (newimagesize.height < newimagesize.width) {
			cal_width = windowsize.width;
			cal_heigth = (int) (cal_width * aspectratio);

			if (cal_heigth > windowsize.height) {
				cal_heigth = windowsize.height;
				cal_width = (int) (cal_heigth / aspectratio);
			}

		}
		if (Type.equalsIgnoreCase("window")) {
			if (cal_heigth < minheight) {
				cal_heigth = minheight;

			}
			if (cal_width < minwidth) {

				cal_width = minwidth;
			}
		}
		if (cal_width < 0) {

			cal_width = 5;
		}
		if (cal_heigth < 0) {

			cal_heigth = 5;

		}
		Size mysize = new Size(cal_width, cal_heigth);
		return mysize;
	}

	public void createListener(Element ImageDiv) {
		this.myEventListener = new EventListener() {
			public void onBrowserEvent(Event be) {

				if (be.getTypeInt() == Events.OnClick.getEventCode()) {
					if (AbstractExtendedImageElement.this.isActiveViewMode()) {
						if (((Element) be.getEventTarget().cast()).getClassName().equals("my_image")) {

							if (!isopened) {
								widget = new Image();
								widget.setUrl(imagesource);
								widget.setWidth(i_adjusted_width + "px");
								widget.setHeight(i_adjusted_heigth + "px");
								imagewindow = new Window();
								imagewindow.setWidth((Integer.parseInt(i_adjusted_width) + 12) + "px");
								imagewindow.setHeight(Integer.toString(Integer.parseInt(i_adjusted_heigth) + 60) + "px");

								imagewindow.setTitle(imagetext);
								imagewindow.setResizable(false);
								imagewindow.setDraggable(false);
								if (dragable)
								imagewindow.setDraggable(true);
								imagewindow.setOnEsc(true);
								imagewindow.setClosable(false);
								imagewindow.setBorders(true);
								vpanel = new VerticalPanel();

								vpanel.setWidth(i_adjusted_width + "px");
								vpanel.add(widget);
								dp = new DisclosurePanel();
								dp = GenerateSourcePanel();
								vpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
								dp.setWidth(i_adjusted_width + "px");
								vpanel.add(dp);

								imagewindow.add(vpanel);
								ToolButton closeBtn = new ToolButton("x-tool-close");

								closeBtn.addListener(Events.OnClick, new Listener<BaseEvent>() {

									@Override
									public void handleEvent(BaseEvent be) {

										imagewindow.hide();
										isopened = false;

									}
								});

								closeBtn.setTitle("Close");

								imagewindow.getHeader().addTool(closeBtn);
								imagewindow.show();

								isopened = true;
								AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
								AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);
							}
						}

					}
				}

				be.stopPropagation();
			}
		};

		DOM.sinkEvents(ImageDiv, Events.OnClick.getEventCode());
		DOM.setEventListener(ImageDiv, myEventListener);

	}

	//@SuppressWarnings("deprecation")
	public DisclosurePanel GenerateSourcePanel() {

		final TextBox urlsource = new TextBox();
		urlsource.setName("sourceurl");
		Button savebtn = new Button("Save");

		final DisclosurePanel panel = new DisclosurePanel();
		panel.setHeight("15" + "px");

		Label datalbl = new Label("Click to change image");
		datalbl.setStyleAttribute("fontWeight", "bold");
		datalbl.setStyleAttribute("color", "black");

		panel.setHeader(datalbl);
		final CloseHandler<DisclosurePanel> ch = new CloseHandler<DisclosurePanel>() {
			public void onClose(CloseEvent<DisclosurePanel> event)
			{
				Label datalbl = new Label("Click to change image");
				datalbl.setStyleAttribute("fontWeight", "bold");
				datalbl.setStyleAttribute("color", "black");
				datalbl.setHeight("10" + "px");
				panel.setHeader(datalbl);

				if (imagewindow != null)
					if (imagewindow != null || imagewindow.getHeight() > 60)
						imagewindow.setHeight(imagewindow.getHeight() - 50 + "px");
			}
		};

		final OpenHandler<DisclosurePanel> oh = new OpenHandler<DisclosurePanel>() {
			public void onOpen(OpenEvent<DisclosurePanel> event)
			{
				Label datalbl = new Label("Close");
				datalbl.setStyleAttribute("fontWeight", "bold");
				datalbl.setHeight("10" + "px");
				datalbl.setStyleAttribute("color", "black");

				panel.setHeader(datalbl);
				if (imagewindow != null || imagewindow.getHeight() > 50)
					imagewindow.setHeight(imagewindow.getHeight() + 50 + "px");
			}
		};
		/*final DisclosureHandler dh = new DisclosureHandler() {

			public void onClose(DisclosureEvent event) {

				Label datalbl = new Label("Click to change image");
				datalbl.setStyleAttribute("fontWeight", "bold");
				datalbl.setStyleAttribute("color", "black");
				datalbl.setHeight("10" + "px");
				panel.setHeader(datalbl);

				if (imagewindow != null)
					if (imagewindow != null || imagewindow.getHeight() > 60)
						imagewindow.setHeight(imagewindow.getHeight() - 50 + "px");
			}

			public void onOpen(DisclosureEvent event) {
				Label datalbl = new Label("Close");
				datalbl.setStyleAttribute("fontWeight", "bold");
				datalbl.setHeight("10" + "px");
				datalbl.setStyleAttribute("color", "black");

				panel.setHeader(datalbl);
				if (imagewindow != null || imagewindow.getHeight() > 50)
					imagewindow.setHeight(imagewindow.getHeight() + 50 + "px");
			}
		};*/

		panel.addCloseHandler(ch);
		panel.addOpenHandler(oh);
		// deprecated: panel.addEventHandler(dh);

		int totalwidth = Integer.parseInt(i_adjusted_width);
		panel.setWidth(totalwidth + "px");

		panel.setHeight("20" + "px");
		panel.setWidth(i_adjusted_width + "px");
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth(i_adjusted_width + "px");

		if (totalwidth > 110) {
			int h = totalwidth - 100;
			urlsource.setWidth(h + "px");
			datalbl.setWidth(h + "px");
		}

		urlsource.setHeight("20" + "px");
		savebtn.setWidth("50" + "px");
		savebtn.setHeight("25" + "px");
		savebtn.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				String source = urlsource.getValue();
				urlsource.setValue("");
				if (!source.equals("")) {
					if (widget != null && image != null) {
						imagesource = source;
						widget.setUrl(imagesource);
						image.setPropertyString("src", imagesource);

						final Image tempimage = new Image();
						tempimage.setUrl(imagesource);
						AbstractExtendedImageElement.this.imagesource = imagesource;
						tempimage.addLoadHandler(new LoadHandler() {
							@Override
							public void onLoad(LoadEvent event) {

								System.out.println("Result:" + RootPanel.get().remove(tempimage));
								Size windowsize = new Size(window_width, window_height);
								Size newimagesize = new Size(tempimage.getWidth(), tempimage.getHeight());
								Size mynewimagesize = new Size(adjustImageSize(windowsize, newimagesize, 40, 200, "window").width, adjustImageSize(windowsize, newimagesize, 40, 200, "window").height);

								if (widget != null) {
									image.setPropertyString("src", imagesource);
									widget.setWidth((mynewimagesize.width) + "px");
									widget.setHeight(mynewimagesize.height + "px");
									panel.setWidth(i_adjusted_width + "px");

									imagewindow.setWidth(((mynewimagesize.width + 12) + "px"));

									imagewindow.setHeight((mynewimagesize.height + 60) + "px");

									int totalwidth = Integer.parseInt(i_adjusted_width);
									if (totalwidth > 100) {
										int h = totalwidth - 100;
										urlsource.setWidth(h + "px");

									}

									AbstractExtendedImageElement.this.imagesource = imagesource;

									//AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
									//AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

									int w = AbstractExtendedImageElement.this.getContentFrame().getOffsetWidth() - 10;
									if (w < 0)
										w = 0;
									int h = AbstractExtendedImageElement.this.getContentFrame().getOffsetHeight();
									if (h < 0)
										h = 0;

									// int double ar=(double)
									AbstractExtendedImageElement.this.i_adjusted_heigth = Integer.toString(mynewimagesize.height);
									AbstractExtendedImageElement.this.i_adjusted_width = Integer.toString(mynewimagesize.width);

									Size small_window_size = new Size(w, h);
									Size small_image_size = new Size(Integer.parseInt(i_adjusted_width), Integer.parseInt(i_adjusted_heigth));
									Size new_small_image_size = new Size(0, 0);
									new_small_image_size = adjustImageSize(small_window_size, small_image_size, 0, 0, "");

									AbstractExtendedImageElement.this.small_image_heigth = Integer.toString(new_small_image_size.height);
									AbstractExtendedImageElement.this.small_image_width = Integer.toString(new_small_image_size.width);

									DOM.setStyleAttribute(image, "height", new_small_image_size.height + "px");
									DOM.setStyleAttribute(imageDiv, "height", new_small_image_size.height + "px");
									DOM.setStyleAttribute(imageTD, "height", new_small_image_size.height + "px");

									DOM.setStyleAttribute(image, "width", new_small_image_size.width + "px");
									DOM.setStyleAttribute(imageDiv, "width", new_small_image_size.width + "px");
									DOM.setStyleAttribute(imageTD, "width", new_small_image_size.width + "px");

									DOM.setStyleAttribute(image, "verticalAlign", "top");
									DOM.setStyleAttribute(imageDiv, "verticalAlign", "top");
									DOM.setStyleAttribute(imageTD, "verticalAlign", "top");

									AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
									AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

								}

							}
						});
						RootPanel.get().add(tempimage);

						AbstractExtendedImageElement.this.imagesource = imagesource;
						AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
						AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

					}

				}
				panel.setOpen(false);

			}
		});

		hPanel.add(urlsource);
		hPanel.add(savebtn);
		panel.add(hPanel);
		// void onMouseOut(MouseOutEvent event)
		/*MouseOutHandler mouseout = new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				// TODO Auto-generated method stub
				panel.setOpen(false);

			}

		};*/

		return panel;

	}

	
	

	
	public void createOnloadListener(Element ImageDiv) {
		this.myOnloadEventListener = new EventListener() {
			public void onBrowserEvent(Event be) {
				if (be.getTypeInt() == Events.OnLoad.getEventCode()) {

					if (AbstractExtendedImageElement.this.isActiveViewMode()) {

						createListener(image);

						int h = AbstractExtendedImageElement.this.getContentFrame().getOffsetHeight();
						int w = AbstractExtendedImageElement.this.getContentFrame().getOffsetWidth();
						if (h > 0 && w > 0) {
							image.setPropertyString("src", imagesource);
							Size windowsize = new Size(w, h);
							int iw = Integer.parseInt(i_adjusted_width);
							int ih = Integer.parseInt(i_adjusted_heigth);
							Size imagesize = new Size(iw, ih);
							Size new_small_image_size = new Size(0, 0);
							new_small_image_size = adjustImageSize(windowsize, imagesize, 0, 0, "");

							if(imageDiv != null && imageTD != null && image != null) {
								
								
								System.out.println("sas");
							DOM.setStyleAttribute(imagerow, "height", new_small_image_size.height + "px");
							DOM.setStyleAttribute(imageTD, "height", new_small_image_size.height + "px");
							DOM.setStyleAttribute(imageDiv, "height", new_small_image_size.height + "px");
							DOM.setStyleAttribute(image, "height", new_small_image_size.height + "px");

							DOM.setStyleAttribute(imageDiv, "align", "center");
							DOM.setStyleAttribute(imageTD, "align", "center");
							DOM.setStyleAttribute(image, "align", "center");

							DOM.setStyleAttribute(imagerow, "width", new_small_image_size.width + "px");
							DOM.setStyleAttribute(imageTD, "width", new_small_image_size.width + "px");
							DOM.setStyleAttribute(imageDiv, "width", new_small_image_size.width + "px");
							DOM.setStyleAttribute(image, "width", new_small_image_size.width + "px");
							
							//AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
							//AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

							}

						}

						//*AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
						//*AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

					}
				}

				be.stopPropagation();
			}
		};

		DOM.sinkEvents(ImageDiv, Events.OnLoad.getEventCode());
		DOM.setEventListener(ImageDiv, myOnloadEventListener);

	}

	protected void setElementSize(Size size) {

		if (imagerow != null) {

			Size mysize = new Size(0, 0);

			/*
			 * if(!small_image_default_width.equals("0") &&
			 * !small_image_default_heigth.equals("0")){
			 * small_image_default_width=Integer.toString(size.width);
			 * small_image_default_heigth=Integer.toString(size.height);
			 * 
			 * //AbstractExtendedImageElement.this.small_image_default_heigth=
			 * small_image_default_heigth; //
			 * AbstractExtendedImageElement.this.small_image_default_width
			 * =small_image_default_width; }
			 */
			if (size.width > 0)
				sizedWidth = size.width;
			if (size.height > 0)
				sizedHight = size.height;

			mysize = adjustsize(size, Integer.parseInt(i_adjusted_width), Integer.parseInt(i_adjusted_heigth));

			mysize.height = mysize.height + 15;

			int elementwidth = mysize.width - 10;
			if (elementwidth <= 2) {

				elementwidth = mysize.width;

			}
			if(imageDiv != null && imageTD != null && image != null) {
			DOM.setStyleAttribute(imagerow, "width", elementwidth + "px");
			DOM.setStyleAttribute(imageTD, "width", elementwidth + "px");
			DOM.setStyleAttribute(imageDiv, "width", elementwidth + "px");
			DOM.setStyleAttribute(image, "width", elementwidth + "px");
			AbstractExtendedImageElement.this.small_image_width = Integer.toString(elementwidth);
			
			//AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
			//AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

			
			
			}
			int elementheight = mysize.height - 15;
			if (elementheight <= 2) {

				elementheight = mysize.height;

			}
			if (sizedHight > 0)
				sizedHight = elementheight;
			if (sizedWidth > 0)
				sizedWidth = elementwidth;
			if(imageDiv != null && imageTD != null && image != null) {
			DOM.setStyleAttribute(imagerow, "height", elementheight + "px");
			DOM.setStyleAttribute(imageTD, "height", elementheight + "px");
			DOM.setStyleAttribute(imageDiv, "height", elementheight + "px");
			DOM.setStyleAttribute(image, "height", elementheight + "px");
			AbstractExtendedImageElement.this.small_image_heigth=Integer.toString(elementheight);

			DOM.setStyleAttribute(imageDiv, "align", "center");
			DOM.setStyleAttribute(imageTD, "align", "center");
			DOM.setStyleAttribute(image, "align", "center");
			
			//AbstractExtendedImageElement.this.getContainer().getFocusHandler().setFocus(AbstractExtendedImageElement.this);
			//AbstractExtendedImageElement.this.getContainer().getFocusHandler().releaseFocus(AbstractExtendedImageElement.this);

			
			}

		}
		if (labelrow != null) {

			DOM.setStyleAttribute(labelrow, "width", size.width + "px");
			DOM.setStyleAttribute(labelTD, "width", size.width + "px");
			DOM.setStyleAttribute(labelDiv, "width", size.width + "px");

		}
		if(imageDiv != null && imageTD != null && image != null) {
		DOM.setStyleAttribute(imageDiv, "align", "center");
		DOM.setStyleAttribute(imageTD, "align", "center");
		DOM.setStyleAttribute(image, "align", "center");
		}
		
		
		//AbstractExtendedImageElement.this.small_image_heigth = Integer.toString(new_small_image_size.height);
		//AbstractExtendedImageElement.this.small_image_width = Integer.toString(new_small_image_size.width);

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
	protected void setElementHighlight(boolean highlight) {
	}

	protected void onEstablishModelConnection() {
	}

	@Override
	protected void onRemoveModelConnection() {
	}
}