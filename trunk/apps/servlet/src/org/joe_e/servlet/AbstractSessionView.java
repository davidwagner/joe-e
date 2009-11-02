package org.joe_e.servlet;

import java.lang.reflect.Field;
import java.util.Enumeration;

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

	protected HttpSession session;
	/**
	 * default constructor
	 */
	public AbstractSessionView(HttpSession ses) {
		session = ses;
	}

	/**
	 * Initialize this SessionView by filling it with values from the 
	 * HttpSession. Uses Reflection API to determine what mappings
	 * from the session should be placed into the SessionView. If
	 * f is marked as @readonly, tries to perform a deep copy using
	 * the serialization technique. If this fails for any reason, then
	 * just set the field in the SessionView, to be a pointer to the
	 * object in the HttpSession (i.e. not a copy). This method is not
	 * override-able
	 * @param ses - the HttpSession
	 * @throws IllegalAccessException - if Reflection stuff goes wrong
	 */
	public final void fillSessionView(HttpSession ses) throws IllegalAccessException {
		ses.setAttribute("token", null);
		copySessionToken(ses);
		for (Field f : this.getClass().getDeclaredFields()) {
			if (ses.getAttribute("__joe-e__"+f.getName()) != null) {
				if (f.isAnnotationPresent(readonly.class)) {
					// try to deep-copy
					Object o = ses.getAttribute("__joe-e__"+f.getName());
					try {
						f.set(this, Cloner.deepCopy(o));
						//						Dispatcher.logger.finest("Deep copy on " + f.getName() + " successful");
					} catch (Exception e) {
						// deep copy failed
					    //						Dispatcher.logger.warning("Failed to deep copy " + f.getName() + 
					    //								". Check that " + f.getName() + " is serializable");
						f.set(this, o);
					} 
				} else {
					// not marked as @readonly.
					f.set(this, ses.getAttribute("__joe-e__"+f.getName()));
				}
			}
		}
	}
	
	/**
	 * This method copies the session token for the requested servlet into the 
	 * session field keyed by "token". We do this so that we can provide a consistent
	 * interface to the session token object for each servlet but so that there is
	 * no way for the servlets to share session tokens.  
	 * @param ses
	 * @throws IllegalAccesException
	 */
	private final void copySessionToken(HttpSession ses) throws IllegalAccessException {
		Class<?> container = this.getClass().getEnclosingClass();
		if (container == null) {
			throw new IllegalAccessException ("HttpSessionView must be contained within a Joe-E Servlet");
		}
		Enumeration<String> e = ses.getAttributeNames();
		while (e.hasMoreElements()) {
			String s = e.nextElement();
			if (ses.getAttribute(s).getClass().getSimpleName().equals(container.getSimpleName())) {
				ses.setAttribute("__joe-e__token", ses.getAttribute(ses.getAttribute(s).getClass().getSimpleName()+"__token"));
			}
		}
	}
	/**
	 * Updates the HttpSession with modifications that may have been
	 * made to objects in the SessionView. This method allows the servlet
	 * to write to the SessionView, and for those writes to persist across
	 * requests. Uses Reflection API to determine which mappings should
	 * get written. This method is not override-able. returns wether we
	 * invalidated the session or not. 
	 * @param ses - the HttpSession
	 * @throws IllegalAccessException - if Reflection stuff goes wrong.
	 */
	public final void fillHttpSession(HttpSession ses) throws IllegalAccessException {
		for (Field f : this.getClass().getDeclaredFields()) {
			if (f.getName().equals("invalidate") && f.getBoolean(this)) {
			    //				Dispatcher.logger.fine("invalidating session after successful dispatch");
				Dispatcher.invalidateSession(ses);
				return;
			}
			// TODO: HACK! ask adrian what's going on here. Fix.
			// shouldn't have to setAccessible each attribute
			f.setAccessible(true);
			if (f.isAnnotationPresent(readonly.class)) {
			    //				Dispatcher.logger.fine(f.getName() + " is marked as readonly and was not copied to HttpSession");
			}
			if (Dispatcher.isSerialized() && !f.isSynthetic() && f.isAccessible() && 
					!f.isAnnotationPresent(readonly.class)) {
				ses.setAttribute("__joe-e__"+f.getName(), f.get(this));
			}
		}
	}
	
	
	/**
	 * Check if the argument object is cloneable. 
	 * @deprecated
	 * @param o
	 */
	private static boolean isCloneable(Object o) {
		Class<?>[] classes = o.getClass().getInterfaces();
		for (Class<?> cl : classes) {
			if (cl.getName().equals("java.lang.cloneable")){
				return true;
			}
		}
		return false;
	}
	
}
