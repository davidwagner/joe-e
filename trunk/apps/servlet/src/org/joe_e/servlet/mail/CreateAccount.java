package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

public class CreateAccount extends JoeEServlet {

	public SessionView session;
	
	public class SessionView extends AbstractSessionView {
//		public String username;
//		@readonly public AccountManager manager;
		private HttpSession session;
		
		public SessionView(HttpSession ses) {
			super(ses);
			session = ses;
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public void setUsername(String arg) {
			session.setAttribute("__joe-e__username", arg);
		}
		public AccountManager getManager() {
			return (AccountManager) session.getAttribute("__joe-e__manager");
		}
	}
	
	public class CookieView extends AbstractCookieView {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies)
		throws IOException, ServletException {
		PrintWriter out = res.getWriter();
		if (session.getUsername() != null) {
			res.sendRedirect("/servlet/inbox");
			return;
		}
		res.addHeader("Content-type", "text/html");
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<p>Create Account</p>");
		out.println("<form method=\"POST\" action=\"/servlet/create\">");
		out.println("<span>Choose a username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Choose a password: <input type=\"password\" value=\"\" name=\"password1\" /></span>");
		out.println("<span>Re-enter password: <input type=\"password\" value=\"\" name=\"password2\" /></span>");
		out.println("<input type=\"submit\" value=\"create\"></form></body>");
		HtmlWriter.printFooter(out);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies)
		throws IOException, ServletException {
		String name = req.getParameter("username");
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		if (password1.equals(password2) && session.getManager().addAccount(name, password1)) {
			res.sendRedirect("/servlet/login");
		} else {
			res.sendRedirect("/servlet/create");
		}
	}
}
