package org.joe_e.webmail;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.MessagingException;

public class Compose extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		/*
		 * first check that the session is properly set.
		 * TODO this doesn't actually check what we want
		 */
		if (request.getSession().getAttribute("user") == null) {
			response.sendRedirect("/webmail/login");
		}
		
		User user = (User) request.getSession().getAttribute("user");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h6>signed in as " + user.getUserName() + "</h6>");
		out.println("<h4>Compose Email</h4>");
		if (errorMessage != null) {
			
			out.println("<b>" + errorMessage + "</b>");
			errorMessage = null;
		}
		out.println("<form method=\"POST\" action=\"/webmail/compose\">");
		out.println("<table border=\"0\">");
		out.println("<tr><td>To:</td><td><input type=\"text\" value=\"\" name=\"to\" /></td></tr>");
		out.println("<tr><td>Subject:</td><td><input type=\"text\" value=\"\" name=\"subject\" /></td></tr>");
		out.println("<tr><td>Body:</td><td><textarea name=\"body\"></textarea></td></tr>");
		out.println("<tr><td><input type=\"submit\" value=\"send\" name=\"send\" /></td></tr>");
		out.println("</table></body>");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
			if (request.getSession().getAttribute("user") == null) {
				response.sendRedirect("/webmail/login");
			}
			
			User user = (User) request.getSession().getAttribute("user");
			
			String to = request.getParameter("to");
			String subject = request.getParameter("subject");
			String body = request.getParameter("body");
			if (to == null || subject == null || body == null
					|| to.equals("") || subject.equals("") || body.equals("") ) {
				errorMessage = "Please fill out all fields";
				//doGet(request, response);
				response.sendRedirect("/webmail/compose");
				return;
			}
			
			/*
			 * args are ok so we can send it to the outgoing mail client
			 */
			//Message message = new Message(user, to, subject, body);
			Properties props = new Properties();
			// fill props with whatever we wat
			// TODO what are we supposed to put in properties?
			// TODO for now we aren't using an Authenticator
			// TODO to accept mail from outside we need to get postfix to listen on port 25
			// because users have already been authenticated
			
			props.put("mail.smtp.host", "localhost");
			props.put("mail.smtp.port", "10025");
			
			Session session = Session.getDefaultInstance(props, null);
			javax.mail.Message msg = new MimeMessage(session);
			try {
				msg.setText(body);
				msg.setSubject(subject);
				msg.setFrom(new InternetAddress(user.getUserName() + "@boink.joe-e.org"));
				msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			} catch (Exception e) {
				errorMessage = "There something wrong, please try again";
				response.sendRedirect("/webmail/compose");
				//doGet(request, response);
				return;
			}
			
			try {
		        Transport.send(msg);
			} catch (Exception e) {
				errorMessage = "error in sending";
				response.sendRedirect("/webmail/compose");
				return;
			}
			response.sendRedirect("/webmail/inbox");
		}
	
	String errorMessage;
}
