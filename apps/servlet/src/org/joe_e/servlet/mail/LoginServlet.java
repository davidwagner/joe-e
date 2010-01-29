package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Element;


public class LoginServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public void setUsername(String arg) {
			session.setAttribute("__joe-e__username", arg);
		}
		public AuthenticationAgent getAuth() {
			return (AuthenticationAgent) session.getAttribute("__joe-e__auth");
		}
		public void setAuth(AuthenticationAgent arg) {
			session.setAttribute("__joe-e__auth", arg);
		}
		public File getMailbox() {
			return (File) session.getAttribute("__joe-e__mailbox");
		}
		public void setMailbox(File arg) {
			session.setAttribute("__joe-e__mailbox", arg);
		}
		public String getToken() {
			return (String) session.getAttribute("LoginServlet__token");
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
		public String getTestCookie() {
			for (Cookie c : cookies) {
				if (c.getName().equals("__joe-e__testCookie")) {
					return c.getValue();
				}
			}
			return null;
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		res.addHeader("Content-type", "text/html");
		ResponseDocument doc = ((ServletResponseWrapper)res).getDocument();
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		if (session.getErrorMessage() != null && !session.getErrorMessage().equals("")) {
			tmp = doc.createElement("b");
			tmp.appendChild(doc.createTextNode(session.getErrorMessage()));
			body.appendChild(tmp);	
			session.setErrorMessage("");
		}
		tmp = doc.createElement("b");
		tmp.appendChild(doc.createTextNode("Log in"));
		body.appendChild(tmp);
		
		tmp = doc.createElement("form");
		tmp.setAttribute("method", "POST");
		tmp.setAttribute("action", "/servlet/login");
		Element span = doc.createElement("span");
		span.appendChild(doc.createTextNode("Username: "));
		Element input = doc.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("value", "");
		input.setAttribute("name", "username");
		span.appendChild(input);
		tmp.appendChild(span);
		tmp.appendChild(doc.createElement("br"));
		
		span = doc.createElement("span");
		span.appendChild(doc.createTextNode("Password: "));
		input = doc.createElement("input");
		input.setAttribute("type", "password");
		input.setAttribute("value", "");
		input.setAttribute("name", "password");
		span.appendChild(input);
		tmp.appendChild(span);
		tmp.appendChild(doc.createElement("br"));
		
		input = doc.createElement("input");
		input.setAttribute("type", "hidden");
		input.setAttribute("value", session.getToken());
		input.setAttribute("name", "secret");
		tmp.appendChild(input);
		
		input = doc.createElement("input");
		input.setAttribute("type", "submit");
		input.setAttribute("value", "login");
		tmp.appendChild(input);
		body.appendChild(tmp);

		body.appendChild(tmp);
		body.appendChild(doc.createTextNode(session.getToken()));
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		String name = req.getParameter("username");
		String password = req.getParameter("password");
		String secret = req.getParameter("secret");
		if (!session.getToken().equals(secret)) {
                        session.setErrorMessage("XSRF attempt");
			res.sendRedirect("/servlet/login");
			return;
		}
		File mailbox = null;
		if ((mailbox = session.getAuth().authenticate(name, password)) != null) {
			session.setAuth(null);
			session.setUsername(name);
			session.setMailbox(mailbox);
			res.sendRedirect("/servlet/inbox");
		}
		else {
			session.setErrorMessage("Unable to authenticate");
			res.sendRedirect("/servlet/login");
		}
	}
}
