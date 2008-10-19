package org.joe_e.webmail;

import java.io.*;
import java.security.NoSuchAlgorithmException;

import javax.servlet.*;
import javax.servlet.http.*;

public class CreateAccount extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String message = null;
		
		if (username != null && password != null) {
			// here we need make a call to create their account
			try {
				Authentication auth = (Authentication) request.getSession().getAttribute("auth");
				auth.addAccount(username, password, request.getSession());
				response.sendRedirect("/webmail/login");
			} catch (Exception e) {
				message = "unable to add account";
			}
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h1>Create Account</h1>");
		out.println("<form method=\"POST\" action=\"/webmail/authcreate\">");
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
		out.println("<tr><td><input type=\"submit\" value=\"create account\"></td></tr>");
		out.println("</table>");
		out.println("</form></body>");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
			doGet(request, response);
		}
}
