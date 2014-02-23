/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Charity;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

/**
 * Handles uploading pictures. Creates unique names for each image using the date and time, 
 * stores the image in the Charity's uploads directory 
 * 
 * @author  Kevin Murphy
 * @version 1.0
 * @date    21/2/14
 */
@WebServlet(name = "uploadPhoto", urlPatterns = {"/uploadPhoto"})
@MultipartConfig
public class UploadPhoto extends HttpServlet {
    
    /* Debug mechinism */
    private static final boolean DEBUG_ON = true;
    /* For session tracking */
    HttpSession session;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        //Get the Session, or create one if does not exsists 
        session = request.getSession(true);
        //Get the charityName attribute
        String charityName = (String)session.getAttribute("charityName");
        
        //If the name is not null or Empty String, outputs upload form, else redirects to login
        if(charityName != null && ! "".equals(charityName)){
            PrintWriter out = response.getWriter();
        
            String servletContext = request.getContextPath();
            String servletPath    = request.getServletPath();
            try {
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Upload Photo</title>");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Upload Photo</h1>");
                out.println("<form method='POST' action='" + servletContext + servletPath +"' enctype='multipart/form-data' >");
                out.println("<form action='uploadPhoto' method='post' >");
                out.println("<label for='file'>File: </label>");
                out.println("<input id='file' type='file' name='filename_1' size='50'/><br/>");
                out.println("<br/>");
                out.println("<input type='submit' value='Upload File'/>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        }else{
            //Redirect to Login
            response.sendRedirect("Login");
        }
            
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        session = request.getSession(true);
        String charityName = (String)session.getAttribute("charityName");
        
        if(charityName != null && ! "".equals(charityName)){
            uploadFile(request, charityName, true);
        }else{
            //Redirect to Login
            response.sendRedirect("Login");
            
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    
    /**
     * Handles uploading of a file, which is converted to .png and named with
     * the current date and time. 
     * 
     * Also handles setting the uploaded image as the charity's main profile image,
     * when specified
     * 
     * @param request
     * @param charityName
     * @param isLogoImage
     * 
     */
    public static String uploadFile(HttpServletRequest request, String charityName, boolean isLogoImage){
        
        /* Build the path for the uploaded file*/
        //Get the Servlet Context for writing the json file
        String servletContext = request.getServletContext().getRealPath("/");
        String charitiesDir   = "charities/";
        //Changes to lower case, trims and removes white spaces from name
        String trimmedCharityName =  charityName.toLowerCase().trim().replaceAll("\\s+","");
        //The final upload path
        String uploadPath = servletContext + charitiesDir + trimmedCharityName + "/uploads/";
        if(DEBUG_ON){
            System.out.println("Upload Path: " + uploadPath );
        }
        
        String dateToday = "";
        
        /* Code taken in-part from https://commons.apache.org/proper/commons-fileupload/using.html */
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            try{
                List<FileItem> items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                
                while(iterator.hasNext()){
                    FileItem item = (FileItem) iterator.next();
                    if (!item.isFormField()) {

                        Date date = new Date();
                        SimpleDateFormat dateFormat = 
                        new SimpleDateFormat ("yyyy.MM.dd'-'hh:mm:ss");
                        dateToday  = dateFormat.format(date);
                        //Set file name as today's date and time
                        File uploadedFile = new File(uploadPath + dateToday + ".png");
                        item.write(uploadedFile);
                        
                        //If the uploaded image is to be made the logo for the Charity
                        if(isLogoImage){
                            /* Add image name to charity.json */
                            //Get the Charity Object from the charity.json file
                            Charity charity = Charity.parseJSONtoCharityObj(charityName, servletContext);
                            //Put the image into the json object
                            charity.setLogo(dateToday + ".png" );
                            //Write out to file
                            charity.createCharityJSONFile(servletContext);
                        }
                    }
                }
               
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return dateToday + ".png";
    }
}//End of uploadPhoto Class
