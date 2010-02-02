package org.joe_e.servlet.perf;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class IndexServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.getAttribute("username") != null) {
			res.sendRedirect("/perf/inbox");
		}
		PrintWriter out = res.getWriter();
		out.println("<html>\n<head>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>Joe-E Mail</title><link href=\"static/css/index.css\" rel=\"stylesheet\" type=\"text/css\">\n"+
			"</head>\n<body>\n<p>Welcome to Joe-E mail</p>\n<a href=\"/perf/login\">Log In</a>\n<br>\n<a href=\"/perf/create\">Create an Account</a>\n<br>\n<a href=\"/perf/\">Stay here</a><br />\n");
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