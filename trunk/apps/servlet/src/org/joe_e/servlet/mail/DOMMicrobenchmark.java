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
import org.joe_e.servlet.response.ResponseElement;
import org.joe_e.servlet.response.ResponseUrl;
import org.joe_e.servlet.response.ServletResponseWrapper;

public class DOMMicrobenchmark extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView (Cookie[] c) {
			super (c);
		}
	}
	
	public AbstractSessionView getSessionView(HttpSession ses) {
		return new SessionView(ses);
	}
	public AbstractCookieView getCookieView(Cookie[] c) {
		return new CookieView(c);
	}
	
	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		ResponseDocument doc = res.getDocument();
		ResponseElement body = HtmlWriter.printHeader(doc);
		for (int i = 0; i < 100; i++) {
			ResponseElement tmp = doc.createElement("p");
			ResponseElement link = doc.createElement("a");
			link.addLinkAttribute("href", new ResponseUrl(0, "www.google.com", null, null, null));
			tmp.appendChild(doc.createTextNode("blah blah blah"));
			link.appendChild(doc.createTextNode("google"));
			body.appendChild(tmp);
			body.appendChild(link);
		}
	}
}
