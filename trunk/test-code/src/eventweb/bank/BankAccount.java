package eventweb.bank;

public class BankAccount {
	String user;
	int balance; // in cents

	BankAccount(int balance, String user) {
		this.balance = balance;
		this.user = user;
	}
	
	int getBalance() {
		return balance;
	}
	
	String getUser() {
		return user;
	}
	
	int delta(int delta) {
		balance += delta;
		return balance;
	}
}
