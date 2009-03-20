package org.joe_e.servlet;

import javax.servlet.http.HttpSession;

public interface SessionInitializer {

	/**
	 * When the dispatcher detects a new session, it passes 
	 * the HttpSession object to this method so that it can
	 * be initialized in an app-specific way. This interface should
	 * be implemented by any Joe-E Servlet application. 
	 * 
	 * One practical use of this method is to add an authentication
	 * capability to the session. This method ensures that for each
	 * session, there can only be one login attempt (or one successful
	 * login)
	 * @param session
	 */
	public void fillHttpSession(HttpSession session);
}
