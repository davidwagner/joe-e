package eventweb;

import org.joe_e.Incapable;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.io.PrintStream;

public class StatelessServer implements Incapable {
	CharsetEncoder ce;
	StatelessServer() {
		ce = Charset.forName("ISO-8859-1").newEncoder();
	}
	
	HTTPResponse serve(String fileName, PrintStream debugOut) 
	{
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("Monkey see, monkey do!\n");
			sb.append(fileName.substring(16) + " is made of poo!\n"); //TODO: avoid out of bounds here
			CharBuffer cb = CharBuffer.wrap(sb);
			ByteBuffer out = ce.encode(cb);
			return new HTTPResponse(200, out.array());
		} catch (Exception e) {
			e.printStackTrace(debugOut);
			return null;
		}
	}
}
