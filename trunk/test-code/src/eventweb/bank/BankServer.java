package eventweb.bank;

import java.util.Map;
import java.util.HashMap;

import eventweb.Service;

public class BankServer implements Service {
	private Map<String, BankAccount> accounts;

	// TODO: add constructor with, say, backing store for persistent state?
	// Add atomic transactions? 
	
	public BankServer() {
		accounts = new HashMap<String, BankAccount>();
	}
		
	public BankSession getSession(String user) {
		BankAccount account = accounts.get(user);
		if (account == null) {
			account = new BankAccount(0, user);
			accounts.put(user, account);
		}
		
		return new BankSession(new TransactionSubmitter(this, account));
	}
	
	TransactionReply deposit(BankAccount account, int amount, String fundingSource) {
		if (!accounts.containsValue(account)) {
			return new TransactionReply("Deposit failed; invalid account.", null);
		} else {
			account.postTransaction("Deposit from " + fundingSource, amount);
			return new TransactionReply("Deposit successful.", new BankAccountSnapshot(account));
		}
	}
	
	TransactionReply transfer(BankAccount account, int amount, String payee) {
		if (!accounts.containsValue(account)) {
			return new TransactionReply("Payment failed; invalid account.", null);
		} else if (amount <= 0) {
			return new TransactionReply("Payment failed; non-positive amount.", new BankAccountSnapshot(account));
		} else if (account.balance < amount) {
			return new TransactionReply("Payment failed; insufficient funds.", new BankAccountSnapshot(account));
		} else {
			BankAccount payeeAccount = accounts.get(payee);
			if (payeeAccount != null) {
				payeeAccount.postTransaction("Payment from " + account.user, amount);
				account.postTransaction("Payment to " + payee, -amount);
				return new TransactionReply("Payment successful.", new BankAccountSnapshot(account));
			} else {
				return new TransactionReply("Payment failed; payee not found.", new BankAccountSnapshot(account));
			}
		}
	}
	
	TransactionReply nop(BankAccount account) {
		if (!accounts.containsValue(account)) {
			return new TransactionReply("Update failed; invalid account.", null);
		} else {
			return new TransactionReply("", new BankAccountSnapshot(account));
		}
	}
}
