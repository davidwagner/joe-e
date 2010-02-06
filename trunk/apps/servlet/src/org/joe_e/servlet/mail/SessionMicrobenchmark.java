package org.joe_e.servlet.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ServletResponseWrapper;

public class SessionMicrobenchmark extends JoeEServlet {
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
	
	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
	}
}
