/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OAuth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author kpmmmurphy
 */
@WebServlet(name = "TwitterOAuth", urlPatterns = {"/TwitterOAuth"})
public class TwitterOAuth extends HttpServlet {
    
    private final boolean DEBUG_ON = true;
    
    private final String CONSUMER_KEY = "LzgDGH7PtPqSFYNHjyV5zQ";
    private final String CONSUMER_SECRET = "avlWZ5xTbxG6sLixu3bzVMzDRoiI5MjmtKvLEmPUHE";

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
        try (PrintWriter out = response.getWriter()) {
            /*
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TwitterOAuth</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TwitterOAuth at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            */
            
            
            String tokenFromRequest = request.getParameter("oat");
            String secretFromRequest = request.getParameter("oas");
            
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setDebugEnabled(DEBUG_ON);
            builder.setOAuthConsumerKey(CONSUMER_KEY);
            builder.setOAuthConsumerSecret(CONSUMER_SECRET);
            //builder.setOAuthAccessToken(tokenFromRequest);
            //builder.setOAuthAccessTokenSecret(secretFromRequest);
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            Twitter twitter = factory.getInstance();
            AccessToken accessToken = twitter.getOAuthAccessToken(new RequestToken(request.getParameter("oauth_token")), request.getParameter("oauth_verifier"));
            
            
            if(DEBUG_ON){
                System.out.println("Request Token from request : " + tokenFromRequest);
                System.out.println("Request Secret from request : " + secretFromRequest);
            }
            //twitter.setOAuthAccessToken(new AccessToken(tokenFromRequest, secretFromRequest));
            System.out.println("No");
            Status status;
            
            try {
                status = twitter.updateStatus("It worked!");
            } catch (TwitterException ex) {
                Logger.getLogger(TwitterOAuth.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //RequestToken requestToken = null;
            //try {
                //requestToken = twitter.getOAuthRequestToken();
               // System.out.println(requestToken.getAuthorizationURL());
            //} catch (TwitterException ex) {
                //Logger.getLogger(TwitterOAuth.class.getName()).log(Level.SEVERE, null, ex);
            //}
            //AccessToken accessToken = null;
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            //response.sendRedirect(requestToken.getAuthorizationURL());
            //twitter.setOAuthAccessToken(accessToken);
            /*
            while (null == accessToken) {
              System.out.println("Open the following URL and grant access to your account:");
              System.out.println(requestToken.getAuthorizationURL());
              
              response.sendRedirect(requestToken.getAuthorizationURL());
              
              System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
              String pin = br.readLine();
              try{
                 if(pin.length() > 0){
                   accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                 }else{
                   accessToken = twitter.getOAuthAccessToken();
                 }
              } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                  System.out.println("Unable to get the access token.");
                }else{
                  te.printStackTrace();
                }
              }
            }
            //persist to the accessToken for future reference.
            //storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
            Status status;
            try {
                status = twitter.updateStatus("It worked!");
            } catch (TwitterException ex) {
                Logger.getLogger(TwitterOAuth.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("Successfully updated the status to [" + status.getText() + "].");
            System.exit(0);
            */
          }
          
        
    }


private static void storeAccessToken(int useId, AccessToken accessToken){
            //store accessToken.getToken()
            //store accessToken.getTokenSecret()
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
