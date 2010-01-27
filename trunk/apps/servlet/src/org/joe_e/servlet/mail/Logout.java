package org.joe_e.servlet.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;

public class Logout extends JoeEServlet {

	public SessionView session;
	public CookieView cookies;
	
	public class SessionView extends AbstractSessionView {
//		public boolean invalidate;
		public HttpSession session;
		public SessionView(HttpSession ses) {
			super(ses);
			session = ses;
		}
		public void invalidate() {
			session.invalidate();
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws IOException, ServletException {
	    Dispatcher.logger.fine("in doGet of Logout");
		session.invalidate();
		res.sendRedirect("/servlet/");
		return;
	}
}
