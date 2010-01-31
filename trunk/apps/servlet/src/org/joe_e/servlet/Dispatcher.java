package org.joe_e.servlet;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.joe_e.servlet.response.ServletResponseWrapper;
import org.joe_e.servlet.response.ResponseDocument;
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
 * @TODO: with immutable servlets and session wrappers as we do them, do we need to serialize requests?
 * @author akshay
 *
 */
public class Dispatcher extends HttpServlet {
	public static final long serialVersionUID = 1L;

	// If the session gets invalidate for some reason, we should tell the user.
	public static String errorMessage = "";
	
	// The map that contains url to servlet mappings
	private HashMap<String, JoeEServlet> servletmapping;
	private HashMap<JoeEServlet, String> jsmappings;
	private HashMap<JoeEServlet, String> cssmappings;
	private SessionInitializer initializer;
	private static boolean serialized;
	private static String jsRoot;
	private static String cssRoot;
	
	private static Logger logger = Logger.getLogger(Dispatcher.class.getName());
	
	/**
	 * initializes the Dispatcher by reading and parsing
	 * the policy.xml file and making the url->servlet 
	 * mappings.
	 * @throws ServletException if the policy file cannot
	 * be found or if the class loader was unable to load the
	 * JoeEServlet instances.
	 */
	public void init() throws ServletException {
		File policy = new File((String) getServletConfig().getInitParameter("policy"));
		if (policy.exists()) {
			this.parsePolicy(policy);
		} else {
			throw new ServletException("Unspecified Policy File");
		}
	}
	
	 	
	/**
	 * Handle an HTTP GET request by finding the correct servlet
	 * and forwarding the request to it. Also restricts access
	 * to the HttpSession members as specified by the servlet's
	 * SessionView inner class.
	 * @throws ServletException if unable to dynamically instantiate
	 * the SessionView object and populate its members. Or if there
	 * are any other reflection problems
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		// initialize the session if it is new
		if (session.isNew()) {
			initializer.fillHttpSession(session);
			transformSession(session);
			session.setAttribute("lock", new ReentrantLock());
		}
		Lock lock = (Lock) session.getAttribute("lock");
		if (serialized) {
			lock.lock();
		}
		
		// find the servlet, construct the session and cookie views
		JoeEServlet servlet = findServlet(session, req.getServletPath());
		AbstractSessionView sessionview = servlet.getSessionView(session);
		AbstractCookieView cookieview = servlet.getCookieView(req.getCookies());

		try {
			
			// wrap the response, forward the request, commit the cookies.
			ServletResponseWrapper responseFacade = new ServletResponseWrapper(response);
			servlet.doGet(req, responseFacade, sessionview, cookieview);
			cookieview.finalizeCookies(response);
			
			// link the static files and commit the response. 
			responseFacade.getDocument().addCSRFTokens((String) session.getAttribute(servlet.getClass().getCanonicalName()+"__token"));
			responseFacade.getDocument().addJSLink(jsRoot+"/"+jsmappings.get(servlet));
			responseFacade.getDocument().addCSSLink(cssRoot+"/"+cssmappings.get(servlet));
			responseFacade.flushBuffer();
		} catch(Exception i) {
			if (serialized) { lock.unlock(); }
			String msg = i.getMessage();
			for (StackTraceElement st : i.getStackTrace()) {
				msg += "\n"+st.getMethodName()+" " + st.getLineNumber() + " " + st.getClassName();
			}
			throw new ServletException(msg);
		}
		if (serialized) {
			lock.unlock();
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
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = req.getSession();

		// initialize the session if it is new
		if (session.isNew()) {
			initializer.fillHttpSession(session);
			transformSession(session);
			session.setAttribute("lock", new ReentrantLock());
		}
		Lock lock = (Lock) session.getAttribute("lock");
		if (serialized) {
			lock.lock();
		}
		
		// find the servlet, construct the session and cookie views
		JoeEServlet servlet = findServlet(session, req.getServletPath());

		// Check the CSRF token. You should do this on every POST request
		if (req.getParameter("__joe-e__csrftoken") == null || !req.getParameter("__joe-e__csrftoken").equals(session.getAttribute(servlet.getClass().getCanonicalName()+"__token"))) {
			throw new ServletException ("CSRF attempt");
		}

		AbstractSessionView sessionview = servlet.getSessionView(session);
		AbstractCookieView cookieview = servlet.getCookieView(req.getCookies());
		
		try {
			// wrap the response, forward the request, commit the cookies.
			ServletResponseWrapper responseFacade = new ServletResponseWrapper(response);
			servlet.doPost(req, responseFacade, sessionview, cookieview);
			cookieview.finalizeCookies(response);
			
			// link the static files and commit the response. 
			responseFacade.getDocument().addJSLink(jsRoot+"/"+jsmappings.get(servlet));
			responseFacade.getDocument().addCSSLink(cssRoot+"/"+cssmappings.get(servlet));
			responseFacade.flushBuffer();
		} catch(Exception i) {
			if (serialized) { lock.unlock(); }
			String msg = i.getMessage();
			for (StackTraceElement st : i.getStackTrace()) {
				msg += "\n"+st.getMethodName()+" " + st.getLineNumber() + " " + st.getClassName();
			}
			throw new ServletException(msg);
		}
		if (serialized) {
			lock.unlock();
		}
	}

	/**
	 * perform a lookup in the url->servlet map for the string s
	 * add servlets to the session as needed
	 * @param s
	 * @return the JoeEServlet corresponding to the url s
	 */
	private JoeEServlet findServlet(HttpSession session, String s) throws ServletException {
		String pattern = "";
		if (s.indexOf('.') != -1) {
			// then we're allowed to escape out the stuff before the .
			pattern = "*"+s.substring(s.indexOf('.'));
		} else if (s.lastIndexOf("/") == s.length()-1) {
			pattern = s.substring(0, s.lastIndexOf("/")+1)+"*";
		}
		
		JoeEServlet servlet = null;
		if (servletmapping.get(s) != null) {
			 servlet = servletmapping.get(s);
		} else if (!pattern.equals("") && servletmapping.get(pattern) != null) {
			servlet = (JoeEServlet) servletmapping.get(pattern);
		}
		
		if (servlet != null) {
			if (session.getAttribute(servlet.getClass().getSimpleName()+"__token") == null) {
				addXSRFToken(session, servlet.getClass().getSimpleName());
			}
			return servlet;
		}
		throw new ServletException("Couldn't find url-pattern for " + s);
	}

