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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        //Redirect to Homepage
        response.sendRedirect("Homepage");
   
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
        
        String encodedAmount    = request.getParameter("amount");
        String encodedCharityID = request.getParameter("charity_id");
        String articleID   = ("undefined".equals(request.getParameter("article_id").toString())) ? "0" : request.getParameter("article_id");
        
        byte[] decodedAmountBytes    = Base64.decodeBase64(encodedAmount);
        byte[] decodedCharityIDBytes = Base64.decodeBase64(encodedCharityID);
        
        System.out.println(decodedAmountBytes.toString());
        
        String amountString = new String(decodedAmountBytes);
        int amount = 0;
        if(amountString.contains(".")){
            amount = (int)Math.round(Double.valueOf(amountString));
        }else {
            amount = Integer.valueOf(amountString);
        }
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
        
        String selectCharityName = "SELECT name "
                + "FROM charities "
                + "WHERE id = ?";
        
        String charityName = "";
        try(PreparedStatement selectCharityNameStatement = connection.prepareStatement(selectCharityName)){
            selectCharityNameStatement.setInt(1, charityID);
            ResultSet charityNameResultSet = selectCharityNameStatement.executeQuery();
            
            if(charityNameResultSet.first()){
                charityName = charityNameResultSet.getString(1);
            }
        }catch(SQLException ex){
            System.err.println(this.getClass().getName() + " : Select Statement failed, no name matches charity_id");
            ex.printStackTrace();
        }
        
        //If it's a Sponsorship donation
        if(!"0".equals(articleID)){
            String insertSponsorship = "INSERT INTO sponsorships (charity_id, amount, article_id) "
                    + "VALUES (?,?,?)";
            
            try(PreparedStatement insertSponsorshipStatement = connection.prepareStatement(insertSponsorship)){
            insertSponsorshipStatement.setInt(1, charityID);
            insertSponsorshipStatement.setInt(2, amount);
            insertSponsorshipStatement.setInt(3, Integer.valueOf(articleID));
            insertSponsorshipStatement.executeUpdate();

            }catch(SQLException ex){
                System.err.println(this.getClass().getName() + " : Insert Sponsorship Statement failed ");
                ex.printStackTrace();
            }
            
        }
        
        
        //Close the Connection
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DonationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if("".equals(charityName) ){
            //Redirect to Homepage
            response.sendRedirect("Homepage");
        }else{
            //Redirect to Charity's homepage with a Thank
            response.sendRedirect( request.getContextPath() + "/charities/" + DirectoryManager.toLowerCaseAndTrim(charityName) + "/index.html");
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
}
    


