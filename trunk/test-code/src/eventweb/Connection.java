package eventweb;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.HashMap;

public class Connection {
	Map<String, Service> services;
	Service defaultService;
	SocketChannel sc;
	PrintStream debugOut;	
	
	enum State {
		NEW;
	}
	
	State state;
	String user;
	Map<Service, ServiceSession> sessions;
	
	ByteBuffer bb;
	StringBuilder sb;
	
	CharsetEncoder ce;
	
	Connection(Map<String, Service> services, Service defaultService, SocketChannel sc, PrintStream debugOut) {
		this.services = services;
		this.defaultService = defaultService;
		this.sc = sc;
		this.debugOut = debugOut;
		
		state = State.NEW;
		user = "";
		this.sessions = new HashMap<Service, ServiceSession>();
		
		bb = ByteBuffer.allocate(4096);
		sb = new StringBuilder();
		ce = Charset.forName("US-ASCII").newEncoder();
	}
	
	int newData() 
	{	
		try {
			int numBytesRead = sc.read(bb);
			while (numBytesRead > 0) {
				debugOut.println("Read " + numBytesRead + " bytes from " + sc.socket());
				bb.flip();	// Crucial! argh.
				CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
				CharBuffer result = decoder.decode(bb);
				sb = sb.append(result);
				bb.clear();
				numBytesRead = sc.read(bb);
			} 
			
			if (numBytesRead < 0) {
				debugOut.println("Peer closed connection to " + sc.socket());
				return -1;
			}
			
			String nextLine;
			
			nextLine = readLine();
			debugOut.println("Input: " + nextLine);
			
			while (nextLine != null) {				
				while (!nextLine.startsWith("GET")) {
					nextLine = readLine();
					debugOut.println("Input: " + nextLine);
					if (nextLine == null) {
						return 0;
					}
				}
				
				/* Service the request */
				
				nextLine = nextLine.substring(4);
				int nextSpace = nextLine.indexOf(" ");
				if (nextSpace >= 0) {
					nextLine = nextLine.substring(0, nextSpace);
				}
		
				debugOut.println("Requested resource \"" + nextLine + "\"");
		
				HTTPResponse response;
				
				if (nextLine.startsWith("/login/")) {
					user = nextLine.substring("/login/".length());
					sessions = new HashMap<Service, ServiceSession>();
					response = new HTTPResponse(200, "Logged in as " + user + ".");
				} else {
					Service service = null;
					
					for (String k : services.keySet()) {
						if (nextLine.startsWith(k)) {
							service = services.get(k);
						}
					}
					
					if (service == null) {
						service = defaultService;
					}
							
					ServiceSession session = sessions.get(service);
					if (session == null) {
						session = service.getSession(user);
						sessions.put(service, session);
					}
					
					response = session.serve(nextLine, debugOut);
				}
				
				writeLine("HTTP/1.1 " + response.code + " " + HTTPResponse.describe(response.code));
				if (response.content == null) {
					writeLine("");
				} else {
				   writeLine("Content-Length: " + response.content.length);
				   writeLine("");
				   writeBytes(response.content);
				}
				/*
				byte[] content;
				if (nextLine.startsWith("/dynamic/monkey")) {
					content = monkey.serve(nextLine);
				} else {
					content = pfs.serve(nextLine);
				}
				*/				
		
			}
			
		} catch (Exception e) {
		 	e.printStackTrace();
		}
		
		return 0;
	}

	String readLine() {
		int	firstNewLine = sb.indexOf("\r\n"); 
		if (firstNewLine == -1) {
			return null;
		} else {
			String nextLine = sb.substring(0, firstNewLine);
			sb.delete(0, firstNewLine + 2);
			// debugOut.println("Remaining: " + sb);
			return nextLine;
		}	
	}
	
	void writeLine(String line)
	{
		try{
			ByteBuffer out = ce.encode(CharBuffer.wrap(line + "\r\n"));
			sc.write(out);
		} catch (Exception e) {
			e.printStackTrace(debugOut);
		}
	}
	
	void writeBytes(byte[] content) {
		try {
			ByteBuffer out = ByteBuffer.wrap(content);
			sc.write(out);
		} catch (Exception e) {
			e.printStackTrace(debugOut);
		}
	}
}
