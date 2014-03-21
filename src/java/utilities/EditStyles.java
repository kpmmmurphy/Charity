/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kealan
 * @version 1.2
 * @date 15/03/14
 */
@WebServlet(name = "EditStyles", urlPatterns = {"/EditStyles"})
public class EditStyles extends HttpServlet {

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
        
        /* If the user is not logged in, redirect to the login page*/
        if(session.getAttribute("authorised") == null) {
            response.sendRedirect("Login");
        }
        
        /* Root directory of project - "Charity" in this case */
        String servletContext = request.getContextPath();
        /* Path of the servlet - "/EditDetails" in this case */
        String servletPath = request.getServletPath();
            
        try (PrintWriter out = response.getWriter()) {
           
            out.println("<div id=\"editStylesForm\">");
            out.println("<form method='POST' action='" + servletContext + servletPath +"'>");
            out.println("<h1>Edit the style of your homepage</h1>");
            out.println("<label for=\"font\">Font:</label><select name='font'>"
                        + "         <option value='default'>Default</option>"
                        + "         <option value='Ariel'>Ariel</option>"
                        + "         <option value='Helvetica'>Helvetica</option>"
                        + "         <option value='Serif'>Serif(Default)</option>"
                        + "         <option value='Times New Roman'>Times New Roman</option>"
                        + "        </select>");
            out.println("<label for=\"fontSize\">Font Size:</label><select name='fontSize'>"
                        + "         <option value='default'>Default</option>"
                        + "         <option value='12'>12(Default)</option>"
                        + "         <option value='13'>13</option>"
                        + "         <option value='14'>14</option>"
                        + "         <option value='15'>15</option>"
                        + "         <option value='16'>16</option>"
                        + "        </select>");
            out.println("<label for=\"backgroundColor\">Background:</label><select name='backgroundColor'>"
                        + "         <option value='image'>Keep Image</option>"
                        + "         <option value='#F0F8FF'>Alice Blue</option>"
                        + "         <option value='#00FFFF'>Aqua</option>"
                        + "         <option value='black'>Black</option>"
                        + "         <option value='#0000FF'>Blue</option>"
                        + "         <option value='#8A2BE2'>BlueViolet</option>"
                        + "         <option value='#A52A2A'>Brown</option>"
                        + "         <option value='#5F9EA0'>CadetBlue</option>"
                        + "         <option value='#D2691E'>Choclate</option>"
                        + "         <option value='#dC143C'>Crimson</option>"
                        + "         <option value='#00008B'>DarkBlue</option>"
                        + "         <option value='#E9967A'>DarkSalmon</option>"
                        + "         <option value='#778899'>LightSladeGrey</option>"
                        + "         <option value='#D2B48C'>Tan</option>"
                        + "        </select>");
            out.println("<label for=\"wrapperBackground\">Content Background:</label><select name='wrapperBackground'>"
                        + "         <option value='default'>Default</option>"
                        + "         <option value='#F0F8FF'>Alice Blue</option>"
                        + "         <option value='#00FFFF'>Aqua</option>"
                        + "         <option value='black'>Black</option>"
                        + "         <option value='#0000FF'>Blue</option>"
                        + "         <option value='#8A2BE2'>BlueViolet</option>"
                        + "         <option value='#A52A2A'>Brown</option>"
                        + "         <option value='#5F9EA0'>CadetBlue</option>"
                        + "         <option value='#D2691E'>Choclate</option>"
                        + "         <option value='#dC143C'>Crimson</option>"
                        + "         <option value='#00008B'>DarkBlue</option>"
                        + "         <option value='#E9967A'>DarkSalmon</option>"
                        + "         <option value='#778899'>LightSladeGrey</option>"
                        + "         <option value='white'>White(Default)</option>"
                        + "         <option value='#D2B48C'>Tan</option>"
                        + "        </select>");
            out.println("<label for=\"textColor\">Text Color:</label><select name='textColor'>"
                        + "         <option value='default'>Default</option>"
                        + "         <option value='#F0F8FF'>Alice Blue</option>"
                        + "         <option value='#00FFFF'>Aqua</option>"
                        + "         <option value='black'>Black(Default)</option>"
                        + "         <option value='#0000FF'>Blue</option>"
                        + "         <option value='#8A2BE2'>BlueViolet</option>"
                        + "         <option value='#A52A2A'>Brown</option>"
                        + "         <option value='#5F9EA0'>CadetBlue</option>"
                        + "         <option value='#D2691E'>Choclate</option>"
                        + "         <option value='#dC143C'>Crimson</option>"
                        + "         <option value='#00008B'>DarkBlue</option>"
                        + "         <option value='#E9967A'>DarkSalmon</option>"
                        + "         <option value='#778899'>LightSladeGrey</option>"
                        + "         <option value='white'>White</option>"
                        + "         <option value='#D2B48C'>Tan</option>"
                        + "        </select>");
            out.println("<input type=\"submit\" value=\"Submit\">");
            out.println("<form>");
            out.println("</div>");
            
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
        /* User supplied data representing their font choice */
        String font = request.getParameter("font");
        /* User supplied data representing their font size choice */
        String fontSize = request.getParameter("fontSize");
        /* User supplied data representing their background color choice */
        String backgroundColor = request.getParameter("backgroundColor");
        /* User supplied data representing their text color choice */
        String textColor = request.getParameter("textColor");
        /* User supplied data representing their content background color choice */
        String wrapperBackground = request.getParameter("wrapperBackground");
        /* Gets the current home directory */
        String homeDir = System.getProperty("user.home");
        
        try {
            FileWriter fileWriter = new FileWriter(homeDir + "/NetBeansProjects/cs3305/charities/" + DirectoryManager.toLowerCaseAndTrim((String) session.getAttribute("charityName")) + "/homeStyles.css", true);
            fileWriter.write("body\n{\n");
            
            /**
             * Determine if default value entered or other value
             * If not the default value write the new CSS3 rule
             * If default value overwrite previous rule with default rule
             */
            if(!font.equals("default")) {
                fileWriter.write("font-family:" + font + ";\n");
            } else {
                fileWriter.write("font-family:serif;\n");
            }
            
            if(!fontSize.equals("default")) {
                fileWriter.write("font-size:" + fontSize + "pt;\n");
            } else {
                fileWriter.write("font-size:12pt;\n");
            }
            
            if(!backgroundColor.equals("image")) {
                fileWriter.write("background-image: none;\n"
                                + "background-color:" + backgroundColor + ";\n");
            } else {
                fileWriter.write("background-image:url('../images/sos.png');\n");
            }
            
            if(!textColor.equals("default")) {
                fileWriter.write("color:" + textColor + ";\n");
            } else {
                fileWriter.write("color:black;\n");
            }
            
            fileWriter.write("}\n\n");
            
            //wrapper css
            if(!wrapperBackground.equals("default")) {
                fileWriter.write("#wrapper\n{\nbackground-color:" + wrapperBackground + ";\n}\n\n");
            } else {
                fileWriter.write("#wrapper\n{\nbackground-color:white;\n}\n\n");
            }
            
            fileWriter.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
        
        /* rediredt user to home page in order to see their changes */
        response.sendRedirect("charities/" + DirectoryManager.toLowerCaseAndTrim((String) session.getAttribute("charityName")) + "/index.html" );
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