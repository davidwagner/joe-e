package eventweb;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.io.PrintStream;

//
// has only per-HTTP session state.
//

public class MonkeyServer {
	int bananas;
	CharsetEncoder ce;
	MonkeyServer() {
		ce = Charset.forName("ISO-8859-1").newEncoder();
		bananas = 0;
	}
	
	HTTPResponse serve(String fileName, PrintStream debugOut) 
	{
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(bananaMessage(bananas) + "\n");
			int displayBananas = (bananas > 14) ? 14 : bananas;
			sb.append("Monkey has eaten " + displayBananas + " bananas.\n");
			if (bananas > 14) {
				sb.append(bananas - displayBananas + " unrequited bananas lie uneaten in the gore.\n");
				sb.append("Reload to waste another banana.\n");
			} else {
				sb.append("Reload it to feed it another banana.\n");
			}
			CharBuffer cb = CharBuffer.wrap(sb);
			ByteBuffer out = ce.encode(cb);
			++bananas;
			return new HTTPResponse(200, out.array());
		} catch (Exception e) {
			e.printStackTrace(debugOut);
			return null;
		}
	}
	
	String bananaMessage(int bananas) {
		if (bananas == 0) {
			return "Starving Monkey!";
		} else if (bananas < 3) {
			return "Hungry Monkey.";
		} else if (bananas < 6) {
			return "Sated Monkey.";
		} else if (bananas < 10) {
			return "Stuffed Monkey.";			
		} else if (bananas < 14) {
			return "Sick Monkey.";
		} else if (bananas < 15) {
			return "KABOOM!";
		} else if (bananas < 18) {
			return "Splatted Monkey.";
		} else if (bananas < 19) {
			return "Stupid Monkey.";
		} else {
			return "Stupid Monkey.  (I mean you, not the dead one.)";
		}
	}
}
