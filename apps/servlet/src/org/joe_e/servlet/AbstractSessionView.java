package org.joe_e.servlet;

import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

/**
 * Abstract class whose subclass represent session instances for 
 * servlets. The SessionView is in fact the policy regarding what
 * subset of the session the servlet has access to. Members specified
 * in the SessionView will be visible from within the servlet's 
 * standard methods. Anything not explicitly declared in the SessionView
 * will be hidden from the servlet's methods. All JoeEServlets must
 * have an inner class called SessionView that extends this class.
 * @author akshay
 */
public abstract class AbstractSessionView {

	/**
	 * default constructor
	 */
	public AbstractSessionView() {
		
	}
	
	/**
	 * initialize this SessionView by filling it with values from the 
	 * HttpSession. Uses Reflection API to determine what mappings
	 * from the session should be placed into the SessionView.
	 * @param ses - the HttpSession
	 * @throws IllegalAccessException - if Reflection stuff goes wrong
	 */
	public void fillSessionView(HttpSession ses) throws IllegalAccessException {
		for (Field f : this.getClass().getDeclaredFields()) {
			if (ses.getAttribute(f.getName()) != null) {
				f.set(this, ses.getAttribute(f.getName()));
			}
		}
	}
	
	/**
	 * Updates the HttpSession with modifications that may have been
	 * made to objects in the SessionView. This method allows the servlet
	 * to write to the SessionView, and for those writes to persist across
	 * requests. Uses Reflection API to determine which mappings should
	 * get written.
	 * @param ses - the HttpSession
	 * @throws IllegalAccessException - if Reflection stuff goes wrong.
	 */
	public void fillHttpSession(HttpSession ses) throws IllegalAccessException {
		for (Field f : this.getClass().getDeclaredFields()) {
			// TODO: HACK! ask adrian what's going on here. Fix.
			// shouldn't have to setAccessible each attribute
			f.setAccessible(true);
			if (!f.isSynthetic() && f.isAccessible()) {
				ses.setAttribute(f.getName(), f.get(this));
			}
		}
	}
}
