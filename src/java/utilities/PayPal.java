/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import org.apache.commons.codec.binary.Base64;
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
 *
 * @author kpmmmurphy
 */
@WebServlet(name = "PayPal", urlPatterns = {"/PayPal"})
public class PayPal extends HttpServlet {
    
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
        
        /* The root directory of the project - In this case "/cs3305"  */
        String servletContext = request.getContextPath();
        /* The servlet name - In this case "/Login" */
        String servletPath = request.getServletPath();
        
        try (PrintWriter out = response.getWriter()) {
            
             
             out.println("<form method='POST' id='donationForm' '>");
             
             out.println("<h1>Donate</h1>");
             out.println("<label for='amount'>Amount:</label><input type='number' value='10' name='amount' min='5' max='10000'/><br/>");
             out.println("<label for='currency'>Type:</label> <select name='currency'>"
                        + "         <option value='EUR' selected>Euro</option>"
                        + "         <option value='USD'>US Dollar</option>"
                        + "         <option value='GBP'>GBP</option>"
                        + "         <option value='CNY'>Chinese Yuan</option>"
                        + "     </select><br />");
             out.println("<input type=\"submit\" value=\"Submit\" id='donationSubmit' >");
             
             out.println("</form>");
            
            
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
         
        try (PrintWriter out = response.getWriter()) {
            buildPayPalForm(request, out);  
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
    
    public static void buildPayPalForm(HttpServletRequest request, PrintWriter out ){
        
        String merchantIDorEmail = "";
        String returnUrl       = "http://localhost:8080/cs3305/DonationManager";
        //String callbackUrl         = "http://localhost:8080/cs3305/HomePage.html";
        String cancelReturnUrl         = "http://localhost:8080/cs3305/HomePage.html";
        String amount            = "10.00";
        String currency          = "EUR";
        
        HttpSession session = request.getSession(true);
        String charityName =  (String)session.getAttribute("charityName");
        String charityID =  (String)session.getAttribute("charity_id");
        if(DEBUG_ON){
                System.out.println("CharityID :" + charityID);
        }
        
        if(charityID != null && Integer.valueOf(charityID) > 0){
            
            String stringInputAmount = request.getParameter("amount");
            if(DEBUG_ON){
                System.out.println("Submitted Amount :" + stringInputAmount);
            }
            if(stringInputAmount != null && !"".equals(stringInputAmount)){
                int intInputAmount = Integer.valueOf(stringInputAmount);
                if(intInputAmount > 0){
                    amount = String.valueOf(intInputAmount);
                }
            }
            
            String inputCurrency = request.getParameter("currency");
            if(inputCurrency != null && ! "".equals(inputCurrency)){
                if(inputCurrency.equalsIgnoreCase("EUR")){
                    currency = "EUR";
                }else if(inputCurrency.equalsIgnoreCase("USD")){
                    currency = "USD";
                }else if(inputCurrency.equalsIgnoreCase("GBP")){
                    currency = "GBP";
                }else if(inputCurrency.equalsIgnoreCase("CNY")){
                    currency = "CNY";
                }
                
                if(DEBUG_ON){
                    System.out.println("Submitted Currency :" + currency);
                }
            }
            
            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();
            
            String selectPayPalEmail = "SELECT paypal_email "
                                     + "FROM   charities "
                                     + "WHERE  id = ?";
            try {
                PreparedStatement payPalStatement = connection.prepareStatement(selectPayPalEmail);
                payPalStatement.setInt(1, Integer.valueOf(charityID));
                ResultSet payPalResultSet = payPalStatement.executeQuery();
                
                if(payPalResultSet.next()){
                    merchantIDorEmail = payPalResultSet.getString(1);
                }
                
                if(DEBUG_ON){
                        System.out.println("MerchantIDorEmail : " + merchantIDorEmail);
                }
            } catch (SQLException ex) {
                Logger.getLogger(PayPal.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            byte[] encodedAmountinBytes = Base64.encodeBase64(amount.getBytes());
            byte[] encodedCharityIDinBytes = Base64.encodeBase64(charityID.getBytes());
            String encodedAmount = new String(encodedAmountinBytes);
            String encodedCharityID = new String(encodedCharityIDinBytes);
            
            
            if(merchantIDorEmail != null && !"".equals(merchantIDorEmail)){
                              
                returnUrl = returnUrl.concat("?amount=" + encodedAmount).concat("&charity_id=" + encodedCharityID);
                System.out.println(returnUrl);
                
                out.println("<article class='paypal_form'>");                
                out.println("<p>Dontaion Amount : " + amount   + "</p>");
                out.println("<p>Currency        : " + currency + "</p>");
                out.println("<script src='https://www.paypalobjects.com/js/external/paypal-button.min.js?merchant=" + merchantIDorEmail + "' ");
                out.println("data-button='donate' ");
                out.println("data-name='" + charityName + " Donation' ");
                out.println("data-amount='" + amount + "' ");
                out.println("data-currency='" + currency + "' ");
                out.println("data-shipping='0' ");
                out.println("data-tax='0' ");
                out.println("data-rm='2' ");
                out.println("data-cancel_return='" + cancelReturnUrl + "' ");
                //out.println("data-custom='amount=" + encodedAmount + "&charity_id='" + encodedCharityID + " ");
                //out.println("data-callback='" + callbackUrl + "' ");
                out.println("data-return='" + returnUrl + "' ");
                out.println("data-env='sandbox'");
                out.println("></script>");
                out.println("</article>");
            }else{
                out.println("<article class='paypal_form'>");
                out.println("<p>This Charity does not have a PayPal account Setup.</p>");
                out.println("</article>");
            }
        }
    }

}
