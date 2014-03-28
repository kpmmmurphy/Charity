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
 * Generates new post/article creation multi-part forms via AJAX calls, and outputs 
 * links on submission to post a link to social media via OAuth of the new article.  
 * 
 * Handles the passing of new posts/articles multi-part form data to the Upload.java class which 
 * uploads the article image, and passes back a hash map of the article's form field parameters,
 * which are in turn passed to the Article.java class where they are written to file
 * 
 * @author Kevin Murphy and Kealan Smyth
 * @version 1.1
 * @date 16/2/14
 */
@WebServlet(name = "CreatePost", urlPatterns = {"/CreatePost"})
public class CreatePost extends HttpServlet {

    /* Debug Mechinism */
    private boolean DEBUG_ON = true;

    private String charityName;
    private String trimmedCharityName;

    private HttpSession session;

    /* Map of all fields of a new Article */
    private LinkedHashMap formFieldMap;
    private String articleImg;

    /**
     * Request via an AJAX call, only the form required is output to the browser
     * 
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

        if (session.getAttribute("charityName") == null) {
            response.sendRedirect("Login");
        } else {

            try (PrintWriter out = response.getWriter()) {
                /* Just the HTML for the form, gotten with an JQuery AJAX call*/
                
                out.println("<form method='POST' id='create_post' enctype='multipart/form-data' >");
                out.println("<fieldset>");
                out.println("<legend>New Post</legend>");
                out.println("<label for='title'>Title:</label> <input type='text' name='title' placeholder='Post Title'> <br />");
                out.println("<hr />");
                out.println("<label for='type'>Type:</label><select name='type' placeholder='Type of Post'>"
                        + "         <option value='general'>General</option>"
                        + "         <option value='lost_and_found'>Lost and Found</option>"
                        + "         <option value='sponsorship'>Sponsorship</option>"
                        + "        </select><br />");
                out.println("<hr />");
                out.println("<label for='description'>Brief Description:</label> <textarea name=\"description\" rows=\"5\" cols=\"10\"></textarea><br />");
                out.println("<hr />");
                out.println("<label for='content'>Content:</label><textarea name=\"content\" rows=\"15\" cols=\"30\"></textarea><br />");
                out.println("<hr />");
                out.println("<label for='filename'>Upload Logo Image :</label> <input id='file' type='file' name='filename' size='50'/><br/>");

                if (articleImg == null) {
                    out.println("<p class=\"float\"> No Image uploaded yet!</p>");
                } else {
                    out.println("<img src='charities/" + trimmedCharityName + "/uploads/" + articleImg + "' id='articleImg' /><br/>");
                }
                out.println("<hr />");
                out.println("<label for='tags'>Tags :</label> <input type='text' name='tags' placeholder='Tags Seperated by a Space' /> <br />");
                out.println("<hr />");
                if (session.getAttribute("authorised") != null) {
                    out.println("<label for='post_to_social_media'>Post to Social Media Accounts :</label> <input type='checkbox' name='post_to_social_media' id='social_media' value='social_media'/>");
                    out.println("<hr />");
                }
                if((Boolean)session.getAttribute("viewing_homepage") == true){
                     out.println("<input type=\"submit\" value=\"Submit\" id='submitPost' onclick='return ajaxPostSubmit(true)'>");
                }else{
                    out.println("<input type=\"submit\" value=\"Submit\" id='submitPost' onclick='return ajaxPostSubmit()'>");
                }
                out.println("</fieldset>");
                out.println("</form>");
                
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
     * Called via an AJAX POST request, passes the multipart form data to the Upload.java servlet, and outputs
     * HTML links to OAuth social media posting functionality. 
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
        Article article = new Article(request, formFieldMap);
        article.writeOutArticle(request);

        String articleTitle = article.getTitle();
        int articleID = article.getId();

        try (PrintWriter out = response.getWriter()) {

            if (formFieldMap.containsKey("post_to_social_media")) {

                String parameterString = "'" + articleTitle + "','" + charityName + "'," + articleID;
                
                out.println("<section id='post_to_social_media'>");
                out.println("<h1>Posting to Social Media</h1>");
                
                out.println("<div class='oauth_init' onclick=\"createTwitterOAuthWindow( " + parameterString + " )\">");
                out.println("<img src='images/social/twitter.png'><p><a>Post to Twitter</a></p>");
                out.println("</div>");

                out.println("<div class='oauth_init' onclick=\"createFacebookOAuthWindow( " + parameterString + " )\">");
                out.println("<img src='images/social/facebook.png' ><p><a>Post to Facebook</a></p>");
                out.println("</div>");
                
                out.println("</section>");
            }else{
                out.println("0");
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

    /**
     * Instantiates all necessary servlet attributes - session, charityName, trimmedCharitName
     * 
     * @param request
     * @return 
     */
    private boolean init(HttpServletRequest request) {
        boolean success = true;
        session = request.getSession(false);
        if (session.getAttribute("authorised") != null) {
            //Get Charity Name from Session
            charityName = (String) session.getAttribute("charityName");
            //Trim, set to lower case and remove white spaces
            trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        } else {
            success = false;
        }
        return success;
    }

}
