package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.JoeEServlet;

public class IndexServlet extends JoeEServlet {

	public class SessionView extends org.joe_e.servlet.SessionView {
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, org.joe_e.servlet.SessionView ses) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		out.println("<html><head><title>Joe-E Mail</title></head>");
		out.println("<body><a href=\"/servlet/login\">Log In</a></body></html>");
		out.flush();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, org.joe_e.servlet.SessionView ses) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		out.println("<html><head><title>Joe-E Mail</title></head>");
		out.println("<body><a href=\"/login\">Log In</a></body></html>");
		out.flush();
	}
}
