package lasad.gwt.client.ui.workspace.tabs.map;

import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Label;

public class CreateSessionFromTemplateContentPanel extends ContentPanel {

	private lasad_clientConstants myConstants = GWT
			.create(lasad_clientConstants.class);

	private ContentPanel details;
	private FormPanel actions;
	private TextField<String> sessionName;

	private Template template = null;
	private String htmlTemplate = "<div class=\"loginTab_MapInfoPanel\">"
			+ "<table class=\"loginTab_MapInfoPanel\">" + "<tr>"
			+ "<td class=\"loginTab_MapInfoPanel_header\">"
			+ myConstants.MapInformationPanelOntology() + "</td>"
			+ "<td>{ontology} / {template}</td>" + "</tr>" + "<tr>"
			+ "<td class=\"loginTab_MapInfoPanel_header\">Template:</td>"
			+ "<td>{templatetitle}</td>" + "</tr>" + "<tr>"
			+ "<td class=\"loginTab_MapInfoPanel_header\">Max. users: </td>"
			+ "<td>{templatemaxuser}</td>" + "</tr>" + "</table>" + "</div>";

	private static CreateSessionFromTemplateContentPanel instance = null;

	public static CreateSessionFromTemplateContentPanel getInstance() {
		if (instance == null) {
			instance = new CreateSessionFromTemplateContentPanel();
		}
		return instance;
	}

	private CreateSessionFromTemplateContentPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		setHeaderVisible(false);
		setBodyBorder(false);

		details = new ContentPanel();
		details.setHeading("Template details");
		details.setLayout(new RowLayout(Orientation.VERTICAL));

		template = new Template(htmlTemplate);
		template.compile();

		actions = new FormPanel();
		actions.setHeading("Possible actions");

