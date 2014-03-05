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
        
        initilizeDetials(request);
        
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
                out.println("<input type=\"submit\" value=\"Submit\">");
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
     
            formFieldMap = Upload.processMultipartForm(request, charityName, false);
            Article article = new Article(request, formFieldMap); 
            article.writeOutArticle(request);
       
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
    
    private boolean initilizeDetials(HttpServletRequest request){
        boolean success = true;
        session = request.getSession(false);
        if(session.getAttribute("authorised") != null){
            //Get Charity Name from Session
            charityName = (String)session.getAttribute("charityName");
            //Trim, set to lower case and remove white spaces
            trimmedCharityName = charityName.toLowerCase().trim().replaceAll("\\s+","");
            //Get servlet Context
            servletContext = request.getServletContext().getRealPath("/");
            if(DEBUG_ON){
                System.out.println("Charity Name: "     + charityName);
                System.out.println("Servlet Context : " + servletContext);
            }
        }else{
            success = false;
        }
        return success;
    }
        
   

}
