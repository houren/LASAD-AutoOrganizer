package lasad.gwt.server;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ScreenShotMergeServlet extends HttpServlet {

	private static final long serialVersionUID = 3L;
	private String filename;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		BufferedReader reader = request.getReader();
		String tempLine;
		String requestData = "";
		while ((tempLine = reader.readLine()) != null) {
			requestData = requestData + tempLine;
		}

		// recieving the Prefix of files
		filename = requestData.substring(0, requestData.indexOf(","));
		requestData = requestData.substring(requestData.indexOf(",") + 1);
		// recieving the number of the cols and rows
		int rows = Integer.parseInt(requestData.substring(0, requestData.indexOf(":"))); // we assume the no. of rows and cols are
																							// known and each chunk has equal
																							// width and height
		int cols = Integer.parseInt(requestData.substring(requestData.indexOf(":") + 1));
		// System.err.println(rows+":"+cols);
		int chunks = rows * cols;

		int chunkWidth, chunkHeight;
		// int type;
		// fetching image files

		File[] imgFiles = new File[chunks];
		for (int i = 0; i < chunks; i++) {
			imgFiles[i] = new File(System.getProperty("java.io.tmpdir") + File.separator + filename + i + ".png");
		}

		// creating a bufferd image array from image files
		BufferedImage[] buffImages = new BufferedImage[chunks];
		for (int i = 0; i < chunks; i++) {
			buffImages[i] = ImageIO.read(imgFiles[i]);
			imgFiles[i].delete();
		}

		/*
		 * type = buffImages[0].getType();
		 * if (type == 0) {
		 * // for some reasons on some machines the image type is not received correctly by the getter -> manually set it to 5...
		 * // type of png
		 * type = 5;
		 * }
		 */
		chunkWidth = buffImages[0].getWidth();
		chunkHeight = buffImages[0].getHeight();

		// Initializing the final image
		// BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

		GraphicsConfiguration gc = buffImages[0].createGraphics().getDeviceConfiguration();
		BufferedImage finalImg = gc.createCompatibleImage(chunkWidth * cols, chunkHeight * rows, Transparency.BITMASK);
		Graphics2D g2d = finalImg.createGraphics();
		g2d.setComposite(AlphaComposite.Src);

		int num = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				g2d.drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);

				// finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
				num++;
			}
		}

		ImageIO.write(finalImg, "png", new File(System.getProperty("java.io.tmpdir") + File.separator + filename + "_finalImg.png"));

		g2d.dispose();
		System.out.println("Image concatenated.....");
		try{reader.close();}catch(Exception e){}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");
		File file = new File(System.getProperty("java.io.tmpdir") + File.separator + filename + "_finalImg.png");
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int) file.length()];
		fin.read(fileContent);
		fin.close();
		response.setContentLength((int) file.length());
		response.setHeader("Content-disposition", "attachment;filename=Screenshot.png");
		file.delete();
		response.getOutputStream().write(fileContent);
		response.getOutputStream().flush();
	}
}