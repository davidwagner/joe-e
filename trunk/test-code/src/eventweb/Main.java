package eventweb;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import eventweb.bank.BankServer;

public class Main {

	public static void main(String[] args) {
		serve(System.out);
	}
	
	public static int serve(PrintStream debugOut) {
		final PublicFileServer pfs = new PublicFileServer(new File("/home/adrian/website"));

		final Map<String, Service> services = new HashMap<String, Service>();

		final MonkeyServer monkey = new MonkeyServer();
		services.put("/monkey", monkey);
		final BankServer bank = new BankServer();
		services.put("/bank", bank);
		
		final int SERVE_PORT = 13579;		
		
		ServerSocketChannel ssc;
		try {
			// Attach selector to server port to listen for incoming connections
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(SERVE_PORT));  // port on default interface (not just local!)
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			System.err.println("Couldn't create a server socket channel!");
			return -20;
		}
		
		Listener list = new Listener(ssc, debugOut, services, pfs);
		return list.listen();
	}
}
