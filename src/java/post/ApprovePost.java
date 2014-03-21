/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package post;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Article;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utilities.DirectoryManager;
import utilities.Upload;

/**
 *
 * @author Kevin Murphy and Kealan Smyth
 */

@WebServlet(name = "ApprovePost", urlPatterns = {"/ApprovePost"})
public class ApprovePost extends HttpServlet {
    
    private final boolean DEBUG_ON = true;

    private HttpSession session;
    
    private String charityName;
    private String trimmedCharityName;
    private String servletContext;
    private String absoluteServletContext;
    private String servletPath;
    
    private String articlesPath;
    
    private JSONObject articlesObj;
    private JSONArray  articlesArray;
    private JSONArray  unapprovedPosts;
    
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
        
        session = request.getSession(true);
        
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            init(request);
            unapprovedPosts = Article.getUnapprovedPosts(request);
            try (PrintWriter out = response.getWriter()) {
                renderUnapprovedPosts(request, out, unapprovedPosts);
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
        String idOfPostToApprove = (request.getParameter("id") == null) ? "" :request.getParameter("id") ;
        System.out.println(idOfPostToApprove);
        if(!"".equals(idOfPostToApprove)){
            Article.approvePost(request, idOfPostToApprove);
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
    
    
     private void init(HttpServletRequest request){
         
        session = request.getSession(true);
        
        //Get Charity Name from Session
        charityName = (String)session.getAttribute("charityName");

        //Trim, set to lower case and remove white spaces
        trimmedCharityName = DirectoryManager.toLowerCaseAndTrim(charityName);

        //Get the relative servlet context path - /cs3305/
        servletContext = request.getContextPath();
        
        //Servlets relitive name
        servletPath    = request.getServletPath();


            
       
    }
     
     
     
     private void renderUnapprovedPosts(HttpServletRequest request, PrintWriter out,JSONArray unapprovedPosts){
        
        LinkedHashMap<String, String> fieldsMap;
        System.out.println(unapprovedPosts.size());
        if(unapprovedPosts.size() != 0 ){
            for(int i = 0; i < unapprovedPosts.size(); i++ ){
                fieldsMap = Article.getDefaultValueMap(request);
                JSONObject post =  (JSONObject)unapprovedPosts.get(i);

                for(String key : fieldsMap.keySet()){
                    String field = fieldsMap.get(key);

                    if("tags".equals(key)){
                        fieldsMap.put("tags", Article.getTagsAsString(post));
                    }else{
                        field = ("".equals(post.get(key).toString()))?   fieldsMap.get(key) : post.get(key).toString();
                        fieldsMap.put(key, field);
                    }

                }
                
                out.println("<article class='unapprovedPost'>");
                out.println("<hr/>");
                
                if("".equals(fieldsMap.get("img"))){
                    out.println("<div class='postImg'><p>No Image Uploaded!</p></div>");
                }else{
                    out.println("<div class='postImg'><img src='" + servletContext  + "/" +Article.CHARITIES_DIR + trimmedCharityName + Upload.UPLOADS_DIR + fieldsMap.get("img") + "'/></div>");
                }
                
                out.println("<div class='postDetials'>");
                out.println("<p>Title: " + fieldsMap.get("title") + "</p>");
                out.println("<p>Description : " + fieldsMap.get("description") + "</p>");
                out.println("<p>Content : " + fieldsMap.get("content") + "</p>");
                out.println("<p>date: " + fieldsMap.get("date") + "</p>");
                out.println("<p>Type: " + fieldsMap.get("type") + "</p>");
                out.println("<p>Tags: " + fieldsMap.get("tags") + "</p>");
                out.println("<p class='approve_post_button' ><a onclick='return ajaxApprovePost(" + fieldsMap.get("id") + ")'>Approve Post</a></p>");
                out.println("<hr/>");
                out.println("</div>");
                out.println("</article>");
            }
        }else{
            out.println("<div id='no_posts'>");
            out.println("<p>There are no Posts for you to approve right now!</p>");
            out.println("</div>");
        }
        
           
     }
     
     
     
     
    
    

}
