package lasad.gwt.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lasad.gwt.client.importer.ImportFileChecker;
import lasad.gwt.client.logger.Logger;
import lasad.gwt.server.filehandling.Imageconverter;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {
	private String outputString;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private boolean multiMaps;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletFileUpload upload = new ServletFileUpload();
		request.setCharacterEncoding("UTF-8");
		this.outputString = "";
		out.reset();
		multiMaps = false;
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			FileItemStream item = null;
			boolean validFileType = true;

			if (!iter.hasNext()) Logger.log("FileUploadWidget: No iter.hasnext :(", Logger.DEBUG_ERRORS);
			while (iter.hasNext()) {
				item = iter.next();

				if (validFileType = ImportFileChecker.checkValidFileType(item.getName())){
					this.processInputStream(item.openStream(), item.getName());

				}
				else if (item.getName().endsWith(".zip")){
					multiMaps = true;
					File f = new File("zipFile");
					InputStream inputStream = item.openStream();
					OutputStream outputStream = new FileOutputStream(f);
					int len;
					byte[] buffer = new byte[1000000];
					while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
						outputStream.write(buffer, 0, len);
					}
					outputStream.close();
					inputStream.close();

					ZipFile zf = new ZipFile(f);
					Enumeration<? extends ZipEntry> entries = zf.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						InputStream entryStream = zf.getInputStream(entry);
						this.processInputStream(entryStream, entry.getName());
						try{entryStream.close();}catch(Exception e){}
					}
					try{zf.close();}catch(Exception e){}
					validFileType = true;

				}
				else {
					validFileType = false;
				}
			}

		if (validFileType) {
				outputString += out.toString("UTF-8");
				
				if (multiMaps) {
					outputString = "<lasad-list>" + outputString + "</lasad-list>";
				}
				
				if(outputString.contains("<backgroundimg>"))
				{
					int begin = outputString.indexOf("<backgroundimg>")+15;
					int end = outputString.indexOf("</backgroundimg>");
					String image = outputString.substring(begin,end);
					System.err.println(image);
					outputString =outputString.substring(0,begin)+"image.jpg</backgroundimg></lasad>";
					System.err.println(outputString);
					Imageconverter.stringToimage(image, "image.jpg");
				}

				//In case the user chose a largo file
				if (ImportFileChecker.checkLargo(outputString)) { 
					outputString = "LARGO-FILE;;" + item.getName() + ";;" + outputString;
				}


				outputString = outputString.replaceAll("<", "THISISABRACKETSUBSTITUTE1");
				outputString = outputString.replaceAll(">", "THISISABRACKETSUBSTITUTE2");

				response.setContentType("text/html; charset=UTF-8");
				PrintWriter output = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
				output.print(outputString);
				output.flush();

				//Does NOT send the response utf-encoded
				//				response.setCharacterEncoding("UTF-8");
				//				response.getOutputStream().print(outputString);
				//				response.getOutputStream().flush();

			}
			else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "file type invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processInputStream(InputStream inStream, String filename) throws IOException {
		//In case the user chose an argunaut file
		if (ImportFileChecker.checkArgunaut(filename)) {
			outputString = "GML-FILE";
		}

		InputStream stream = inStream;

		// Process the input stream
		int len;
		byte[] buffer = new byte[1000000];
		while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
			out.write(buffer, 0, len);
		}
	}
}