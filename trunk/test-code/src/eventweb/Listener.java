package eventweb;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.PrintStream;

public class Listener {
	ServerSocketChannel ssc;
	PrintStream debugOut;
	Map<String, Service> services;
	Service defaultService;
	
	Listener(ServerSocketChannel ssc, PrintStream debugOut, Map<String, Service> services, Service defaultService) 
	{
		this.ssc = ssc;
		this.debugOut = debugOut;
		this.services = services;
		this.defaultService = defaultService;
	}
	
	int listen() {
		try {
			Selector selector = Selector.open();
		
			ssc.configureBlocking(false);
			
			SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);			
			Map<SelectionKey, Connection> connectionMap = new HashMap<SelectionKey, Connection>();
			
			boolean quit = false;
			while(!quit) {
				int numEvents = selector.select();		
				
				if (numEvents == 0) {
					// TODO: wait a while before polling again?
				} else {
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> iter = keys.iterator();
					while(iter.hasNext()) {
						SelectionKey k = iter.next();
						iter.remove();
						if (k == sscKey) {
							SocketChannel newChan = ssc.accept();
							while (newChan != null) {
								debugOut.println("Accepting connection to " + newChan.socket().toString());
								newChan.configureBlocking(false);
								SelectionKey newKey = newChan.register(selector, SelectionKey.OP_READ);
	
								connectionMap.put(newKey, 
										new Connection(services, defaultService, newChan, debugOut));
								
								// another?
								newChan = ssc.accept();
							}
						} else {
							int status = connectionMap.get(k).newData();
							if (status < 0) {
								k.cancel();
							}
							
							// SelectableChannel sc = (k.channel());
							// if (sc instanceof SocketChannel) {
								
								//processNewData(k, debugOut);
							// } else {
							//	System.out.println("wtf mates? not a socket!");
							// }							
						}
					}
				}
			}
		
			return 0;
		
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			return 10;
		}
	}
}
