package lasad.gwt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lasad.gwt.client.logger.Logger;
import lasad.gwt.client.settings.DebugSettings;
import lasad.gwt.server.filehandling.Imageconverter;

public class SaveToXmlServlet extends HttpServlet{

    private static final long serialVersionUID = 4597216801906728328L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		String tempLine;
		String requestData = "";
		while ((tempLine = reader.readLine()) != null) {
			if(tempLine.contains("<backgroundimg>"))
		    {
		    	int begin = tempLine.indexOf("<backgroundimg>")+15;
		    	int end = tempLine.indexOf("</backgroundimg>");
		    	if (end>begin){
		    		// TODO: Check, if "end-1" is ok: substring(begin,end) gives the substring from index begin to index end-1!!!
		    		String replaced =tempLine.substring(0,begin)+Imageconverter.imageTostring("uploads/"+tempLine.substring(begin,end-1))+tempLine.substring(end);
		    		tempLine = replaced;
		    	}
		    }
		    requestData = requestData + tempLine;
		}

		File file = new File(System.getProperty("java.io.tmpdir") + File.separator +"tempfile.xml");
		Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));

		fw.write(requestData);
		fw.flush();
		fw.close();
		if (reader != null) {
		    reader.close();
		}
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator +"tempfile.xml");
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try
		{
		    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		    String text = null;

		    // repeat until all lines is read
		    while ((text = reader.readLine()) != null) {
				contents.append(text).append(System.getProperty( "line.separator"));
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
			if (reader != null) {
			    reader.close();
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
		
		if (DebugSettings.debug && file.delete()) {
		 	Logger.log("SaveToXmlServlet: Temporary file successfully deleted.", Logger.DEBUG);
		}
		

		String text = contents.toString();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment;filename=YourMap.xml"); 
		PrintWriter out = response.getWriter();
		out.println(text);
		out.flush();
		out.close();
	
    }

}