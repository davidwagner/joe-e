package eventweb;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;

public class PublicFileServer {
	File base;
	CharsetEncoder ce;

	PublicFileServer(File base) {
		this.base = base;
		ce = Charset.forName("ISO-8859-1").newEncoder();
	}
	
	HTTPResponse serve(String fileName, PrintStream debugOut) 
	{
		try {
			File toServe = findFile(fileName);
		
			if (toServe == null) {
				return new HTTPResponse(404, new byte[]{});				
				/*
				PrintStream ps = new PrintStream(serveOut);
				ps.println("<html><head><title>Not found.</title></head><body>");
				ps.println("<p>Error: Requested resource " + fileName + " not found.</p>" + 
						   "<p>Please check the URL.</p>");
				ps.println("</body></html>");
				serveOut.flush();
				*/
			} else {
				debugOut.println("Serving " + toServe.getAbsolutePath());
			
				// log here
		
				if (toServe.isDirectory()) {
					StringBuilder sb = new StringBuilder();
					sb.append("<html>\n<head><title>Directory of " + fileName 
							  + "</title></head>\n<body>\n"); 
					
					sb.append("<h2>Directory of " + fileName + "</h2>\n");
					
					File[] contents = toServe.listFiles();
					for (File f : contents) {
						sb.append("<p><a href=\"" + f.getName() + "\">" 
						          + f.getName() + "</a></p>\n");
					}
				
					sb.append("</body>\n</html>");
					ByteBuffer out = ce.encode(CharBuffer.wrap(sb));
					return new HTTPResponse(200, out.array());
				} else {
					FileChannel fc = new FileInputStream(toServe).getChannel();
		
					long length = fc.size();
					if (length > 10 * 1024 * 1024) {	// only serve first 10 megs of file
						length = 10 * 1024 * 1024;
					}
					ByteBuffer out = ByteBuffer.allocate((int) length);
					fc.read(out);
					return new HTTPResponse(200, out.array());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param fileName string supplied in URL
	 * @return
	 */
	File findFile(String fileName) {
		File current = base;

		// invariant: remaining URL starts with
		while (current != null && fileName.length() > 0) {
			if (!current.isDirectory()) {
				return null;
			}
			
			int i = 0;
			while (i < fileName.length() && fileName.charAt(i) == '/') {
				++i;
			}
		
			fileName = fileName.substring(i);
			
			String next = fileName;
			int nextSlash = fileName.indexOf('/');
			if (nextSlash >= 0) {
				next = fileName.substring(0, nextSlash);
			}
			
			File[] contents = current.listFiles();
			current = null;
			for (File f : contents) {
				if (f.getName().equals(next)) {
					current = f;
					break;
				}
			}
			
			if (nextSlash < 0) {
				fileName = "";
			} else {
				fileName = fileName.substring(nextSlash);
			}		
		}
		
		return current;
	}	
}
