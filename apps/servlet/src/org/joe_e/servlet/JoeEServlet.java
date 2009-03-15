package org.joe_e.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the base Servlet class for web applications built in Joe-E using the servlet technology. 
 * This works similarly to the http servlet implementation, where you override the methods do(Get, Post, etc.)
 * to add application level functionality to your webapp. When a user visits a url the Joe-E Servlet Dispatcher
 * will receive the request and forward it to the proper servlet as specified in the policy.xml file. 
 * @author akshay
 *
 */
public class JoeEServlet extends HttpServlet {

	public class SessionView extends org.joe_e.servlet.SessionView {
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, org.joe_e.servlet.SessionView ses) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, org.joe_e.servlet.SessionView ses) throws ServletException, IOException {
		throw new ServletException("Unimplemented method in servlet");	
	}
	
	public org.joe_e.servlet.SessionView getSessionView() throws InstantiationException, IllegalAccessException, InvocationTargetException {
		for (Class c : this.getClass().getClasses()) {
			if (c.getName().equals(this.getClass().getName() + "$SessionView")) {
				for (Constructor cr : c.getConstructors()) {
					return (org.joe_e.servlet.SessionView) cr.newInstance(this);
				}
			}
		}
		return null;
	}
}
