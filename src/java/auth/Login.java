package auth;

import database.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Handles login functionality, checking user supplied username matches one
 * present in the database, it then retrieves the stored salt value and uses it 
 * to hash the user supplied password, and checks to see if they match, if they 
 * do, it creates a session and authenticates the user
 * 
 * @author kealan
 * @version 1.1
 * @date 12/2/14
 */
@WebServlet(name ="Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {
    
    /* */
    private final boolean DEBUG_ON = true;
    
    private String username;
    private String password;
    
    private boolean unenteredInput;
    private boolean usernameMismatch;
    private boolean passwordMismatch;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        
        String servletContext = request.getContextPath();
        String servletPath = request.getServletPath();
        
        try(PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Login</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Login!</h1>");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<fieldset>");
            out.println("<legend>Login</legend>");
            out.println("Username: <input type='text' name='username' placeholder='username'> <br />");
            out.println("Password: <input type='password' name='password' placeholder='password'> <br />");
            out.println("<input type=\"submit\" value=\"Submit\">");
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if(usernameMismatch || passwordMismatch ){
                out.println("<p>Username or Password mismatch, please try again.</p>");
            }
            
            out.println("</fieldset>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");   
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
        
        unenteredInput   = false;
        usernameMismatch = false;
        passwordMismatch = false;
        
        username = request.getParameter("username");
        password = request.getParameter("password");
        
        if(DEBUG_ON) {
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        }
        
        if((username == null || "".equals(username))
            || (password ==  null || "".equals(password))) {
            
            unenteredInput = true;
            processRequest(request, response);
        } else {
            if(DEBUG_ON) {
                System.out.println("DEBUG: All input entered");
            }
            
            HashMap<String, String> unfilteredInputMap = new HashMap<String, String>();
            unfilteredInputMap.put("username", username);
            unfilteredInputMap.put("password", password);
            
            HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
            PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();
            
            String cleanUsername = stripAllTagsPolicy.sanitize(username);
            String cleanPassword = stripAllTagsPolicy.sanitize(password);
            
            HashMap<String, String> cleanInputMap = new HashMap<String, String>();
            cleanInputMap.put("username", cleanUsername);
            cleanInputMap.put("password", cleanPassword);
            
            
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();
            
            PreparedStatement usernameStatement;
            PreparedStatement selectDetailsStatement;
            PreparedStatement saltStatement;
            
            ResultSet usernameResultSet;
            ResultSet saltResultSet;
            
            /*Check for username*/
            String usernameQuery = "SELECT username "
                                 + "FROM charities "
                                 + "WHERE username = ?";
            
            /* Query for selecting salt value */
            String saltQuery = "SELECT salt "
                             + "FROM   charities "
                             + "WHERE  username = ?";
            
            try{
                usernameStatement = connection.prepareStatement(usernameQuery);
                usernameStatement.setString(1, cleanInputMap.get("username"));
                usernameResultSet = usernameStatement.executeQuery();
                usernameResultSet.next();
                if(! usernameResultSet.first()){
                    usernameMismatch = true;
                }
            }catch(SQLException e){
                e.printStackTrace();
                System.err.println("Unable to retrieve username!");
            }
            
            //if usernames don't match 
            if(usernameMismatch){
                processRequest(request, response);
            }else{
                //Username matched, retrieve salt value
                String salt = "";
                try{
                    saltStatement = connection.prepareStatement(saltQuery);
                    saltStatement.setString(1, cleanInputMap.get("username"));
                    saltResultSet = saltStatement.executeQuery();
                    saltResultSet.next();
                    salt = saltResultSet.getString(1);
                }catch(SQLException e){
                    e.printStackTrace();
                    System.err.println("Unable to retrieve username!");
                }
                
                if(DEBUG_ON){
                    System.out.println("Salt value from DB: " + salt);
                }
                
                Password passwordToBeHashed = new Password(cleanInputMap.get("password"), salt);
                String   hashedPassword     = passwordToBeHashed.getHashedPassword();
                cleanInputMap.put("password", hashedPassword);
                
                if(DEBUG_ON){
                    System.out.println("Hashed Password and Salt: " + hashedPassword);
                }
                
                String selectDetailsQuery = "SELECT * "
                                          + "FROM   charities "
                                          + "WHERE  password = ?";
                ResultSet detailsResultSet = null;
                
                try{
                    selectDetailsStatement = connection.prepareStatement(selectDetailsQuery);
                    selectDetailsStatement.setString(1, cleanInputMap.get("password"));
                    detailsResultSet = selectDetailsStatement.executeQuery();
                    detailsResultSet.next();
                    if(! detailsResultSet.first()){
                        passwordMismatch = true;
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                    System.err.println("Problem when retrieving Charity info, Password doesn't match.");
                }
                
                //If hased passwords didnt match, re-output login form
                if(passwordMismatch){
                    processRequest(request, response);
                }else{
                    //Username and password match
                    //Store basic info in a Session Object
                    
                    try{
                        HttpSession session = request.getSession(true);
                        session.setAttribute("username", detailsResultSet.getString(1));
                        session.setAttribute("charity_id", detailsResultSet.getString(5));
                        session.setAttribute("authorised", true);
                    }catch(SQLException e){
                        e.printStackTrace();
                        System.out.println("Problem creating Session");
                    }
                    

                    //Redirect to Dashboard
                    response.sendRedirect("/Dashboard");
                }
            }
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}