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

public class CreateAccount extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		@readonly public String username;
		public AccountManager manager;
	}
	
	public class CookieView extends AbstractCookieView {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies)
		throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		if (session.username != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<p>Create Account</p>");
		out.println("<form method=\"POST\" action=\"/servlet/create\">");
		out.println("<span>Choose a username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Choose a password: <input type=\"password\" value=\"\" name=\"password1\" /></span>");
		out.println("<span>Re-enter password: <input type=\"password\" value=\"\" name=\"password2\" /></span>");
		out.println("<input type=\"submit\" value=\"create\"></form></body>");
		HtmlWriter.printFooter(out);
		out.flush();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies)
		throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		String name = req.getParameter("username");
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		if (password1.equals(password2) && session.manager.addAccount(name, password1)) {
			res.sendRedirect("/servlet/login");
		} else {
			res.sendRedirect("/servlet/create");
		}
	}
}
