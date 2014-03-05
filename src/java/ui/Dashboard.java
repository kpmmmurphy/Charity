package ui;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import database.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        session = request.getSession();
        
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String servletContext = request.getContextPath();
            String servletPath = request.getServletPath();
            String charityName = (String)session.getAttribute("charityName");
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>" + charityName + "'s Dashboard</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/dashboardStyles.css\"/>");
            out.println("<link href='http://fonts.googleapis.com/css?family=Muli' rel='stylesheet' type='text/css' />");
            out.println("<script src='//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js'></script>");
            out.println("<script src='https://www.paypalobjects.com/js/external/paypal-button.min.js'></script>");
            out.println("<script src='javascript/createpost.js'></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"wrapper\">");
            out.println("<h1>" + charityName + "'s Dashboard</h1>");
            out.println("<nav>");
            out.println("<div id=\"logodiv\">");
            out.println("<img src=\"images/concern.png\" id=\"logo\"/>");
            out.println("</div>");
            out.println("<ul>");
            out.println("<li><a href=\"EditStyles\">Edit Styles</a></li>");
            out.println("<li><a href=\"Register\">Edit Details</a></li>");
            out.println("<li><a href=\"Analytics\">Analytics</a></li>");
            out.println("<li><a href=\"FAQ.jsp\">FAQ</a></li>");
            out.println("<li><a href=\"Signout\">Sign Out</a></li>");
            out.println("</ul>");
            out.println("</nav>");
            out.println("<section id=\"main\">");
            out.println("<a onclick='getCreatePost()'>");
            out.println("<article class=\"option\">");
            out.println("<h2>Create a Post</h2>");
            out.println("<h3>Create a new post and upload an image</h3>");
            out.println("</article>");
            out.println("</a>");
            out.println("<a onclick='getApprovePost()'>");
            out.println("<article class=\"option\">");
            out.println("<h2>Approve a Post</h2>");
            out.println("<h3>Approve a user submitted post</h3>");
            out.println("</article>");
            out.println("</a>");
            out.println("<a onclick='getListPosts()'>");
            out.println("<article class=\"option\">");
            out.println("<h2>Edit a Post</h2>");
            out.println("<h3>Edit one of your posts</h3>");
            out.println("</article>");
            out.println("</a>");
            out.println("</section>");
            out.println("<footer>");
            out.println("<small>CMS 2014 - Team 9</small>");
            out.println("</footer>");
            out.println("</div>");
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
