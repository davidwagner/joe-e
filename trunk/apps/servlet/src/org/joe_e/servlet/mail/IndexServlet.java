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
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;
import org.joe_e.servlet.CajaVerifier;

/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	// NOTE: making this public is OK, b/c you still need an reference to this
	// servlet to get ahold of the session members we expose in this view.
	public SessionView session;
	public CookieView cookies;
	
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
		public void setTestCookie(String arg) {
			boolean done = false;
			for (Cookie c : cookies) {
				if (c.getName().equals("__joe-e__testCookie")) {
					c.setValue(arg);
					done = true;
				}
			}
			if (!done) {
				cookies.add(new Cookie("__joe-e__testCookie", arg));
			}
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
		}
		//		String output = CajaVerifier.cajole("<body><p>Hello World</p><script type=\"text/javascript\">alert(\"hello world\");</script></body>");
		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		//		out.println("<body><p>Hello World</p><script type=\"text/javascript\">alert(\"hello world\");</script></body>");
		out.println("<body>" +
			    "<p>Welcome to Joe-E mail</p>" +
			    "<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a><br />");
		out.println("<a href=\"/servlet/\">Stay here</a><br />");
		out.println(cookies.getTestCookie()+ "<br />");
		out.println("token: " + session.getToken()+"<br />");
    //		out.println(output);
		out.println("</body>");
		HtmlWriter.printFooter(out);
		if (cookies.getTestCookie() == null) {
			cookies.setTestCookie("1");
		} else {
			cookies.setTestCookie("" + (Integer.parseInt(cookies.getTestCookie())+1));
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		this.doGet(req, res);
	}
}
