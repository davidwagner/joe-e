package org.joe_e.webmail;

import java.io.*;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

public class WebForm extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		HtmlWriter.printHeader(out);
		out.println("<body><h1>Information</h1>");
		out.println("<form method=\"POST\" action=\"/webmail/webform\">");
		out.println("<table border=\"0\"");
		out.println("<tr><td>First Name:</td><td><input type=\"text\" name=\"fname\" /></td></tr>");
		out.println("<tr><td>Last Name:</td><td><input type=\"text\" name=\"lname\" /></td></tr>");
		out.println("<tr><td><input type=\"submit\" value=\"submit\"></td></tr>");
		out.println("</table>");
		out.println("</form></body>");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h1>Information</h1>");
		Enumeration<String> params = request.getParameterNames();
		out.println("<ul>");
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			out.println("<li>" + param + ": " + request.getParameter(param) + "</li>");
		}
		out.println("</ul>");
		HtmlWriter.printFooter(out);
	}
}
