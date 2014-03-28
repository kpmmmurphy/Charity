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
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import utilities.GoogleMail;

/**
 * Allows the charity admin to reset their password in the database by supplying their
 * original password and their newly chosen password.
 * 
 
 * @author Kealan Smyth
 * @version 1.0
 * @date 6/3/14
 */
@WebServlet(name ="ForgotPassword", urlPatterns = {"/ForgotPassword"})
public class ForgotPassword extends HttpServlet {

    /* Debug mechinism */
    private final boolean DEBUG_ON = true;    
    /* user supplied username */
    private String username;
    /* user supplied email */
    private String email;    
    /* for indicateing if all input was entered */
    private boolean unenteredInput;
    /* for indicateing if the user supplied username matches one in the DB*/
    private boolean usernameMismatch;
    /* for indicateing if the user supplied email matches the one in the DB */
    private boolean emailMismatch;
    
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
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Forgot Password</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"wrapper\">");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<h1>Login!</h1>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"username\">Username:</label><input type='text' name='username' placeholder='username'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"email\">Email:</label><input type='email' name='email' placeholder='email'> <br />");
            out.println("</p>");
            out.println("<p class=\"clearfix\">");
            out.println("<input type=\"submit\" value=\"Submit\">");
            out.println("</p>");
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if(usernameMismatch || emailMismatch ){
                out.println("<p>Username or Email mismatch, please try again.</p>");
            }
            out.println("</form>");
            out.println("<footer>");
            out.println("<small>&copy;CMS - Team9 - 2014</small>");
            out.println("</footer>");
            out.println("</div>");
            out.println("<div id=\"faq\">");
            out.println("<nav>");
            out.println("<li><a href=\"gateway.html\">Home</a></li>");
            out.println("<li><a href=\"\">FAQ</a></li>");
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
        
        //Initilize context sensitive help booleans
        unenteredInput   = false;
        usernameMismatch = false;
        emailMismatch = false;
        
        //Get the user submitted parameter
        username = request.getParameter("username");
        email = request.getParameter("email");
        
        if(DEBUG_ON) {
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
        }
        
        //Checks if all input was entered
        if((username == null  || "".equals(username))
          || (email ==  null || "".equals(email))) {
            
            unenteredInput = true;
            processRequest(request, response);
        } else {
            if(DEBUG_ON) {
                System.out.println("DEBUG: All input entered");
            }
            
            //HashMap to house all unfiltered input
            HashMap<String, String> unfilteredInputMap = new HashMap<String, String>();
            unfilteredInputMap.put("username", username);
            unfilteredInputMap.put("email", email);
            
            //OWASP sanitizer to pretect against XSS attacks
            HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
            PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();
            
            //Sanitizes input
            String cleanUsername = stripAllTagsPolicy.sanitize(username);
            
            //Stores all clean input in HashMap
            HashMap<String, String> cleanInputMap = new HashMap<String, String>();
            cleanInputMap.put("username", cleanUsername);
            cleanInputMap.put("email", email);
            
            //ResultSet to for usernameStatement
            ResultSet usernameResultSet = null;
            //ResultSet for emailStatement
            ResultSet emailResultSet = null;
            
            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();
            
            //PreparedStatement to check for matching username
            PreparedStatement usernameStatement = null;
            //PreparedStatement to retrieve email belonging to the supplied username
            PreparedStatement emailStatement = null;
            //PreparedStatement to update password belonging to the supplied uername
            PreparedStatement updatePasswordStatement = null;
            
            /* Query for selecting username */
            String usernameQuery = "SELECT username "
                                 + "FROM charities "
                                 + "WHERE username = ?";
            
            /* Query for selecting salt value */
            String emailQuery = "SELECT email "
                             + "FROM   charities "
                             + "WHERE  username = ?";
            
            /* Query for updating password*/
            String updatePasswordQuery = "UPDATE charities "
                            + "SET password = ? "
                            + "WHERE username = ?";
            
            try{
               usernameStatement = connection.prepareStatement(usernameQuery);
               usernameStatement.setString(1, cleanInputMap.get("username"));
               usernameResultSet = usernameStatement.executeQuery();
               
               if(! usernameResultSet.next()) {
                   usernameMismatch = true;
                   processRequest(request, response);
               }
               
               username = usernameResultSet.getString("username");
               usernameStatement.close();
            } catch(SQLException exception) {
                exception.printStackTrace();
                System.err.println("Unable to retrieve username!");
            }
            
            try {
                emailStatement = connection.prepareStatement(emailQuery);
                emailStatement.setString(1, cleanInputMap.get("username"));
                emailResultSet = emailStatement.executeQuery();

                if(! emailResultSet.next()) {
                    emailMismatch = true;
                    processRequest(request, response);
                }

                email = emailResultSet.getString("email");

                emailStatement.close();
            } catch(SQLException exception) {
                exception.printStackTrace();
                System.err.println("Unable to retreive email!");
            }

            /* Determine if user submitted data equals values in the DB*/
            if(! username.equals(cleanInputMap.get("username"))
                    ||  ! email.equals(cleanInputMap.get("email"))) {

                usernameMismatch = true;
                emailMismatch = true;

                processRequest(request, response);
            } else {
                //generate temporary random password 
                String generatedPassword = UUID.randomUUID().toString();

                try {
                    //update DB with temporary password
                    updatePasswordStatement = connection.prepareStatement(updatePasswordQuery);
                    updatePasswordStatement.setString(1, generatedPassword);
                    updatePasswordStatement.setString(2, cleanInputMap.get("username"));
                    System.out.println(updatePasswordStatement);
                    updatePasswordStatement.executeUpdate();

                } catch(SQLException exception) {
                    exception.printStackTrace();
                    System.err.println("Unable to update password");
                }

                GoogleMail mail = new GoogleMail("kealantest@gmail.com", "thirdyearteam_9", email);
                mail.send("", "Generated Password", "Your generated password is " + generatedPassword);
                response.sendRedirect("ChangePassword?forgotten_password=true");
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