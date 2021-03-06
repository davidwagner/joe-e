package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.mail.notjoe_e.TransportAgent;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Compose extends JoeEServlet {
	public static final long serialVersionUID = 1L;

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public String getErrorMessage() {
			return (String) session.getAttribute("__joe-e__errorMessage");
		}
		public void setErrorMessage(String arg) {
			session.setAttribute("__joe-e__errorMessage", arg);
		}
	    public TransportAgent getTransportAgent() {
		return (TransportAgent) session.getAttribute("__joe-e__transportAgent");
	    }
	}
	
	public AbstractSessionView getSessionView(HttpSession ses) {
		return new SessionView(ses);
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public AbstractCookieView getCookieView(Cookie[] c) {
		return new CookieView(c);
	}

	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c) 
		throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		ResponseDocument doc = res.getDocument();
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h6");
		tmp.appendChild(doc.createTextNode("signed in as " + session.getUsername()));
		body.appendChild(tmp);
		
		tmp = doc.createElement("h4");
		tmp.appendChild(doc.createTextNode("Compose Email"));
		body.appendChild(tmp);
		
		if (session.getErrorMessage() != null) {
			tmp = doc.createElement("b");
			tmp.appendChild(doc.createTextNode(session.getErrorMessage()));
			body.appendChild(tmp);
			session.setErrorMessage(null);
		}
		
		Element form = doc.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", "/servlet/compose");
		Element table = doc.createElement("table");
		table.setAttribute("border", "0");
		
		// the to field
		Node tr = table.appendChild(doc.createElement("tr"));
		Node td = tr.appendChild(doc.createElement("td"));
		td.appendChild(doc.createTextNode("To:"));
		td = tr.appendChild(doc.createElement("td"));
		Element input = doc.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("value", "");
		input.setAttribute("name", "to");
		td.appendChild(input);
		
		// the subject field
		tr = table.appendChild(doc.createElement("tr"));
		td = tr.appendChild(doc.createElement("td"));
		td.appendChild(doc.createTextNode("Subject:"));
		td = tr.appendChild(doc.createElement("td"));
		input = doc.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("value", "");
		input.setAttribute("name", "subject");
		td.appendChild(input);
		
		// the body field
		tr = table.appendChild(doc.createElement("tr"));
		td = tr.appendChild(doc.createElement("td"));
		td.appendChild(doc.createTextNode("To:"));
		td = tr.appendChild(doc.createElement("td"));
		input = doc.createElement("textarea");
		input.setAttribute("name", "body");
		td.appendChild(input);
		
		form.appendChild(table);
		input = doc.createElement("input");
		input.setAttribute("type", "submit");
		input.setAttribute("value", "send email");
		form.appendChild(input);
		body.appendChild(form);
	}
	
	public void doPost(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c)
		throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
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
		    session.getTransportAgent().send(msg);
		} catch (Exception e) {
		        session.setErrorMessage("error in sending");
			res.sendRedirect("/servlet/compose");
			return;
		}
		res.sendRedirect("/servlet/inbox");
	}
}
