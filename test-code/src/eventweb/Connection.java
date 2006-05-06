package eventweb;

import java.net.Socket;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;

public class Connection {
	File base;
	Socket sock;
	PublicFileServer pfs;
	MonkeyServer monkey;
	
	enum State {
		NEW;
	}
	
	State state;
	BufferedReader sockReader;
	OutputStream sockOut;
	PrintStream debugOut;
	
	Connection(File base, Socket sock, PrintStream debugOut) {
		this.base = base;
		this.sock = sock;
		this.debugOut = debugOut;
		
		pfs = new PublicFileServer(base);
		monkey = new MonkeyServer();
		state = State.NEW;
		try {
			sockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			sockOut = sock.getOutputStream();
		} catch (IOException ioe) {
			debugOut.println("can't get input and output streams from socket!");
			ioe.printStackTrace(debugOut);
		}
	}
	
	void newData() 
	{
		String nextLine;
		try {
			nextLine = sockReader.readLine();
		
			while (!nextLine.startsWith("GET")) {
				nextLine = sockReader.readLine();
			}
			
			System.out.println(nextLine);

			/* Service the request */
			
			nextLine = nextLine.substring(4);
			int nextSpace = nextLine.indexOf(" ");
			if (nextSpace >= 0) {
				nextLine = nextLine.substring(0, nextSpace);
			}

			System.out.println("Requested resource \"" + nextLine + "\"");

			if (nextLine.startsWith("/dynamic/monkey")) {
				monkey.serve(nextLine, sockOut);
			} else {
				pfs.serve(nextLine, sockOut);
			}
			
			sock.close();
		} catch (SocketTimeoutException e) {
			debugOut.println("out of input.  hooray for setSoTimeout.");
			return;
		} catch (Exception e) {
		 	e.printStackTrace();
		}
	}
}
