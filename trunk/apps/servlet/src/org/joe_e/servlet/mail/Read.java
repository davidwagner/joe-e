package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

public class Read extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		@readonly public String username;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("Hello");
		HtmlWriter.printFooter(out);
	}
}
