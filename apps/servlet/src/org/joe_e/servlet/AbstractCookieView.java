package org.joe_e.servlet;

import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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

	
	public ArrayList<Cookie> cookies;
	
	
	public AbstractCookieView(Cookie[] c) {
		cookies = new ArrayList<Cookie>();
		if (c != null) {
			for (Cookie ck : c) {
				cookies.add(ck);
			}
		}
	}
	
	/**
	 * default constructor
	 */
	public AbstractCookieView() {
		cookies = new ArrayList<Cookie>();
	}
	
	
	/**
	 * Add all of the cookies associated with this view to the response. This
	 * makes sure that we persist all of the cookies.
	 * @param res
	 */
	public final void finalizeCookies(HttpServletResponse res) {
		for (Cookie c : cookies) {
			res.addCookie(c);
		}
	}
}
