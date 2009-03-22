package org.joe_e.servlet.mail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;


public class LoginServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public String name;
		@readonly public File mailbox;
		// TODO: read only? maybe reflective constructor
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res,  AbstractSessionView ses) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		if (session.name == null) {
			out.println("<html><head><title>Joe-E Mail</title></head>");
			out.println("<body>I don't know your name yet");
			out.println("<form method=\"POST\" action=\"/servlet/login\"> <input type=\"text\" value=\"\" name=\"name\" />");
			out.println("<input type=\"submit\" value=\"login\"></form></body>");
		} else {
			out.println("Welcome " + session.name);
		}
		out.flush();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		String name = req.getParameter("name");
		if (name != null) {
			session.name = name;
		}
		session.mailbox = new File("/");
		doGet(req, res, session);
	}
}
