package org.joe_e.servlet.perf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReadTest extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		res.addHeader("Content-type", "text/html");
		HtmlWriter.printHeader(out);
		out.println("<body><h2>Joe-E Mail</h2>");
		out.println("<h4> inbox of  p1 </h4>");
		
		out.println("<a href=\"/perf/compose\">Write an email</a><br />");

		File maildir = null, newFolder = null;
		maildir = new File("/var/mail/vhosts/boink.joe-e.org/p1");
		newFolder = new File(maildir, "new");
		
		for (File f : newFolder.listFiles()) {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = "";
			String id = f.getName();
			String subject = "";
			while ((line = reader.readLine()) != null) {
				if (line.length() > 7 && line.substring(0,7).equals("Subject")) {
					subject = line.substring(8);
				}
			}
			if (!"".equals(id) && !"".equals(subject)) {
				out.println("<a href=\"/perf/read?id="+id+"\">"+subject+"</a><br />");
			}
		}
		
		out.println("<a href=\"/perf/logout\">logout</a><br />");
		out.println("</body>");
		HtmlWriter.printFooter(out);
	}
}
