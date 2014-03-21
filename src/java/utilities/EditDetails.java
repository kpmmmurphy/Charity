/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kealan
 * @version 1.0
 * @date 09/03/14
 */
@WebServlet(name ="EditDetails", urlPatterns = {"/EditDetails"})
public class EditDetails extends HttpServlet {

    private HttpSession session;
    private String charityName;
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
        }
        
        charityName = (String)session.getAttribute("charityName");
        
        try (PrintWriter out = response.getWriter()) {
            String mainSection = "#main"; 
            out.println("<a onclick=\"getRegister('"+ mainSection +"', false, false)\">");
            out.println("<article class=\"option\">");
            out.println("<h2>Edit you Charities Details</h2>");
            out.println("<h3>Change address, telephone, logo, etc</h3>");
            out.println("</article>");
            out.println("</a>");
            out.println("<a onclick='getChangePassword()' >");
            out.println("<article class=\"option\">");
            out.println("<h2>Change Password</h2>");
            out.println("<h3>Change your current password</h3>");
            out.println("</article>");
            out.println("</a>");
            out.println("<a href=\"DeleteAccount\">");
            out.println("<article class=\"option\">");
            out.println("<h2>Delete your account</h2>");
            out.println("<h3>Remove your account and charity page</h3>");
            out.println("</article>");
            out.println("</a>");
            /*
            out.println("<footer>");
            out.println("<small>CMS 2014 - Team 9</small>");
            out.println("</footer>");
            */
            
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
