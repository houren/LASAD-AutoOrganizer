package lasad.gwt.client.ui.workspace.tabs;

import lasad.gwt.client.settings.DebugSettings;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.ui.HTML;

/**
 *	Shockingly, this class represents the About Tab for LASAD, i.e. the tab providing general info (version, description, etc)
 *	Should be used as a singleton
 *	@author Kevin Loughlin
 *	@since 28 August 2015
 */
public class AboutTab extends TabItem
{
	private static AboutTab instance = null;

	// Private, only to be called by getInstance
	private AboutTab()
	{
		this.setText("About");
		this.setClosable(true);
		this.addStyleName("pad-text");
		this.setStyleAttribute("backgroundColor", "#FBFBFB");

		// TODO This text should be loaded from the server.
		HTML introduction = new HTML();
		introduction
				.setHTML("<br><div align=\"center\"><img src=\"resources/images/lasad.png\" border=\"0\"><font color=#000000><br><br><h2>Version " + DebugSettings.version + "<br>Developed at Carnegie Mellon University and Humboldt-Universität zu Berlin</h2><br><p>LASAD is a web-based argumentation system that allows users to graphically represent their arguments via diagrams, referred to as argument maps.<br>These argument maps can be constructed individually or collaboratively, with collaboration supported by the software's simulatenous editing and chat system features.<br>LASAD is compatible with any diagram-friendly argumentation language, and each argumentation language is represented as an argument map ontology in LASAD's database.<br>Ontologies can be created by an administrative account, using LASAD's authoring tool, and can then be accessed by users with appropriate permissions.</p></div></font>");
		this.add(introduction);
	}

	// Allows for singleton nature
	public static AboutTab getInstance()
	{
		if (instance == null)
		{
			instance = new AboutTab();
		}

		return instance;
	}


}