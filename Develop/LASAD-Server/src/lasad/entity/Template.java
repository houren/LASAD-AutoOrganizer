package lasad.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import lasad.Config;
import lasad.database.DatabaseConnectionHandler;
import lasad.logging.Logger;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


public class Template {
	
	private int id, ontologyID;
	private String name, xml;
	private String ontologyName;
	
	public Template(String templateName, String template, int ontologyID) {
		this.name = templateName;
		this.xml = template;
		this.ontologyID = ontologyID;
	}
	
	public Template(int id, String templateName, String template, int ontologyID) {
		this(templateName, template, ontologyID);
		this.id = id;
	}
	
	private Template(String templateName, int ontology_id) {
		this.name = templateName;
		this.ontologyID = ontology_id;
		this.ontologyName = Ontology.getOntologyName(ontology_id);
	}

	public static String getTemplateName(int templateID) {
		
		String name = null;
		
		Connection con = null; 		
		ResultSet rs = null;
		PreparedStatement getTemplateName = null;
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplateName = con.prepareStatement("SELECT name FROM "+Config.dbName+".templates WHERE id = ?;");
			getTemplateName.setInt(1, templateID);
			
			rs = getTemplateName.executeQuery();
			if(rs.next()) {
				name = rs.getString(1);
			}

		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { rs.close();}catch(Exception e){}
			try { getTemplateName.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return name;
	}
	
	public static boolean isCorrectTemplateFile(String tName, File f) {
		
		boolean returnVal = false;
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
	    	
	    	String templateName;
	    	
			SAXBuilder builder = new SAXBuilder();
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
						
			if(rootElement.getName().equalsIgnoreCase("maptemplate")){
				
				templateName = rootElement.getAttributeValue("uniquename");
				
				if(tName.equals(templateName)) {
					returnVal = true;
				}
			}
							
		} catch(Exception e){
			e.printStackTrace();
		}
		try{in.close();}catch(Exception e){}
		return returnVal;
	}
	
	public static Template parseTemplateFromFile(File f) {
		FileInputStream in = null;
		Template newTemplate = null;

		try {
			in = new FileInputStream(f);
	    	
	    	String templateName;
	    	String ontologyName;
	    	int ontologyID;
	    	
			SAXBuilder builder = new SAXBuilder();
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
						
			if(rootElement.getName().equalsIgnoreCase("maptemplate")){
				
				templateName = rootElement.getAttributeValue("uniquename");
				ontologyName = rootElement.getAttributeValue("ontology");
				
				ontologyID = Ontology.getOntologyID(ontologyName);
				
				XMLOutputter out = new XMLOutputter();
				String template = out.outputString(rootdoc);
				newTemplate = new Template(templateName, template, ontologyID);		
			}		
			
		} catch(Exception e){
			e.printStackTrace();
		}
		try{in.close();}catch(Exception e){}
		return newTemplate;
	}

	public void saveToDatabase() {
		
		if(Template.getTemplateID(this.name) == -1) { // Template does not exist
			
			Connection con = null; 	
			PreparedStatement insertTemplate = null;	
			
			String SQL = null;
			try {
				con = DatabaseConnectionHandler.getConnection(Template.class);
//				con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
				
				insertTemplate = con.prepareStatement("INSERT INTO "+Config.dbName+".templates (id, name, xmlConfig, ontology_id) VALUES (NULL, ?, ?, ?);");
				insertTemplate.setString(1, this.name);
				insertTemplate.setString(2, this.xml);
				insertTemplate.setInt(3, ontologyID);
				
				SQL = insertTemplate.toString();
				
				insertTemplate.executeUpdate();			
			} catch (SQLException e){
				System.err.println(SQL);
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try{insertTemplate.close();}catch(Exception e){}
				if(con != null) {
					DatabaseConnectionHandler.closeConnection(Template.class, con);
//					try {
//						con.close();
//					} catch (SQLException e){
//						e.printStackTrace();
//					}
				}
			}
		}
		else {
			// Template exists already, do nothing.
		}
	}

