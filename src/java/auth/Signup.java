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
import json.Charity;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import utilities.DirectoryManager;

/**
 * Handles Charity Sign-up, checking for duplicates and
 * inserting values into the Database accordingly
 * 
 * @author  Kevin Murphy
 * @version 1.1
 * @date    11/2/14
 */
@WebServlet(name = "Signup", urlPatterns = {"/Signup"})
public class Signup extends HttpServlet {
    
    /* Debug mechinism */
    private final boolean DEBUG_ON = true;
    
    /* The name of the charity to be entered in the DB */
    private String charityName;
    /* The username of the charity's account to be entered in the DB */
    private String username;
    /* The email address of the charity to be entered in the DB */
    private String email;
    /* The password of the charity to be entered in the DB */
    private String password;
    /* The retyped password to ensure correctness*/
    private String retypedPassword;
    
    /* Indicates if any fields were left empty */
    private boolean unenteredInput;
    /* Indicates if passwords did not match*/
    private boolean passwordMismatch;
    /* Indicates if username chosen already exists in DB */
    private boolean usernameDuplication;
    /* Indicates if the chose charity name already exists in the DB */
    private boolean charityNameDuplication;
    
    

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
        
        /* Root directory of project - "Charity" in this case */
        String servletContext = request.getContextPath();
        /* Path of the Servlet - "/Signup" in this case */
        String servletPath = request.getServletPath();
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Signup</title>");
             out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/faq.css\"/>");
            out.println("<script src='javascript/jquery/jquery-1.11.0.js'></script>");
            out.println("<script src='javascript/general.js'></script>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div id=\"wrapper\">");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<h1>Signup!</h1>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"charityName\">Charity Name:</label><input type='text' name='charityName' placeholder='Charity Name'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"charityNumber\">Charity Number:</label><input type='text' name='charityNumber' placeholder='Charity Number'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"username\">Username:</label><input type='text' name='username' placeholder='Username'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"email\">Email:</label><input type='text' name='email' placeholder='E-mail'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"password\">Password:</label><input type='password' name='password' placeholder='Password'> <br />");
            out.println("</p>");
            out.println("<p class=\"float\">");
            out.println("<label for=\"retyped_password\">Retype Password:</label><input type='password' name='retyped_password' placeholder='Password'> <br />");
            out.println("</p>");
            out.println("<p class=\"claerfix\">");
            out.println("<input type=\"submit\" value=\"Submit\">");
            out.println("<input type=\"reset\" value=\"Clear\">");
            if(unenteredInput){
                out.println("<p>Please fill in all fields.</p>");
            }
            if(passwordMismatch){
                out.println("<p>Password mismatch, please try again.</p>");
            }
            if(usernameDuplication){
                out.println("<p>Username already taken, please try again.</p>");
            }
            if(charityNameDuplication){
                out.println("<p>Charity name already registered, please choose another.</p>");
            }
            out.println("</p>");
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
     * This method takes all inputted parameters, sanitizes them to prevent XSS 
     * attacks,  checks to see if duplications exists in the Database using 
     * PreparedStatements to defend against SQL Injections, handles 
     * hashing and salting of the password by calling the auth.Password class, 
     * and finally inserts all parameters into the Database.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        StringBuffer servletURL = request.getRequestURL();
        
        System.out.println("ServletURL: " + servletURL.toString());
        
        //initialize context sensitive help booleans
        unenteredInput      = false;
        passwordMismatch    = false;
        usernameDuplication = false;
        charityNameDuplication = false;
        
        //read in all paramaters from request
        charityName        = request.getParameter("charityName");
        username           = request.getParameter("username");
        email              = request.getParameter("email");
        password           = request.getParameter("password");
        retypedPassword    = request.getParameter("retyped_password");
        
        if(DEBUG_ON){
            System.out.println("Charity Name:" + charityName);
            System.out.println("Username:"     + username);
            System.out.println("Email:"        + email);
            System.out.println("Password:"     + password);
        }
        
        //check to see if an of the parameter value are null or the empty string
        if((charityName == null || "".equals(charityName) )
           || (username == null || "".equals(username)    )
           || (password == null || "".equals(password)    )
           || (email    == null || "".equals(email)       )){
            
            unenteredInput = true;
            processRequest(request,response);
        }else{
            
            //checks if passwords match up correctly
            if(password.equals(retypedPassword)){
                if(DEBUG_ON){
                    System.out.println("DEBUG: All input entered");
                }
                
                //A HashMap to house all unfiltered user input
                HashMap<String, String> unfilteredInputMap = new HashMap<String, String>();
                unfilteredInputMap.put("charityName", charityName);
                unfilteredInputMap.put("username"   , username);
                unfilteredInputMap.put("email"      , email);
                unfilteredInputMap.put("password"   , password);

                /* OWASP HTML Sanitizer for filtering input */
                //Filters all tags that may be present in input paramaters
                //The following policy removes all tags
                HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
                PolicyFactory     stripAllTagsPolicy = htmlPolicyBuilder.toFactory();

                String cleanCharityName = stripAllTagsPolicy.sanitize(charityName);
                String cleanUsername    = stripAllTagsPolicy.sanitize(username);
                String cleanEmail       = stripAllTagsPolicy.sanitize(email);
                String cleanPassword    = stripAllTagsPolicy.sanitize(password);

                //HashMap to house all clean input parameters
                HashMap<String, String> cleanInputMap = new HashMap<String, String>();
                cleanInputMap.put("charityName" , cleanCharityName);
                cleanInputMap.put("username"    , cleanUsername);
                cleanInputMap.put("email"       , email);
                cleanInputMap.put("password"    , cleanPassword);
                
                //Connect to Database
                DBConnect dbConnect   = new DBConnect();
                Connection connection = dbConnect.getConnection();
                
                //Prepare statments to defend against SQL injections
                PreparedStatement usernameStatement;
                PreparedStatement charityNameStatement;
                
                /* Checks for username duplicates */
                String usernameDuplicateQuery = "SELECT * "
                                              + "FROM charities "
                                              + "WHERE username = ?";
                
                /* Checks for charity name duplicates */
                String charityNameDuplicateQuery = "SELECT * "
                                                 + "FROM charities "
                                                 + "WHERE name = ?";
                try {
                    
                    //PreparedStatement for username duplication checking
                    usernameStatement = connection.prepareStatement(usernameDuplicateQuery);
                    usernameStatement.setString(1, cleanInputMap.get("username") );
                    ResultSet usernameResultSet = usernameStatement.executeQuery();
                    
                    //if there is a value in the first row returned, the username has been taken
                    if(usernameResultSet.first()){
                        usernameDuplication = true;
                    }
                    
                    
                    //PreparedStatement for username duplication checking
                    charityNameStatement = connection.prepareStatement(charityNameDuplicateQuery);
                    charityNameStatement.setString(1, cleanInputMap.get("charityName") );
                    ResultSet charityNameResultSet = usernameStatement.executeQuery();
                    
                    //if there is a value in the first row returned, the charity name has been taken
                    if(charityNameResultSet.first()){
                        charityNameDuplication = true;
                    }
                    
                    //Close statement and Result Set
                    usernameStatement.close();
                    usernameResultSet.close();
                    charityNameStatement.close();
                    charityNameResultSet.close();
                    
                    
                    //if either have been duplicated, reoutputs the form
                    if(usernameDuplication || charityNameDuplication){
                        processRequest(request, response);
                    }else{
                        /* No duplications in DB, hash password and insert paramaters into DB */
                        
                        //Hash password
                        Password passwordToBeHashed = new Password(cleanInputMap.get("password"));
                        String hashedPassword       = passwordToBeHashed.getHashedPassword();
                        String saltValue            = passwordToBeHashed.getSaltValue();
                        
                        //place values into HashMap
                        cleanInputMap.put("password", hashedPassword);
                        cleanInputMap.put("salt", saltValue);
                        
                        //PrepareStatement to defend against SQL Injection attacks
                        //Inserting all user inputted fields
                        PreparedStatement insertStatement;
                        String insertQuery = "INSERT INTO charities (name, username, email, password, salt,address)"
                                           + "VALUES (?,?,?,?,?,?)";
                        insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, cleanInputMap.get("charityName"));
                        insertStatement.setString(2, cleanInputMap.get("username"));
                        insertStatement.setString(3, cleanInputMap.get("email"));
                        insertStatement.setString(4, cleanInputMap.get("password"));
                        insertStatement.setString(5, cleanInputMap.get("salt"));
                        insertStatement.setString(6, ""); //Address is Not Null, must have a value
                        insertStatement.executeUpdate();
                        
                        //Close statement
                        insertStatement.close();
                        
                        //Selecting the newly generated charity id from DB
                        PreparedStatement selectCharityIDStatement;
                        ResultSet charityIdResultSet;
                        
                        String selectQuery       = "SELECT id FROM charities WHERE name = ?";
                        selectCharityIDStatement = connection.prepareStatement(selectQuery);
                        selectCharityIDStatement.setString(1, cleanInputMap.get("charityName"));
                        charityIdResultSet = selectCharityIDStatement.executeQuery();
                        
                        charityIdResultSet.next();
                        cleanInputMap.put("charity_id", charityIdResultSet.getString(1) );
                        
                        //Close the statement and Result Set
                        selectCharityIDStatement.close();
                        charityIdResultSet.close();
                        
                        
                        //Store basic info in a Session Object
                        HttpSession session = request.getSession(true);
                        session.setAttribute("charityName", cleanInputMap.get("charityName"));
                        session.setAttribute("charity_id", cleanInputMap.get("charity_id"));
                        session.setAttribute("username"   , cleanInputMap.get("username"));
                        session.setAttribute("authorised" , true);
                        
                        if(DEBUG_ON){
                            System.out.println("SESSION - charityName: " + (String)session.getAttribute("charityName"));
                            System.out.println("SESSION - charity_id: "  + (String)session.getAttribute("charity_id"));
                            System.out.println("SESSION - charityName: " + (String)session.getAttribute("username"));
                            System.out.println("SESSION - charityName: " + (Boolean)session.getAttribute("authorised"));
                        }
                        
                        //Create new directory structure for this Charity
                        DirectoryManager dirManager = new DirectoryManager(cleanInputMap.get("charityName"));
                        
                        String servletContext = request.getServletContext().getRealPath("/");
                        System.out.print("ServletContext: " + servletContext );
                        //Creates the DirStructure, placing the file in the Document Root, accessable through the browser
                        dirManager.createDirStructure(servletContext);
                        
                        //Create the JSON File for the Charity
                        Charity charity = new Charity(cleanInputMap.get("charityName"),"","","","","","", "");
                        charity.createCharityJSONFile(servletContext);
                        
                        //Redirect to basic registration of info page
                        response.sendRedirect("./Register?from_signup=true&with_header=true");
                    }
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }else{
                //Passwords did not match, re-output form
                passwordMismatch = true;
                processRequest(request,response);
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