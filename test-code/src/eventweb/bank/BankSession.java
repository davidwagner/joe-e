package eventweb.bank;

import java.io.PrintStream;

import eventweb.HTTPResponse;
import eventweb.ServiceSession;

public class BankSession implements ServiceSession {
	BankAccount account;
	
	BankSession(BankAccount account) {
		this.account = account;
	}
	
	public HTTPResponse serve(String fileName, PrintStream debugOut) {
		StringBuilder sb;
		if (fileName.startsWith("/bank/deposit/")) {
			int delta = Integer.parseInt(fileName.substring("/bank/deposit/".length()));
			account.delta(delta);
			sb = new StringBuilder("Deposited $" + String.format("%.2f", delta/100.0) + ".  Hooray.\n");
			sb.append(balanceDisplay());
		} else {
			sb = balanceDisplay();
		}
		
		try {
			return new HTTPResponse(200, sb);
		} catch (Exception e) {
			e.printStackTrace(debugOut);
			return null;
		}
	}
	
	StringBuilder balanceDisplay() {
		StringBuilder sb = new StringBuilder("Account balance for user ");
		sb.append(account.getUser() + " is: ");
		sb.append(String.format("%.2f", account.getBalance() / 100.0));
		return sb;
	}
}
