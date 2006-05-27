package eventweb.bank;

import java.io.PrintStream;

import eventweb.HTTPRequest;
import eventweb.HTTPResponse;
import eventweb.ServiceSession;

public class BankSession implements ServiceSession {
	final TransactionSubmitter ts;
	
	BankSession(TransactionSubmitter ts) {
		this.ts = ts;
	}
	
	public HTTPResponse serve(HTTPRequest request, PrintStream debugOut) {
		String fileName = request.requestURI;
		
		TransactionReply reply;
		if (fileName.startsWith("/bank/print/")) {
			int delta = Integer.parseInt(fileName.substring("/bank/print/".length()));
			reply = ts.deposit(delta, "my very own virtual printing press");		
		} else if (fileName.startsWith("/bank/transfer/")) {
			int nextSlash = fileName.indexOf("/", "/bank/transfer/".length());
			if (nextSlash > 0) {
				String payee = fileName.substring("/bank/transfer/".length(), nextSlash);
				int amount = Integer.parseInt(fileName.substring(nextSlash + 1));
				reply = ts.transfer(amount, payee);
			} else {
				TransactionReply nopReply = ts.nop();
				reply = new TransactionReply("Transfer request parsing failed.", nopReply.snapshot);
			}
		} else {
			reply = ts.nop();
		}
		
		CharSequence webpage = new PrettyDisplay(reply).display();
		
		try {
			return new HTTPResponse(200, webpage);
		} catch (Exception e) {
			e.printStackTrace(debugOut);
			return null;
		}
	}
}
