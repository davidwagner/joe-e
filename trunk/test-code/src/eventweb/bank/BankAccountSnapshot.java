package eventweb.bank;

public class BankAccountSnapshot implements org.joe_e.Powerless {
	final String user;
	final int balance;
	final String transactionHistory;
	
	BankAccountSnapshot(BankAccount account) {
		this.user = account.user;
		this.balance = account.balance;
		this.transactionHistory = account.transactionHistory;
	}
}