	/**
	 * Generate a random xsrf token and add it as a member of the HttpSession
	 * @param session
	 * @param servletName
	 * @throws ServletException
	 */
	public void addXSRFToken(HttpSession session, String servletName) throws ServletException {
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update((Long.toHexString(System.currentTimeMillis())).getBytes());
			session.setAttribute(servletName+"__token", (new BigInteger(md5.digest())).toString(16));
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException (e.getMessage());
		}
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
	 * Is this web server supposed to allow concurrent accesses to one users
	 * data or not?
	 * @return
	 */
	public static boolean isSerialized() {
		return serialized;
	}
	
	/**
	 * Parses the policy file and fills the map with the url->servlet
	 * mappings.
	 * @param File policy
	 * @throws ServletException if anything goes wrong in the parsing
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
	 */
	private class PolicyHandler extends DefaultHandler {
		
		String servletName = null;
		boolean inServletName = false;
		String servletClass = null;
		boolean inServletClass = false;
		ArrayList<String> urlPattern = new ArrayList<String>();
		boolean inUrlPattern = false;
		String sessionClass = null;
		boolean sessionInit = false;
		String concurrencyPolicy = null;
		boolean concurrency = false;
		
		String jsFile = null;
		String cssFile = null;
	
		public void startDocument() {
			servletmapping = new HashMap<String, JoeEServlet> ();
			jsmappings = new HashMap<JoeEServlet, String> ();
			cssmappings = new HashMap<JoeEServlet, String> ();
		}
		
		public void startElement(String uri, String localname, String qName, Attributes attributes) {
			if (qName.equals("servlet")) {
				servletName = null;
				urlPattern.clear();
				servletClass = null;
				jsFile = null;
				cssFile = null;
			} else if (qName.equals("servlet-name") && servletName == null && inServletName == false) {
				inServletName = true;
			} else if (qName.equals("servlet-class") && servletClass == null && inServletClass == false) {
				inServletClass = true;
			} else if (qName.equals("url-pattern")) {
				inUrlPattern = true;
			} else if (qName.equals("session-init")) {
				sessionInit = true;
			} else if (qName.equals("concurrency")) {
				concurrency = true;
			} else if (qName.equals("js-root")) {
				jsRoot = attributes.getValue("path");
			} else if (qName.equals("css-root")) {
				cssRoot = attributes.getValue("path");
			} else if (qName.equals("js-file")) {
				jsFile = attributes.getValue("name");
			} else if (qName.equals("css-file")) {
				cssFile = attributes.getValue("name");
			}
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("servlet-name") && servletName != null && inServletName == true) {
				inServletName = false;
			} else if (qName.equals("servlet-class") && servletClass != null && inServletClass == true) {
				inServletClass = false;
			} else if (qName.equals("servlet") && servletClass != null && servletName != null) {
				if (urlPattern.size() > 0) {
					try {
						JoeEServlet cl = (JoeEServlet) this.getClass().getClassLoader().loadClass(servletClass).newInstance();
						for (String p : urlPattern) {
							servletmapping.put(p, cl);
							jsmappings.put(cl, jsFile);
							cssmappings.put(cl, cssFile);
						}
					} catch (ClassNotFoundException c) {
						log("caught exception... probably due to class loader issues", c);
						throw new SAXException();
					} catch (InstantiationException c) {
						log("caught instantiation exception.");
						throw new SAXException();
					} catch (IllegalAccessException e) {
						log("caught illegal access exception.");
						throw new SAXException();
					}
				}
			} else if (qName.equals("url-pattern")) {
				inUrlPattern = false;
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
			} else if (qName.equals("concurrency")) {
				if (concurrencyPolicy != null && concurrencyPolicy.equals("serialized")) {
					serialized = true;
				} else if (concurrencyPolicy != null && concurrencyPolicy.equals("immutable")) {
					serialized = false;
				} else {
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
			    log("adding " + new String(ch, start, length) + " to urlPattern");
				urlPattern.add(new String(ch, start, length));
			} else if (sessionInit) {
				sessionClass = new String(ch, start, length);
			} else if (concurrency) {
				concurrencyPolicy = new String(ch, start, length);
			}
		}
		
		public void endDocument() {
		}
	}
	
	/**
	 * write <code>s</code> to the log file specified in the application
	 * configuration. 
	 * @param s
	 */
	public static void logMsg(String s) {
		s = s.replaceAll("\n", " ");
		Dispatcher.logger.fine(s);
	}
}
