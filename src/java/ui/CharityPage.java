/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Charity;
import static json.Charity.CHARITIES_DIR;
import static json.Charity.CHARITY_FILE_NAME;
import static json.Charity.JSON_DIR;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import utilities.DirectoryManager;

/**
 *
 * @author kpmmmurphy
 */
@WebServlet(name = "CharityPage", urlPatterns = {"/CharityPage"})
public class CharityPage extends HttpServlet {

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
        
        HttpSession session = request.getSession(true);
        String charityName = request.getParameter("name");
        Map<String, String[]> params = request.getParameterMap();
        Iterator i = params.keySet().iterator();

        while ( i.hasNext() ){
            String key = (String) i.next();
            String value = ((String[]) params.get( key ))[ 0 ];
            System.out.println(key);
            System.out.println(value);
            System.out.println(1);
        }
        
        //System.out.println(charityName);
        
        //String servletContext = request.getServletContext().getRealPath("/");
        
        //String jsonPath = servletContext + /* CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityName) + */ JSON_DIR + CHARITY_FILE_NAME ;
        
        //Charity charity = Charity.parseJSONtoCharityObj(jsonPath);
        //System.out.println(charity.getName());
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CharityPage</title>");            
            out.println("</head>");
            out.println("<body>");
            
            
            
            //printNav(out);
            
            
            
            
            out.println("</body>");
            out.println("</html>");
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
        //processRequest(request, response);
        
        HttpSession session = request.getSession(true);
        String charityName = request.getParameter("charity_name");
        
        Object charityJson;
        JSONObject charityJSONObj = new JSONObject();
        
        Map<String, String[]> params = request.getParameterMap();
        Iterator i = params.keySet().iterator();
        while ( i.hasNext() ){
            String key = (String) i.next();
            String value = ((String[]) params.get( key ))[ 0 ];
            
            
            charityJson        = JSONValue.parse(key);
            charityJSONObj = (JSONObject)charityJson;
            
            System.out.println("Key: " + key);
            System.out.println("Value: " + value);
        }
        
        
        String servletContext = request.getServletContext().getRealPath("/");
        String jsonPath = servletContext + CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityJSONObj.get("charity_name").toString()) + JSON_DIR + CHARITY_FILE_NAME ;
        System.out.println(jsonPath);
        Charity charity = Charity.parseJSONtoCharityObj(jsonPath);
        
        session.setAttribute("charityName", charity.getName());
        
         try (PrintWriter out = response.getWriter()) {
            
            printNav(charity.getName(), out);
            
            
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
    
    private void printNav(String charityName, PrintWriter out ){
        
        //charityName = "'" + charityName + "'";
        
        out.println("<nav>");
        out.println("<ul>");
        out.println("<li><a onclick=\"getCharityDetails('" + charityName + "')\">Home</a></li>");
        out.println("<li><a onclick=\"getArticles('" + charityName + "',  null           )\">News</a></li>");
        out.println("<li><a onclick=\"getArticles('" + charityName + "', 'lost_and_found')\">Lost and Found</a></li>");
        out.println("<li><a onclick=\"getArticles('" + charityName + "', 'sponsorship'   )\">Sponsorship</a></li>");
        out.println("<li><a onclick=\"getSubmitForm()\">Submit a Post</a></li>");
        out.println("<li><a onclick=\"getDonate()\">Donate</a></li>");
        out.println("<li><a onclick=\"getFAQ()\">FAQ</a></li>");
        out.println("</ul>");
        out.println("</nav>");  
    }

}
