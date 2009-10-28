package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	// NOTE: making this public is OK, b/c you still need an reference to this
	// servlet to get ahold of the session members we expose in this view.
	public SessionView session;
	
	public class SessionView extends AbstractSessionView {
		//@readonly public String username;
		//@readonly public String token;
		private HttpSession session;
		
		public SessionView(HttpSession ses) {
			super (ses);
			session = ses;
		}
		
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public String getToken() {
			return (String) session.getAttribute("IndexServlet__token");
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public String testCookie;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies) throws ServletException, IOException {
		CookieView cookie = (CookieView) cookies;
		Dispatcher.logger.fine("Inside INDEX SERVLET");
		if (session == null) {
			res.getWriter().println("SESSION IS NULL");
			return;
		}
//		if (super.getSession() == null) {
//			res.getWriter().println("SUPER SESSION IS NULL");
//			return;
//		}
		Dispatcher.logger.fine(session.toString());
		Dispatcher.logger.fine(session.getUsername());
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
		}
		Dispatcher.logger.fine("Inside INDEX SERVLET");

		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body>" +
				"<p>Welcome to Joe-E mail</p>" +
				"<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a><br />");
		out.println("<a href=\"/servlet/\">Stay here</a><br />");
//		out.println(cookie.testCookie+ "<br />");
		out.println("token: " + session.getToken()+"<br />");
		out.println("<div id=\"TOKEN_\"></div>");
		out.println("</body>");
		HtmlWriter.printFooter(out);
//		if (cookie.testCookie.equals("")) {
//			cookie.testCookie = "1";
//		} else {
//			cookie.testCookie = "" + (Integer.parseInt(cookie.testCookie)+1);
//		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies) throws ServletException, IOException {
		this.doGet(req, res, cookies);
	}
}
