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
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.readonly;
import org.joe_e.servlet.Dispatcher;

public class Inbox extends JoeEServlet {

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
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractCookieView cookies)
		throws IOException, ServletException {
		if (session.getUsername() == null) {
		    Dispatcher.logger.fine("redirecting to /servlet/login");
		    res.sendRedirect("/servlet/login");
		    return;
		}
		res.addHeader("Content-type", "text/html");
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<h4> inbox of " + session.getUsername() + "</h4>");
		out.println("<a href=\"/servlet/compose\">Write an email</a><br />");
		File maildir = Filesystem.file(session.getMailbox(), "Maildir");
		File newFolder = Filesystem.file(maildir, "new");
		for (File f : Filesystem.list(newFolder)) {
		    Dispatcher.logger.finest(f.getCanonicalPath());
			Reader reader = ASCII.input(Filesystem.read(f));
			BufferedReader in = new BufferedReader(reader);
			String line = "";
			String id = f.getName();
			String subject = "";
			while ((line = in.readLine()) != null) {
				if (line.length() > 7 && line.substring(0, 7).equals("Subject")) {
					subject = line.substring(8);
				}
			}
			if (!"".equals(id) && !"".equals(subject)) {
				// TODO: on this request, how do we give only that message to the read servlet
				// we can't put it in the session now b/c it has to be dynamic. 
				out.println("<a href=\"/servlet/read?id="+id+"\">"+subject+"</a><br />");
			}
		}
		
		out.println("<a href=\"/servlet/logout\">logout</a><br />");
		out.println("</body>");
		HtmlWriter.printFooter(out);
	}
}