	public static int getTemplateID(String template) {
		Connection con = null; 
		PreparedStatement getTemplateID = null;	
		ResultSet rs = null;
		int returnVal = -1;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplateID = con.prepareStatement("SELECT id FROM "+Config.dbName+".templates WHERE name = ?;");
			getTemplateID.setString(1, template);
			rs = getTemplateID.executeQuery();
			if(rs.next()) {
				returnVal = rs.getInt(1);
			}		
		} catch (SQLException e){
			e.printStackTrace();
			returnVal = -1;
		} catch (Exception e) {
			e.printStackTrace();
			returnVal = -1;
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getTemplateID.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return returnVal;
	}
	
	public static int getOntologyID(String template) {
		Connection con = null; 
		PreparedStatement getOntologyID = null;	
		ResultSet rs = null;
		int returnVal = -1;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntologyID = con.prepareStatement("SELECT ontology_id FROM "+Config.dbName+".templates WHERE name = ?;");
			getOntologyID.setString(1, template);
			rs = getOntologyID.executeQuery();
			rs.next();
			
			returnVal = rs.getInt("ontology_id");			
		} catch (SQLException e){
			e.printStackTrace();
			System.out.println(e.getSQLState());
			returnVal = -1;
		} catch (Exception e) {
			e.printStackTrace();
			returnVal = -1;
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntologyID.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return returnVal;
	}
	
