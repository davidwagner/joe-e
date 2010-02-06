package org.joe_e.servlet.mail.notjoe_e;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.mail.HtmlWriter;
import org.joe_e.servlet.mail.ReadOnlyFile;
import org.joe_e.servlet.mail.Read.SessionView;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ResponseElement;
import org.joe_e.servlet.response.ResponseUrl;
import org.joe_e.servlet.response.ServletResponseWrapper;

public class ReadTest extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		public SessionView(HttpSession ses) {
			super(ses);
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
		ReadOnlyFile newFolder = new ReadOnlyFile(new File("/var/mail/vhosts/boink.joe-e.org/p1"));
		ResponseDocument doc = res.getDocument();
		ResponseElement body = HtmlWriter.printHeader(doc);
		
		ResponseElement tmp = doc.createElement("h2");
		tmp.appendChild(doc.createTextNode("Joe-E Mail"));
		body.appendChild(tmp);
		
		
		String msgName = req.getParameter("id");
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
		tmp.appendChild(doc.createTextNode("Delete"));
		body.appendChild(tmp);
		body.appendChild(doc.createElement("br"));
		tmp = doc.createElement("a");
		tmp.addLinkAttribute("href", new ResponseUrl("/servlet/inbox", null));
		tmp.appendChild(doc.createTextNode("Back to Inbox"));
		body.appendChild(tmp);
	}
}
