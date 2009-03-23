package org.joe_e.servlet.mail.notjoe_e;

import javax.servlet.http.HttpSession;

import org.joe_e.servlet.Dispatcher;
import org.joe_e.servlet.SessionInitializer;

public class SessionInit implements SessionInitializer {

	public void fillHttpSession(HttpSession session) {
		//default do nothing
		session.setAttribute("auth", new AuthenticationAgent());
		session.setAttribute("manager", new AccountManager());
	}

}
