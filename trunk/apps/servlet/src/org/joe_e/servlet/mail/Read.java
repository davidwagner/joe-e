package org.joe_e.servlet.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.charset.ASCII;
import org.joe_e.file.Filesystem;
import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ResponseElement;
import org.joe_e.servlet.response.ResponseUrl;
import org.joe_e.servlet.response.ServletResponseWrapper;

public class Read extends JoeEServlet {
	public static final long serialVersionUID = 1L;

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public ReadOnlyFile getMailbox() {
			return (ReadOnlyFile) session.getAttribute("__joe-e__mailbox");
		}
	}
	
	public AbstractSessionView getSessionView(HttpSession ses) {
		return new SessionView(ses);
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public AbstractCookieView getCookieView(Cookie[] c) {
		return new CookieView(c);
	}
	
	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c)
	throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		ResponseDocument doc = res.getDocument();
		if (session.getUsername() == null) {
		    res.sendRedirect("/servlet/login");
		}
		ResponseElement body = HtmlWriter.printHeader(doc);
		
		ResponseElement tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		
		
		String msgName = req.getParameter("id");
		ReadOnlyFile newFolder = session.getMailbox().getChild("Maildir").getChild("new");
		for (ReadOnlyFile f : newFolder.list()) {
			if (f.getName().equals(msgName)) {
				Reader reader = f.getReader();
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
		tmp = doc.createElement("br");
		body.appendChild(tmp);
		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/delete", "id="+msgName));
		body.appendChild(tmp);
		body.appendChild(doc.createElement("br"));
		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/inbox", null));
		tmp.appendChild(doc.createTextNode("Back to Inbox"));
		body.appendChild(tmp);
	}
}
