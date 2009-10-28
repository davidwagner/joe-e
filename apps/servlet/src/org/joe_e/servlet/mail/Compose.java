package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

public class Compose extends JoeEServlet {
	
	public SessionView session; 
	public CookieView cookies;
	
	public class SessionView extends AbstractSessionView {
		//@readonly public String username;
		//@readonly public File mailbox;
		//public String errorMessage;
		private HttpSession session;
		public SessionView(HttpSession ses) {
			super(ses);
			this.session = ses;
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public String getToken() {
			return (String) session.getAttribute("Compose__token");
		}
		public File getMailbox() {
			return (File) session.getAttribute("__joe-e__mailbox");
		}
		public String getErrorMessage() {
			return (String) session.getAttribute("__joe-e__errorMessage");
		}
		public void setErrorMessage(String arg) {
			session.setAttribute("__joe-e__errorMessage", arg);
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException {
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h6>signed in as " + session.getUsername() + "</h6>");
		out.println("<h4>Compose Email</h4>");
		if (session.getErrorMessage() != null) {
			out.println("<b>" + session.getErrorMessage() + "</b>");
			session.setErrorMessage(null);
		}
		out.println("<form method=\"POST\" action=\"/servlet/compose\">");
		out.println("<table border=\"0\">");
		out.println("<tr><td>To:</td><td><input type=\"text\" value=\"\" name=\"to\" /></td></tr>");
		out.println("<tr><td>Subject:</td><td><input type=\"text\" value=\"\" name=\"subject\" /></td></tr>");
		out.println("<tr><td>Body:</td><td><textarea name=\"body\"></textarea></td></tr>");
		out.println("<tr><td><input type=\"submit\" value=\"send\" name=\"send\" /></td></tr>");
		out.println("</table></body>");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
		}
		
		String to = req.getParameter("to");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		if (to == null || subject == null || body == null
				|| to.equals("") || subject.equals("") || body.equals("") ) {
			session.setErrorMessage("Please fill out all fields");
			res.sendRedirect("/servlet/compose");
			return;
		}
		
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "10025");
		
		Session mailSession = Session.getDefaultInstance(props, null);
		javax.mail.Message msg = new MimeMessage(mailSession);
		try {
			msg.setText(body);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(session.getUsername() + "@boink.joe-e.org"));
			msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		} catch (Exception e) {
			session.setErrorMessage("There something wrong, please try again");
			res.sendRedirect("/servlet/compose");
			return;
		}
		
		try {
	        Transport.send(msg);
		} catch (Exception e) {
		        session.setErrorMessage("error in sending: " + e.getMessage());
			res.sendRedirect("/servlet/compose");
			return;
		}
		res.sendRedirect("/servlet/inbox");
	}
}
