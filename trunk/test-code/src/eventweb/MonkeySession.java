package eventweb;

import java.io.PrintStream;

public class MonkeySession implements ServiceSession {

	int bananas;
	
	MonkeySession() {
		bananas = 0;
	}
	
	public HTTPResponse serve(HTTPRequest request, PrintStream debugOut) 
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
				sb.append("Reload page to feed it another banana.\n");
			}
			++bananas;
			return new HTTPResponse(200, sb);
		} catch (Exception e) {
			e.printStackTrace(debugOut);
			return null;
		}
	}
	
	String bananaMessage(int bananas) {
		if (bananas == 0) {
			return "Starving Monkey.";
		} else if (bananas < 3) {
			return "Hungry Monkey.";
		} else if (bananas < 6) {
			return "Sated Monkey.";
		} else if (bananas < 10) {
			return "Stuffed Monkey.";			
		} else if (bananas < 14) {
			return "Sick Monkey.";
		} else if (bananas < 15) {
			return "Banana pressure critical!  She's gonna blow! . . . BOOM!";
		} else if (bananas < 18) {
			return "Splatted Monkey.";
		} else if (bananas < 19) {
			return "Stupid Monkey.";
		} else {
			return "Stupid Monkey.  (I mean you, not the splode.)";
		}
	}
}
