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
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kealan
 */

@WebServlet(name = "Analytics", urlPatterns = {"/Analytics"})
public class Analytics extends HttpServlet {

    private HttpSession session;
    private int charity_id;
    private ResultSet donationsResultSet = null;
    private DBConnect dbConnect;
    private Connection connection;
    private PreparedStatement donationsStatement;
    private String donationsQuery;
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
        
        if(session.getAttribute("authorised") != null) {
            charityName = (String)session.getAttribute("charityName");
            String id = (String)session.getAttribute("charity_id");
            charity_id = Integer.parseInt(id);
            dbConnect = new DBConnect();
            connection = dbConnect.getConnection();
            donationsStatement = null;
            donationsQuery = "SELECT amount, date FROM donations WHERE charity_id = ?";
        
            try {
                donationsStatement = connection.prepareStatement(donationsQuery);
                donationsStatement.setInt(1, charity_id);
                donationsResultSet = donationsStatement.executeQuery();
                //donationsStatement.close();
                //donationsResultSet.close();
            } catch(SQLException exception) {
                exception.printStackTrace();
                System.err.println("Unable to retreive donations");
            }
            
            System.out.println("Donations Query Successful");
        
            try (PrintWriter out = response.getWriter()) {
                out.println("<div id=\"donations_table\">");
                out.println("<table align=\"center\">");
                out.println("<tr>");
                out.println("<th>Date</th>");
                out.println("<th>Amount</th>");
                out.println("</tr>");
                
                int totalDonations = 0;
                try {
                    while(donationsResultSet.next()) {
                        int amount = donationsResultSet.getInt("amount");
                        totalDonations += amount;
                        Date date = donationsResultSet.getDate("date");
                        out.println("<tr>");
                        out.println("<td>" + date + "</td>");
                        out.println("<td>" + amount + "</td>");
                        out.println("</tr>");
                    }
                } catch(SQLException exception) {
                    exception.printStackTrace();
                    System.err.println("error displaying results");
                }
                
                out.println("<tr>");
                out.println("<th>Total</th><td>" + totalDonations + "</td>");
                out.println("</tr>");
                out.println("</table>");
                out.println("</div>");
            }
        } else {
            response.sendRedirect("Login");
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
