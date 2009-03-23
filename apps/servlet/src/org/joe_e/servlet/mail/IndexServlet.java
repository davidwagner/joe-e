package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;

/**
 * TODO: we need to do taming data for a lot of stuff
 *       to make a meaningful app.
 * @author akshay
 *
 */
public class IndexServlet extends JoeEServlet {

	public class SessionView extends AbstractSessionView {
		String username;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses) throws ServletException, IOException {
		SessionView session = (SessionView) ses;
		if (session.username != null) {
			res.sendRedirect("/servlet/inbox");
		}
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		out.println("<body>" +
				"<p>Welcome to Joe-E mail</p>" +
				"<a href=\"/servlet/login\">Log In</a><br />" +
				"<a href=\"/servlet/create\">Create an Account</a>" +
				"</body></html>");
		HtmlWriter.printFooter(out);
		out.flush();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses) throws ServletException, IOException {
		this.doGet(req, res, ses);
	}
}
