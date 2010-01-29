package org.joe_e.servlet.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CreateAccount extends JoeEServlet {
	public static final long serialVersionUID = 1L;

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
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
		public String getErrorMessage() {
			return (String) session.getAttribute("__joe-e__errorMessage");
		}
		public void setErrorMessage(String arg) {
			session.setAttribute("__joe-e__errorMessage", arg);
		}
		public AccountManager getManager() {
			return (AccountManager) session.getAttribute("__joe-e__manager");
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
		throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		ResponseDocument doc = res.getDocument();
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		
		tmp = doc.createElement("p");
		tmp.appendChild(doc.createTextNode("Create Account"));
		body.appendChild(tmp);
		if (session.getErrorMessage() != null && !session.getErrorMessage().equals("")) {
			tmp = doc.createElement("b");
			tmp.appendChild(doc.createTextNode(session.getErrorMessage()));
			body.appendChild(tmp);
			session.setErrorMessage("");
		}
		
		tmp = doc.createElement("form");
		body.appendChild(tmp);
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

	public void doPost(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c)
		throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		String name = req.getParameter("username");
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		String secret = req.getParameter("secret");
		if (!secret.equals(session.getToken())) {
			session.setErrorMessage("XSRF attempt");
			res.sendRedirect("/servlet/create");
		}
		
		if (password1.equals(password2) && session.getManager().addAccount(name, password1)) {
			res.sendRedirect("/servlet/login");
		} else {
			res.sendRedirect("/servlet/create");
		}
	}
}
