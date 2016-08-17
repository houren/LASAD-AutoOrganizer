package lasad.gwt.client.ui.workspace.tabs;

import java.util.LinkedHashMap;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.ui.HTML;

/**
 *	Represents the singleton instance of the Tutorial Videos Tab, reachable by the "Help" anchor link on the login screen, or by the argument map
 *	menu bar.  Used javascript function found in html2canvas (writing my own JS file in this code didn't work since it needs to be in the HTML head)
 *	@author Kevin Loughlin
 *	@since 28 August 2015
 */
public class TutorialVideosTab extends TabItem
{
	private final int VID_HEIGHT = 360;
	private final int VID_WIDTH = 640;
	private final String VID_PATH = "resources/videos/";
	private final String DEFAULT = "Logging in and Joining Maps";
	private LinkedHashMap<String, String> titlesFilenames;
	private static TutorialVideosTab instance = null;

	private TutorialVideosTab()
	{
		titlesFilenames = new LinkedHashMap<String, String>();
		this.setText("Tutorial Videos");
		this.setClosable(true);
		this.setScrollMode(Scroll.AUTO);
		this.addStyleName("pad-text");
		this.setStyleAttribute("backgroundColor", "#FBFBFB");
		
		HTML page = new HTML();
		StringBuilder buffer = new StringBuilder("<div align=\"center\"><h1 style=\"font-size:20px; font-family:Georgia, serif\">LASAD Tutorial Videos</h1><style>a.vidlist:link {color:blue; background-color:transparent; text-decoration:underline} a.vidlist:visited {color:blue; background-color:transparent; text-decoration:underline} a.vidlist:hover   {color:purple; background-color:transparent; text-decoration:underline} a.vidlist:active  {color:blue; background-color:transparent; text-decoration:underline}</style>");//+CHANGE_VID_SCRIPT);
		putEntry(DEFAULT, "login.mp4");
		putEntry("Workspace Tour", "workspace.mp4");
		putEntry("Adding Contributions", "contributions.mp4");
		putEntry("Adding Relations", "relations.mp4");
		putEntry("Deleting Contributions & Relations", "deleting.mp4");
		putEntry("Linked Connections", "linked.mp4");
		putEntry("Finding & Centering Contributions", "centering.mp4");
		putEntry("Map Organization", "organization.mp4");
		putEntry("Capturing Screenshots", "screenshots.mp4");
		putEntry("Importing & Exporting Maps", "importing.mp4");
		putEntry("Closing Maps and Logging Out", "logout.mp4");

		for (String title : titlesFilenames.keySet())
		{
			buffer.append("<a class=\"vidlist\" href=\"javascript:changeVid('"+title+"', '"+titlesFilenames.get(title)+"');\" style=\"font-family:Georgia, serif\" style=\"font-size: 0.750em\">"+title+"</a><br>");
		}

		buffer.append(vidString(DEFAULT, titlesFilenames.get(DEFAULT)));

		buffer.append("<font color=#000000></div></font>");
		page.setHTML(buffer.toString());
		this.add(page);
	}

	private String vidString(String title, String filename)
	{
		return "<br><h2 id=\"header\" style=\"font-family:Georgia, serif\">"+title+"</h2><video id=\"playing\" width=\""+VID_WIDTH+"\" height=\""+VID_HEIGHT+"\" preload=\"auto\" style=\"border:2px solid black\" controls><source src=\""+filename+"\" type=\"video/mp4\">Your browser does not support the video tag.</video><br>";
	}

	private void putEntry(String title, String filename)
	{
		titlesFilenames.put(title, VID_PATH + filename);
	}

	public static TutorialVideosTab getInstance()
	{
		if (instance == null)
		{
			instance = new TutorialVideosTab();
		}
		
		return instance;
	}
}