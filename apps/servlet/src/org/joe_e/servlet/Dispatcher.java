package org.joe_e.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Joe-E Servlet Dispatcher. This class is the core of the Joe-E Servlet Framework. 
 * It is responsible for reading and parsing the policy.xml file for the webapp, and
 * for dispatching specific requests to the actual JoeEServlet instances. Every request
 * goes through the dispatcher, which looks up which servlet to forward the request to
 * and performs the necessary session subsetting as specified by that servlet's 
 * SessionView object.
 * 
 * A key difference between this dispatcher behavior and the regular servlet framework
 * is that the Joe-E dispatcher keeps one instance of each servlet per session, rather 
 * than a singleton instance of each servlet. This prevents 
 * TODO: what to do about session timeouts? We need to have something kind of polling
 * 
 * @author akshay
 * TODO: read only by annotation, just don't write it back
 * TODO: debug mode: warn if read only field gets modified
 * TODO: maybe clone if read only... so deep write doesn't actually modify
 *  anything marked with @readonly has to be cloneable or immutable. 
 *
 */
public class Dispatcher extends HttpServlet {

	// The map that contains url to servlet mappings
	// TODO: does the map work if we have complex url-patterns? (i.e. regex stuff)
	private HashMap<String, Class<?>> servletmapping;
	//private HashMap<String, HashMap<String, JoeEServlet>> perSessionServlets;
	private SessionInitializer initializer;
	private static Dispatcher instance;
	
	public static Logger logger = Logger.getLogger(Dispatcher.class.getName());
	
