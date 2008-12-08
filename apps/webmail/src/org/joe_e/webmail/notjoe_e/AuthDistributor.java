package org.joe_e.webmail.notjoe_e;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

import org.joe_e.webmail.Authentication;
import org.joe_e.webmail.User;
/**
 * AuthDistributor class
 * This class is used to get a capability to the authentication agent.
 * It cannot be Joe-e because it will create an Authentication agent and
 * give it to the user (Joe-e objects can't create objects that they don't
 * have capabilities to). All that this class does it give out capabilities
 * to the Authentication agent (org.joe_e.webmail.Authentication)
 * 
 * @author akshay
 *
 */
public class AuthDistributor extends HttpServlet {

	private final String accountsFile = "/home/akshayk/accounts/";
	private final String mailboxesRoot = "/var/mail/vhosts/boink.joe-e.org/";
	private final String postfixRecipients = "/etc/postfix/virtual_mailbox_recipients";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		doPost(request, response);
	}
	/**
	 * Hand out a capability to an Authentication agent by setting a field in the
	 * passed in session and redirect to the page specified in the POST or
	 * the login page if non are specified.
	 * 
	 * This is still secure because the AuthDistributor will only give the caller
	 * a capability to the Authentication agent when if the caller's session does not
	 * have a user field set (implying that a user is logged in). If the caller's
	 * session does have the user field set, this method will redirect to the logout
	 * page
	 * 
	 * @param HttpServletRequest request, HttpServletResponse response
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws IOException, ServletException {
		HttpSession session = request.getSession();
		Enumeration<String> e = session.getAttributeNames();
		/*
		 * Check that the session has no capabilities
		 * For not we're just checking that it has no user capabilities
		 */
		while(e.hasMoreElements()) {
			Object o = session.getAttribute(e.nextElement());
			if (o instanceof User) {
				response.sendRedirect("/webmail/logout");
			}
		}
		
		// then we can give out the authentication agent.
		
		try {
			session.setAttribute("auth", new Authentication(new File(accountsFile), 
										 	new File(mailboxesRoot), 
										 	new File(postfixRecipients), 
										 	MessageDigest.getInstance("md5"),
										 	new PostfixClient()));
		} catch (NoSuchAlgorithmException e1) {
		}
		
		// then redirect as appropriate
		if (request.getServletPath().equals("/create")) {
			response.sendRedirect("/webmail/authcreate");
		} else {
			response.sendRedirect("/webmail/authlogin");
		}
	}
}
