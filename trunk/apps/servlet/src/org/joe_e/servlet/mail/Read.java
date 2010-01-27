package org.joe_e.servlet.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Read extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public File getMailbox() {
			return (File) session.getAttribute("__joe-e__mailbox");
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		res.addHeader("Content-type", "text/html");
		Document doc = ((ServletResponseWrapper)res).getDocument();
		if (session.getUsername() == null) {
		    res.sendRedirect("/servlet/login");
		}
		Element body = HtmlWriter.printHeader(doc);
		
		Element tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		
		
		String msgName = req.getParameter("id");
		File maildir = Filesystem.file(session.getMailbox(), "Maildir");
		File newFolder = Filesystem.file(maildir, "new");
		for (File f : Filesystem.list(newFolder)) {
			if (f.getName().equals(msgName)) {
				Reader reader = ASCII.input(Filesystem.read(f));
				BufferedReader in = new BufferedReader(reader);
				String line = "";
				String total = "";
				tmp = doc.createElement("p");
				while ((line = in.readLine()) != null) {
					total += line + "\n";
				}
				tmp.appendChild(doc.createTextNode(total));
				body.appendChild(tmp);
			}
		}
		tmp = doc.createElement("a");
		tmp.setAttribute("href", "/servlet/inbox");
		tmp.appendChild(doc.createTextNode("Back to Inbox"));
		body.appendChild(tmp);
	}
}
