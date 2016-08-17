package lasad.gwt.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class ImageUploadServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        
        // Create a new file upload handler  
        ServletFileUpload upload = new ServletFileUpload();  
  
        try {  
            // Parse the request  
            FileItemIterator iter = upload.getItemIterator(request);  
            while (iter.hasNext()) {  
                FileItemStream item = iter.next();  
                String name = item.getFieldName();  
                InputStream stream = item.openStream(); 
                if (item.isFormField()) {  
                    System.out.println("Form field " + name + " with value "  
                            + Streams.asString(stream) + " detected.");  
                } else {  
                    System.out.println("File field " + name  
                            + " with file name " + item.getName().substring(item.getName().lastIndexOf("\\")+1)  
                            + " detected.");  
                    BufferedInputStream inputStream = new BufferedInputStream(  
                            stream);
                    String realPath = request.getSession().getServletContext().getRealPath("/");
                    File f = new File(realPath + "uploads");
                    if(!f.exists())
                    {
                        f.mkdir();
                    }
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(realPath + "uploads/"+item.getName().substring(item.getName().lastIndexOf("\\")+1)))); 
                    Streams.copy(inputStream, outputStream, true); 
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                }
                
                stream.close();
//                response.setStatus(HttpServletResponse.SC_CREATED);
//                response.getWriter().print(item.getName().substring(item.getName().lastIndexOf("\\")+1));
//                response.flushBuffer();
            }  
            System.err.println("upload finished!");

        } catch (Exception ex) {  
            ex.printStackTrace();  
        }
    }
}