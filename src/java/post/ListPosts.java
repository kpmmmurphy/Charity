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
import json.Charity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utilities.DirectoryManager;
import utilities.PayPal;
import utilities.Upload;

/**
 * List all posts/articles present in a charity specific articles.json file
 * 
 * @author Kevin Murphy
 * @version 1.0
 * @date 17/2/14
 */
@WebServlet(name = "ListPosts", urlPatterns = {"/ListPosts"})
public class ListPosts extends HttpServlet {
    
    private HttpSession session;
    
    private String charityName;
    private String servletContext;
    
    private JSONArray  articlesArray;

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
        try (PrintWriter out = response.getWriter()) {
            init(request);
            renderPosts(request, out, articlesArray);
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
        processRequest(request, response);
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
     * Initilizes all class attributes used in subsequent methods
     * @param request The HttpSevletRequest 
     */ 
    private void init(HttpServletRequest request){
         
        session = request.getSession(true);
        
        //Get Charity Name from Session
        charityName = (String)session.getAttribute("charityName");

        //Get the relative servlet context path - /cs3305/
        servletContext = request.getContextPath();
        

        //Get the articles JSONArray from the object created in the previous line
        articlesArray = Article.getArticlesArrayFromFile(request);

            
       
    }
    
    /**
     * Create HTML article elements for each post/article present in the article.json file 
     * of the given charity
     * 
     * @param request The HttpServlet request used to identify the specific charity
     * @param out The PrintWriter
     * @param articles The JSONArray of articles from the articles.json file
     */
    private void renderPosts(HttpServletRequest request, PrintWriter out,JSONArray articles){
        String id;
        String title;
        String description;
        String content;
        String img;
        String type;
        String date;
        String tags;
        
        LinkedHashMap<String, String> fieldsMap;
        if(articles.size() > 0){
            for(int i = 0; i < articles.size(); i++ ){
            fieldsMap = Article.getDefaultValueMap(request);
            JSONObject article =  (JSONObject)articles.get(i);
            String field;
            for(String key : fieldsMap.keySet()){
                if("tags".equals(key)){
                    fieldsMap.put("tags", Article.getTagsAsString(article));
                }else{
                    field = ("".equals(article.get(key).toString()))?   fieldsMap.get(key) : article.get(key).toString();
                    fieldsMap.put(key, field);
                }
                
            }

            out.println("<article class='article'>");
            if("".equals(fieldsMap.get("img"))){
                out.println("<div class='postImg'><p>No Image Uploaded!</p></div>");
            }else{
                out.println("<div class='postImg'><img id='postImage' src='" + servletContext  +"/" +Article.CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityName) + Upload.UPLOADS_DIR + fieldsMap.get("img") + "'/></div>");
            }
            out.println("<div class='articleDetials'>");
            out.println("<p><h4>Title: " + fieldsMap.get("title") + "</h4></p>");
            out.println("<p><h4>Description : " + fieldsMap.get("description") + "</h4></p>");
            //out.println("<p>Content : " + fieldsMap.get("content") + "</p>");
            out.println("<p><h4>Date: " + fieldsMap.get("date") + "</h4></p>");
            out.println("<p><h4>Type: " + fieldsMap.get("type") + "</h4></p>");
            out.println("<p><h4>Tags: " + fieldsMap.get("tags") + "</h4></p>");
            out.println("<p><a onclick='getEditPost(" + fieldsMap.get("id") + ")'>Edit</a></p>");
            out.println("<p><a onclick='getDeletePost(" + fieldsMap.get("id") + ")'>Delete</a></p>");
            out.println("</div>");
            out.println("</article>");
            }
        }else{
            out.println("<div id='no_posts'>");
            out.println("<p>There are no Posts for you to edit right now, try creating one!</p>");
            out.println("</div>");
        }
        
           
     }

}
