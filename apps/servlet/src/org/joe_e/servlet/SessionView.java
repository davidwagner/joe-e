package org.joe_e.servlet;

import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

public class SessionView {

	public SessionView() {
		
	}
	
	public void fillSessionView(HttpSession ses) throws IllegalAccessException {
		for (Field f : this.getClass().getDeclaredFields()) {
			if (ses.getAttribute(f.getName()) != null) {
				f.set(this, ses.getAttribute(f.getName()));
			}
		}
	}
	
	public void fillHttpSession(HttpSession ses) throws IllegalAccessException {
		System.out.println(this);
		for (Field f: this.getClass().getDeclaredFields()) {
			if (!f.isSynthetic() && !f.isAccessible()) {
				System.out.println(f.getName());
				ses.setAttribute(f.getName(), f.get(this));
			}
		}
	}
}
