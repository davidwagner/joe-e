package org.joe_e.servlet.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;

import junit.framework.TestCase;

/**
 * Tests the session subsetting features of the Joe-E Servlet framework. In particular,
 * it tests that a session gets properly subsetted and the correct fields/values can be
 * accessed in the servlet methods and it tests that modifications to the SessionView
 * gets properly translated back to the underlying HttpSession
 * @author akshay
 *
 */
public class Session extends TestCase {

	/**
	 * Dummy Servlet for use in the test cases. Defines a SessionView Policy
	 * and doGet and doPost interact with the SessionView only. In fact they
	 * do not even recieve an HttpSession instance.
	 * @author akshay
	 *
	 */
	public class TestServlet extends org.joe_e.servlet.JoeEServlet {
		/**
		 * trivial SessionView with name and mailbox fields
		 * @author akshay
		 *
		 */
		public class SessionView extends AbstractSessionView {
			public String name;
			public File mailbox;
		}
		
		public File file = null;
		public String string = null;
		
		/**
		 * Basic GET method that does simple interactions with the session
		 */
		public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
			SessionView session = (SessionView) ses;
			this.file = session.mailbox;
			this.string = session.name;
		}
		
		/**
		 * basic POST method that modifies session variables and creates a new one.
		 */
		public void doPost(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView cookies) throws ServletException, IOException {
			SessionView session = (SessionView) ses;
			this.string = session.name;
			session.name = "krishnamurthy";
			session.mailbox = new File("/");
		}
	}
	
	/**
	 * Trivial HttpSession implementation for use in these test cases.
	 * @author akshay
	 *
	 */
	public class DummySession implements HttpSession {
		public HashMap<String, Object> map;
		public DummySession() {	
			map = new HashMap<String, Object>();
		}
		public Object getAttribute(String name) {
			return map.get(name);
		}
		public Enumeration getAttributeNames() {
			
			return new Vector<String>(map.keySet()).elements();
		}
		public long getCreationTime() {
			return 0;
		}
		public String getId() {
			return "0";
		}
		public long getLastAccessedTime() {
			return 0;
		}
		public int getMaxInactiveInterval() {
			return 0;
		}
		public HttpSessionContext getSessionContext() {
			return null;
		}
		public ServletContext getServletContext() {
			return null;
		}
		public Object getValue(String name) {
			return map.get(name);
		}
		public String[] getValueNames() {
			return (String[]) map.keySet().toArray();
		}
		public void invalidate() {
		}
		public boolean isNew() {
			return true;
		}
		public void putValue(String name, Object value) {
			map.put(name, value);
		}
		public void removeAttribute(String name) {
			map.remove(name);
		}
		public void removeValue(String name) {
			map.remove(name);
		}
		public void setAttribute(String name, Object value) {
			map.put(name, value);
		}
		public void setMaxInactiveInterval(int interval) {
		}
	}

	/**
	 * tests initializing the SessionView and reading from the SessionView.
	 */
	public void testBasic() {
		HttpSession session = new DummySession();
		session.setAttribute("mailbox", new File("/"));
		session.setAttribute("name", "akshay");
		TestServlet s = new TestServlet();
		try {
			AbstractSessionView ses = s.getSessionView();
			ses.fillSessionView(session);
			s.doGet(null, null, ses, null);
			ses.fillHttpSession(session);
		} catch (InstantiationException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (ServletException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		
		assertTrue(s.file.getAbsolutePath().equals("/"));
		assertTrue(s.string.equals("akshay"));	
	}
	
	/**
	 * tests writing to the SesssionView and updating the HttpSession with 
	 * the new information after the servlet method returns.
	 */
	public void testInsert() {
		HttpSession session = new DummySession();
		session.setAttribute("name", "akshay");
		TestServlet s = new TestServlet();
		try {
			AbstractSessionView ses = s.getSessionView();
			ses.fillSessionView(session);
			s.doPost(null, null, ses, null);
			ses.fillHttpSession(session);
		} catch (InstantiationException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (ServletException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		}
		
		assertTrue(((File)session.getAttribute("mailbox")).getAbsolutePath().equals("/"));
		assertTrue(((String)session.getAttribute("name")).equals("krishnamurthy"));
	}
}
