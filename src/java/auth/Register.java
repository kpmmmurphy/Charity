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
import json.Charity;
import org.json.simple.JSONObject;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * Handles the entering of the Charity's address, telephone number, domain name,
 * and their  PayPal account email address.
 * 
 * @author  Kevin Murphy
 * @version 1.0
 * @date    12/2/14
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private final boolean DEBUG_ON = true;
    
    private boolean authorised = false;
    
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
        
        session    = request.getSession(false);
        //authorised = ((boolean)session.getAttribute("authorised"));
        
        if(session != null){
            String servletContext = request.getContextPath();
            String servletPath    = request.getServletPath();
            
            initialiseDetails(request);

            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Edit Charity Info</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Edit Details of your Charity</h1>");
                out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
                out.println("<fieldset>");
                out.println("<legend>Edit Details</legend>");
                //out.println("Description :  <input type='text' name='description' value='" + description + "'   placeholder='Description of your Charity' /> <br />");
                //out.println("Address :      <input type='text' name='address'     value='" + address + "'       placeholder='Address' maxlength='255' size='50' /> <br />");
                out.println("Description :  <textarea          name='description' value='" + description + "'   placeholder='Description of your Charity' ></textarea> <br />");
                out.println("Address :      <textarea          name='address'     value='" + address + "'       placeholder='Address' maxlength='255' size='50' ></textarea> <br />");
                out.println("Telephone :    <input type='text' name='telephone'   value='" + telephone + "'     placeholder='Telephone' maxlength='12' /> <br />");
                out.println("Domain name :  <input type='text' name='domain'      value='" + domain + "'        placeholder='Domain Name' /> <br />");
                out.println("PayPal Email : <input type='text' name='paypalemail' value='" + payPalEmail + "'   placeholder='PayPal Email' /> <br />");
                out.println("Facebook Url : <input type='text' name='facebook'    value='" + facebookUrl + "'   placeholder='Your Facebook page Url' /> <br />");
                out.println("Twitter Url :  <input type='text' name='twitter'     value='" + twitterUrl + "'    placeholder='Your Twitter page'/> <br />");
                out.println("Google+ Url :  <input type='text' name='googleplus'  value='" + googleplusUrl + "' placeholder='Your Google+ Url' /> <br />");
                out.println("<input type=\"submit\" value=\"Submit\">");
                out.println("</fieldset>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            }
        }else{
            //Redirect to basic registration of info page
            response.sendRedirect("./Signup");
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
        
        if(session.getAttribute("authorised") != null){
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
            String cleanPayPalEmail   = stripAllTagsPolicy.sanitize(payPalEmail);
            String cleanDescription   = stripAllTagsPolicy.sanitize(description);
            String cleanFacebookUrl   = stripAllTagsPolicy.sanitize(facebookUrl);
            String cleanTwitterUrl    = stripAllTagsPolicy.sanitize(twitterUrl);
            String cleanGooglePlusUrl = stripAllTagsPolicy.sanitize(googleplusUrl);

            HashMap<String, String> cleanInputMap = new HashMap<>();
            cleanInputMap.put("address", cleanAddress);
            cleanInputMap.put("telephone", cleanTelephone);
            cleanInputMap.put("domain", cleanDomain);
            cleanInputMap.put("payPalEmail", cleanPayPalEmail);
            cleanInputMap.put("description", cleanDescription);
            cleanInputMap.put("facebook", cleanFacebookUrl);
            cleanInputMap.put("twitter", cleanTwitterUrl);
            cleanInputMap.put("googleplus", cleanGooglePlusUrl);

            //Connect to Database
            DBConnect dbConnect   = new DBConnect();
            Connection connection = dbConnect.getConnection();

            //Get the CharityName from the Session
            String charityName = (String)session.getAttribute("charityName");
            if(DEBUG_ON){
                System.out.println("Charity Name from Session: " + charityName);
            }

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
                                          cleanInputMap.get("googleplus") );

            //Get the Servlet Context for writing the json file
            String servletContext = request.getServletContext().getRealPath("/");

            //Writes the json file to the defined directory
            charity.createCharityJSONFile(servletContext);

            //Redirect to Dashboard
            response.sendRedirect("/Dashboard");
        }else{
            response.sendRedirect("/Login");
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
        
        String charityName = (String)session.getAttribute("charityName");
        String servletContext = request.getServletContext().getRealPath("/");
        if(DEBUG_ON){
            System.out.println("Charity Name: " + charityName);
            System.out.println("Servlet Context : " + servletContext);
        }
        JSONObject charity = (JSONObject)Charity.parseJSON(charityName, servletContext).get("charity");
        
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
        
        this.description    = charity.get("description").toString();
        this.facebookUrl    = charity.get("facebook").toString();
        this.twitterUrl     = charity.get("twitter").toString();
        this.googleplusUrl  = charity.get("googleplus").toString();

        if(DEBUG_ON){
            System.out.println("Address from DB: "     + address);
            System.out.println("Domain from DB: "      + domain);
            System.out.println("Telephone from DB: "   + telephone);
            System.out.println("PayPalEmail from DB: " + payPalEmail);

            System.out.println("Description from JSON: "  + description);
            System.out.println("Facebook Url from JSON: " + facebookUrl);
            System.out.println("Twitter Url from JSON: "    + twitterUrl);
            System.out.println("GooglePlus Url from JSON: " + googleplusUrl);
        }
    }
}
