package simpleweb;

import java.net.Socket;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Connection {
	File base;
	Socket sock;
	PublicFileServer pfs;
	MonkeyServer monkey;
	
	Connection(File base, Socket sock) {
		this.base = base;
		this.sock = sock;
		this.pfs = new PublicFileServer(base);
		this.monkey = new MonkeyServer();
	}
	
	void handle() 
	{
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

			if (nextLine.startsWith("/dynamic/monkey")) {
				monkey.serve(nextLine, sockOut);
			} else {
				pfs.serve(nextLine, sockOut);
			}
			
			sock.close();
		} catch (Exception e) {
		 	e.printStackTrace();
		}
	}
}
