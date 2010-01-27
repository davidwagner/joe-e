package org.joe_e.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class StaticServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	PrintWriter out = res.getWriter();

	String file = req.getPathTranslated();
	if (file == null) {
	    throw new ServletException("no static file specified");
	}

	if (file.contains("css")) {
	    res.setContentType("text/css");
	} else if (file.contains("js")) {
	    res.setContentType("script/javascript");
	} else {
	    String contentType = getServletContext().getMimeType(file);
	    res.setContentType(contentType);
	}
	try {
	    FileReader fis = null;
	    try {
		fis = new FileReader(file);
		char[] buf = new char[4*1024];
		int bytesRead ;
		while ((bytesRead = fis.read(buf, 0, 4*1024)) != -1) {
		    out.write(buf, 0, bytesRead);
		}
	    }
	    finally {
		if (fis != null) fis.close();
	    }
	} catch (FileNotFoundException e) {
	    throw new ServletException ("static file " + file + " not found");
	} catch (IOException e) {
	    throw new ServletException ("problem sending file: " + e.getMessage());
	}
    }
}