	public static int getTemplateMaxUsers(String xml) {
		int maximumUsers = 0;
		Reader in = null;
		try {
			in = new StringReader(xml);
	    	
			SAXBuilder builder = new SAXBuilder();
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
						
			if(rootElement.getName().equalsIgnoreCase("maptemplate")) {								
				Element mapDetailsElement = rootElement.getChild("mapdetails");
				Element options = mapDetailsElement.getChild("options");
				
				maximumUsers = Integer.parseInt(options.getAttributeValue("maxuser"));
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		try{in.close();}catch(Exception e){}
		return maximumUsers;
	
	}
	
	public String getTitle() {
		String templateTitle = null;
		Reader in = null;
		
		try {    	
	    	
			SAXBuilder builder = new SAXBuilder();
			
			in = new StringReader(this.xml);
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
						
			if(rootElement.getName().equalsIgnoreCase("maptemplate")){
				templateTitle = rootElement.getAttributeValue("title");		
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		try{in.close();}catch(Exception e){}
		return templateTitle;
	}
	
	public String getOntologyNameFromDB() {		
		String ontologyName = null;
		Connection con = null;
		PreparedStatement getTemplateID = null; 
		ResultSet rs = null;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplateID = con.prepareStatement("SELECT name FROM "+Config.dbName+".ontologies WHERE id = ?;");
			getTemplateID.setInt(1, this.ontologyID);
			rs = getTemplateID.executeQuery();
			rs.next();
			
			ontologyName = rs.getString(1);			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getTemplateID.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return ontologyName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOntologyID() {
		return ontologyID;
	}

	public void setOntologyID(int ontologyID) {
		this.ontologyID = ontologyID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public static File generateTemplateConfigurationFile(Action a) {

		Document doc = null;
		org.jdom.Element root = new org.jdom.Element("maptemplate");
		
		String name = a.getParameterValue(ParameterTypes.TemplateName);
		root.setAttribute("uniquename", name);
		root.setAttribute("title", name);
		root.setAttribute("ontology", a.getParameterValue(ParameterTypes.Ontology_Name));	
		
		doc = new Document(root);

		
		org.jdom.Element mapdetails = new org.jdom.Element("mapdetails");
		org.jdom.Element description = new org.jdom.Element("description");
		description.setText(a.getParameterValue(ParameterTypes.TemplateDescription));
		
		org.jdom.Element options = new org.jdom.Element("options");
		options.setAttribute("maxuser", a.getParameterValue(ParameterTypes.MaxUsers));
		options.setAttribute("chatsystem", a.getParameterValue(ParameterTypes.UseChat));
		options.setAttribute("listofusers", a.getParameterValue(ParameterTypes.UseUserlist));
		options.setAttribute("minimap", a.getParameterValue(ParameterTypes.UseMiniMap));
		options.setAttribute("track-cursor", a.getParameterValue(ParameterTypes.UseCursorTracking));
		options.setAttribute("selection-details", a.getParameterValue(ParameterTypes.UseSelectionDetails));
		
		// additional options
		options.setAttribute("onlyauthorcanmodify", a.getParameterValue(ParameterTypes.OnlyAuthorCanModify));
		options.setAttribute("committextbyenter", a.getParameterValue(ParameterTypes.CommitTextByEnter));
		options.setAttribute("straightlink", a.getParameterValue(ParameterTypes.StraightLink));

		// MODIFIED BY BM Adds the Parameter to the options Use the
		// Parametertype name as Key
		options.setAttribute(ParameterTypes.AutoGrowTextArea.name(),
				a.getParameterValue(ParameterTypes.AutoGrowTextArea));
		// MODIFY END

		mapdetails.addContent(description);
		mapdetails.addContent(options);
		
		if(a.getParameterValue(ParameterTypes.Transcript) != null) {
			org.jdom.Element transcript = new org.jdom.Element("transcript");
			transcript.setAttribute("id", name);
			
			org.jdom.Element lines = new org.jdom.Element("lines");
			
			String transcriptText = a.getParameterValue(ParameterTypes.Transcript);
			
			String[] transcriptLines = transcriptText.split("\n");
			
			for(int i=0; i<transcriptLines.length; i++) {
				org.jdom.Element singleLine = new org.jdom.Element("line");
				singleLine.setAttribute("number", i+"");
				singleLine.setAttribute("text", transcriptLines[i]);
				lines.addContent(singleLine);
			}
			
			transcript.addContent(lines);
			root.addContent(transcript);
		}
		
		root.addContent(mapdetails);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		File templateDir = new File(Config.configDir, "template");
		File f = new File(templateDir, name + " - " + timestamp.toString().substring(0, 10) + ".xml");

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

		try {
			FileOutputStream output = new FileOutputStream(f);
			outputter.output(doc, output);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
	public static Vector<Template> getAllTemplatesWithOntology(int ontologyID) {
		Vector<Template> templateList = new Vector<Template>();
			
		Connection con = null; 
		PreparedStatement getTemplate = null;
		ResultSet rs = null;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplate = con.prepareStatement("SELECT * FROM "+Config.dbName+".templates WHERE ontology_id = ?;");
			getTemplate.setInt(1, ontologyID);
			rs = getTemplate.executeQuery();
			
			while(rs.next()) {
				templateList.add(new Template(rs.getInt("id"), rs.getString("name"), rs.getString("xmlConfig"), ontologyID));
			}
						
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getTemplate.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}	
		
		return templateList;
	}

	public static String getXMLString(int template_id) {
		String templateXML = null;
		Connection con = null; 
		PreparedStatement getTemplateXML = null;
		ResultSet rs = null;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplateXML = con.prepareStatement("SELECT xmlConfig FROM "+Config.dbName+".templates WHERE id = ?;");
			getTemplateXML.setInt(1, template_id);
			
			rs = getTemplateXML.executeQuery();
			
			
			boolean foundTemplate = rs.next();
			
			if (foundTemplate){
				templateXML = rs.getString(1);			
			}
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getTemplateXML.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return templateXML;
	}

	public static Vector<Template> getAllTemplates() {
		Vector<Template> t = new Vector<Template>();
	
		Connection con = null; 	
		PreparedStatement getTemplateXML = null;
		ResultSet rs = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getTemplateXML = con.prepareStatement("SELECT name, ontology_id FROM "+Config.dbName+".templates;");
			
			rs = getTemplateXML.executeQuery();
			
			while(rs.next()) {
				t.add(new Template(rs.getString("name"), rs.getInt("ontology_id")));
			}
				
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getTemplateXML.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return t;
	}

	public static void removeFromFileSystem(String templateName) {
		File templateDir = new File(Config.configDir, "template");
		File[] templates = templateDir.listFiles();
		
		for(File f : templates) {
			if(!f.isHidden()) {
				if(Template.isCorrectTemplateFile(templateName, f)) {
					Logger.log("Deleting template file: "+f);
					f.delete();
				}
			}
		}
		
	}

	public static void removeTemplateFromDB(int templateID) {
		Connection con = null; 	
		PreparedStatement deleteTemplate = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			deleteTemplate = con.prepareStatement("DELETE FROM "+Config.dbName+".templates WHERE id = ? LIMIT 1;");
			deleteTemplate.setInt(1, templateID);
			
			deleteTemplate.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{deleteTemplate.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
	}
	
	public static boolean delete(int templateID) {
	
		// Delete maps that use these templates
		Vector<Integer> mapIDs = Map.getIDsOfMapsThatUseTheTemplate(templateID);
		
		for(int i : mapIDs) {
			Map.delete(i);
		}
		
		// Delete from file system
		Template.removeFromFileSystem(Template.getTemplateName(templateID));
		
		// Delete from database
		Template.removeTemplateFromDB(templateID);
		
		return true;
	}

	public static Vector<Integer> getIDsOfTemplatesThatUseTheOntology(int ontologyID) {
		Vector<Integer> templateIDs = new Vector<Integer>();
				
				Connection con = null; 		
				ResultSet rs = null;
				PreparedStatement getTemplateIDs = null;
				try {
					con = DatabaseConnectionHandler.getConnection(Template.class);
//					con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
					
					getTemplateIDs = con.prepareStatement("SELECT id FROM "+Config.dbName+".templates WHERE ontology_id = ?;");
					getTemplateIDs.setInt(1, ontologyID);
					
					rs = getTemplateIDs.executeQuery();
					
					while(rs.next()) {
						templateIDs.add(rs.getInt(1));
					}
					
				} catch (SQLException e){
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					try{rs.close();}catch(Exception e){}
					try{getTemplateIDs.close();}catch(Exception e){}
					if(con != null) {
						DatabaseConnectionHandler.closeConnection(Template.class, con);
//						try {
//							con.close();
//						} catch (SQLException e){
//							e.printStackTrace();
//						}
					}
				}
				
				return templateIDs;
	}
	
	public static Template getTemplate(String templateName) {
		Template t = null;
	
		Connection con = null; 	
		ResultSet rs = null;	
		PreparedStatement templateQuery = null;
		
		try {
			con = DatabaseConnectionHandler.getConnection(Template.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			// Get template information
			templateQuery = con.prepareStatement("SELECT * FROM "+Config.dbName+".templates WHERE name = ?;");
			templateQuery.setString(1, templateName);

			rs = templateQuery.executeQuery();
			rs.next();
			
			t = new Template(rs.getInt("id"), rs.getString("name"), rs.getString("xmlConfig"), rs.getInt("ontology_id"));
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{templateQuery.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Template.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}		
		
		return t;
	}

//	public static Template parseTemplateFromXML(String templateXML) {
//		try {
//			StringReader in = new StringReader(templateXML);
//	    	
//	    	Template newTemplate = null;
//	    	String templateName;
//	    	String ontologyName;
//	    	int ontologyID;
//	    	
//			SAXBuilder builder = new SAXBuilder();
//			Document rootdoc = builder.build(in);
//			
//			Element rootElement = rootdoc.getRootElement();
//						
//			if(rootElement.getName().equalsIgnoreCase("maptemplate")){
//				
//				templateName = rootElement.getAttributeValue("uniquename");
//				ontologyName = rootElement.getAttributeValue("ontology");
//				
//				ontologyID = Ontology.getOntologyID(ontologyName);
//				
//				XMLOutputter out = new XMLOutputter();
//				String template = out.outputString(rootdoc);
//				newTemplate = new Template(templateName, template, ontologyID);		
//			}
//			
//			return newTemplate;
//			
//		} catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}

//	public static String getTemplateNameFromXMLFile(String templateXML) {
//    	String templateName = null;
//    	
//		try {
//			StringReader in = new StringReader(templateXML);
//	    	
//			SAXBuilder builder = new SAXBuilder();
//			Document rootdoc = builder.build(in);
//			
//			Element rootElement = rootdoc.getRootElement();
//						
//			if(rootElement.getName().equalsIgnoreCase("maptemplate")){
//				
//				templateName = rootElement.getAttributeValue("uniquename");
//			}
//			
//			return templateName;
//			
//		} catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//	}
}