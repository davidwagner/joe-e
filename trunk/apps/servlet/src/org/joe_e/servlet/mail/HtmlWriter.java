package org.joe_e.servlet.mail;

import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HtmlWriter {

	public static Element printHeader(Document doc) {
		Element root = doc.createElement("html");
		doc.appendChild(root);

		Element head = doc.createElement("head");
		Element title = doc.createElement("title");
		head.appendChild(title);
		title.appendChild(doc.createTextNode("Joe-E Mail"));
		root.appendChild(head);

		Element body = doc.createElement("body");
		root.appendChild(body);
		return body;
	}
	
	public static void printHeader(PrintWriter out) {
		out.println("<!doctype html>");
		out.println("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"><title>Joe-E Mail</title></head>");
	}
	
	public static String getHeader() {
		return "<html><head><title>Joe-E Mail</title></head>";
	}
	
	public static void printFooter(PrintWriter p) {
		p.println("</html>");
	}
	
	public static String getFooter() {
		return "</html>";
	}

}
