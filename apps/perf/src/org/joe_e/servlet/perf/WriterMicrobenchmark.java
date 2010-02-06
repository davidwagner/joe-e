package org.joe_e.servlet.perf;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WriterMicrobenchmark extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		out.println("<html><head><title>Joe-E Mail</title><body>");
		for (int i = 0; i < 100; i++) {
			out.println("<p>blah blah blah</p><a href=\"http://www.google.com\">google</a>");
		}
	}
}
