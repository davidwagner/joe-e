package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		@readonly public String username;
		@readonly public String token;
	}
	
	public class CookieView extends AbstractCookieView {
		public String testCookie;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		CookieView cookie = (CookieView) cookies;
		if (session.username != null) {
			res.sendRedirect("/servlet/inbox");
		}
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body>" +
				"<p>Welcome to Joe-E mail</p>" +
				"<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a><br />");
		out.println("<a href=\"/servlet/\">Stay here</a><br />");
		out.println(cookie.testCookie+ "<br />");
		out.println("token: " + session.token+"<br />");
		out.println("<div id=\"TOKEN_\"></div>");
		out.println("</body>");
		HtmlWriter.printFooter(out);
		if (cookie.testCookie.equals("")) {
			cookie.testCookie = "1";
		} else {
			cookie.testCookie = "" + (Integer.parseInt(cookie.testCookie)+1);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
		this.doGet(req, res, ses, null);
	}
}
