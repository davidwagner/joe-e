package org.joe_e.servlet.perf;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMMicrobenchmark extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element root = doc.createElement("html");
			doc.appendChild(root);

			Element head = doc.createElement("head");
			Element title = doc.createElement("title");
			head.appendChild(title);
			title.appendChild(doc.createTextNode("Joe-E Mail"));
			root.appendChild(head);

			Element body = doc.createElement("body");
			root.appendChild(body);

			for (int i = 0; i < 100; i++) {
				Element tmp = doc.createElement("p");
				Element link = doc.createElement("a");
				link.setAttribute("href", "http://www.google.com");
				tmp.appendChild(doc.createTextNode("blah blah blah"));
				link.appendChild(doc.createTextNode("google"));
				body.appendChild(tmp);
				body.appendChild(link);
			}

			
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			if (xmlString != null) {
				res.addHeader("Content-type", "text/html");
				res.getWriter().write(xmlString);
				res.flushBuffer();
			}
		} catch (ParserConfigurationException p) {
			throw new ServletException("Parser config exception");
		} catch (TransformerConfigurationException c) {
			throw new ServletException ("Transformer configuration exception");
		} catch (TransformerException t) {
			throw new ServletException ("Transformer exception");
		}
	}
}
