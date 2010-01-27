package org.joe_e.servlet.mail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joe_e.servlet.AbstractCookieView;
import org.joe_e.servlet.AbstractSessionView;
import org.joe_e.servlet.JoeEServlet;

/**
 * A class to test all the functionality of the BufferedPrintWriter
 * @author akshay
 *
 */
public class TestBufferedPrintWriter extends JoeEServlet {

	
	public class SessionView extends AbstractSessionView {
		public SessionView (HttpSession ses) {
			super(ses);
		}
	}
	
	public class CookieView extends AbstractCookieView {	
		public CookieView(Cookie[] c) {
			super(c);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, AbstractSessionView ses, AbstractCookieView c) 
		throws ServletException, IOException {
		PrintWriter writer = res.getWriter();
		writer.println("<html><body>");
		writer.append("1<br />");
		writer.append("2<br />".subSequence(0, 7));
		writer.append("13<br />".subSequence(0, 8), 1, 8);
		writer.format("%d%s\n", 4, "<br />");
		writer.print(true);
		writer.println("<br />");
		writer.print('5');
		writer.println("<br />");
		writer.print(new char[]{'6', '<', 'b', 'r', ' ', '/', '>'});
		writer.print(7.0);
		writer.println("<br />");
		writer.print(8.0f);
		writer.println("<br />");
		writer.print(9);
		writer.println("<br />");
		writer.print(10l);
		writer.println("<br />");
		writer.print(new Object());
		writer.println("<br />");
		writer.print("11<br />");
		writer.printf("%d%s", 12, "<br />");
		writer.println();
		writer.println("</body></html>");
	}
}
