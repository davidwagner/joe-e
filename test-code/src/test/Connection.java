package test;

import java.net.Socket;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Connection {
	File base;
	Socket sock;
	
	Connection(File base, Socket sock) {
		this.base = base;
		this.sock = sock;
	}
	
	void handle() {
		try{
			InputStream sockIn = sock.getInputStream();
			OutputStream sockOut = sock.getOutputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sockIn));
		
			String nextLine = br.readLine();
		
			while (!nextLine.startsWith("GET")) {
				nextLine = br.readLine();
			}
			
			System.out.println(nextLine);

			/* Service the request */
			
			nextLine = nextLine.substring(4);
			int nextSpace = nextLine.indexOf(" ");
			if (nextSpace >= 0) {
				nextLine = nextLine.substring(0, nextSpace);
			}

			System.out.println("Requested resource \"" + nextLine + "\"");

			File toServe = findFile(nextLine);
			
			if (toServe == null) {
				PrintStream ps = new PrintStream(sockOut);
				ps.println("<html><head><title>Not found.</title></head><body>");
				ps.println("<p>Error: Requested resource " + nextLine + " not found.</p>" + 
						   "<p>Please check the URL.</p>");
				ps.println("</body></html>");
				sockOut.flush();
			} else {
				System.out.println("Serving " + toServe.getAbsolutePath());

				// log here
			
				if (toServe.isDirectory()) {
					PrintStream ps = new PrintStream(sockOut);
					
					ps.println("<html><head><title>Directory of " + nextLine 
							   + "</title></head></title>"); 
	
					ps.println("<h2>Directory of " + nextLine + "</h2>");
					
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
						sockOut.write(nextByte);
						nextByte = fis.read();
					}
				}
			}
			
			sock.close();
			
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
