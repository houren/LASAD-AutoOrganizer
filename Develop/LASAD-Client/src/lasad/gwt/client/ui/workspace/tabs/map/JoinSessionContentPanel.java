package lasad.gwt.client.ui.workspace.tabs.map;

import lasad.gwt.client.LASAD_Client;
import lasad.gwt.client.communication.LASADActionSender;
import lasad.gwt.client.communication.helper.ActionFactory;
import lasad.gwt.client.constants.lasad_clientConstants;
import lasad.gwt.client.model.argument.MapInfo;
import lasad.gwt.client.ui.workspace.LASADInfo;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingMapDialogue;
import lasad.gwt.client.ui.workspace.loaddialogues.LoadingReplayDialogue;
import lasad.gwt.client.xml.OntologyReader;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.ActionPackage;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;



public class JoinSessionContentPanel extends ContentPanel {
	
	private lasad_clientConstants myConstants = GWT.create(lasad_clientConstants.class);
	
	private ContentPanel details;
	private ContentPanel actions;
	
	private Template template = null;
	
	private String htmlTemplate = "<div class=\"loginTab_MapInfoPanel\">" + "<table class=\"loginTab_MapInfoPanel\">" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelMapID() + "</td>" + "<td>{mapid}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelMapName() + "</td>" + "<td>{mapname}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelOriginator() + "</td>" + "<td>{creatorname}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelOntology() + "</td>" + "<td>{ontology} / {template}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">Template:</td>" + "<td>{templatetitle}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelDescription() + "</td>" + "<td>{mapdescription}</td>" + "</tr>" + "<tr>" + "<td class=\"loginTab_MapInfoPanel_header\">" + myConstants.MapInformationPanelActiveUsers() + "</td>" + "<td>{activeusercount} (max. {templatemaxuser})</td>" + "</tr>" + "</table>" + "</div>";
	
	private static JoinSessionContentPanel instance = null;
	
	public void clearMapDetails() {
		details.removeAll();
		actions.removeAll();
	}
	
	public static JoinSessionContentPanel getInstance() {
		if(instance == null) {
			instance = new JoinSessionContentPanel();
		}
		return instance;
	}
	
	private JoinSessionContentPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		setHeaderVisible(false);
		setBodyBorder(false);
		
		details = new ContentPanel();
		details.setHeading("Map details");
		details.setLayout(new RowLayout(Orientation.VERTICAL));
		
		template = new Template(htmlTemplate);
		template.compile();
			
		actions = new ContentPanel();
		actions.setHeading("Possible actions");
		
		add(details, new RowData(1.0, 0.65, new Margins(0)));
		add(actions, new RowData(1.0, 0.35, new Margins(0)));
	}
	
	public void openMapDetails(final Action a) {
		updateMapDetails(a);
		updateMapActions(a);
		
		layout();
	}

	private void updateMapActions(final Action a) {
		actions.removeAll();
		actions.add(new Button("Join", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				// Check if max user count is reached
				boolean allowedToJoin = Integer.parseInt(a.getParameterValue(ParameterTypes.ActiveUsers)) < Integer.parseInt(a.getParameterValue(ParameterTypes.TemplateMaxUsers));
				if (allowedToJoin) {
					LoadingMapDialogue.getInstance().showLoadingScreen();

					ActionPackage p = ActionFactory.getInstance().joinMap(a.getParameterValue(ParameterTypes.MapId));
					LASADActionSender.getInstance().sendActionPackage(p);
					
					clearMapDetails();
				} else {
					LASADInfo.display("Error", "Cannot join the map, because the maximum number of users is already reached!");
				}

			}
		}), new HBoxLayoutData(new Margins(3)));

		// Add Replay-Button
		if (LASAD_Client.getInstance().getRole().equalsIgnoreCase("DEVELOPER")) {

			actions.add(new Button("Start replay", new SelectionListener<ButtonEvent>() {
				public void componentSelected(ButtonEvent ce) {
					LoadingReplayDialogue.getInstance().showLoadingScreen();

					ActionPackage p = ActionFactory.getInstance().startReplay(a.getParameterValue(ParameterTypes.MapId));
					LASADActionSender.getInstance().sendActionPackage(p);
					
					clearMapDetails();
				}
			}), new HBoxLayoutData(new Margins(3)));
		}

		actions.layout();
	}
	


	private void updateMapDetails(final Action a) {
		
		String description = OntologyReader.buildTemplateInfosFromXML(new MapInfo(a.getParameterValue(ParameterTypes.MapId)), a.getParameterValue(ParameterTypes.TemplateXML)).getDescription();
		
		final Params params = new Params();
		params.set("mapid", a.getParameterValue(ParameterTypes.MapId));
		params.set("mapname", a.getParameterValue(ParameterTypes.MapName));
		params.set("creatorname", a.getParameterValue(ParameterTypes.CreatorName));
		params.set("creatorid", a.getParameterValue(ParameterTypes.CreatorId));
		params.set("ontology", a.getParameterValue(ParameterTypes.OntologyName));
		params.set("template", a.getParameterValue(ParameterTypes.TemplateName));
		params.set("templatetitle", a.getParameterValue(ParameterTypes.TemplateTitle));
		params.set("templatemaxuser", a.getParameterValue(ParameterTypes.TemplateMaxUsers));
		params.set("activeusercount", a.getParameterValue(ParameterTypes.ActiveUsers));
		params.set("mapdescription", description);

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
