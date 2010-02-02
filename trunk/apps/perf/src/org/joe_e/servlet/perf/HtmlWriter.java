package org.joe_e.servlet.perf;

import java.io.PrintWriter;

public class HtmlWriter {

	public static void printHeader(PrintWriter p) {
		p.println("<html><head><title>Joe-E Mail (performance testing)</title><link href=\"static/css/index.css\" rel=\"stylesheet\" type=\"text/css\"></head>");
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
