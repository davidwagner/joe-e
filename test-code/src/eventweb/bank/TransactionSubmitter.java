package eventweb.bank;

public class TransactionSubmitter {
	private final BankServer server;
	private final BankAccount account;
	
	TransactionSubmitter(BankServer server, BankAccount account) {
		this.server = server;
		this.account = account;
	}
	
	TransactionReply deposit(int amount, String source) {
		return server.deposit(account, amount, source);
	}
	
	TransactionReply transfer(int amount, String payee) {
		return server.transfer(account, amount, payee);
	}
	
	TransactionReply nop() {
		return server.nop(account);
	}
	
	
}
