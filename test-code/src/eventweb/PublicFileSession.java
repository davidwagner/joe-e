package eventweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class PublicFileSession implements ServiceSession {
	File base;
	
	PublicFileSession(File base) {
		this.base = base;
	}
	
	public HTTPResponse serve(HTTPRequest request, PrintStream debugOut) 
	{
		try {
			String fileName = request.requestURI;
			File toServe = findFile(fileName);
		
			if (toServe == null) {
				debugOut.println("404'ed!");
				StringBuilder errorMsg = new StringBuilder("<html>\n<head><title>404 File not found.</title></head>\n<body>\n");
				errorMsg.append("<h1>404 File not found.</h1>");
				errorMsg.append("<p>Error: Requested resource " + fileName + " not found.</p>\n");
				errorMsg.append("<p>Please check the URL.</p>\n</body></html>\n");
				return new HTTPResponse(404, errorMsg);
			} else {
				debugOut.println("Serving " + toServe.getAbsolutePath());
			
				// log here
		
				if (toServe.isDirectory()) {
					if (fileName.endsWith("/")) {
						StringBuilder sb = new StringBuilder("<html>\n<head><title>Directory of "
								+ fileName + "</title></head>\n<body>\n"); 
						
						if (fileName.length() > 1) {
							sb.append("<a href=\"..\">Parent directory</a>");
						}
						
						sb.append("<h2>Directory of " + fileName + "</h2>\n");
						sb.append("<ul>");
					
						File[] contents = toServe.listFiles();
						for (File f : contents) {
							String dirEntry = f.getName();
							if (f.isDirectory()) {
								dirEntry += "/";
							}
							sb.append("<li><a href=\"" + dirEntry + "\">"
									+ dirEntry + "</a></li>\n");
						}
				
						sb.append("</ul>");
						sb.append("</body>\n</html>");
						return new HTTPResponse(200, sb);
					} else { // redirect to version with "/"
						String host = request.headers.get("Host").trim();
						String redirectName = "http://" + host + fileName + "/";
						String msg = "<html>\n<head><title>301 Moved Permanently</title>\n"
								+ "</head><body>\n<h1>Moved Permanently</h1>\n<p>Use a trailing slash for "
								+ "directories, like <a href=\"" + redirectName + "\">" + redirectName + "</a>.\n"
								+ "</body></html>";

						return new HTTPResponse(301, new String[]{"Location:" + redirectName}, msg);
					}
						
					
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

		// invariant: remaining URL starts with '/' or is empty
		while (current != null && fileName.length() > 0) {
			if (!current.isDirectory()) {
				return null;
			}
			
			int i = 0;
			while (i < fileName.length() && fileName.charAt(i) == '/') {
				++i;
			}
		
			fileName = fileName.substring(i);
			
			if (fileName.length() == 0) {
				break;
			}
			
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
