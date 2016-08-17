package lasad.entity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import lasad.Config;
import lasad.database.DatabaseConnectionHandler;
import lasad.entity.helper.Contribution;
import lasad.logging.Logger;
import lasad.shared.communication.objects.Action;
import lasad.shared.communication.objects.parameters.ParameterTypes;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


public class Ontology {
	
	private int id;
	private String name, xml;
	
	public Ontology(String name, String xml) {
		this.name = name;
		this.xml = xml;
	}
	
	public Ontology(int id, String name, String xml) {
		this(name, xml);
		this.id = id;
	}
	
	public static int getOntologyID(String ontologyName) {
		Connection con = null; 		
		String SQL = null;
		ResultSet rs = null;
		PreparedStatement getOntologyID = null;
		int returnInt = -1;
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntologyID = con.prepareStatement("SELECT id FROM "+Config.dbName+".ontologies WHERE name = ?;");
			getOntologyID.setString(1, ontologyName);
			
			SQL = getOntologyID.toString();
			
			rs = getOntologyID.executeQuery();
			if(rs.next()) {
				returnInt = rs.getInt(1);
			}			
		} catch (SQLException e){
			System.err.println(SQL);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntologyID.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return returnInt;
	}
	
	public static Ontology parseOntologyFromFile(File f) {	
	    FileInputStream in = null;
	    Ontology newOntology = null;	
		try {
			in = new FileInputStream(f);
	    	String ontologyName;
	    	
			SAXBuilder builder = new SAXBuilder();
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
						
			if(rootElement.getName().equalsIgnoreCase("ontology")){
				
				// Get Ontology Name
				ontologyName = rootElement.getAttributeValue("type");
				
				// Create Ontology Element
				XMLOutputter out = new XMLOutputter();
				String ontology = out.outputString(rootdoc);
				newOntology = new Ontology(ontologyName, ontology);		
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		try{in.close();}catch(Exception e){}
		return newOntology;
	}
	
	public void saveToDatabase() {
	
		if(Ontology.getOntologyID(this.name) == -1) { // Ontology does not exist
			
			Connection con = null;
			PreparedStatement insertOntology = null; 		
			
			try {
				con = DatabaseConnectionHandler.getConnection(Ontology.class);
//				con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
				
				insertOntology = con.prepareStatement("INSERT INTO "+Config.dbName+".ontologies (id, name, xmlConfig) VALUES (NULL, ?, ?);");
				insertOntology.setString(1, this.name);
				insertOntology.setString(2, this.xml);
				insertOntology.executeUpdate();			
			} catch (SQLException e){
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try{insertOntology.close();}catch(Exception e){}
				if(con != null) {
					DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//					try {
//						con.close();
//					} catch (SQLException e){
//						e.printStackTrace();
//					}
				}
			}
		}
		else {
			// Ontology with this name exists already, do nothing
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

	public static String getOntologyXML(int ontologyID) {
		
		String ontologyXML = null;
		Connection con = null; 
		PreparedStatement getOntologyXML = null;
		ResultSet rs = null;		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntologyXML = con.prepareStatement("SELECT xmlConfig FROM "+Config.dbName+".ontologies WHERE id = ?;");
			getOntologyXML.setInt(1, ontologyID);
			
			rs = getOntologyXML.executeQuery();
			rs.next();
			
			ontologyXML = rs.getString(1);			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntologyXML.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return ontologyXML;
	}
	
	public static HashMap<Contribution, Vector<String>> getOntologyElements(String ontologyName) {
		
		HashMap<Contribution, Vector<String>> ontologyElements = new HashMap<Contribution, Vector<String>>();
		
		String XML = getOntologyXML(getOntologyID(ontologyName));
		
		try {
			StringReader in = new StringReader(XML);
	    	
			SAXBuilder builder = new SAXBuilder();
			Document rootdoc = builder.build(in);
			
			Element rootElement = rootdoc.getRootElement();
			Element elementsElement = rootElement.getChild("elements");
			
			// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
			@SuppressWarnings("unchecked")
			List<Element> boxesAndRelations = elementsElement.getChildren();
			
			for(Element boxOrRelation : boxesAndRelations) {
				
				String type = boxOrRelation.getAttributeValue("type");
				type = type.toUpperCase();
				
				Contribution currentContribution = null;
				
				if(type.equals("BOX") || type.equals("RELATION")) {		
					Element elementOptions = boxOrRelation.getChild("elementoptions");
					String name = elementOptions.getAttributeValue("heading");
					
					currentContribution = new Contribution(type, name);
					
					ontologyElements.put(currentContribution, new Vector<String>());
				}
	
				Element childrenElements = boxOrRelation.getChild("childelements");

				// JDOM 1 returned a raw list (that was ultimately a list of elements), whereas JDOM 2 returns a list of elements
				@SuppressWarnings("unchecked")
				List<Element> children = childrenElements.getChildren("element");
				
				for(Element child : children) {
					String childrenType = child.getAttributeValue("elementtype");
					ontologyElements.get(currentContribution).add(childrenType);
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return ontologyElements;
	}

	public static Vector<Ontology> getOntologyList() {
		Vector<Ontology> ontologyList = new Vector<Ontology>();
		
		Connection con = null;
		Statement getOntologies = null;
		ResultSet rs = null; 		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntologies = con.createStatement();
			getOntologies.executeQuery("SELECT * FROM "+Config.dbName+".ontologies;");
			
			rs = getOntologies.executeQuery("SELECT * FROM "+Config.dbName+".ontologies;");
			while(rs.next()) {
				ontologyList.add(new Ontology(rs.getInt("id"), rs.getString("name"), rs.getString("xmlConfig")));
			}
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntologies.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		return ontologyList;
	}

	public static String getOntologyName(int ontologyId) {
		
		String ontologyName = null;
		Connection con = null;
		PreparedStatement getOntologyXML = null;
		ResultSet rs = null; 		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntologyXML = con.prepareStatement("SELECT name FROM "+Config.dbName+".ontologies WHERE id = ?;");
			getOntologyXML.setInt(1, ontologyId);
			
			rs = getOntologyXML.executeQuery();
			rs.next();
			
			ontologyName = rs.getString(1);			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntologyXML.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return ontologyName;
	}
	
	public static void removeOntologyFromDB(int ontologyID) {
		Connection con = null; 	
		PreparedStatement deleteOntology = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			deleteOntology = con.prepareStatement("DELETE FROM "+Config.dbName+".ontologies WHERE id = ? LIMIT 1;");
			deleteOntology.setInt(1, ontologyID);
			
			deleteOntology.executeUpdate();
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{deleteOntology.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}
	
	public static void removeFromFileSystem(String ontologyName) {
		File dir = new File(Config.configDir, "ontology");
		File[] ontologies = dir.listFiles();

		for (File f : ontologies) {
			if (!f.isHidden()) {

				Document doc = null;

				try {
					SAXBuilder builder = new SAXBuilder();
					doc = builder.build(f);

					org.jdom.Element root = doc.getRootElement();
					String ontoNameFromFile = root.getAttributeValue("type");
					if (ontoNameFromFile.equals(ontologyName)) {
						Logger.log("Deleting ontology file: " + f);
						f.delete();
						return;
					}
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
	
	public static boolean delete(int ontologyID) {
		
		// Delete templates that use these ontologies
		Vector<Integer> templateIDs = Template.getIDsOfTemplatesThatUseTheOntology(ontologyID);
		
		for(int i : templateIDs) {
			Template.delete(i);
		}
		
		// Delete ontology from File system
		Ontology.removeFromFileSystem(Ontology.getOntologyName(ontologyID));
		
		// Delete ontology from DB
		Ontology.removeOntologyFromDB(ontologyID);
		
		return true;
	}

	public static Vector<String> getAllOntologyNames() {
		Vector<String> ontologyList = new Vector<String>();
		
		Connection con = null; 	
		PreparedStatement getAllOntologyNames = null;
		ResultSet rs = null;	
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getAllOntologyNames = con.prepareStatement("SELECT name FROM "+Config.dbName+".ontologies;");
			
			rs = getAllOntologyNames.executeQuery();
			
			while(rs.next()) {
				ontologyList.add(rs.getString("name"));
			}
				
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getAllOntologyNames.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return ontologyList;
	}

	public static boolean isExisting(String ontologyName) {

		boolean existent = false;
		
		Connection con = null;
		PreparedStatement getOntology = null;
		ResultSet rs = null; 		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			getOntology = con.prepareStatement("SELECT id FROM "+Config.dbName+".ontologies WHERE name = ?;");
			getOntology.setString(1, ontologyName);
			
			rs = getOntology.executeQuery();
			if(rs.next()) {
				existent = true;
			}
						
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{rs.close();}catch(Exception e){}
			try{getOntology.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
		
		return existent;
	}

//	public static String getOntologyNameFromXMLFile(String ontologyXML) {
//		String title = null;
//		
//		try {
//			StringReader in = new StringReader(ontologyXML);
//	    	
//			SAXBuilder builder = new SAXBuilder();
//			Document rootdoc = builder.build(in);
//			
//			Element rootElement = rootdoc.getRootElement();
//						
//			if(rootElement.getName().equalsIgnoreCase("ontology")){
//				// Get ontology name
//				title = rootElement.getAttributeValue("type");
//			}		
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return title;
//	}

	public static void updateOntologyInDB(String ontologyName, String xml) {
		Connection con = null;
		PreparedStatement updateOntology = null; 		
		
		try {
			con = DatabaseConnectionHandler.getConnection(Ontology.class);
//			con = DriverManager.getConnection(Config.connection, Config.dbUser, Config.dbPassword);
			
			updateOntology = con.prepareStatement("UPDATE "+Config.dbName+".ontologies SET xmlConfig = ? WHERE name = ?;");
			updateOntology.setString(1, xml);
			updateOntology.setString(2, ontologyName);
			
			updateOntology.executeUpdate();						
		} catch (SQLException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{updateOntology.close();}catch(Exception e){}
			if(con != null) {
				DatabaseConnectionHandler.closeConnection(Ontology.class, con);
//				try {
//					con.close();
//				} catch (SQLException e){
//					e.printStackTrace();
//				}
			}
		}
	}

	public static void updateOntologyInFileSystem(String ontologyName, String xml) {
		try {
			File f = Ontology.getFileOfOntology(ontologyName);
			
			if(f != null) {
				String name = f.getAbsolutePath();
				f.delete();
				f = new File(name);
			}
					
			FileOutputStream out = new FileOutputStream(f);
	    	
			// output a UTF-8 File
			out.write(xml.getBytes("UTF-8"));
			
			out.flush();
			out.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private static File getFileOfOntology(String ontologyName) {
		File dir = new File(Config.configDir, "ontology");
		File[] ontologies = dir.listFiles();

		File returnFile = null;
		
		for (File f : ontologies) {
			if (!f.isHidden()) {
				FileInputStream in = null; 
				try {
			    	in = new FileInputStream(f);
					SAXBuilder builder = new SAXBuilder();
					Document rootdoc = builder.build(in);
					
					Element rootElement = rootdoc.getRootElement();
								
					if(rootElement.getName().equalsIgnoreCase("ontology")){					
						// Get Ontology Name
						if(!rootElement.getAttributeValue("type").equals(ontologyName)) {
							continue;
						}
						else {
							returnFile = f;
							break;
						}
					}		
				} catch(Exception e){
					e.printStackTrace();
				}
				try{in.close();}catch(Exception e){}
			}
		}
		
		return returnFile;		
	}

	public static File generateOntologyConfigurationFile(Action a) {
		
			String name = a.getParameterValue(ParameterTypes.OntologyName);
			String xml = a.getParameterValue(ParameterTypes.OntologyXML);
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			File ontologyDir = new File(Config.configDir, "ontology");
			File f = new File(ontologyDir, name + " - " + timestamp.toString().substring(0, 10) + ".xml");
			

			try {
				FileOutputStream output = new FileOutputStream(f);
				
				// output a UTF-8 File
				output.write(xml.getBytes("UTF-8"));
				
				output.flush();
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return f;
	}
}