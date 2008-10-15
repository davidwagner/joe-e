package org.joe_e.webmail;

import java.io.*;

public class HtmlWriter {

	public static void printHeader(PrintWriter p) {
		p.println("<html><head><title>Joe-E WebMail</title></head>");
	}
	
	public static void printFooter(PrintWriter p) {
		p.println("</html>");
	}
}
