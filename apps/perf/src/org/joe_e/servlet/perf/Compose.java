package org.joe_e.servlet.perf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Compose extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		PrintWriter out = res.getWriter();
		HtmlWriter.printHeader(out);
		if (session.getAttribute("username") == null) {
			res.sendRedirect("/perf/login");
		}
		out.println("<body><h6>signed in as " + req.getAttribute("username") + "</h6>");
		out.println("<h4>Compose Email</h4>");
		if (session.getAttribute("errorMessage") != null) {
			out.println("<b>" + session.getAttribute("errorMessage") + "</b>");
			session.setAttribute("errorMessage", null);
		}
		out.println("<form method=\"POST\" action=\"/perf/compose\">");
		out.println("<table border=\"0\">");
		out.println("<tr><td>To:</td><td><input type=\"text\" value=\"\" name=\"to\" /></td></tr>");
		out.println("<tr><td>Subject:</td><td><input type=\"text\" value=\"\" name=\"subject\" /></td></tr>");
		out.println("<tr><td>Body:</td><td><textarea name=\"body\"></textarea></td></tr>");
		out.println("<tr><td><input type=\"submit\" value=\"send\" name=\"send\" /></td></tr>");
		out.println("</table></body>");
		HtmlWriter.printFooter(out);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.getAttribute("username") == null) {
			res.sendRedirect("/perf/login");
		}
		
		String to = req.getParameter("to");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		if (to == null || subject == null || body == null
				|| to.equals("") || subject.equals("") || body.equals("") ) {
			session.setAttribute("errorMessage", "Please fill out all fields");
			res.sendRedirect("/perf/compose");
			return;
		}
		
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "10025");
		
		Session mailSession = Session.getDefaultInstance(props, null);
		javax.mail.Message msg = new MimeMessage(mailSession);
		try {
			msg.setText(body);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress(session.getAttribute("username") + "@boink.joe-e.perf.org"));
			msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		} catch (Exception e) {
			session.setAttribute("errorMessage", "There something wrong, please try again");
			res.sendRedirect("/perf/compose");
			//doGet(request, response);
			return;
		}
		
		try {
	        Transport.send(msg);
		} catch (Exception e) {
			session.setAttribute("errorMessage", "error in sending");
			res.sendRedirect("/perf/compose");
			return;
		}
		res.sendRedirect("/perf/inbox");
	}

}
