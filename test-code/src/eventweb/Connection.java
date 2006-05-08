package eventweb;

import java.net.Socket;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class Connection {
	File base;
	SocketChannel sc;
	PrintStream debugOut;	
	
	PublicFileServer pfs;
	MonkeyServer monkey;
	
	enum State {
		NEW;
	}
	
	State state;
	
	ByteBuffer bb;
	StringBuilder sb;
	
	CharsetEncoder ce;
	
	Connection(File base, SocketChannel sc, PrintStream debugOut) {
		this.base = base;
		this.sc = sc;
		this.debugOut = debugOut;
		
		pfs = new PublicFileServer(base);
		monkey = new MonkeyServer();
		state = State.NEW;
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
				debugOut.println("59:result: " + result);
				debugOut.println("60:sb: length " + sb.length() + "<" + sb + ">");
				sb = sb.append(result);
				debugOut.println("62:sb: length " + sb.length() + "<" + sb + ">");
				bb.clear();
				numBytesRead = sc.read(bb);
			} 
			
			if (numBytesRead < 0) {
				debugOut.println("Peer closed connection to " + sc.socket());
				return -1;
			}
			
			String nextLine;
			
			nextLine = readLine();
			debugOut.println("A line of input: " + nextLine);
			
			while (nextLine != null) {				
				while (!nextLine.startsWith("GET")) {
					nextLine = readLine();
					debugOut.println("Additional line: " + nextLine);
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
				if (nextLine.startsWith("/dynamic/monkey")) {
					response = monkey.serve(nextLine, debugOut);
				} else {
					response = pfs.serve(nextLine, debugOut);
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
			debugOut.println("Remaining: " + sb);
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
