package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;


public class LoginServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public String username;
		public AuthenticationAgent auth;
		public File mailbox;
		public String token;
		public String errorMessage;
	}
	
	public class CookieView extends AbstractCookieView {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res,  AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		if (session.username != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		res.addHeader("Content-type", "text/html");
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		if (Dispatcher.getErrorMessage() != null) {
			out.println("<b>"+Dispatcher.getErrorMessage()+"</b>");
			Dispatcher.setErrorMessage(null);
		}
		if (session.errorMessage != null) {
			out.println("<b>"+session.errorMessage+"</b>");
		}
		out.println("<p>Log in</p>");
		out.println("<form method=\"POST\" action=\"/servlet/login\">");
		out.println("<span>Username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Password: <input type=\"password\" value=\"\" name=\"password\" /></span>");
		out.println("<input type=\"submit\" value=\"login\"></form></body>");
		out.println("token: " + session.token + "<br />");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		String name = req.getParameter("username");
		String password = req.getParameter("password");
		File mailbox = null;
		if ((mailbox = session.auth.authenticate(name, password)) != null) {
			session.auth = null;
			session.username = name;
			session.mailbox = mailbox;
			res.sendRedirect("/servlet/inbox");
		}
		else {
			session.errorMessage = "Unable to authenticate";
			res.sendRedirect("/servlet/login");
		}
	}
}
