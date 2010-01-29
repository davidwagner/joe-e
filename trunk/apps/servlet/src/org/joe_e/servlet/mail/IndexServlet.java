package org.joe_e.servlet.mail;

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
import org.joe_e.servlet.response.ResponseElement;
import org.joe_e.servlet.response.ResponseUrl;
import org.joe_e.servlet.response.ServletResponseWrapper;

/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super (ses);
		}
		
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public String getToken() {
			return (String) session.getAttribute("IndexServlet__token");
		}
	}
	
	public AbstractSessionView getSessionView(HttpSession ses) {
		return new SessionView(ses);
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
		public String getTestCookie() {
			for (Cookie c : updatedCookies) {
				if (c.getName().equals("__joe-e__testCookie")) {
					return c.getValue();
				}
			}
			for (Cookie c : recievedCookies) {
				if (c.getName().equals("__joe-e__testCookie")) {
					return c.getValue();
				}
			}
			return null;
		}
		public void setTestCookie(String arg) {
			boolean done = false;
			for (Cookie c : updatedCookies) {
				if (c.getName().equals("__joe-e__testCookie")) {
					c.setValue(arg);
					done = true;
				}
			}
			if (!done) {
				updatedCookies.add(new Cookie("__joe-e__testCookie", arg));
			}
		}
	}
	
	public AbstractCookieView getCookieView(Cookie[] c) {
		return new CookieView(c);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		CookieView cookies = (CookieView) c;
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
		}
		res.addHeader("Content-type", "text/html");
		ResponseDocument doc = ((ServletResponseWrapper)res).getDocument();
		ResponseElement body = HtmlWriter.printHeader(doc);

		ResponseElement tmp = doc.createElement("p");
		body.appendChild(tmp);
		tmp.appendChild(doc.createTextNode("Welcome to Joe-E mail"));
		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/login", null));
		tmp.appendChild(doc.createTextNode("Log In"));
		body.appendChild(tmp);
		body.appendChild(doc.createElement("br"));

		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/create", null));
		tmp.appendChild(doc.createTextNode("Create an Account"));
		body.appendChild(tmp);
		body.appendChild(doc.createElement("br"));
		
		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/", null));
		tmp.appendChild(doc.createTextNode("Stay here"));
		body.appendChild(tmp);
		body.appendChild(doc.createElement("br"));

		if (cookies.getTestCookie() != null) {
		    body.appendChild(doc.createTextNode(cookies.getTestCookie()));
		}
		body.appendChild(doc.createElement("br"));

		body.appendChild(doc.createTextNode("token: " + session.getToken()));
		body.appendChild(doc.createElement("br"));		

		if (cookies.getTestCookie() == null) {
			cookies.setTestCookie("1");
		} else {
			cookies.setTestCookie("" + (Integer.parseInt(cookies.getTestCookie())+1));
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		this.doGet(req, res, ses, c);
	}
}
