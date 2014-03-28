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
 * This servlet is called via an AJAX POST request, with the charity name as a request parameter, and dynamically 
 * retrieves the charity.json file associated with the given charity, and then builds and sends to the browser a HTML nav element with each link containing 
 * an onclick Javascript listener, which calls a Javascript function on the client side, present in the homepage.js file. 
 * 
 * The index.html file that visitors to the charity's page see, is copied to each new charity directory on signup. The body element of the html page
 * has an onload attribute, which calls the init() function present in the homepage.js file when it is loaded. This init() function first retrieves the relative charity's charity.json 
 * file and extracts the charity's name from it. Next the init() function makes an AJAX POST request to this servlet(CharityPage.java) with the charity name as a parameter.The servlet then dynamically 
 * builds the nav and sends it back to the browser. This implementation allows us to maintain a servlet session with the visiting browser, which is used when submitting comments and tracking donations
 * on the server side. 
 * 
 * @author  Kevin Murphy and Kealan Smyth
 * @version 1.1
 * @date 10/3/14
 */
@WebServlet(name = "CharityPage", urlPatterns = {"/CharityPage"})
public class CharityPage extends HttpServlet {
   
    /* Debug Mechinism */
    private final boolean DEBUG_ON = true;
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
     * Using the charity name parameter, this method creates a charity object using 
     * the Charity.java class, this is used when setting session attributes and
     * dynamically creating the html nav element
     * 
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
        
        session = request.getSession(true);
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
            
            if(DEBUG_ON){
                System.out.println("Key: " + key);
                System.out.println("Value: " + value);
            }
        }
        
        
        String servletContext = request.getServletContext().getRealPath("/");
        String jsonPath = servletContext + CHARITIES_DIR + DirectoryManager.toLowerCaseAndTrim(charityJSONObj.get("charity_name").toString()) + JSON_DIR + CHARITY_FILE_NAME ;
        Charity charity = Charity.parseJSONtoCharityObj(jsonPath);
        
        session.setAttribute("charityName", charity.getName());
        session.setAttribute("viewing_homepage", true);
        
         try (PrintWriter out = response.getWriter()) {
            printNav(charity, out);
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
     * Dynamically builds the index.html nav element for a specific charity
     * 
     * @param charity
     * @param out 
     */
    private void printNav(Charity charity, PrintWriter out ){
        
        String charityName = charity.getName();
        String charityLogo = charity.getLogo();
        
        charityName = DirectoryManager.toLowerCaseAndTrim(charityName);
        
        out.println("<div id='nav'>");
        out.println("<img src=./uploads/" + charityLogo +" />");
        
        out.println("<p>" + charity.getDescription() + "</p>");
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
        
        if(session.getAttribute("authorised") != null){
            out.println("<div id=\"faq\">");
            out.println("<p><a href=\"../../Dashboard\">Dashboard</a></p>");
            out.println("</div>");
        }
        out.println("</div>");  
    }

}
