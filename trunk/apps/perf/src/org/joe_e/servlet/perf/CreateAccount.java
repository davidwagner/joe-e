package org.joe_e.servlet.perf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
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

import org.joe_e.servlet.perf.HtmlWriter;

public class CreateAccount extends HttpServlet {
	
	private static final String subject = "Welcome to Joe-E Mail Performance Experiement";
	private static final String body = "Welcome to Joe-E Mail Performance Experiment";
	private static final String hostname ="boink.joe-e.org";
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		PrintWriter out = res.getWriter();
		if (session.getAttribute("username") != null) {
			res.sendRedirect("/perf/inbox");
		}
		res.addHeader("Content-type", "text/html");
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<p>Create Account</p>");
		out.println("<form method=\"POST\" action=\"/perf/create\">");
		out.println("<span>Choose a username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Choose a password: <input type=\"password\" value=\"\" name=\"password1\" /></span>");
		out.println("<span>Re-enter password: <input type=\"password\" value=\"\" name=\"password2\" /></span>");
		out.println("<input type=\"submit\" value=\"create\"></form></body>");
		HtmlWriter.printFooter(out);
		out.flush();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String name = req.getParameter("username");
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		if (password1.equals(password2) && this.addAccount(name, password1)) {
			res.sendRedirect("/perf/login");
		} else {
			res.sendRedirect("/perf/create");
		}
	}

	public boolean addAccount(String name, String password) {
		File accounts = new File("/Users/akshay/Desktop/accounts/");
		for (File f : accounts.listFiles()) {
			if (f.getName().equals(name)) {
				return false;
			}
		}
		try {
			File newAcc = new File(accounts, name);
			newAcc.createNewFile();
			byte[] bytes = password.getBytes();
			MessageDigest digest = MessageDigest.getInstance("sha");
			digest.update(bytes);
			String hashedPassword = new BigInteger(1, digest.digest()).toString(16);
			Writer out = new FileWriter(newAcc);
			for (char c : hashedPassword.toCharArray()) {
				out.append(c);
			}
			out.flush();
			// TODO: set up postfix directory
			if (!this.initializeAccount(name)) {
				throw new ServletException();
			}
		} catch (Exception e) {
			for (File f : accounts.listFiles()) {
				if (f.getName().equals(name)) {
					f.delete();
				}
			}
			return false;
		}
		
		sendWelcomeMessage(name);
		return true;
		
		// TODO: send a welcome email
	}
	
	public boolean initializeAccount(String username) {
		String recipients = "/etc/postfix/virtual_mailbox_recipients";
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(recipients, true));
			writer.write(username+"@"+hostname+"\t"+hostname+"/"+username+"/Maildir/");
			writer.newLine();
			writer.close();
			
			Process p = Runtime.getRuntime().exec("/usr/sbin/postmap /etc/postfix/virtual_mailbox_recipients");
			p.waitFor();
			if (p.exitValue() == 0) {
				return true;
			}
			return false;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	public boolean sendWelcomeMessage(String username) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", "10025");
		
		Session mailSession = Session.getDefaultInstance(props, null);
		javax.mail.Message msg = new MimeMessage(mailSession);
		try {
			msg.setText(body);
			msg.setSubject(subject);
			msg.setFrom(new InternetAddress("akshayk@boink.joe-e.org"));
			msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(username+"@"+hostname));
		} catch (Exception e) {
			return false;
		}
		
		try {
			Transport.send(msg);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
