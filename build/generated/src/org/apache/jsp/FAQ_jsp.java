package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class FAQ_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("         <link rel=\"stylesheet\" type=\"text/css\" href=\"styles/styles2.css\"/>\n");
      out.write("        <title>FAQ</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("       <div id=\"main\">\n");
      out.write("        <h1>FAQ</h1>\n");
      out.write("        \n");
      out.write("        <section>\n");
      out.write("          <Legend>For Posts:</Legend>\n");
      out.write("           <h2>How do I create a post?</h2>\n");
      out.write("             <p>1.Click the corresponding bottom on the dashboard; </p>\n");
      out.write("             <p>2.Fill all the required fields in the post page;</p>\n");
      out.write("             <p>(including the title/abstract/content/tags)</p>\n");
      out.write("             <p>3.Click the 'Submit' bottom to submit your post.</p>\n");
      out.write("             <p>(you can click the 'clear' bottom if you fill something wrong)</p>\n");
      out.write("             <hr/>\n");
      out.write("           <h2>How do I delete/edit a post?</h2>\n");
      out.write("             <p>1.Check the list of post you have created;</p>\n");
      out.write("             <p>2.Click the post for which you want to delete/edit;</p>\n");
      out.write("             <p>3.Fill and submit the form just like what you did when created it.</p>\n");
      out.write("             <hr/>\n");
      out.write("            <h2>How do I approve a post?</h2>\n");
      out.write("             <p>1.Check the list of post;</p>\n");
      out.write("             <p>2.Click the box to approve the post.</p>\n");
      out.write("        </section>\n");
      out.write("        <section>\n");
      out.write("            <Legend>Other functions</legend>\n");
      out.write("            <h2>How do I update my account details?</h2>\n");
      out.write("            <p>1.Click the corresponding link ('Edit the details') on the left of the dashboard;</p>\n");
      out.write("            <p>2.Choose an option;</p>\n");
      out.write("            <p>3.Edit the details;</p>\n");
      out.write("            <p>3.Click the 'Submit' bottom to submit your update.</p>\n");
      out.write("            <p>(you can click the 'clear' bottom if you fill something wrong)</p>\n");
      out.write("            <hr/>\n");
      out.write("            <h2>How do I get to my charity site</h2>\n");
      out.write("            <p>Click the corresponding link ('My charity') on the left of the dashboard;</p>\n");
      out.write("            <hr/>\n");
      out.write("        </section>\n");
      out.write("       </div>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
