package org.joe_e.servlet;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.ParserException;
import org.joe_e.servlet.response.ServletResponseWrapper;
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
 * 
 * We specify two possible concurrency policies. In the policy.xml file, we allow for
 * "<concurrency>policy</concurrency>" where policy is either "serialized" or "immutable"
 * If policy is "serialized" Then we guarantee that only 1 thread  per session will ever
 * be executing app-level code. TODO: not yet
 * 
 * If policy is "immutable" then we simply do not copy references back to the HttpSession, 
 * and so the only way to change session members is to change members of an object that is
 * contained within the session. In this policy, changing an immutable field locally will not
 * be reflected on the HttpSession. So in a servlet like this:
 * <code>
 * public class TestServlet extends JoeEServlet {
 * 		public class SessionView extends AbstractSessionView {
 * 			public String str;
 * 		}
 * 
 * 		public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses) {
 * 			SessionView session = (SessionView) ses;
 * 			session.str = "hello there";
 * 		}
 * }
 * </code>
 * the HttpSession's str member will not be changed to "hello there". To change this member, we need
 * a wrapper object that contains a pointer to str.
 *  
 * This paradigm is like pass-by-copy with functions; a servlet is given a parameter list, and the
 * calling function does not copy any modifications back to it's own local variables. To change
 * values in the calling function (in this case HttpSession), we need to pass in pointers and
 * the callee (app-level servlet) can change the contents of these pointers (although not the pointer
 * themselves).
 * 
 * @author akshay
 *
 */
public class Dispatcher extends HttpServlet {

	// If the session gets invalidate for some reason, we should tell the user.
	public static String errorMessage = "";
	
	// The map that contains url to servlet mappings
	// TODO: does the map work if we have complex url-patterns? (i.e. regex stuff)
	private HashMap<String, Class<?>> servletmapping;
	private SessionInitializer initializer;
	private static boolean serialized;
	
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
			} else {
				throw new ServletException("Unspecified Policy File");
			}
		} catch (ServletException e) {
			throw new ServletException(e.getMessage());
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
	 * TODO: should we allow GET requests to modify the session? 
	 * aren't they not supposed to change state?
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.isNew()) {
		    //			log("New session instance");
			initializer.fillHttpSession(session);
			transformSession(session);
			session.setAttribute("lock", new ReentrantLock());
		}
		Lock lock = (Lock) session.getAttribute("lock");
		if (serialized) {
			lock.lock();
		}
		JoeEServlet servlet = findServlet(session, req, req.getServletPath());
		try {
		    //			log("servlet session: " + servlet.getSession());
		    //			log("Dispatching GET request for DIFFERENT KIND OF JOE-E SERVLET " + req.getServletPath() + " to " + servlet.getClass().getName());

			ServletResponseWrapper responseFacade = new ServletResponseWrapper(response);
			servlet.setCookies(servlet.getCookieView(req.getCookies()));
			servlet.doGet(req, responseFacade);
			servlet.getCookies().finalizeCookies(response);			
			responseFacade.reallyFlushBuffer();
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
	 * TODO: exceptions should have meaningful messages
	 * TODO: what are the differences between GET and POST methods from
	 * the point of view of the dispatcher?
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.isNew()) {
		    //			log("New session instance");
			initializer.fillHttpSession(session);
			transformSession(session);
			session.setAttribute("lock", new ReentrantLock());
		}
		Lock lock = (Lock) session.getAttribute("lock");
		if (serialized) {
			lock.lock();
		}
		JoeEServlet servlet = findServlet(session, req, req.getServletPath());
		//		log("Dispatching POST request for DIFFERENT KIND OF JOE-E SERVLET " + req.getServletPath() + " to " + servlet.getClass().getName());
		try {
		ServletResponseWrapper responseFacade = new ServletResponseWrapper(response);
		servlet.setCookies(servlet.getCookieView(req.getCookies()));
		servlet.doPost(req, responseFacade);
		servlet.getCookies().finalizeCookies(response);
		responseFacade.reallyFlushBuffer();
		// TODO: this isn't correct. What if the session was invalidated?
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
	private JoeEServlet findServlet(HttpSession session, HttpServletRequest req, String s) throws ServletException {
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
				JoeEServlet servlet = (JoeEServlet) servletmapping.get(s).newInstance();
				servlet.setSession (servlet.getSessionView(session));
				session.setAttribute(s, servlet);
				try {
					MessageDigest md5 = MessageDigest.getInstance("md5");
					md5.update((Long.toHexString(System.currentTimeMillis())).getBytes());
					session.setAttribute(session.getAttribute(s).getClass().getSimpleName()+"__token", (new BigInteger(md5.digest())).toString(16));
				} catch (NoSuchAlgorithmException e) {
					throw new ServletException (e.getMessage());
				}

				//				log("Added instance of " + servletmapping.get(s).getName() + " to session " + session.getId());
				//				log("Added session token at " +session.getAttribute(s).getClass().getSimpleName()+"__token");
			} 
			if (session.getAttribute(s) != null) {
				return (JoeEServlet) session.getAttribute(s);
			}
			if (session.getAttribute(pattern) == null && servletmapping.get(pattern) != null) {
				JoeEServlet servlet = (JoeEServlet) servletmapping.get(s).newInstance();
				servlet.setSession (servlet.getSessionView(session));
				session.setAttribute(pattern, servlet);
				try {
					MessageDigest md5 = MessageDigest.getInstance("md5");
					md5.update((Long.toHexString(System.currentTimeMillis())).getBytes());
					session.setAttribute(session.getAttribute(pattern).getClass().getSimpleName()+"__token", (new BigInteger(md5.digest())).toString(16));
				} catch (NoSuchAlgorithmException e) {
					throw new ServletException (e.getMessage());
				}
				
				//				log("Added instance of " + servletmapping.get(pattern).getName() + " to session " + session.getId());
				//				log("added session token at " + session.getAttribute(pattern).getClass().getSimpleName()+"__token");
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
	 */
	public static void invalidateSession(HttpSession session) {
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
	
	
	public static boolean isSerialized() {
		return serialized;
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
		String concurrencyPolicy = null;
		boolean concurrency = false;
		
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
			} else if (qName.equals("concurrency")) {
				concurrency = true;
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
				urlPattern = new String(ch, start, length);
			} else if (sessionInit) {
				sessionClass = new String(ch, start, length);
			} else if (concurrency) {
				concurrencyPolicy = new String(ch, start, length);
			}
		}
		
		public void endDocument() {
		}
	}
	
	public void log(String s) {
	    //		logger.fine(s);
	}
	
	public void log(String s, Throwable t) {
	    //		logger.severe(s + t.getLocalizedMessage());
	}
	
	public static void logMsg(String s) {	
	}
	public static void logException(Exception e) {
	    //		String msg = e.getMessage();
	    //		for (StackTraceElement st : e.getStackTrace()) {
	    //			msg += "\n"+st.getMethodName()+" " + st.getLineNumber() + " " + st.getClassName();
	    //		}
	    //		logger.fine(msg);
	}
	
	public static String getErrorMessage() {
		return errorMessage;
	}
	public static void setErrorMessage(String e) {
		errorMessage = e;
	}
}
