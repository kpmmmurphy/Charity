/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package post;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kealan
 * @version 1.1
 * @date 16/2/14
 */
@WebServlet(name = "CreatePost", urlPatterns = {"/CreatePost"})
public class CreatePost extends HttpServlet {

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
        
        session = request.getSession();
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            String servletContext = request.getContextPath();
            String servletPath = request.getServletPath();
            
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Create Post</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Create Post for " + session.getAttribute("name") + "</h1>");
                out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
                out.println("<fieldset>");
                out.println("<legend>New Post</legend>");
                out.println("Title: <input type='text' name='title' placeholder='Post Title'> <br />");
                out.println("Description: <textarea name=\"description\" rows=\"5\" cols=\"10\"></textarea><br />");
                out.println("Content: <textarea name=\"content\" rows=\"15\" cols=\"30\"></textarea><br />");
                out.println("<input type=\"submit\" value=\"Submit\">");
                out.println("<input type=\"reset\" value=\"Clear\">");
                out.println("</fieldset>");
                out.println("</form>");
                out.println("<p>Return to <a href=\"Dashboard\">Dashboard</a></p>");
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
        processRequest(request, response);
        
        HttpSession session = request.getSession();
        
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        } else {
            String charity = (String)session.getAttribute("name");
            String postTitle = request.getParameter("title");
            String postDescription = request.getParameter("description");
            String postContent = request.getParameter("content");
            
            Writer writer;
            try{
                writer = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream("/home/kealan/College/CS3305/charities/" 
                                            + charity + "/articles/" + postTitle + ".txt")));
                
                writer.write("Title: " + postTitle);
                writer.append(System.getProperty("line.separator"));
                writer.append("Description: " + postDescription);
                writer.append(System.getProperty("line.separator"));
                writer.append("Content: " + postContent);
                writer.flush();
                writer.close();
            } catch(IOException exception) {
                exception.printStackTrace();
                System.err.println("Unable to write to file " + postTitle + ".txt");
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
