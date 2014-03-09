/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package post;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Article;
import utilities.DirectoryManager;
import utilities.Upload;

/**
 *
 * @author kealan
 * @version 1.1
 * @date 16/2/14
 */
@WebServlet(name = "CreatePost", urlPatterns = {"/CreatePost"})
public class CreatePost extends HttpServlet {

    private boolean DEBUG_ON = true;
    
    private String charityName;
    private String trimmedCharityName;
    private String servletContext;
    private String articleImg;
    
    private HttpSession session;
    
    private LinkedHashMap formFieldMap; 
    
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
        
        session = request.getSession();
        
        init(request);
        
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            String servletContext = request.getContextPath();
            String servletPath = request.getServletPath();
            
            try (PrintWriter out = response.getWriter()) {
                /* Just the HTML for the form, gotten with an JQuery AJAX call*/
                
                out.println("<form method='POST' action='" + servletContext + servletPath +"' enctype='multipart/form-data' >");
                out.println("<fieldset>");
                out.println("<legend>New Post</legend>");
                out.println("Title: <input type='text' name='title' placeholder='Post Title'> <br />");
                out.println("<hr />");
                out.println("Type: <select name='type' placeholder='Type of Post'>"
                        + "         <option value='general'>General</option>"
                        + "         <option value='lost_and_found'>Lost and Found</option>"
                        + "         <option value='sponsorship'>Sponsorship</option>"
                        + "        </select><br />");
                out.println("<hr />");
                out.println("Brief Description: <textarea name=\"description\" rows=\"5\" cols=\"10\"></textarea><br />");
                out.println("<hr />");
                out.println("Content: <textarea name=\"content\" rows=\"15\" cols=\"30\"></textarea><br />");
                out.println("<hr />");
                out.println("Upload Logo Image : <input id='file' type='file' name='filename' size='50'/><br/>");
                
                if(articleImg == null){
                    out.println("<p class=\"float\"> No Image uploaded yet!</p>");
                }else{
                    out.println("<img src='charities/" + trimmedCharityName  + "/uploads/" + articleImg + "' id='articleImg' /><br/>");
                }
                out.println("<hr />");
                out.println("Tags : <input type='text' name='tags' placeholder='Tags Seperated by a Space' /> <br />");
                out.println("<hr />");
                out.println("Post to Twitter Account : <input type='checkbox' name='twitter_oauth' id='twitter_oauth' value='post_to_twitter'/>");
                out.println("<hr />");
                out.println("<input type=\"submit\" value=\"Submit\" id='submitPost'>");
                out.println("<input type=\"reset\" value=\"Clear\">");
                out.println("</fieldset>");
                out.println("</form>");
                out.println("<p>Return to <a href=\"Dashboard\">Dashboard</a></p>");
                
            }
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
        
            init(request);
     
            formFieldMap = Upload.processMultipartForm(request, charityName, false);
            String twitterOAuth = formFieldMap.get("twitter_oauth").toString();
            System.out.println(twitterOAuth);
            
            Article article = new Article(request, formFieldMap); 
            article.writeOutArticle(request);
            
            String articleTitle = article.getTitle();
            int    articleID    = article.getId();
            
            try (PrintWriter out = response.getWriter()) {
                
                if(twitterOAuth != null && !"".equals(twitterOAuth)){
                    
                    String parameterString = "'" + articleTitle + "','" + charityName + "'," + articleID;
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Servlet DonationManager</title>"); 
                    out.println("<script src='javascript/createpost.js'></script>");
                    out.println("<script src='javascript/jquery/jquery-1.11.0.js'></script>");
                    out.println("<script src='javascript/jquery/jquery-ui-1.10.4/ui/jquery-ui.js'></script>");
                    out.println(" <script src=\"javascript/colorbox-master/jquery.colorbox-min.js\"></script>");
                    out.println("<link rel=\"stylesheet\" href=\"javascript/jquery/jquery-ui-1.10.4/themes/base/jquery-ui.css\" />");
                    out.println("<link rel=\"stylesheet\" href=\"javascript/colorbox-master/colorbox.css\" />");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<section>Posting to Social Media</section>");
                    out.println("<p><a onclick=\"createTwitterOAuthWindow( " + parameterString + " )\">Post to Twitter</a></p>");
                    out.println("<p><a onclick=\"createFacebookOAuthWindow( " + parameterString + " )\">Post to Facebook</a></p>");
                    out.println("</body>");
                    out.println("</html>");
                    
                   
        
        
        
       
        
        
        
                }
                
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
    
    private boolean init(HttpServletRequest request){
        boolean success = true;
        session = request.getSession(false);
        if(session.getAttribute("authorised") != null){
            //Get Charity Name from Session
            charityName = (String)session.getAttribute("charityName");
            //Trim, set to lower case and remove white spaces
            trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        }else{
            success = false;
        }
        return success;
    }
        
   

}
