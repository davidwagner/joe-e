package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class LoginServlet extends JoeEServlet {

	public SessionView session;
	public CookieView cookies;
	
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
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		res.addHeader("Content-type", "text/html");
		Document doc = ((ServletResponseWrapper)res).getDocument();
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		if (Dispatcher.getErrorMessage() != null) {
			tmp = doc.createElement("b");
			tmp.appendChild(doc.createTextNode(Dispatcher.getErrorMessage()));
			body.appendChild(tmp);
			Dispatcher.setErrorMessage(null);
		}
		if (session.getErrorMessage() != null) {
			tmp = doc.createElement("b");
			tmp.appendChild(doc.createTextNode(session.getErrorMessage()));
			body.appendChild(tmp);			
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
		
		span = doc.createElement("span");
		span.appendChild(doc.createTextNode("Password: "));
		input = doc.createElement("input");
		input.setAttribute("type", "password");
		input.setAttribute("value", "");
		input.setAttribute("name", "password");
		span.appendChild(input);
		tmp.appendChild(span);
		
		input = doc.createElement("input");
		input.setAttribute("type", "submit");
		input.setAttribute("value", "login");
		tmp.appendChild(input);
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String name = req.getParameter("username");
		String password = req.getParameter("password");
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
