package simpleweb;

import java.io.File;
import java.net.Socket;
import java.net.ServerSocket;


public class Listener {
	public static void main (String[] args) {
		final File SERVE_BASE = new File("/home/adrian/website");
		final int SERVE_PORT = 8080;
		
		System.out.println("Web server starting on port " + SERVE_PORT + " serving " 
				           + SERVE_BASE.getAbsolutePath() + ".");
	
		try {
			ServerSocket ssock = new ServerSocket(SERVE_PORT);
			
			while (true) {
				Socket sock = ssock.accept();
				
				Connection worker = new Connection(SERVE_BASE, sock);
				worker.handle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
