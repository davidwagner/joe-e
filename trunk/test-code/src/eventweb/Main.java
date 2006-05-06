package eventweb;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Set;
import java.util.HashMap;
import java.io.File;

public class Main {

	public static void main(String[] args) {
		serve(System.out);
	}
	
	public static int serve(PrintStream debugOut) {
		final File SERVE_BASE = new File("/home/adrian/website");
		final int SERVE_PORT = 13579;		
		final int SOCKET_TIMEOUT_MS = 10;
		try {
			Selector selector = Selector.open();
		
			// Attach selector to server port to listen for incoming connections
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(SERVE_PORT));  // port on default interface (not just local!)
			ssc.configureBlocking(false);
			
			SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);			
			HashMap<SelectionKey, Connection> connectionMap = new HashMap<SelectionKey, Connection>();
			
			boolean quit = false;
			while(!quit) {
				int numEvents = selector.select();		
				
				if (numEvents == 0) {
					// TODO: wait a while before polling again?
				} else {
					Set<SelectionKey> keys = selector.selectedKeys();
					for (SelectionKey k : keys) {
						keys.remove(k);
						if (k == sscKey) {
							SocketChannel newChan = ssc.accept();
							while (newChan != null) {
								debugOut.println("Accepting connection to " + newChan.socket().toString());
								newChan.configureBlocking(false);
								SelectionKey newKey = newChan.register(selector, SelectionKey.OP_READ);
								Socket newSocket = newChan.socket();
								newSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
								connectionMap.put(key, new Connection(SERVE_BASE, newChan.socket()));
								
								// another?
								newChan = ssc.accept();
							}
						}
						else {
							Connection c = connectionMap.get(k);
						SelectableChannel chan = k.channel();
						if (chan instanceof ServerSocketChannel) {
							ServerSocketChannel ssc = (ServerSocketChannel) chan;
							SocketChannel newChan = ssc.accept();
							while (newChan != null) {
								debugOut.println("Accepting connection to " + newChan.socket().toString());
								newChan.configureBlocking(false);
								SelectionKey newKey = newChan.register(selector, SelectionKey.OP_READ);
								
								newChan = ssc.accept();
							}
						} else if (chan instanceof SocketChannel) {
							SocketChannel sc = (SocketChannel) chan;
							processNewData(sc);
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

	void processNewData(SocketChannel sc) {
		ByteBuffer bb = ByteBuffer.allocate(4096);
		int numBytesRead = sc.read(bb);
		if (numBytesRead > 0) {
			debugOut.println("Read " + numBytesRead + " bytes from " + sc.socket());
			bb.rewind();	// Crucial! argh.
			CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
			CharBuffer result = decoder.decode(bb);
			debugOut.println("Decoded: " + result);
		} else if (numBytesRead < 0) {
			debugOut.println("Peer closed connection to " + sc.socket());
			k.cancel(); // remove from selector.
		}
	}
}
