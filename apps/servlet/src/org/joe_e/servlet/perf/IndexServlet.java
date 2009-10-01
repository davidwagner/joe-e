package org.joe_e.servlet.perf;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.mail.HtmlWriter;

public class IndexServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.getAttribute("username") != null) {
			res.sendRedirect("/perf/inbox");
		}
		
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body>" +
				"<p>Welcome to Joe-E mail</p>" +
				"<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a><br />");
		out.println("<a href=\"/servlet/\">Stay here</a><br />");
		if (req.getCookies() != null) {
			boolean done = false;
			for (Cookie c : req.getCookies()) {
				if (c.getName().equals("__perf__testCookie")) {
					out.println(c.getValue() + "<br />");
					done = true;
					res.addCookie(new Cookie("__perf__testCookie", ""+(Integer.parseInt(c.getValue())+1)));
				}
			}
			if (!done) {
				out.println("<br />");
				res.addCookie(new Cookie("__perf__testCookie", ""+1));
			}
		} else {
			out.println("<br />");
			res.addCookie(new Cookie("__perf__testCookie", ""+1));
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		this.doGet(req, res);
	}
}
