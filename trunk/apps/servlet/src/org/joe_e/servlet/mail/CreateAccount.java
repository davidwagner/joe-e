package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CreateAccount extends JoeEServlet {

	public SessionView session;
	public CookieView cookies;
	
	public class SessionView extends AbstractSessionView {
		private HttpSession session;
		
		public SessionView(HttpSession ses) {
			super(ses);
			session = ses;
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public String getToken() {
			return (String) session.getAttribute("CreateAccount__token");
		}
		public void setUsername(String arg) {
			session.setAttribute("__joe-e__username", arg);
		}
		public AccountManager getManager() {
			return (AccountManager) session.getAttribute("__joe-e__manager");
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {
		Document doc = ((ServletResponseWrapper)res).getDocument();
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		res.addHeader("Content-type", "text/html");
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		
		tmp = doc.createElement("p");
		tmp.appendChild(doc.createTextNode("Create Account"));
		body.appendChild(tmp);
		
		tmp = doc.createElement("form");
		tmp.setAttribute("method", "POST");
		tmp.setAttribute("action", "/servlet/create");
		
		Node span = tmp.appendChild(doc.createElement("span"));
		span.appendChild(doc.createTextNode("Choose a username: " ));
		Element input = doc.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("value", "");
		input.setAttribute("name", "username");
		span.appendChild(input);
		
		span = tmp.appendChild(doc.createElement("span"));
		span.appendChild(doc.createTextNode("Choose a password: " ));
		input = doc.createElement("input");
		input.setAttribute("type", "password");
		input.setAttribute("value", "");
		input.setAttribute("name", "password1");
		span.appendChild(input);
		
		span = tmp.appendChild(doc.createElement("span"));
		span.appendChild(doc.createTextNode("Re-enter password: " ));
		input = doc.createElement("input");
		input.setAttribute("type", "password");
		input.setAttribute("value", "");
		input.setAttribute("name", "password2");
		span.appendChild(input);
		
		// session token
		input = doc.createElement("input");
		input.setAttribute("type", "hidden");
		input.setAttribute("value", session.getToken());
		input.setAttribute("name", "secret");
		tmp.appendChild(input);
		
		input = doc.createElement("input");
		input.setAttribute("type", "submit");
		input.setAttribute("value", "create");
		tmp.appendChild(input);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {
		String name = req.getParameter("username");
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		if (password1.equals(password2) && session.getManager().addAccount(name, password1)) {
			res.sendRedirect("/servlet/login");
		} else {
			res.sendRedirect("/servlet/create");
		}
	}
}
