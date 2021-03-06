package org.joe_e.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.response.ServletResponseWrapper;

/**
 * This is the base Servlet class for web applications built in Joe-E using the servlet technology. 
 * This works similarly to the HttpServlet implementation, where you override the methods do(Get, Post, etc.)
 * to add application level functionality to your webapp. When a user visits a url the Joe-E Servlet Dispatcher
 * will receive the request and forward it to the proper servlet as specified in the policy.xml file. 
 * @author akshay
 */
public class JoeEServlet extends HttpServlet implements org.joe_e.Immutable {
	public static final long serialVersionUID = 1L;

	/**
	 * All JoeEServlets must have an inner class called SessionView that extends
	 * org.joe_e.servlet.AbstractSessionView. This class specifies which session variables
	 * the servlet has access to and also specifies which session variables are
	 * read only or read/write. This is just a default implementation where none of the
	 * session is accessible to the servlet.
	 * @author akshay
	 *
	 */
	public class SessionView extends AbstractSessionView {
		public SessionView (HttpSession ses) {
			super(ses);
		}
	}
	
	/**
	 * All JoeEServlets must have an inner class called CookieView that specifies
	 * which cookies this servlet will have access too. 
	 * @author akshay
	 *
	 */
	public class CookieView extends AbstractCookieView {
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	/**
	 * Default implementation of the JoeEServlet doGet method
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");
	}
	
	/**
	 * Default implementation of the JoeEServlet doPost method
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest req, ServletResponseWrapper res, AbstractSessionView ses, AbstractCookieView c) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");	
	}
	
	/**
	 * creates an instance of the SessionView for this specific JoeEServlet.
	 * @return AbstractSessionView object (but really it's a subclass of this)
	 * @throws InstantiationException if reflection stuff goes wrong
	 * @throws IllegalAccessException if reflection stuff goes wrong
	 * @throws InvocationTargetException if reflection stuff goes wrong
	 */
	public AbstractSessionView getSessionView(HttpSession session) {
		try {
			for (Class<?> c : this.getClass().getClasses()) {
				if (c.getName().equals(this.getClass().getName() + "$SessionView")) {
					for (Constructor<?> cr : c.getConstructors()) {
						return (AbstractSessionView) cr.newInstance(this, session);
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * Creates an instance of the CookieView for this specific JoeEServlet
	 * @return AbstractCookieView object (but really a subclass of it)
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AbstractCookieView getCookieView(Cookie[] ck) {
		try {
			for (Class<?> c : this.getClass().getClasses()) {
				if (c.getName().equals(this.getClass().getName() + "$CookieView")) {
					for (Constructor<?> cr : c.getConstructors()) {
						return (AbstractCookieView) cr.newInstance(this, ck);
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
}
