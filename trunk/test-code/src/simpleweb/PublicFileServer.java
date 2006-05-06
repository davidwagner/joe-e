package simpleweb;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileInputStream;

public class PublicFileServer {
	File base;
	
	PublicFileServer(File base) {
		this.base = base;
	}
	
	void serve(String fileName, OutputStream serveOut) 
	{
		try {
			File toServe = findFile(fileName);
		
			if (toServe == null) {
				PrintStream ps = new PrintStream(serveOut);
				ps.println("<html><head><title>Not found.</title></head><body>");
				ps.println("<p>Error: Requested resource " + fileName + " not found.</p>" + 
						   "<p>Please check the URL.</p>");
				ps.println("</body></html>");
				serveOut.flush();
			} else {
				System.out.println("Serving " + toServe.getAbsolutePath());
			
				// log here
		
				if (toServe.isDirectory()) {
					PrintStream ps = new PrintStream(serveOut);
				
					ps.println("<html><head><title>Directory of " + fileName 
							   + "</title></head></title>"); 
					
					ps.println("<h2>Directory of " + fileName + "</h2>");
					
					File[] contents = toServe.listFiles();
					for (File f : contents) {
						ps.println("<p><a href=\"" + f.getName() + "\">" 
							        + f.getName() + "</a></p>");
					}
				
					ps.println("</body></html>");
			
				} else {
					FileInputStream fis = new FileInputStream(toServe);
		
					int nextByte = fis.read();
					while (nextByte >= 0) {
						serveOut.write(nextByte);
						nextByte = fis.read();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
