package org.joe_e.servlet.mail;

import java.io.File;
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


public class LoginServlet extends JoeEServlet {

	public SessionView session;
	
	public class SessionView extends AbstractSessionView {
//		public String username;
//		public AuthenticationAgent auth;
//		public File mailbox;
//		@readonly public String token;
//		public String errorMessage;
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
		public AuthenticationAgent getAuth() {
			return (AuthenticationAgent) session.getAttribute("__joe-e__auth");
		}
		public void setAuth(AuthenticationAgent arg) {
			session.setAttribute("__joe-e__auth", arg);
		}
		public File getMailbox() {
			return (File) session.getAttribute("__joe-e__mailbox");
		}
		public void setMailbox(File arg) {
			session.setAttribute("__joe-e__mailbox", arg);
		}
		public String getToken() {
			return (String) session.getAttribute("LoginServlet__token");
		}
		public String getErrorMessage() {
			return (String) session.getAttribute("__joe-e__errorMessage");
		}
		public void setErrorMessage(String arg) {
			session.setAttribute("__joe-e__errorMessage", arg);
		}
	}
	
	public class CookieView extends AbstractCookieView {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		if (session.getUsername() != null) {
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
		if (session.getErrorMessage() != null) {
			out.println("<b>"+session.getErrorMessage()+"</b>");
		}
		out.println("<p>Log in</p>");
		out.println("<form method=\"POST\" action=\"/servlet/login\">");
		out.println("<span>Username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Password: <input type=\"password\" value=\"\" name=\"password\" /></span>");
		out.println("<input type=\"submit\" value=\"login\"></form></body>");
		out.println("token: " + session.getToken() + "<br />");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies) throws ServletException, IOException {
		String name = req.getParameter("username");
		String password = req.getParameter("password");
		File mailbox = null;
		if ((mailbox = session.getAuth().authenticate(name, password)) != null) {
			session.setAuth(null);
			session.setUsername(name);
			session.setMailbox(mailbox);
			res.sendRedirect("/servlet/inbox");
		}
		else {
			session.setErrorMessage("Unable to authenticate");
			res.sendRedirect("/servlet/login");
		}
	}
}
