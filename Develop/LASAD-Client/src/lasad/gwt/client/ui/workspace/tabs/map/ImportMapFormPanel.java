package lasad.gwt.client.ui.workspace.tabs.map;

import java.util.Vector;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.importer.ImportFileChecker;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.argumentmap.ImportMapDialog;
import lasad.gwt.client.ui.workspace.loaddialogues.ImportingMapDialogue;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingMapFromFileDialogue;
import lasad.gwt.client.xml.LoadSessionFromXMLFileParser;
import lasad.shared.communication.objects.ActionPackage;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
//import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class ImportMapFormPanel extends FormPanel {

	private TextField<String> sessionName;
	private FileUploadField file;
	//private Radio privateMap, publicMap;
	
	private CheckBox privateMapChk;
	
	public ImportMapFormPanel(final ImportMapDialog CONTAINER) {
		super();

		setHeading("Import existing map from local file system");
		setLabelWidth(130);	
		
		sessionName = new TextField<String>();
		sessionName.setFieldLabel("Map name");
		sessionName.setAllowBlank(false);
		sessionName.setMinLength(3);
		sessionName.setEmptyText("Enter name of map that will be created from file");
		
		add(sessionName);
		
		file = new FileUploadField();
		file.setAllowBlank(false);
		file.setName("uploadedfile");
		file.setFieldLabel("File");		
		
		add(file);
		
		privateMapChk = new CheckBox();
		privateMapChk.setFieldLabel("Private map");
		add(privateMapChk);
		
		Button loadMap = new Button("Load") {
			@Override
			protected void onClick(ComponentEvent ce) {
				
				if(sessionName.validate()) {
					if(file.validate()) {
						try {
							ImportMapFormPanel.this.submit();
						}
						catch (Exception e) {
							Logger.log("File upload failed", Logger.DEBUG_ERRORS);
						}
					}
					else {
						LASADInfo.display("Error", "Please choose a file!");
					}
				}
				else {
					LASADInfo.display("Error", "Please specify a name for the new map with at least 3 characters!");
				}
			}
		};

		addListener(Events.Submit, new Listener<FormEvent>() {
            public void handleEvent(FormEvent be) {
				String result = be.getResultHtml();
				
				if (!result.startsWith("<H2>HTTP ERROR: 400</H2><PRE>file type invalid</PRE>") && !result.startsWith("<h2>HTTP ERROR: 400</h2><pre>file type invalid</pre>")) {

					result = result.replaceAll("THISISABRACKETSUBSTITUTE1", "<");
					result = result.replaceAll("THISISABRACKETSUBSTITUTE2", ">");

					// the following if-block is needed because of some
					// compiled-mode problems
					if (result.contains("<pre>")) {
						result = result.replaceAll("<pre>", "");
						result = result.replaceAll("</pre>", "");
					}
					

					// the following if-block is needed because of some
					// parser problems
					if (result.contains("nbsp")) {
						result = result.replaceAll("&nbsp;", " ");
					}
					
					
					if (LoadSessionFromXMLFileParser.checkMultipleMaps(result)){
						ImportMapFormPanel.this.processMultiMaps(result);
					} 
					else {
						ImportMapFormPanel.this.processSingleMap(result, sessionName.getValue(), true);
					}

					if (CONTAINER != null)
					{
						CONTAINER.hide();
					}
				}
				else {
					LASADInfo.display("Error", "The file type is invalid.");
				}
            }
        });       
        
		add(loadMap);

		// set the form to use the POST method, and multipart MIME encoding for file upload
		setEncoding(FormPanel.Encoding.MULTIPART);
		setMethod(FormPanel.Method.POST);
		setAction(GWT.getModuleBaseURL() + "fileupload");
		
		layout();
	}
	
	/**
	 * 
	 * @param xmlText
	 * @param sessionName
	 * @param userJoin false, if the user is not supposed to join the loading map. This might
	 * be the case when multiple maps were loaded from a zip file
	 */
	private void processSingleMap(String xmlText, String sessionName, boolean userJoin) {
		String ontology, template, content, chatlog;
		chatlog = LoadSessionFromXMLFileParser.getChatlog(xmlText);

		if (xmlText.startsWith("GML-FILE")) {
			ontology = ImportFileChecker.getArgunautOntologyName();
			template = ImportFileChecker.getArgunautTemplateName();
			ActionPackage p = ActionFactory.getInstance().importAndJoinMap(sessionName, ontology, template, xmlText.substring(8));
			LASADActionSender.getInstance().sendActionPackage(p);
			
			ImportingMapDialogue.getInstance().showLoadingScreen();
		}
		else if (xmlText.startsWith("LARGO-FILE")){
			ontology = ImportFileChecker.getLargoOntologyName();

			String[] parts = xmlText.split(";;");
			template = ImportFileChecker.getLargoTemplateName(parts[1]);

			int startIndex = 14 + parts[1].length();

			ActionPackage p = ActionFactory.getInstance().importAndJoinMap(sessionName, ontology, template, xmlText.substring(startIndex));
			LASADActionSender.getInstance().sendActionPackage(p);
			
			ImportingMapDialogue.getInstance().showLoadingScreen();
		}
		else {
			ontology = LoadSessionFromXMLFileParser.getOntology(xmlText);
			template = LoadSessionFromXMLFileParser.getTemplate(xmlText);
			content = LoadSessionFromXMLFileParser.getContent(xmlText);

			String image = null;
			if(xmlText.contains("<backgroundimg>"))
			{
				int begin = xmlText.indexOf("<backgroundimg>")+15;
		    	int end = xmlText.indexOf("</backgroundimg>");
		    	image = xmlText.substring(begin,end);
			}

			
			String username = null;
			if(ImportMapFormPanel.this.privateMapChk.getValue()) {
				username = LASAD_Client.getInstance().getUsername();
			}
			if (ontology!=null&&template!=null&&content!=null){
				LASADActionSender.getInstance().sendActionPackage(ActionFactory.getInstance().loadSessionFromXML(sessionName, ontology, template, content, username, chatlog, image, userJoin));
				if (userJoin) {
					LoadingMapFromFileDialogue.getInstance().showLoadingScreen();
				}
			} else {
			    LASADInfo.display("Error", "An error occured while parsing the xml-file. File could not be loaded.");
			}
		}
	}
	
	/**
	 * Processes an xml text which contains multiple maps. This is the case
	 * when a zip-file with multiple xml files was imported by the use.
	 * @param xmlText The xml text.
	 * @author MB
	 */
	private void processMultiMaps(String xmlText) {
		Vector<String> maps = LoadSessionFromXMLFileParser.getMultipleMaps(xmlText);
		String sessionName;
		for (int i = 0; i < maps.size(); i++) {
			sessionName = this.sessionName.getValue() + "-" + i;
			this.processSingleMap(maps.get(i), sessionName, false);
		}
	}
	
}
