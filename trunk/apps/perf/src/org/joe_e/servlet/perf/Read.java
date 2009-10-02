package org.joe_e.servlet.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Read extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		PrintWriter out = res.getWriter();
		if (session.getAttribute("username") == null) {
			res.sendRedirect("/perf/login");
		}
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		String msgName = req.getParameter("id");
		
		File maildir = null, newFolder = null;
		for (File f : ((File) session.getAttribute("mailbox")).listFiles()) {
			if (f.getName().equals("Maildir")) {
				maildir = f;
				break;
			}
		}
		for (File f : maildir.listFiles()) {
			if (f.getName().equals("new")) {
				newFolder = f;
				break;
			}
		}
		
		for (File f : newFolder.listFiles()) {
			if (f.getName().equals(msgName)) {
				BufferedReader in = new BufferedReader(new FileReader(f));
				String line = "";
				out.println("<p>");
				while ((line = in.readLine()) != null) {
					out.println(line);
				}
				out.println("</p>");
			}
		}
		out.println("<a href=\"/perf/inbox\">Back to Inbox</a>");
		HtmlWriter.printFooter(out);
	}

}
