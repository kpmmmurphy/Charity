/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import auth.Signup;
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
//import org.owasp.html.HtmlPolicyBuilder;
//import org.owasp.html.PolicyFactory;

/**
 *
 * @author ty1
 */
@WebServlet(name = "Homepage", urlPatterns = {"/Homepage"})
public class Homepage extends HttpServlet {
    private String username;
    private String charityName;
    private String results;
    private String password;
    private String retypedPassword;
    
    ResultSet usernameResultSet;
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HomePage</title>"); 
            out.println("<link rel='stylesheet' type='text/css' href='styles/styles.css'>");
            out.println("</head>");
            out.println("<body>");
           
            out.println("<div id='main'>");
            out.println("<h1>Home Page</h1>");

            //out.println("<h2>Mission:</h2>");
            //out.println("<p>We work with the world's poorest people to transform their lives. Together with our amazing supporters,we are working for a world free from hunger and poverty. Now start your life helping others. Gods bless</p>");
            //out.println("<hr/>");
            out.println("<ul>");
            out.println("<li>");
            out.println("<a href='Login'>");         
            out.println("<h3>Login</h3>");
            out.println("</a>");
            out.println("</li>");
            out.println("<li>");
            out.println("<a href='Signup'>");
            out.println("<h3>Signup</h3>");
            out.println("</a>");
            out.println("</li>");
            out.println("</ul>");
           
        
            /*
            out.println("<hr/>");
           
            out.println("<p style='font-size:15px;color:#FFFFFF'>'O human child,to the water and wild,with a fairy,hand in hand,for the world's more full of weeping than you can understand'--W.B.Yeats</p>");
            out.println("<img src=\"images.jpg\" class=\"nav\">");
            out.println("</div>");
         
            /*
            out.println("<nav>");
            out.println("<img src=\"1.jpg\" class=\"nav\">");
            out.println("<hr/>");
            try{
               while(usernameResultSet.next()) {
                    results = usernameResultSet.getString("name"); 
                    out.println("<p style='font-size:15px;color:black'>charities using our website:" + results + "</p>");
               }
            } catch(SQLException e) {
                
            }
            
            //out.println("<p style='font-size:15px'>charities using our website:</p>"+results);
            
            out.println("</nav>");
            */
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
           //     HtmlPolicyBuilder htmlPolicyBuilder  = new HtmlPolicyBuilder();
           //     PolicyFactory     stripAllTagsPolicy = htmlPolicyBuilder.toFactory();
                
        DBConnect dbConnect   = new DBConnect();
        Connection connection = dbConnect.getConnection();
        
        PreparedStatement usernameStatement;
        PreparedStatement charityNameStatement;
                
               String result = "SELECT * "
                         + "FROM charities "
                                     ;
                 try {
                    usernameStatement = connection.prepareStatement(result);
                    usernameResultSet = usernameStatement.executeQuery();
                    
                    
                    //results= usernameResultSet.getString("name");
                    //out.println("<p style='font-size:15px'>charities using our website:</p>"+results);
                    //usernameResultSet.next();
                 }
                 catch(SQLException ex)
                 {
                 Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 processRequest(request, response);
                 
                 
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

       
                
            
                
                
                
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
