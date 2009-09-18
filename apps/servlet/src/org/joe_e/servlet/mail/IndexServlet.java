package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JSLintVerifier;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

/**
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		@readonly public String username;
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
		// TODO: this may be how we have to call Adsafe, instead of writing to the printWriter, these routines return a 
		// string of HTML that we send to jslint and then write to the PrintWriter.
		String output = HtmlWriter.getHeader();
		output += "<body><p>Welcome to Joe-E Mail</p><a href=\"/servlet/login\">Log In</a><br />"+
				  "<a href=\"/servlet/create\">Create an Account</a><br />";
		output += "<script>function f() {\n\talert(\"hello world\"); \n}</script>";
		output += "<a href=\"/servlet/\">Stay here</a><br />" + cookie.testCookie + "<br /></body>"+HtmlWriter.getFooter();
		
		if (!JSLintVerifier.verify(output)) {
			throw new ServletException ("Illegal javascript: " + JSLintVerifier.getMessage());
		}
		out.println("<body>" +
				"<p>Welcome to Joe-E mail</p>" +
				"<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a><br />");
		out.println("<a href=\"/servlet/>Stay here</a><br />");
		out.println(cookie.testCookie+ "<br />");
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
