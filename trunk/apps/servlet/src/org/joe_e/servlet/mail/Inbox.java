package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;

public class Inbox extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		@readonly public String username;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses)
		throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<h4>inbox of " + session.username + "</h4>");
		out.println("<a href=\"/servlet/logout\">logout</a>");
		out.println("</body>");
		HtmlWriter.printFooter(out);
	}
}
