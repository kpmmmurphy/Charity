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
 * @version 1.3
 * @date 06/03/14
 */
@WebServlet(name ="ChangePassword", urlPatterns = {"/ChangePassword"})
public class ChangePassword extends HttpServlet {
    
    /* user supplied username*/
    private String username;
    /* user supplied generated password*/
    private String generatedPassword;
    /* user supplied new password*/
    private String newPassword;
    /* user supplied retyped password*/
    private String repeatPassword;
    /* for indicateing if the user supplied username matches one in the DB*/
    private boolean usernameMismatch;
    /* for indicateing if the user supplied password matches the retyped password*/
    private boolean passwordMismatch;
    /* for indicateing if the user supplied generated password matches one in the DB*/
    private boolean generatedPasswordMismatch;
    /* for indicateing if all input was entered */
    private boolean unenteredInput;
    /* for identifying the user*/
    private HttpSession session;
    /* Debug mechinism */
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
        
        /* The root directory of the project - In this case "/Charity"  */
        String servletContext = request.getContextPath();
        /* The servlet name - In this case "/Login" */
        String servletPath = request.getServletPath();
        
        session = request.getSession();
        
        boolean isForgottenPassword = (request.getParameter("forgotten_password") != null);
        
        try (PrintWriter out = response.getWriter()) {
            if(isForgottenPassword){
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Login</title>");
                out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div id=\"wrapper\">");
            }
            
            out.println("<form method='POST' id='change_password_form'>");
            out.println("<h1>Change Password</h1>");
            out.println("</p>");
            
            if(session.getAttribute("authorised") == null) {
                out.println("<p class=\"float\">");
                out.println("<label for=\"username\">Username:</label><input type='text' name='username' placeholder='username'> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"generatedPassword\">Generated Password:</label><input type='text' name='generatedPassword' placeholder='generatedPassword'> <br />");
                out.println("</p>");
            } else {
                username = (String)session.getAttribute("username");
                
                out.println("<p class=\"float\">");
                out.println("<label for=\"username\">Username:</label><input type='text' name='username' value=" + username + " readonly='true'> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"generatedPassword\">Currnet Password:</label><input type='password' name='generatedPassword' placeholder='Currnet Password'> <br />");
                out.println("</p>");
            }
            
            out.println("<p class=\"float\">");
            out.println("<label for=\"newPassword\"> New Password:</label><input type='password' name='newPassword' placeholder='newPassword'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"repeatPassword\">Retype Password:</label><input type='password' name='repeatPassword' placeholder='repeatPassword'> <br />");
            out.println("</p>");
            out.println("<p class=\"clearfix\">");
            out.println("<input type=\"submit\" value=\"Submit\" onclick='return ajaxChangePassword()'>");
            out.println("</p>");
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if(usernameMismatch) {
                out.println("<p>Username mismatch</p>");
            }
            if(generatedPasswordMismatch || passwordMismatch ){
                out.println("<p>Generated Password or Password mismatch, please try again.</p>");
            }
            out.println("</form>");
            out.println("</div>");
            
