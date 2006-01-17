package test;

import java.io.File;
import java.net.Socket;
import java.net.ServerSocket;

public class Listener {
	public static void main (String[] args) {
		File base = new File("/home/adrian/website");
		int port = 8080;
		
		System.out.println("Web server starting on port " + port + " serving " 
				           + base.getAbsolutePath() + ".");
	
		try {
			ServerSocket ssock = new ServerSocket(8080);
			
			while (true) {
				Socket sock = ssock.accept();
				
				Connection worker = new Connection(base, sock);
				worker.handle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
