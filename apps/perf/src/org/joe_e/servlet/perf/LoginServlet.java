package org.joe_e.servlet.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		PrintWriter out = res.getWriter();
		if (session.getAttribute("username") != null) {
			res.sendRedirect("/perf/inbox");
		}
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<p>Log in</p>");
		out.println("<form method=\"POST\" action=\"/perf/login\">");
		out.println("<span>Username: <input type=\"text\" value=\"\" name=\"username\" /></span>");
		out.println("<span>Password: <input type=\"password\" value=\"\" name=\"password\" /></span>");
		out.println("<input type=\"submit\" value=\"login\"></form></body>");
		HtmlWriter.printFooter(out);
		out.flush();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String name = req.getParameter("username");
		String password = req.getParameter("password");
		File mailbox = null;
		if ((mailbox = this.authenticate(name, password)) != null) {
			session.setAttribute("username", name);
			session.setAttribute("mailbox", mailbox);
			res.sendRedirect("/perf/inbox");
		}
		else {
			res.sendRedirect("/perf/login");
		}
	}
	
	public File authenticate(String username, String password) throws ServletException {
		File accounts = new File("/Users/akshay/Desktop/perfaccounts/");
		try {
			byte[] bytes = password.getBytes();
			MessageDigest digest = MessageDigest.getInstance("md5");
			digest.update(bytes);
			String hashedPassword = new BigInteger(1, digest.digest()).toString(16);
			for (File acc : accounts.listFiles()) {
				if (acc.getName().equals(username)) {
					BufferedReader br = new BufferedReader(new FileReader(acc));
					if (hashedPassword.equals(br.readLine())) {
						return new File("/var/mail/vhosts/boink.joe-e.org/"+username);
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			throw new ServletException("no such algorithm");
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return null;
	}
}
