package org.joe_e.webmail;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Login extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String message = null;
		
		if (username != null && password != null) {
			// here we need to do some validation of username and password
			// for now we assume that they're authenticated, so we should set their session
			User u = null;
			Authentication auth = (Authentication) request.getSession().getAttribute("auth");
			if (auth == null) {
				response.sendRedirect("/webmail/logout");
			} else {
				u = auth.authenticate(username, password, request.getSession());
			}
			if (u != null) {
				HttpSession session = request.getSession();
				if (session.getAttribute("user") ==  null || session.getAttribute("user") != u) {
					session.setAttribute("user", u);
					// then they were successfully logged in, so we should send them to their inbox
					response.sendRedirect("/webmail/inbox");
				} else {
					message = "Session was already set to " + ((User)session.getAttribute("user")).getUserName() + "<a href=\"/webmail/logout\">Logout</a> first";
				}
			}
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h1>Login</h1>");
		out.println("<form method=\"POST\" action=\"/webmail/authlogin\">");
		out.println("<table border=\"0\"");
		if (message != null) {
			out.println("<b>" + message + "</b><br />");
		}
		if (username != null) {
			out.println("<tr><td>username</td><td><input type=\"text\" value=\"" + username + "\" name=\"username\" /></td></tr>");
		} else {
			out.println("<tr><td>username</td><td><input type=\"text\" name=\"username\" /></td></tr>");
		}
		out.println("<tr><td>password</td><td><input type=\"password\" name=\"password\" /></td></tr>");
		out.println("<tr><td><input type=\"submit\" value=\"login\"></td></tr>");
		out.println("</table>");
		out.println("</form></body>");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		doGet(request, response);
	}
}
