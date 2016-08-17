package lasad.gwt.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// Deprecated, relaced with DataTypeConverter by Kevin Loughlin
	//import sun.misc.BASE64Decoder;
import javax.xml.bind.DatatypeConverter;

public class ScreenShotServlet extends HttpServlet {

	private static final long serialVersionUID = 2L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reader = request.getReader();
		String tempLine;
		String requestData = "";
		while ((tempLine = reader.readLine()) != null) {
			requestData = requestData + tempLine;
		}
		String filename = requestData.substring(0, requestData.indexOf(":"));
		requestData = requestData.substring(requestData.indexOf(":") + 1);

		// Don't know what this is or why it was here, most likely something similar to the replacement I (Kevin Loughlin) made
			// Imageconverter.stringToimage(requestData, "image.png");

		// I got rid of these two lines because they were deprecated, replaced with line below it
			// BASE64Decoder decoder = new BASE64Decoder();
			// byte[] decodedBytes = decoder.decodeBuffer(requestData);
		byte[] decodedBytes = DatatypeConverter.parseBase64Binary(requestData);

		File f = new File(System.getProperty("java.io.tmpdir") + File.separator + filename + ".png");

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
		if (image == null) {
			System.err.println("Buffered Image is null");
		}
		// write the image
		ImageIO.write(image, "png", f);
		try{reader.close();}catch(Exception e){}
	}
}