	/**
	 * initializes the Dispatcher by reading and parsing
	 * the policy.xml file and making the url->servlet 
	 * mappings.
	 * @throws ServletException if the policy file cannot
	 * be found or if the class loader was unable to load the
	 * JoeEServlet instances.
	 * TODO exceptions should have meaningful messages
	 */
	public void init() throws ServletException {
		try {
			File policy = new File((String) getServletConfig().getInitParameter("policy"));
			if (policy.exists()) {
				this.parsePolicy(policy);
				for (String s : servletmapping.keySet()) {
					log("Loaded mapping: " + s + ": " + servletmapping.get(s).toString());
				}
			} else {
				throw new ServletException("Unspecified Policy File");
			}
		} catch (ServletException e) {
			throw new ServletException(e.getMessage());
		}
		//perSessionServlets = new HashMap<String, HashMap<String, JoeEServlet>>();
		instance = this;
	}
	
	 	
	/**
	 * Handle an HTTP GET request by finding the correct servlet
	 * and forwarding the request to it. Also restricts access
	 * to the HttpSession members as specified by the servlet's
	 * SessionView inner class.
	 * @throws ServletException if unable to dynamically instantiate
	 * the SessionView object and populate its members. Or if there
	 * are any other reflection problems
	 * TODO: should we allow GET requests to modify the session? 
	 * aren't they not supposed to change state?
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		if (req.getSession().isNew()) {
			log("New session instance");
			initializer.fillHttpSession(req.getSession());
			transformSession(req.getSession());
			//perSessionServlets.put(req.getSession().getId(), new HashMap<String, JoeEServlet>());
		}
		JoeEServlet servlet = findServlet(req.getSession(), req.getServletPath());
		AbstractSessionView s = null;
		try {
			s = servlet.getSessionView();
			if (s != null) {
				s.fillSessionView(req.getSession());
				log("Dispatching GET request for " + req.getServletPath() + " to " + servlet.getClass().getName());
				servlet.doGet(req, response, s);
				s.fillHttpSession(req.getSession());
			}
		} catch(IllegalAccessException i) {
			throw new ServletException(i.getMessage());
		} catch(InstantiationException i) {
			throw new ServletException(i.getMessage());
		} catch(InvocationTargetException i) {
			throw new ServletException(i.getMessage());
		}
	}
	
	
	/**
	 * Handle an HTTP POST request by finding the correct servlet
	 * and forwarding the request to it. Also restricts access
	 * to the HttpSession members as specified by the servlet's
	 * SessionView inner class.
	 * @throws ServletException if unable to dynamically instantiate
	 * the SessionView object and populate its members. Or if there
	 * are any other reflection problems
	 * TODO: exceptions should have meaningful messages
	 * TODO: what are the differences between GET and POST methods from
	 * the point of view of the dispatcher?
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		if (req.getSession().isNew()) {
			log("New session instance");
			initializer.fillHttpSession(req.getSession());
			transformSession(req.getSession());
			//perSessionServlets.put(req.getSession().getId(), new HashMap<String, JoeEServlet>());
		}
		JoeEServlet servlet = findServlet(req.getSession(), req.getServletPath());
		AbstractSessionView s = null;
		try {
			s = servlet.getSessionView();
			if (s != null) {
				s.fillSessionView(req.getSession());
				log("Dispatching POST request for " + req.getServletPath() + " to " + servlet.getClass().getName());
				servlet.doPost(req, response, s);
				s.fillHttpSession(req.getSession());
			}
		} catch(IllegalAccessException i) {
			throw new ServletException();
		} catch(InstantiationException i) {
			throw new ServletException();
		} catch(InvocationTargetException i) {
			throw new ServletException();
		}
	}
	
	/**
	 * perform a lookup in the url->servlet map for the string s
	 * TODO correctly handle wildcards. there are only three options
	 * /.../*
	 * *.<ext>
	 * /fully/qualified/name
	 * @param s
	 * @return the JoeEServlet corresponding to the url s
	 */
	private JoeEServlet findServlet(HttpSession session, String s) throws ServletException {
		try {
			String pattern = "";
			if (s.indexOf('.') != -1) {
				// then we're allowed to escape out the stuff before the .
				pattern = "*"+s.substring(s.indexOf('.'));
			} else if (s.lastIndexOf("/") != s.length()-1) {
				pattern = s.substring(0, s.lastIndexOf("/")+1)+"*";
			}
			
			if (session.getAttribute(s) == null && servletmapping.get(s) != null) {
				// instantiate the class.
				session.setAttribute(s, (JoeEServlet) servletmapping.get(s).newInstance());
				log("Added instance of " + servletmapping.get(s).getName() + " to session " + session.getId());
			} 
			if (session.getAttribute(s) != null) {
				return (JoeEServlet) session.getAttribute(s);
			}
			if (session.getAttribute(pattern) == null && servletmapping.get(pattern) != null) {
				session.setAttribute(pattern, (JoeEServlet) servletmapping.get(pattern).newInstance());
				log("Added instance of " + servletmapping.get(pattern).getName() + " to session " + session.getId());
			}
			if (session.getAttribute(pattern) != null) {
				return (JoeEServlet) session.getAttribute(pattern);
			}
			throw new ServletException("Couldn't find url-pattern for " + s);
		} catch (InstantiationException e) {
			log("Unable to instantiate class: " + servletmapping.get(s).getName() + " for url: " + s);
			throw new ServletException ("unable to instantiate servlet for url");
		} catch (IllegalAccessException e) {
			log("Unable to instantiate class: " + servletmapping.get(s).getName() + " for url: " + s);
			throw new ServletException ("unable to instantiate servlet for url");
		}
	}
	
	
	/**
	 * invalidate a session object and free up space in the perSessionServlets
	 * data structure. This is how sessions should be invalidated. 
	 * @param HttpSession
	 * @deprecated
	 */
	public static void invalidateSession(HttpSession session) {
		//instance.perSessionServlets.remove(session.getId());
		session.invalidate();
	}
	
	/**
	 * After initializing the session object, we'll transform the
	 * names of the members so that every user-level session object
	 * starts with __joe-e__. This way we can put url-patterns
	 * in the session without any naming conflicts
	 * @param session
	 */
	public void transformSession(HttpSession session) {
		Enumeration<?> en = session.getAttributeNames();
		while(en.hasMoreElements()) {
			String s = (String) en.nextElement();
			Object o = session.getAttribute(s);
			session.removeAttribute(s);
			session.setAttribute("__joe-e__"+s, o);
		}
	}
	
