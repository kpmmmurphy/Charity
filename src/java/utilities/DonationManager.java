/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import database.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author kpmmmurphy
 */
@WebServlet(name = "DonationManager", urlPatterns = {"/DonationManager"})
public class DonationManager extends HttpServlet {
    
    private static final boolean DEBUG_ON = true;

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
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DonationManager</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DonationManager at " + request.getContextPath() + "</h1>");
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
        System.out.println("Posting: "    );
        String encodedAmount    = request.getParameter("amount");
        String encodedCharityID = request.getParameter("charity_id");
        
        byte[] decodedAmountBytes    = Base64.decodeBase64(encodedAmount);
        byte[] decodedCharityIDBytes = Base64.decodeBase64(encodedCharityID);
        
        System.out.println(decodedAmountBytes.toString());
        
        int amount    = Integer.valueOf(new String(decodedAmountBytes));
        int charityID = Integer.valueOf(new String (decodedCharityIDBytes));
        
        if(DEBUG_ON){
            System.out.println("Amount: "    + amount);
            System.out.println("Charity ID: " + charityID);
        }
        
        //Connect to Database
        DBConnect dbConnect   = new DBConnect();
        Connection connection = dbConnect.getConnection();
        
        String insertNewDonation= "INSERT INTO donations (charity_id, amount)"
                + "VALUES (?,?)";
        
        try(PreparedStatement insertDonationStatement = connection.prepareStatement(insertNewDonation)){
            insertDonationStatement.setInt(1, charityID);
            insertDonationStatement.setInt(2, amount);
            insertDonationStatement.executeUpdate();
        }catch(SQLException e){
            System.err.println(this.getClass().getName() + " : INSERT Statement failed, either charity_id or amounr entered in error");
            e.printStackTrace();
        }
        
        //Redirect to Dashboard
        response.sendRedirect("HomePage.html");
        
        
        
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
    


