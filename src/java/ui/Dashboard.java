package ui;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Charity;
import utilities.DirectoryManager;
/**
 *
 * @author kealan
 * @version 1.1
 * @date 16/2/14
 */
@WebServlet(name = "Dashboard", urlPatterns = {"/Dashboard"})
public class Dashboard extends HttpServlet {

    private HttpSession session;
       
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
        session.setAttribute("viewing_homepage", false);
        
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String servletContext = request.getContextPath();
            String servletPath = request.getServletPath();
            String charityName = (String)session.getAttribute("charityName");
            
            Charity charity = Charity.parseJSONtoCharityObj(request);
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>" + charityName + "'s Dashboard</title>");
            //out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/normalize.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/dashboardStyles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/post_styles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/submit_post.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/edit_styles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/ajax_msgs.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/oauth.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/faq.css\"/>");
            out.println("<script src='javascript/jquery/jquery-1.11.0.js'></script>");
            out.println("<script src='javascript/postfunctions.js'></script>");
            out.println("<script src='javascript/dashboard.js'></script>");
            out.println("</head>");
            out.println("<body onload='initDashboard()'>");
            out.println("<div id=\"wrapper\">");
            out.println("<nav>");
            out.println("<div id=\"logodiv\">");
            out.println("<img src=\"" + Charity.getCharityUploadsPath(request) + charity.getLogo() + "\" id=\"logo\"/>");
            out.println("</div>");
            out.println("<ul>");
            out.println("<li><a href='charities/" + DirectoryManager.toLowerCaseAndTrim(charity.getName()) + "/index.html'>Your Page</a></li>");
            out.println("<hr />");
            out.println("<li><a onclick='initDashboard()'>Home</a></li>");
            out.println("<li><a onclick='getEditStyles()'>Edit Styles</a></li>");
            out.println("<li><a onclick='getEditDetails()'>Edit Details</a></li>");
            out.println("<li><a onclick='getAnalytics()'>Analytics</a></li>");
            out.println("<li><a onclick='getDashboardFaq()'>FAQ</a></li>");
            out.println("<li><a href=\"Signout\">Sign Out</a></li>");
            
            out.println("</ul>");
            out.println("<footer>");
            out.println("<small>CMS 2014 - Team 9</small>");
            out.println("</footer>");
            out.println("</nav>");
            
            out.println("</div>");
            out.println("<section id=\"main\">");
            out.println("</section>");
            out.println("</body>");
            out.println("</html>");
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

}
