package org.joe_e.webmail;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Inbox extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		if (request.getSession().getAttribute("user") == null) {
			response.sendRedirect("/webmail/");
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		User user = (User) request.getSession().getAttribute("user");
		out.println("<body>");
		out.println("<h1>Inbox of " + user.getUserName() + "</h1>");
		out.println("<a href=\"/webmail/logout\">Logout</a>");
		out.println("</body>");
		HtmlWriter.printFooter(out);
		
	}
}
