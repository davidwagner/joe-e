package org.joe_e.servlet.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;
import org.joe_e.servlet.Dispatcher;

public class Read extends JoeEServlet {

	public SessionView session;
	
	public class SessionView extends AbstractSessionView {
//		@readonly public String username;
//		@readonly public File mailbox;
		private HttpSession session;
		
		public SessionView(HttpSession ses) {
			super(ses);
			session = ses;
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public File getMailbox() {
			return (File) session.getAttribute("__joe-e__mailbox");
		}
	}
	
	public class CookieView extends AbstractCookieView {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies) throws ServletException, IOException {
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		if (session.getUsername() == null) {
		    res.sendRedirect("/servlet/login");
		}
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		String msgName = req.getParameter("id");
		File maildir = Filesystem.file(session.getMailbox(), "Maildir");
		File newFolder = Filesystem.file(maildir, "new");
		for (File f : Filesystem.list(newFolder)) {
			if (f.getName().equals(msgName)) {
				Reader reader = ASCII.input(Filesystem.read(f));
				BufferedReader in = new BufferedReader(reader);
				String line = "";
				out.println("<p>");
				while ((line = in.readLine()) != null) {
					out.println(line);
				}
				out.println("</p>");
			}
		}
		out.println("<a href=\"/servlet/inbox\">Back to Inbox</a>");
		out.println("<div id=\"INBOX_\"></div>");
		HtmlWriter.printFooter(out);
	}
}
