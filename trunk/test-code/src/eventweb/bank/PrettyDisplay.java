package eventweb.bank;

public class PrettyDisplay {
	final TransactionReply tr;
	
	PrettyDisplay(TransactionReply tr) {
		this.tr = tr;
	}
	
	CharSequence display() {
		BankAccountSnapshot bas = tr.snapshot;
		String status = tr.status;

		StringBuilder sb = new StringBuilder("<html>\n<head><title>");
		sb.append(bas.user + "'s Bank Account</title>\n");
		sb.append("<body>\n<h1><i>First Bank of Ephemera</i></h1>\n");
		sb.append("<p>Imagine pretty logos and stuff here...</p>\n");
		sb.append("<p><b>Account details for " + bas.user + ".</p>\n");
		sb.append(String.format("<p>Current account balance: <b>%.2f</b></p>\n", 
				 bas.balance / 100.0));
		if (status.length() > 0) {
			sb.append("<table border=0 bgcolor=#f0f0ff width=100%>\n<tr><td>");
			sb.append("<font size=+1><b>" + status + "</b></font>\n");
			sb.append("</td></tr></table>\n");
		} 
		sb.append("<table border=1 bgcolor=#f0f0f0 width=100%>\n");
		sb.append("<tr><td colspan=2>Recent transactions</td></tr>\n");
	
		String transactions = bas.transactionHistory;
		int start = 0;
		int nextLine = transactions.indexOf('\n');
		while (nextLine >= 0) {
			String line = transactions.substring(start, nextLine);
			int comma = line.lastIndexOf(',');
			sb.append("<tr><td>" + line.substring(0, comma) + "</td><td><b>" + line.substring(comma + 1) 
					  + "</b></td></tr>\n");
			start = nextLine + 1;
			nextLine = transactions.indexOf('\n', start);
		}
		
		sb.append("</table>\n");
		sb.append("</head></html>\n");
		
		return sb;
	}
}
