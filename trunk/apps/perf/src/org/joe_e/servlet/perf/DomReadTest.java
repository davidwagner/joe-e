package org.joe_e.servlet.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
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
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomReadTest extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
			Element tmp = doc.createElement("h2");
			tmp.appendChild(doc.createTextNode("Joe-E Mail"));
			body.appendChild(tmp);
			tmp = doc.createElement("h4");
			tmp.appendChild(doc.createTextNode("inbox of p1"));
			body.appendChild(tmp);
			
			tmp = doc.createElement("a");
			tmp.setAttribute("href", "/perf/compose");
			tmp.appendChild(doc.createTextNode("Write an email"));
			body.appendChild(tmp);
			body.appendChild(doc.createElement("br"));
			
			File newFolder = new File("/var/mail/vhosts/boink.joe-e.org/p1");
			for (File f : newFolder.listFiles()) {
				Reader reader = new FileReader(f);;
				BufferedReader in = new BufferedReader(reader);
				String line = "";
				String id = f.getName();
				String subject = "";
				while ((line = in.readLine()) != null) {
					if (line.equals(""))
						break;
					if (line.length() > 7 && line.substring(0, 7).equals("Subject")) {
						subject = line.substring(8);
						break;
					}
				}
				if (!"".equals(id) && !"".equals(subject)) {
					tmp = doc.createElement("a");
					tmp.setAttribute("href", "/perf/read?id="+id);
					tmp.appendChild(doc.createTextNode(subject));
					body.appendChild(tmp);
					body.appendChild(doc.createElement("br"));
				}
			}
			
			tmp = doc.createElement("a");
			tmp.setAttribute("href", "/perf/logout");
			tmp.appendChild(doc.createTextNode("logout"));
			body.appendChild(tmp);
			body.appendChild(doc.createElement("br"));
			
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
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