            if(isForgottenPassword){ 
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
        
        //Initialize content sensitive help booleans
        usernameMismatch = false;
        unenteredInput = false;
        passwordMismatch = false;
        generatedPasswordMismatch = false;
        
        //Get the user submitted data
        username = request.getParameter("username");
        generatedPassword = request.getParameter("generatedPassword");
        newPassword = request.getParameter("newPassword");
        repeatPassword = request.getParameter("repeatPassword");
        
        if(DEBUG_ON) {
            System.out.println("Username: " + username);
            System.out.println("Generated Password: " + generatedPassword);
            System.out.println("New Password: " + newPassword);
            System.out.println("Repeat Password: " + repeatPassword);
        }
        
        //Checks if all input was entered
        if((generatedPassword == null  || "".equals(generatedPassword))
          || (newPassword ==  null || "".equals(newPassword))
          || (repeatPassword == null || "".equals(newPassword))
          || (username == null || "".equals(username))) {
            
            unenteredInput = true;
            processRequest(request, response);
        } else {
            if(DEBUG_ON) {
                System.out.println("DEBUG: All input entered");
            }
            
            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();
            
            //Check if the new password equals the repeat passowrd
            if(! newPassword.equals(repeatPassword)) {
                passwordMismatch = true;
                processRequest(request, response);
            }
            
            //Store user supplied values in a hashmap
            HashMap<String, String> unfilteredInputMap = new HashMap<String, String>();
            unfilteredInputMap.put("newPassword", newPassword);
            unfilteredInputMap.put("repeatPassword", repeatPassword);
            unfilteredInputMap.put("generatedPassword", generatedPassword);
            unfilteredInputMap.put("username", username);
            
            //OWASP sanitizer to pretect against XSS attacks
            HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
            PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();
            
            //Sanitizes input
            String cleanUsername = stripAllTagsPolicy.sanitize(username);
            String cleanGeneratedPassword = stripAllTagsPolicy.sanitize(generatedPassword);
            String cleanNewPassword = stripAllTagsPolicy.sanitize(newPassword);
            String cleanRepeatPassword = stripAllTagsPolicy.sanitize(repeatPassword);
            
            //Stores all sanitized input in HashMap
            HashMap<String, String> cleanInputMap = new HashMap<String, String>();
            cleanInputMap.put("cleanUsername", cleanUsername);
            cleanInputMap.put("newPassword", cleanNewPassword);
            cleanInputMap.put("repeatPassword", cleanRepeatPassword);
            
            if(session.getAttribute("authorised") != null) {
                //PreparedStatement to retrieve salt value belonging to the supplied username
                PreparedStatement saltStatement = null;
                
                //Result set for saltStatement
                ResultSet saltResultSet;
                
                /* Query for selecting salt value */
                String saltQuery = "SELECT salt "
                                 + "FROM   charities "
                                 + "WHERE  username = ?";
                
                String salt = "";
                
                try{
                    saltStatement = connection.prepareStatement(saltQuery);
                    saltStatement.setString(1, cleanInputMap.get("cleanUsername"));
                    saltResultSet = saltStatement.executeQuery();
                    
                    if(saltResultSet.next()){
                        salt = saltResultSet.getString(1);
                    }
                    
                    saltStatement.close();
                    saltResultSet.close();
                } catch(SQLException exception) {
                    exception.printStackTrace();
                    System.err.println("Error retreiving salt!");
                }
                
                if(DEBUG_ON) {
                    System.err.println("Salt value from DB: " + salt);
                }
                
                //Creates a new Password object using the user supplied password and salt value gotten from the DB
                Password passwordToBeHashed = new Password(cleanGeneratedPassword, salt);
                //Gets the hashed value of the password and salt
                String   hashedPassword     = passwordToBeHashed.getHashedPassword();
                cleanInputMap.put("cleanGeneratedPassword", hashedPassword);
                
            } else {
                cleanInputMap.put("cleanGeneratedPassword", cleanGeneratedPassword);
            }
            
            //PreparedStatement to retrieve the generated password belonging to the supplied username
            PreparedStatement generatedPasswordStatement = null;
            //PreparedStatement to update password belonging to the supplied uername
            PreparedStatement updatePasswordStatement = null;
            //PreparedStatemet to update the salt value belonging to the supplied username
            PreparedStatement updateSaltStatement = null;
            
            //Result set to for usernameStatement
            ResultSet generatedPasswordResultSet = null;
            
            /* Query for selecting generated password value */
            String generatedPasswordQuery = "SELECT password "
                             + "FROM charities "
                             + "WHERE username = ?";
            
            /* Statement for updating password*/
            String updatePasswordQuery = "UPDATE charities "
                            + "SET password = ? "
                            + "WHERE username = ?";
            
            /* Statement for updating salt value*/
            String updateSaltQuery = "UPDATE charities "
                            + "SET salt = ? "
                            + "WHERE username = ?";
            
            try {
                //Retreive the generated password from the DB
                generatedPasswordStatement = connection.prepareStatement(generatedPasswordQuery);
                generatedPasswordStatement.setString(1, cleanInputMap.get("cleanUsername"));
                generatedPasswordResultSet = generatedPasswordStatement.executeQuery();
                
                if(! generatedPasswordResultSet.next()) {
                    generatedPasswordMismatch = true;
                }
                
                //Check if user supplied generated passeword equals on in the DB
                if(! generatedPasswordResultSet.getString("password").equals(cleanInputMap.get("cleanGeneratedPassword"))) {
                    generatedPasswordMismatch = true;
                }
            } catch(SQLException exception) {
                exception.printStackTrace();
                System.err.println("Unable to retreive password");
            }
            
            if(generatedPasswordMismatch) {
                processRequest(request, response);
            } else {
                try {
                    
                    //Hash password
                    Password passwordToBeHashed = new Password(cleanInputMap.get("newPassword"));
                    String hashedPassword       = passwordToBeHashed.getHashedPassword();
                    String saltValue            = passwordToBeHashed.getSaltValue();
                        
                    //place values into HashMap
                    cleanInputMap.put("hashedPassword", hashedPassword);
                    cleanInputMap.put("salt", saltValue);
                    
                    //update the database with the new hashed passowrd
                    updatePasswordStatement = connection.prepareStatement(updatePasswordQuery);
                    updatePasswordStatement.setString(1, cleanInputMap.get("hashedPassword"));
                    updatePasswordStatement.setString(2, cleanInputMap.get("cleanUsername"));
                    updatePasswordStatement.executeUpdate();
                    
                    //update the database with the new salt value
                    updateSaltStatement = connection.prepareStatement(updateSaltQuery);
                    updateSaltStatement.setString(1, saltValue);
                    updateSaltStatement.setString(2, cleanInputMap.get("cleanUsername"));
                    updateSaltStatement.executeUpdate();
                    
                } catch(SQLException exception) {
                    exception.printStackTrace();
                    System.err.println("Unable to update password!");
                }
                
                //Redirect to the Login page
                response.sendRedirect("Login");
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