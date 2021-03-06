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
 * Handles login functionality, checking user supplied username matches one
 * present in the database, it then retrieves the stored salt value and uses it 
 * to hash the user supplied password, and checks to see if they match, if they 
 * do, it creates a session and authenticates the user
 * 
 * @author Kealan and Kevin
 * @version 1.1
 * @date 12/2/14
 */
@WebServlet(name ="Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {
    
    /* Debug mechinism */
    private final boolean DEBUG_ON = true;
    
    /* user supplied username */
    private String username;
    /* user supplied password */
    private String password;
    
    /* for indicateing if all input was entered */
    private boolean unenteredInput = false;
    /* for indicateing if the user supplied username matches one in the DB*/
    private boolean usernameMatch = true;
    /* for indicateing if the user supplied password matches the one in the DB */
    private boolean passwordMismatch = false;
    /* HttpSession object*/
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
                    throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        
        /* The root directory of the project - In this case "/cs3305"  */
        String servletContext = request.getContextPath();
        /* The servlet name - In this case "/Login" */
        String servletPath = request.getServletPath();
        session = request.getSession();
        
        if(session.getAttribute("authorised") != null) {
            response.sendRedirect("Dashboard");
        }
        
        try(PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Login</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/faq.css\"/>");
            out.println("<script src='javascript/jquery/jquery-1.11.0.js'></script>");
            out.println("<script src='javascript/general.js'></script>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"wrapper\">");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<h1>Login!</h1>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"username\">Username:</label><input type='text' name='username' placeholder='username'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"password\">Password:</label><input type='password' name='password' placeholder='password'> <br />");
            out.println("</p>");
            out.println("<p class=\"clearfix\">");
            out.println("<input type=\"submit\" value=\"Submit\">");
            out.println("</p>");
            //Context sensitive help
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if( ! usernameMatch || passwordMismatch ){
                out.println("<p>Username or Password mismatch, please try again.</p>");
                out.println("<p>Don't have an account? Sign up <a href=\"Signup\">HERE</a></p>");
            }
            out.println("<p class='float'><a href=\"ForgotPassword\">Forgot your password?</a></p>");
            out.println("</form>");
            out.println("<footer>");
            out.println("<small>&copy;CMS - Team9 - 2014</small>");
            out.println("</footer>");
            out.println("</div>");
            out.println("<div id=\"faq\">");
            out.println("<nav>");
            out.println("<li><a href=\"gateway.html\">Home</a></li>");
            out.println("<li><a onclick='getFAQ()'>FAQ</a></li>");
            out.println("</div>");
            out.println("</nav>");
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
        
        //Initilize context sensitive help booleans
        unenteredInput   = false;
        usernameMatch    = false;
        passwordMismatch = false;
        
        //Get the user submitted parameter
        username = request.getParameter("username");
        password = request.getParameter("password");
        if(DEBUG_ON) {
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        }
        
        //Checks if all input was entered
        if((username == null  || "".equals(username))
          || (password ==  null || "".equals(password))) {
            unenteredInput = true;
            processRequest(request, response);
        } else {
            
            if(DEBUG_ON) {
                System.out.println("DEBUG: All input entered");
            }
            
            //HashMap to house all unfiltered input
            HashMap<String, String> unfilteredInputMap = new HashMap<String, String>();
            unfilteredInputMap.put("username", username);
            unfilteredInputMap.put("password", password);
            
            //OWASP sanitizer to pretect against XSS attacks
            HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
            PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();
            
            //Sanitizes input
            String cleanUsername = stripAllTagsPolicy.sanitize(username);
            String cleanPassword = stripAllTagsPolicy.sanitize(password);
            
            //Stores all clean input in HashMap
            HashMap<String, String> cleanInputMap = new HashMap<String, String>();
            cleanInputMap.put("username", cleanUsername);
            cleanInputMap.put("password", cleanPassword);
            
            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();
            
            //PreparedStatement to check for matching username
            PreparedStatement usernameStatement = null;
            //PreparedStatement to retrieve salt value belonging to the supplied username
            PreparedStatement saltStatement = null;
            //PreparedStatement to select all charity details
            PreparedStatement selectDetailsStatement = null;
            
            //Result set to for usernameStatement
            ResultSet usernameResultSet;
            //Result set for saltStatement
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
                //Executing the Prepared Statement to check if usernames match
                usernameStatement = connection.prepareStatement(usernameQuery);
                usernameStatement.setString(1, cleanInputMap.get("username"));
                usernameResultSet = usernameStatement.executeQuery();
                
                if(usernameResultSet.next()){
                    usernameMatch = true;
                }
                
            }catch(SQLException e){
                e.printStackTrace();
                System.err.println("Unable to retrieve username!");
            }
            
            //if usernames don't match re-output login form
            if(! usernameMatch){
                processRequest(request, response);
            }else{
                //Username matched, retrieve salt value
                String salt = "";
                try{
                    //Execute Prepared Statement
                    saltStatement = connection.prepareStatement(saltQuery);
                    saltStatement.setString(1, cleanInputMap.get("username"));
                    saltResultSet = saltStatement.executeQuery();
                    
                    if(saltResultSet.next()){
                        salt = saltResultSet.getString(1);
                    }
                    
                    saltStatement.close();
                    saltResultSet.close();
                }catch(SQLException e){
                    e.printStackTrace();
                    System.err.println("Unable to retrieve username!");
                }
                
                if(DEBUG_ON){
                    System.out.println("Salt value from DB: " + salt);
                }
                
                //Creates a new Password object using the user supplied password and salt value gotten from the DB
                Password passwordToBeHashed = new Password(cleanInputMap.get("password"), salt);
                //Gets the hashed value of the password and salt
                String   hashedPassword     = passwordToBeHashed.getHashedPassword();
                cleanInputMap.put("password", hashedPassword);
                
                if(DEBUG_ON){
                    System.out.println("Hashed Password and Salt: " + hashedPassword);
                }
                
                //Query to select all charity detials
                String selectDetailsQuery = "SELECT * "
                                          + "FROM   charities "
                                          + "WHERE  password = ?";
                
                //ResultSet to hold all retrieved values of the charity
                ResultSet detailsResultSet = null;
                
                try{
                    //Executes the PreparedStatement to retrieve the charity detials
                    selectDetailsStatement = connection.prepareStatement(selectDetailsQuery);
                    selectDetailsStatement.setString(1, cleanInputMap.get("password"));
                    detailsResultSet = selectDetailsStatement.executeQuery();
                    
                    //if there are no elements in the ResultSet, Passwords mismatched
                    if(! detailsResultSet.first()){
                        passwordMismatch = true;
                    }
                    
                }catch(SQLException e){
                    e.printStackTrace();
                    System.err.println("Problem when retrieving Charity info, Password doesn't match.");
                }
                
                //If hashedd passwords didnt match, re-output login form
                if(passwordMismatch){
                    processRequest(request, response);
                }else{
                    //Username and password match
                    //Store basic info in a Session Object
                    
                    try{
                        //Create a Session
                        session = request.getSession(true);
                        session.setAttribute("username", detailsResultSet.getString(1));
                        session.setAttribute("charity_id", detailsResultSet.getString(5));
                        session.setAttribute("charityName", detailsResultSet.getString(6));
                        session.setAttribute("authorised", true);
                        
                        
                        selectDetailsStatement.close();
                        detailsResultSet.close();
                        //Close the Database
                        connection.close();
                    }catch(SQLException e){
                        e.printStackTrace();
                        System.out.println("Problem creating Session");
                        
                    }
                    
                    //Redirect to Dashboard
                    response.sendRedirect("Dashboard");
                }
            }
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}