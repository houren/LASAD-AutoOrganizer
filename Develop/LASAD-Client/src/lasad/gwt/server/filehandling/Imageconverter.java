package lasad.gwt.server.filehandling;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.fileupload.util.Streams;

import com.google.gwt.user.server.Base64Utils;

public class Imageconverter {

	public static String imageTostring(String imagepath)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Streams.copy(new FileInputStream(new File(imagepath)), byteArrayOutputStream,true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    byte[] data = byteArrayOutputStream.toByteArray();
	    return Base64Utils.toBase64(data);
	}
	
	public static void stringToimage(String txt, String name)
	{
		byte[] data = Base64Utils.fromBase64(txt);
		try {
			FileOutputStream fos = new FileOutputStream(name);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}