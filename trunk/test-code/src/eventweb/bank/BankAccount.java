package eventweb.bank;

public class BankAccount {
	String user;
	int balance; // in cents
	String transactionHistory;

	BankAccount(int balance, String user) {
		this.balance = balance;
		this.user = user;
		this.transactionHistory = "";
	}
	
	int delta(int delta) {
		balance += delta;
		return balance;
	}
	
	int postTransaction(String description, int delta) {
		transactionHistory += description + String.format(", %.2f\n", delta / 100.0);
		balance += delta;
		return balance;
	}
}
