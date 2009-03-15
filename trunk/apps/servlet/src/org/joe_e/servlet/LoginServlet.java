package org.joe_e.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends JoeEServlet {

	public class SessionView extends org.joe_e.servlet.SessionView {
		String name;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res,  org.joe_e.servlet.SessionView ses) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		out.println("login " + session.name);
		out.flush();
	}
}
