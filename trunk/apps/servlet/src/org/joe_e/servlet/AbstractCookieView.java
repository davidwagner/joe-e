package org.joe_e.servlet;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.joe_e.array.ConstArray;

/**
 * This class represents a view into some of the cookies associated with the
 * HTTP request. To limit information leakage, and to fully understand the 
 * communication channels between servlets, we require that each servlet 
 * programmatically specify which cookies it plans to access, and we then only
 * give the servlet access to these specific cookies. This is done by
 * subclassing an <code>AbstractCookieView</code>, similarly to what we do with
 * <code>AbstractSessionView</code>s. All JoeEServlets must have an inner class 
 * called <code>CookieView</code> that extends this class. 
 * @author akshay
 */
public abstract class AbstractCookieView {

	
	protected ConstArray<Cookie> recievedCookies;
	protected ArrayList<Cookie> updatedCookies;
	
	
	public AbstractCookieView(Cookie[] c) {		
		recievedCookies = ConstArray.array(c);
		updatedCookies = new ArrayList<Cookie>();
	}
	
	/**
	 * default constructor
	 */
	public AbstractCookieView() {
		updatedCookies = new ArrayList<Cookie>();
	}
	
	
	/**
	 * Add all of the cookies associated with this view to the response. This
	 * makes sure that we persist all of the cookies.
	 * @param res
	 */
	public final void finalizeCookies(HttpServletResponse res) {
		for (Cookie c : updatedCookies) {
			res.addCookie(c);
		}
	}
}
