package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;


public class LoginServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public String username;
		public AuthenticationAgent auth;
		public File mailbox;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res,  AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		if (session.username != null) {
			res.sendRedirect("/servlet/inbox");
		}
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<p>Log in</p>");
		out.println("<form method=\"POST\" action=\"/servlet/login\">");
		out.println("<span>Username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Password: <input type=\"password\" value=\"\" name=\"password\" /></span>");
		out.println("<input type=\"submit\" value=\"login\"></form></body>");
		HtmlWriter.printFooter(out);
		out.flush();
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
			doGet(req, res, ses, null);
		}
	}
}
