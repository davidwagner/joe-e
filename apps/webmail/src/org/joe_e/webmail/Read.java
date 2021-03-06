package org.joe_e.webmail;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Servlet instance that displays an email message in the browser.
 * @author akshay
 *
 */
public class Read extends HttpServlet {

	/**
	 * Takes a message id as a GET parameter and prints out the 
	 * message corresponding to that id. 
	 * 
	 * Note: this method is part of the UI, it contains no
	 * security sensitive operations and thus can be modified in 
	 * its entirety
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		if (request.getSession().getAttribute("user") == null) {
			response.sendRedirect("/webmail/index");
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		String messageId = request.getParameter("id");
		if (messageId == null || messageId.equals("")) {
			out.println("message id is null");
			//response.sendRedirect("/webmail/inbox");
		} else {
			Message m = ((User) request.getSession().getAttribute("user")).getMessage(messageId);
			if (m == null) {
				out.println("message is null");
			} else {
				out.println("<h3>" + m.getSubject() + "</h3>");
				out.println("<h4>From: " + m.getSender() + "</h4>");
				out.println("<h4>Timestamp: " + m.getTimeStamp() + "</h4>");
				out.println("<h4>Id: " + m.getId() + "</h4>");
				out.println("<h4>Status: " + m.getStatus() + "</h4>");
				out.println("<p>" + m.getBody() + "</p>");
			}
		}
		HtmlWriter.printFooter(out);
	}
}