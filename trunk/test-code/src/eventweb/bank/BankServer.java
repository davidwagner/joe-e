package eventweb.bank;

import java.util.Map;
import java.util.HashMap;

import eventweb.Service;

public class BankServer implements Service {
	Map<String, BankAccount> accounts;

	// add constructor with, say, backing store for persistent state?
	public BankServer() {
		accounts = new HashMap<String, BankAccount>();
	}
	
	
	public BankSession getSession(String user) {
		BankAccount account = accounts.get(user);
		if (account == null) {
			account = new BankAccount(0, user);
			accounts.put(user, account);
		}
		
		return new BankSession(account);
	}
}
