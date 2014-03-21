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
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import json.Charity;
import org.json.simple.JSONObject;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import utilities.Upload;

/**
 * Handles the entering of the Charity's address, telephone number, domain name,
 * PayPal account email address, their social media page addresses and uploading 
 * a Charity logo image.
 * 
 * @author  Kevin Murphy
 * @version 1.1
 * @date    22/2/14
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
@MultipartConfig
public class Register extends HttpServlet {

    /* Debug Mechinism*/
    private final boolean DEBUG_ON = true;
    
    /* Attributes to be stored into DB */
    private String domain      = "";
    private String payPalEmail = "";
    
    /* Attributes to be stored in both DB and JSON File*/
    private String address     = "";
    private String telephone   = "";
    
    /* Attributes to be stored in JSON file  */
    private String description   = "";
    private String facebookUrl   = "";
    private String twitterUrl    = "";
    private String googleplusUrl = "";
    private String logoImage     = "";
    
    /* Charity Name to be gotten from Session */
    private String charityName;
    /* The trimmed and lower case charity name for upload directory*/
    private String trimmedCharityName;
    
    /* Session */
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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        //Checks if there is a session
        session = request.getSession(false);
        
        //If the session is not null, process the request, else redirect to login
        if(session != null){
            String servletContext = request.getContextPath();
            String servletPath    = request.getServletPath();
            
            //Initilise all fields with stored data
            initialiseDetails(request);

            try (PrintWriter out = response.getWriter()) {
                
                //If it's the Charity has just signed up, print entier page
                if(request.getParameter("from_signup") != null && request.getParameter("with_header") != null){
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Edit Charity Info</title>"); 
                    
                    //out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/normalize.css\"/>"); 
                    out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/formStyles.css\"/>"); 
                    out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/submit_post.css\"/>"); 
                    out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/ajax_msgs.css\"/>"); 
                    out.println("<script src='javascript/jquery/jquery-1.11.0.js'></script>");
                    out.println("<script src='javascript/dashboard.js'></script>");
                    out.println("</head>");
                    out.println("<body>");
                }
                
                out.println("<div id=\"wrapper\">");
                out.println("<div id=\"registerDetails\">");
                
                //The General Charity Details From
                out.println("<form method='POST' id='register_form'>");
                out.println("<h1>Edit Details of your Charity</h1>");
		out.println("<p class=\"float\">");
                out.println("<label for=\"description\">Description:</label><textarea name='description'  placeholder='Description of your Charity' >" + description + "</textarea> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"address\">Address:</label><textarea name='address' placeholder='Address' maxlength='255' size='50' >" + address + "</textarea> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"telephone\">Telephone:</label><input type='text' name='telephone'   value='" + telephone + "'     placeholder='Telephone' maxlength='12' /> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"domain\">Domain Name:</label><input type='text' name='domain' value='" + domain + "'        placeholder='Domain Name' /> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"payPalEmail\">PayPal Email:</label><input type='text' name='paypalemail' value='" + payPalEmail + "'   placeholder='PayPal Email' /> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"facebook\">Facebook Url:</label><input type='text' name='facebook' value='" + facebookUrl + "'   placeholder='Your Facebook page Url' /> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"twitter\">Twitter Url:</label><input type='text' name='twitter' value='" + twitterUrl + "'    placeholder='Your Twitter page'/> <br />");
                out.println("</p>");
                out.println("<p class=\"float\">");
                out.println("<label for=\"googleplus\">Google+ Url:</label><input type='text' name='googleplus'  value='" + googleplusUrl + "' placeholder='Your Google+ Url' /> <br />");
                out.println("</p>");
                out.println("<p class=\"clearfix\">");
                if(request.getParameter("from_signup") != null){
                    out.println("<input type=\"submit\" value=\"Submit\" onclick='return ajaxRegisterSubmit(true)' >");
                }else{
                    out.println("<input type=\"submit\" value=\"Submit\" onclick='return ajaxRegisterSubmit()' >");
                }
                out.println("</p>");
                out.println("</form>");
                out.println("</div>");
                
                //The Multipart Upload form
                out.println("<div id=\"registerImage\">");
                out.println("<form method='POST' id='register_upload' enctype='multipart/form-data'>");
                out.println("<h1>Upload your charities logo</h1>");
                out.println("<p class=\"float\">");
                out.println("<label for='filename'>Upload Logo Image : </label><input id='file' type='file' name='filename' size='50'/><br/>");
                out.println("</p>");
                if("".equals(logoImage) ||logoImage == null){
                    out.println("<p class=\"float\"> No Image uploaded yet!</p>");
                }else{
                    out.println("<img src='charities/" + trimmedCharityName  + "/uploads/" + logoImage + "' id='logoImg' /><br/>");
                }
                out.println("<p class=\"clearfix\">");
                if(request.getParameter("from_signup") != null){
                    out.println("<input type=\"submit\" value=\"Submit\" onclick='return ajaxRegisterUpload(true)' >");
                }else{
                    out.println("<input type=\"submit\" value=\"Submit\" onclick='return ajaxRegisterUpload()' >");
                }
                out.println("</p>");
                out.println("</form>");
                out.println("</div>");
                out.println("</div>");
                
                
                if(request.getParameter("from_signup") != null){
                    out.println("<div id=\"faq\">");
                    out.println("<nav>");
                    out.println("<ul>");
                    out.println("<li><a href=\"Dashboard\">Dashboard</a></li>");
                    out.println("<li><a href=\"EditDetails\">Edit Details</a></li>");
                    out.println("</ul>");
                    out.println("</div>");
                    out.println("</nav>");
                    out.println("</body>");
                    out.println("</html>");
                }
                
            }
        }else{
            //Redirect to Signup page
            response.sendRedirect("Signup");
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
        
        //Check if session attribute authorised is set, if so process request
        if(session.getAttribute("authorised") != null){
            //Get the CharityName from the Session
            charityName = (String)session.getAttribute("charityName");
            if(DEBUG_ON){
                System.out.println("Charity Name from Session: " + charityName);
            }
            
            //If a logo is being uploaded, checks if it's a multipart request
            if (isMultipart(request)) {
                //Upload logo image, returns the name of the uploaded file, today's date and time
                Upload.processMultipartForm(request, charityName, true);
                
                //Redirect to Dashboard
                response.sendRedirect("Register");
            }
            
            //Not a multipart request, just form fields from first form
            if(! isMultipart(request)){
                /* Read in all paramaters from request */
                //For Database only
                domain      = request.getParameter("domain");
                payPalEmail = request.getParameter("paypalemail");

                //For Databse and JSON
                address     = request.getParameter("address");
                telephone   = request.getParameter("telephone");

                //For JSON only
                description     = request.getParameter("description");
                facebookUrl     = request.getParameter("facebook");
                twitterUrl      = request.getParameter("twitter");
                googleplusUrl   = request.getParameter("googleplus");

                if(DEBUG_ON){
                    System.out.println("Address from request :"     + address);
                    System.out.println("Telephone from request :"   + telephone);
                    System.out.println("Domain from request :"      + domain);
                    System.out.println("payPalEmail from request :" + payPalEmail);

                    System.out.println("Description from request :  " + description);
                    System.out.println("Facebook Url from request : " + facebookUrl);
                    System.out.println("Twitter Url from request  : " + twitterUrl);
                    System.out.println("Google+ Url from request :  " + googleplusUrl);
                }

                //OWASP sanitizer to pretect against XSS attacks
                HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
                PolicyFactory stripAllTagsPolicy     = htmlPolicyBuilder.toFactory();

                String cleanAddress       = stripAllTagsPolicy.sanitize(address);
                String cleanTelephone     = stripAllTagsPolicy.sanitize(telephone);
                String cleanDomain        = stripAllTagsPolicy.sanitize(domain);
                //Not cleaning the PayPal email as it is escaping the "@"
                //String cleanPayPalEmail   = stripAllTagsPolicy.sanitize(payPalEmail);
                String cleanDescription   = stripAllTagsPolicy.sanitize(description);
                String cleanFacebookUrl   = stripAllTagsPolicy.sanitize(facebookUrl);
                String cleanTwitterUrl    = stripAllTagsPolicy.sanitize(twitterUrl);
                String cleanGooglePlusUrl = stripAllTagsPolicy.sanitize(googleplusUrl);

                //HashMap to track all clean data
                HashMap<String, String> cleanInputMap = new HashMap<>();
                cleanInputMap.put("address", cleanAddress);
                cleanInputMap.put("telephone", cleanTelephone);
                cleanInputMap.put("domain", cleanDomain);
                cleanInputMap.put("payPalEmail", payPalEmail);
                cleanInputMap.put("description", cleanDescription);
                cleanInputMap.put("facebook", cleanFacebookUrl);
                cleanInputMap.put("twitter", cleanTwitterUrl);
                cleanInputMap.put("googleplus", cleanGooglePlusUrl);
                
                //Connect to Database
                DBConnect dbConnect   = new DBConnect();
                Connection connection = dbConnect.getConnection();

                //PreparedStatement for inserting all nessesary fields
                PreparedStatement insertStatement;

                //Insert Query
                String insertQuery = "UPDATE charities "
                                   + "SET address = ?, domain_url = ?, telephone = ?, paypal_email = ? "
                                   + "WHERE name = ? ";

                try{
                    //Create the PreparedStatement and enter all input
                    insertStatement = connection.prepareStatement(insertQuery);

                    insertStatement.setString(1, cleanInputMap.get("address"));
                    insertStatement.setString(2, cleanInputMap.get("domain"));
                    insertStatement.setString(3, cleanInputMap.get("telephone"));
                    insertStatement.setString(4, cleanInputMap.get("payPalEmail"));

                    insertStatement.setString(5, charityName);
                    System.out.println(insertStatement);
                    //Execute and close the connnection
                    insertStatement.executeUpdate();
                    connection.close();
                }catch(SQLException e){
                    System.err.println("Problem entering input in Database");
                    e.printStackTrace();
                }

                //Enter input into JSON file
                Charity charity = new Charity(charityName, cleanInputMap.get("description"),
                                              cleanInputMap.get("address"), cleanInputMap.get("telephone"),
                                              cleanInputMap.get("facebook"),cleanInputMap.get("twitter"),
                                              cleanInputMap.get("googleplus"), logoImage );

                //Get the Servlet Context for writing the json file
                String servletContext = request.getServletContext().getRealPath("/");

                //Writes the json file to the defined directory
                charity.createCharityJSONFile(servletContext);

                //Redirect to Dashboard
                response.sendRedirect("Register");
            }
        }else{
            response.sendRedirect("Login");
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

    /**
     * Gets the details already stored on disk of the Charity and outputs them
     * in the form, allowing the user to modify them accordingly
     * 
     * @param request 
     */
    private void initialiseDetails(HttpServletRequest request){
        
        //Get Charity Name from Session
        charityName = (String)session.getAttribute("charityName");
        //Trim, set to lower case and remove white spaces
        trimmedCharityName = charityName.toLowerCase().trim().replaceAll("\\s+","");
        //Get servlet Context
        String servletContext = request.getServletContext().getRealPath("/");
        if(DEBUG_ON){
            System.out.println("Charity Name: "     + charityName);
            System.out.println("Servlet Context : " + servletContext);
        }
        //Parses the charity.json file, and get the Charity
        Charity charity = Charity.parseJSONtoCharityObj(request);
        
        //Connect to Database
        DBConnect dbConnect   = new DBConnect();
        try (Connection connection = dbConnect.getConnection()) {
            PreparedStatement selectStatement;
            
            /* Checks for username duplicates */
            String selectQuery = "SELECT * "
                    + "FROM charities "
                    + "WHERE name = ?";
            
            //PreparedStatement for username duplication checking
            selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setString(1, charityName);
            ResultSet charityResultSet = selectStatement.executeQuery();

            charityResultSet.next();
            this.address     = (charityResultSet.getString(7) != null) ? charityResultSet.getString(7) : "";
            this.domain      = (charityResultSet.getString(8) != null) ? charityResultSet.getString(8) : "";
            this.telephone   = (charityResultSet.getString(9) != null) ? charityResultSet.getString(9) : "";
            this.payPalEmail = (charityResultSet.getString(10) != null) ? charityResultSet.getString(10) : "";
            
        }catch(SQLException e){
            System.err.println("Error connceting with Database to retrieve Charity Detials");
            e.printStackTrace();
        }
        
        //Initilise all attributes from the Charity JSONObject
        this.description    = charity.getDescription();
        this.facebookUrl    = charity.getFacebook();
        this.twitterUrl     = charity.getTwitter();
        this.googleplusUrl  = charity.getGoogleplus();
        this.logoImage      = charity.getLogo();

        if(DEBUG_ON){
            System.out.println("Address from DB: "     + address);
            System.out.println("Domain from DB: "      + domain);
            System.out.println("Telephone from DB: "   + telephone);
            System.out.println("PayPalEmail from DB: " + payPalEmail);

            System.out.println("Description from JSON: "  + description);
            System.out.println("Facebook Url from JSON: " + facebookUrl);
            System.out.println("Twitter Url from JSON: "    + twitterUrl);
            System.out.println("GooglePlus Url from JSON: " + googleplusUrl);
            System.out.println("Logo Image from JSON: "     + logoImage);
        }
    }
    
    /**
     * Checks if the request is a Multipart request holding encoded file data,
     * or is a regular plain-text form
     * 
     * @param request
     * @return boolean isMultipart  
     */
    public static boolean isMultipart(HttpServletRequest request ){
        boolean isMultipart = false;
        
        if(request.getContentType() != null && request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1){
            isMultipart = true;
        }
        
        return isMultipart;
    }
}