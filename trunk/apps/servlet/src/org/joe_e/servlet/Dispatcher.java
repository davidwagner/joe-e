package org.joe_e.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Dispatcher extends HttpServlet {

	private HashMap<String, JoeEServlet> map;
	
	public void init() throws ServletException {
		// TODO: get policy.xml and parse it
		// TODO: this is currently a naive policy.xml file
		// set up data structure containing url->servlet mappings
		// and set up the data structure with servlet->policy mappings
		try {
			File policy = new File((String) getServletConfig().getInitParameter("policy"));
			if (policy.exists()) {
				this.parsePolicy(policy);
				for (String s : map.keySet()) {
					getServletConfig().getServletContext().log(s + ": " + map.get(s).toString());
				}
			}
		} catch (ServletException e) {
			getServletConfig().getServletContext().log("caught exception... probably due to class loader issues");
			throw new ServletException();
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		// 1. figure out what servlet to dispatch to
		getServletConfig().getServletContext().log("request for: " + req.getServletPath());
		String dispatchTo = req.getServletPath();
		
		req.getSession().setAttribute("name", "Akshay");
		// 2. subset req.getSession()
		// TODO: need to check that this is fine
		JoeEServlet servlet = map.get(dispatchTo);
		SessionView s = null;
		try {
			s = servlet.getSessionView();
			if (s != null) {
				s.fillSessionView(req.getSession());
			}
		} catch(IllegalAccessException i) {
			throw new ServletException();
		} catch(InstantiationException i) {
			getServletConfig().getServletContext().log(i.getMessage());
			throw new ServletException();
		} catch(InvocationTargetException i) {
			throw new ServletException();
		}
		
		// TODO: 3. pull out the print writer
		// TODO: 4. call appServlet.doGet(sessionSubset, printWriter)
		getServletConfig().getServletContext().log("session type: " + s.getClass());
		servlet.doGet(req, response, s);	
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
	}
	
	private void parsePolicy(File policy) throws ServletException {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(policy, new PolicyHandler());
		} catch (SAXException s) {
			getServletConfig().getServletContext().log("SAXException " + s.getMessage());
			throw new ServletException();
		} catch (ParserConfigurationException p) {
			getServletConfig().getServletContext().log("ParserConfigurationException " + p.getMessage());
			throw new ServletException();
		} catch (IOException i) {
			getServletConfig().getServletContext().log("couldn't read policy file");
			throw new ServletException();
		}
	}
	
	private class PolicyHandler extends DefaultHandler {
		
		HashMap<String, String> servletMappings = new HashMap<String, String>();
		String servletName = null;
		boolean inServletName = false;
		String servletClass = null;
		boolean inServletClass = false;
		String urlPattern = null;
		boolean inUrlPattern = false;
		
		public void startDocument() {
			map = new HashMap<String, JoeEServlet> ();
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
					map.put(urlPattern, (JoeEServlet) cl.newInstance());
				} catch (ClassNotFoundException c) {
					getServletConfig().getServletContext().log("caught exception... probably due to class loader issues");
					throw new SAXException();
				} catch (InstantiationException i) {
					getServletConfig().getServletContext().log("caught exception... probably due to class loader issues");
					throw new SAXException();
				} catch (IllegalAccessException a) {
					getServletConfig().getServletContext().log("caught exception... probably due to class loader issues");
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
			}
		}
		
		public void endDocument() {
		}
	}
}
