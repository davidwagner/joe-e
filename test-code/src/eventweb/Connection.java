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
	
	enum State implements org.joe_e.Powerless, org.joe_e.Equatable {
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
				// TODO: 'contents', if any present, may not be in ASCII; need to walk byte
				// stream to see how much to decode (to first \r\n\r\n), for which I'd have to write
				// my own grep code.  Not happening today.
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
			
			String nextRequest = readRequest();
			
			while (nextRequest != null) {
				debugOut.println("HTTP Request:\n" + nextRequest);
				HTTPRequest request = new HTTPRequest(nextRequest);
			
				String requestURI = request.requestURI;
				debugOut.println("Requested resource \"" + requestURI + "\"");

				HTTPResponse response;
				
				if (requestURI.startsWith("/login/")) {
					user = requestURI.substring("/login/".length());
					sessions = new HashMap<Service, ServiceSession>();
					response = new HTTPResponse(200, "Logged in as " + user + ".");
				} else {
					Service service = null;
				
					for (String k : services.keySet()) {
						if (requestURI.startsWith(k)) {
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
				
					response = session.serve(request, debugOut);
				}
							
				writeLine("HTTP/1.1 " + response.code + " " + HTTPResponse.describe(response.code));
				writeBytes(response.headers);
				if (response.content == null) {
					writeLine("");
				} else {
					writeLine("Content-Length: " + response.content.length);
					writeLine("");
					writeBytes(response.content);
				}
				
				nextRequest = readRequest();
			}
			
			debugOut.println("End of input lines.");			
			
		} catch (Exception e) {
		 	e.printStackTrace();
		}
		
		return 0;
	}

	String readRequest() {
		int	firstNewRequest = sb.indexOf("\r\n\r\n"); 
		if (firstNewRequest == -1) {
			return null;
		} else {
			String nextLine = sb.substring(0, firstNewRequest + "\r\n".length());
			sb.delete(0, firstNewRequest + "\r\n\r\n".length());
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
