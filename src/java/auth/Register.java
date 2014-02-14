/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package auth;

import database.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles the entering of the Charity's address, telephone number, domain name,
 * and their  PayPal account email address.
 * 
 * @author  Kevin Murphy
 * @version 1.0
 * @date    12/2/14
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private final boolean DEBUG_ON = true;
    
    private boolean authorised = false;
    
    private String address     = "";
    private String telephone   = "";
    private String domain      = "";
    private String payPalEmail = "";
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
        
        session    = request.getSession(false);
        //authorised = ((boolean)session.getAttribute("authorised"));
        
        if(session != null){
            String servletContext = request.getContextPath();
            String servletPath    = request.getServletPath();
            
            initialiseDetails();

            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Edit Charity Info</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Edit Details of your Charity</h1>");
                out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
                out.println("<fieldset>");
                out.println("<legend>Edit Details</legend>");
                out.println("Address: <input type='text' name='address' value='" + address + "' placeholder='Address' maxlength='255' size='50'> <br />");
                out.println("Telephone: <input type='text' name='telephone' value='" + telephone + "' placeholder='Telephone' maxlength='12'> <br />");
                out.println("Domain name: <input type='text' name='domain' value='" + domain +"'placeholder='Domain Name'> <br />");
                out.println("PayPal Email: <input type='paypalemail' name='password' value='" + payPalEmail + "'placeholder='Password'> <br />");
                out.println("<input type=\"submit\" value=\"Submit\">");
                out.println("</fieldset>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            }
        }else{
            //Redirect to basic registration of info page
            response.sendRedirect("./Signup");
        }
        
    }
    
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
        
        //read in all paramaters from request
        address     = request.getParameter("address");
        telephone   = request.getParameter("telephone");
        domain      = request.getParameter("domain");
        payPalEmail = request.getParameter("paypalemail");
        
        if(DEBUG_ON){
            System.out.println("Address:"     + address);
            System.out.println("Telephone:"   + telephone);
            System.out.println("Domain:"      + domain);
            System.out.println("payPalEmail:" + payPalEmail);
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

    private void initialiseDetails(){
        String charityName = (String)session.getAttribute("charityName");
        if(DEBUG_ON){
            System.out.println(charityName);
        }
        
        //Connect to Database
        DBConnect dbConnect   = new DBConnect();
        try (Connection connection = dbConnect.getConnection()) {
            PreparedStatement selectStatement;
            
            /* Checks for username duplicates */
            String selectQuery = "SELECT * "
                    + "FROM charities "
                    + "WHERE name = ?";
            
            //PreparedStatement for username duplication checking
            selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, charityName);
            ResultSet charityResultSet = selectStatement.executeQuery();

            charityResultSet.next();
            address     = (charityResultSet.getString(7) != null) ? charityResultSet.getString(7) : "";
            domain      = (charityResultSet.getString(8) != null) ? charityResultSet.getString(8) : "";
            telephone   = (charityResultSet.getString(9) != null) ? charityResultSet.getString(9) : "";
            payPalEmail = (charityResultSet.getString(10) != null) ? charityResultSet.getString(10) : "";

            if(DEBUG_ON){
                System.out.println("Address from DB:"     + address);
                System.out.println("Domain from DB:"      + domain);
                System.out.println("Telephone from DB:"   + telephone);
                System.out.println("PayPalEmail from DB:" + payPalEmail);
            }
                
        }catch(SQLException e){
            System.err.println("Error connceting with Database to retrieve Charity Detials");
            e.printStackTrace();
        }
    }
}
