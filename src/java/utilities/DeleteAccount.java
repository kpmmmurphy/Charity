/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import auth.Password;
import database.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 *
 * @author kealan
 * @version 1.0
 * @date 09/03/14
 */
@WebServlet(name ="DeleteAccount", urlPatterns = {"/DeleteAccount"})
public class DeleteAccount extends HttpServlet {

    private HttpSession session;
    /* user supplied username */
    private String username;
    /* user supplied password */
    private String password;
    /* user supplied retyped password */
    private String retypePassword;
    /* used for indicating if the user supplied username matches the one in the DB */
    private boolean unenteredInput;
    /* used for indicating if the user supplied password matches the one in the DB*/
    private boolean passwordMismatch;
    /* DEGUB mechanism */
    private final boolean DEBUG_ON = true;
    
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
        
        /* if the user is not logged in redirect to the login page*/
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        }
        
        /* The root directory of the project - In this case "/cs3305"  */
        String servletContext = request.getContextPath();
        /* The servlet name - In this case "/DeleteAccount" */
        String servletPath = request.getServletPath();
        /* retreive the user's username from the session */
        username = (String)session.getAttribute("username");
        
        /* ouptut the form with any necessary error messages */
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Delete Account</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"wrapper\">");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<h1>Delete Account</h1>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"username\">Username:</label><input type='text' name='username' value=" + username + " readonly='true'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"password\">Password:</label><input type='password' name='password' placeholder='password'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"retypePassword\">Retype Password:</label><input type='password' name='retypePassword' placeholder='Retype Password'> <br />");
            out.println("</p>");
            out.println("<p class=\"clearfix\">");
            out.println("<input type=\"submit\" value=\"Delete Account\">");
            out.println("</p>");
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if(passwordMismatch){
                out.println("<p>Password mismatch, please try again.</p>");
            }
            out.println("</form>");
            out.println("</div>");
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
        
        //Initialize content sensitive help booleans
        unenteredInput = false;
        passwordMismatch = false;
        
        //Get the user input
        password = request.getParameter("password");
        retypePassword = request.getParameter("retypePassword");
        
        if(DEBUG_ON) {
            System.out.println("username: " + username);
            System.out.println("password: " + password);
            System.out.println("retype password: " + retypePassword);
        }
        
        //Check that all input was entered
        if((password == null || "".equals(password))
                || (retypePassword == null || "".equals(retypePassword))) {
            
            unenteredInput = true;
            processRequest(request, response);
        } else {
            if(DEBUG_ON) {
                System.out.println("All input entered");
            }
            
            //OWASP sanitizer to pretect against XSS attacks
            HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
            PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();
            
            //Sanitizes input
            String cleanUsername = stripAllTagsPolicy.sanitize(username);
            String cleanPassword = stripAllTagsPolicy.sanitize(password);
            String cleanRetypePassword = stripAllTagsPolicy.sanitize(retypePassword);
            
            //Check that the user supplied pasword matches the user supplied retype password
            if(! cleanPassword.equals(cleanRetypePassword)) {
                passwordMismatch = true;
                processRequest(request, response);
            } else {
                //Stores all clean input in HashMap
                HashMap<String, String> cleanInputMap = new HashMap<String, String>();
                cleanInputMap.put("username", cleanUsername);
                cleanInputMap.put("password", cleanPassword);
                cleanInputMap.put("retypePassword", cleanRetypePassword);
                
                //Connect to Database
                DBConnect dbConnect   = new DBConnect();
                Connection connection = dbConnect.getConnection();
                
                //Prepared statement to check for matching passwords
                PreparedStatement passwordStatement = null;
                //PreparedStatement to retrieve salt value belonging to the supplied username
                PreparedStatement saltStatement = null;
                
                //Result set to for passwordStatement
                ResultSet passwordResultSet;
                //Result set for saltStatement
                ResultSet saltResultSet;
                
                /* Query for selecting password*/
                String passwordQuery = "SELECT password"
                        + " FROM charities"
                        + " WHERE username = ?";
                
                /* Query for selecting salt value*/
                String saltQuery = "SELECT salt"
                        + " FROM charities"
                        + " WHERE username = ?";
                
                //String to hold the salt value
                String salt = "";
                
                try {
                    saltStatement = connection.prepareStatement(saltQuery);
                    saltStatement.setString(1, username);
                    saltResultSet = saltStatement.executeQuery();
                    
                    if(saltResultSet.next()) {
                        salt = saltResultSet.getString(1);
                    }
                } catch(SQLException exception) {
                    System.err.println("Unable to retreive salt value");
                    exception.printStackTrace();
                }
                
                if(DEBUG_ON){
                    System.out.println("Salt value from DB: " + salt);
                }
                
                //Creates a new Password object using the user supplied password and salt value gotten from the DB
                Password passwordToBeHashed = new Password(cleanInputMap.get("password"), salt);
                //Gets the hashed value of the password and salt
                String   hashedPassword     = passwordToBeHashed.getHashedPassword();
                cleanInputMap.put("hashedPassword", hashedPassword);
                
                if(DEBUG_ON){
                    System.out.println("Hashed Password and Salt: " + hashedPassword);
                }
                
                String storedPassword = "";
                
                try {
                    passwordStatement = connection.prepareStatement(passwordQuery);
                    passwordStatement.setString(1, username);
                    passwordResultSet = passwordStatement.executeQuery();
                    
                    if(passwordResultSet.next()) {
                        storedPassword = passwordResultSet.getString("password");
                    }
                    
                } catch(SQLException exception) {
                    System.err.println("Unable to retreive passoword");
                    exception.printStackTrace();
                }
                
                if(! cleanInputMap.get("hashedPassword").equals(storedPassword)) {
                    passwordMismatch = true;
                    processRequest(request, response);
                } else {
                    //Retreive the charities ID from the session
                    String charity_id = (String)session.getAttribute("charity_id");
                    int id = Integer.parseInt(charity_id);
                    
                    //PreparedStatement to delete entries in multiple tables
                    PreparedStatement deleteStatement = null;
                    //PreparedStatement to delete entries in the charities table
                    PreparedStatement charitiesStatement = null;
                    
                    //Statement to delete entries in the charities table
                    String charitiesQuery = "DELETE FROM charities"
                            + " WHERE id = ?";
                    
                    //Statement to delete entries in multiple tables
                    String deleteQuery = "DELETE FROM $tableName"
                            + " WHERE charity_id = ?";
                    
                    //String array to hold the name of multiple tables
                    String[] tables = {"donations", "sponsorships", "lost_and_found", "articles"};
                    
                    try {
                        for (String table : tables) {
                            String query = deleteQuery.replace("$tableName", table);
                            deleteStatement = connection.prepareStatement(query);
                            //deleteStatement.setString(2, charity_id);
                            deleteStatement.setInt(1, id);
                            deleteStatement.executeUpdate();
                        }
                        
                        charitiesStatement = connection.prepareStatement(charitiesQuery);
                        charitiesStatement.setInt(1, id);
                        charitiesStatement.executeUpdate();
                        
                    } catch(SQLException exception) {
                        System.err.println("Unable to delete account");
                        exception.printStackTrace();
                    }
                    
                    session.invalidate();
                    response.sendRedirect("AccountDeleted.html");
                }
            }
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