	/**
	 * Write the context of the HttpSession to the tomcat logs
	 * for debugging purposes only
	 * @param ses
	 */
	private void logSession(HttpSession ses) {
		Enumeration<?> e = ses.getAttributeNames();
		while (e.hasMoreElements()) {
			String s = (String) e.nextElement();
			log(s + ": " + ses.getAttribute(s));
		}
	}
	
	
	/**
	 * Parses the policy file and fills the map with the url->servlet
	 * mappings.
	 * @param File policy
	 * @throws ServletException if anything goes wrong in the parsing
	 * TODO: this may need some cleaning up
	 */
	private void parsePolicy(File policy) throws ServletException {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(policy, new PolicyHandler());
		} catch (SAXException s) {
			log("SAXException " + s.getMessage(), s);
			throw new ServletException();
		} catch (ParserConfigurationException p) {
			log("ParserConfigurationException " + p.getMessage(), p);
			throw new ServletException();
		} catch (IOException i) {
			log("couldn't read policy file", i);
			throw new ServletException();
		}
	}
	
	
	/**
	 * The call back class for the SAXParser. This implements the actual 
	 * functionality of the parser that finds the url mappings and puts
	 * them in the map. The details of implementation or not entirely 
	 * important and this should only be used to parse the policy file
	 * @author akshay
	 *
	 */
	private class PolicyHandler extends DefaultHandler {
		
		HashMap<String, String> servletMappings = new HashMap<String, String>();
		String servletName = null;
		boolean inServletName = false;
		String servletClass = null;
		boolean inServletClass = false;
		String urlPattern = null;
		boolean inUrlPattern = false;
		String sessionClass = null;
		boolean sessionInit = false;
		
		public void startDocument() {
			servletmapping = new HashMap<String, Class<?>> ();
		}
		
		public void startElement(String uri, String localname, String qName, Attributes attributes) {
			if (qName.equals("servlet")) {
				servletName = null;
				servletClass = null;
			} else if (qName.equals("servlet-name") && servletName == null && inServletName == false) {
				inServletName = true;
			} else if (qName.equals("servlet-class") && servletClass == null && inServletClass == false) {
				inServletClass = true;
			} else if (qName.equals("servlet-mapping")) {
				servletName = null;
				urlPattern = null;
			} else if (qName.equals("url-pattern") && urlPattern == null && inUrlPattern == false) {
				inUrlPattern = true;
			} else if (qName.equals("session-init")) {
				sessionInit = true;
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("servlet-name") && servletName != null && inServletName == true) {
				inServletName = false;
			} else if (qName.equals("servlet-class") && servletClass != null && inServletClass == true) {
				inServletClass = false;
			} else if (qName.equals("servlet") && servletClass != null && servletName != null) {
				servletMappings.put(servletName, servletClass);
			} else if (qName.equals("url-pattern") && urlPattern != null && inUrlPattern == true) {
				inUrlPattern = false;
			} else if (qName.equals("servlet-mapping")) {
				try {
					Class<?> cl = this.getClass().getClassLoader().loadClass(servletMappings.get(servletName));
					servletmapping.put(urlPattern, cl);
				} catch (ClassNotFoundException c) {
					log("caught exception... probably due to class loader issues", c);
					throw new SAXException();
				}
			} else if (qName.equals("session-init")) {
				try {
					Class<?> cl = this.getClass().getClassLoader().loadClass(sessionClass);
					initializer = (SessionInitializer) cl.newInstance();
				} catch (ClassNotFoundException c) {
					log("caught exception... probably due to class loader issues", c);
					throw new SAXException();
				} catch (InstantiationException i) {
					log("caught exception... probably due to class loader issues", i);
					throw new SAXException();
				} catch (IllegalAccessException a) {
					log("caught exception... probably due to class loader issues", a);
					throw new SAXException();
				}
			}
		}
		
		public void characters(char[] ch, int start, int length) {
			if (inServletName) {
				servletName = new String(ch, start, length);
			} else if (inServletClass) {
				servletClass = new String(ch, start, length);
			} else if (inUrlPattern) {
				urlPattern = new String(ch, start, length);
			} else if (sessionInit) {
				sessionClass = new String(ch, start, length);
			}
		}
		
		public void endDocument() {
		}
	}
	
	public void log(String s) {
		logger.fine(s);
	}
	
	public void log(String s, Throwable t) {
		logger.severe(s + t.getLocalizedMessage());
	}
}
