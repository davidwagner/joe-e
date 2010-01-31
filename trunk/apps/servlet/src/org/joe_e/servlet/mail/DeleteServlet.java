package org.joe_e.servlet.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;
import org.joe_e.servlet.response.ResponseDocument;
import org.joe_e.servlet.response.ResponseElement;
import org.joe_e.servlet.response.ResponseUrl;
import org.joe_e.servlet.response.ServletResponseWrapper;
import org.w3c.dom.Element;

public class DeleteServlet extends JoeEServlet {
	public static final long serialVersionUID = 1L;

	public class SessionView extends AbstractSessionView {		
		public SessionView (HttpSession ses) {
			super(ses);
		}
		public String getUsername() {
			return (String) session.getAttribute("__joe-e__username");
		}
		public DeleteOnlyFile getMessageDeleter() {
			return (DeleteOnlyFile) session.getAttribute("__joe-e__messageDeleter");
		}
	}
	
	public class CookieView extends AbstractCookieView {
		public CookieView (Cookie[] c) {
			super(c);
		}
	}
	
	/**
	 * TODO: this GET is not side-effect free
	 */
	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c) throws IOException, ServletException {
		SessionView session = (SessionView) ses;
		if (session.getUsername() == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		if (req.getParameter("id") == null) {
			res.sendRedirect("/servlet/login");
			return;
		}
		String msgId = req.getParameter("id");
		DeleteOnlyFile d = session.getMessageDeleter().getChild("Maildir").getChild("new").getChild(msgId);
		if (d.delete()) {
			res.sendRedirect("/servlet/inbox");
		} else {
			ResponseDocument doc = res.getDocument();
			ResponseElement body = HtmlWriter.printHeader(doc);
			body.appendChild(doc.createTextNode("Unable to delete file"));
			ResponseElement tmp = doc.createElement("a");
			tmp.addLinkAttribute("href", new ResponseUrl("/servlet/inbox", null));
			tmp.appendChild(doc.createTextNode("back to inbox"));
			body.appendChild(tmp);
		}
	}
}