		add(details, new RowData(1.0, 0.65, new Margins(0)));
		add(actions, new RowData(1.0, 0.35, new Margins(0)));
	}

	public void openTemplateDetails(final Action a) {
		updateTemplateDetails(a);
		updateTemplateActions(a.getParameterValue(ParameterTypes.Ontology),
				a.getParameterValue(ParameterTypes.Template));

//		layout();
	}

	private void updateTemplateActions(final String templateName,
			final String ontologyName) {
		actions.removeAll();
		
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(170);
		actions.setLayout(layout);
		
		sessionName = new TextField<String>();
		sessionName.setFieldLabel("Name");
		sessionName.setEmptyText("Enter a map name");
		sessionName.setAllowBlank(false);
		sessionName.setMinLength(3);
		actions.add(sessionName);
		
		//TODO Zhenyu
		actions.setAction(GWT.getModuleBaseURL() + "ImageUpload");
		// set the form to use the POST method, and multipart MIME
		// encoding for file upload
		actions.setEncoding(FormPanel.Encoding.MULTIPART);
		actions.setMethod(FormPanel.Method.POST);
		
		Label upname = new Label("BackgroundImage (optional):");
		actions.add(upname);
		// Create a FileUpload widget.
		final FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		upload.setStyleName("x-btn");
		actions.add(upload);
		
		Button createSession = new Button("Create new map") {
			@Override
			protected void onClick(ComponentEvent ce) {
				if (sessionName.validate()) {
					String backgroundImage = "";
					if(upload.getFilename().length()>0)
					{
						actions.submit();
						backgroundImage=GWT.getHostPageBaseURL()+"uploads/"+upload.getFilename().substring(upload.getFilename().lastIndexOf('\\')+1);
					}
					ActionPackage p = ActionFactory.getInstance()
							.createAndJoinMap(sessionName.getValue(),
									templateName, ontologyName,backgroundImage);
					LASADActionSender.getInstance().sendActionPackage(p);
				} else {
					LASADInfo.display("Error",
							"Please specify a unique map name");
				}
			}
		};

//		actions.add(sessionName);
		actions.add(createSession);
		
//		//In case LARGO or ARGUNAUT is selected an import button is shown
//		if (templateName.equalsIgnoreCase("LARGO") || templateName.equalsIgnoreCase("ARGUNAUT")) {
//			String buttonLabel;
//			if (templateName.equalsIgnoreCase("LARGO")) {
//				buttonLabel = "Largo";
//			}
//			else {
//				buttonLabel = "Argunaut";
//			}
//			Button importSession = new Button("Import " + buttonLabel) {
//				protected void onClick(ComponentEvent ce) {
//					if (sessionName.validate()) {
//
//						// The dialog window
//						final Window window = new Window();
//
//						// Create a FormPanel and point it at a service.
//						final com.google.gwt.user.client.ui.FormPanel form = new com.google.gwt.user.client.ui.FormPanel();
//						form.setAction(GWT.getModuleBaseURL() + "fileupload");
//
//						// set the form to use the POST method, and multipart
//						// MIME
//						// encoding for file upload
//						form.setEncoding(com.google.gwt.user.client.ui.FormPanel.ENCODING_MULTIPART);
//						form.setMethod(com.google.gwt.user.client.ui.FormPanel.METHOD_POST);
//
//						// Create a panel to hold all of the form widgets.
//						VerticalPanel panel = new VerticalPanel();
//						form.setWidget(panel);
//
//						// Create a FileUpload widget.
//						final FileUpload upload = new FileUpload();
//						upload.setName("uploadFormElement");
//						upload.setStyleName("x-btn");
//						panel.add(upload);
//
//						// Add a 'submit' button.
//						com.google.gwt.user.client.ui.Button submit = new com.google.gwt.user.client.ui.Button(
//								"Submit", new ClickHandler() {
//
//									@Override
//									public void onClick(ClickEvent event) {
//										form.submit();
//									}
//								});
//						submit.setStylePrimaryName("x-btn");
//						panel.add(submit);
//
//						form.addSubmitHandler(new com.google.gwt.user.client.ui.FormPanel.SubmitHandler() {
//
//							public void onSubmit(SubmitEvent event) {
//
//							}
//						});
//
//						form.addSubmitCompleteHandler(new com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler() {
//
//							@Override
//							public void onSubmitComplete(
//									SubmitCompleteEvent event) {
//								window.hide();
//								String result = event.getResults();
//								result = result.replaceAll(
//										"THISISABRACKETSUBSTITUTE1", "<");
//								result = result.replaceAll(
//										"THISISABRACKETSUBSTITUTE2", ">");
//
//								// the following if-block is needed because of
//								// some
//								// compiled-mode problems
//								if (result.contains("<pre>")) {
//									result = result.replaceAll("<pre>", "");
//									result = result.replaceAll("</pre>", "");
//								}
//
//								// the following if-block is needed because of
//								// some
//								// parser problems
//								if (result.contains("nbsp")) {
//									result = result.replaceAll("&nbsp;", " ");
//								}
//
//								ActionPackage p = ActionFactory.getInstance()
//										.importAndJoinMap(
//												sessionName.getValue(),
//												templateName, ontologyName,
//												result);
//								LASADActionSender.getInstance()
//										.sendActionPackage(p);
//							}
//						});
//						window.setSize(240, 70);
//						window.setHeading("Choose XML-File");
//						window.add(form);
//						window.show();
//					} else {
//						LASADInfo.display("Error",
//								"Please specify a unique map name");
//					}
//				}
//			};
//			actions.add(importSession);
//		}

		actions.layout();
	}

	private void updateTemplateDetails(final Action a) {
		final Params params = new Params();
		params.set("ontology", a.getParameterValue(ParameterTypes.Ontology));
		params.set("template", a.getParameterValue(ParameterTypes.Template));
		params.set("templatetitle", a.getParameterValue(ParameterTypes.TemplateTitle));
		params.set("templatemaxuser", a.getParameterValue(ParameterTypes.TemplateMaxUsers));

		details.removeAll();

		details.add(new BoxComponent() {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				setElement(template.create(params), target, index);
			}
		}, new RowData(1, 1, new Margins(5)));
	}
}
