package org.joe_e.servlet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This is the base Servlet class for web applications built in Joe-E using the servlet technology. 
 * This works similarly to the HttpServlet implementation, where you override the methods do(Get, Post, etc.)
 * to add application level functionality to your webapp. When a user visits a url the Joe-E Servlet Dispatcher
 * will receive the request and forward it to the proper servlet as specified in the policy.xml file. 
 * @author akshay
 *
 */
public class JoeEServlet extends HttpServlet {
	
	/**
	 * All JoeEServlets must have an inner class called SessionView that extends
	 * org.joe_e.servlet.SessionView. This class specifies which session variables
	 * the servlet has access to and also specifies which session variables are
	 * read only or read/write (TODO: not yet... how?). The names of the instance
	 * variables of the SessionView must be exactly the same as the key's of the
	 * HttpSession map. This is just a default implementation where none of the
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
	 * @param ses
	 * @param cookies TODO
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");
	}
	
	/**
	 * Default implementation of the JoeEServlet doPost method
	 * @param req
	 * @param res
	 * @param ses
	 * @param cookies TODO
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");	
	}
	
	protected void setSession(AbstractSessionView ses) {
		try {
			this.getClass().getField("session").setAccessible(true);
			this.getClass().getField("session").set(this, ses);
		} catch (Exception e) {
			//Dispatcher.logException(e);
		}
	}

	protected AbstractSessionView getSession() {
		try {
			return (AbstractSessionView) this.getClass().getField("session").get(this);
		} catch (Exception e) {
			//Dispatcher.logException(e);
		}
		return null;
	}
	
	protected void setCookies(AbstractCookieView c) {
		try {
			this.getClass().getField("cookies").setAccessible(true);
			this.getClass().getField("cookies").set(this, c);
		} catch (Exception e) {
			//Dispatcher.logException(e);
		}
	}
	
	protected AbstractCookieView getCookies() {
		try {
			return (AbstractCookieView) this.getClass().getField("cookies").get(this);
		} catch (Exception e) {
			//Dispatcher.logException(e);
		}
		return null;
	}
	
	/**
	 * creates an instance of the SessionView for this specific JoeEServlet.
	 * Returns an empty SessionView, which should then be filled by the 
	 * SessionView.fillSession(HttpSession) method.
	 * @return AbstractSessionView object (but really it's a subclass of this)
	 * @throws InstantiationException if reflection stuff goes wrong
	 * @throws IllegalAccessException if reflection stuff goes wrong
	 * @throws InvocationTargetException if reflection stuff goes wrong
	 */
	public AbstractSessionView getSessionView() throws InstantiationException, IllegalAccessException, InvocationTargetException {
		for (Class<?> c : this.getClass().getClasses()) {
			if (c.getName().equals(this.getClass().getName() + "$SessionView")) {
				for (Constructor<?> cr : c.getConstructors()) {
					return (AbstractSessionView) cr.newInstance(this);
				}
			}
		}
		return null;
	}
	
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
			//Dispatcher.logException(e);
		}
		return null;
	}
	
	public AbstractCookieView getCookieView() throws InstantiationException, IllegalAccessException, InvocationTargetException {
		for (Class<?> c : this.getClass().getClasses()) {
			if (c.getName().equals(this.getClass().getName() + "$CookieView")) {
				for (Constructor<?> cr : c.getConstructors()) {
					return (AbstractCookieView) cr.newInstance(this);
				}
			}
		}
		return null;
	}
	
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
