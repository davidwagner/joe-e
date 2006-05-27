package eventweb.bank;

public class TransactionReply {
	final String status;
	final BankAccountSnapshot snapshot;
	
	TransactionReply(String status, BankAccountSnapshot snapshot) {
		this.status = status;
		this.snapshot = snapshot;
	}
}
