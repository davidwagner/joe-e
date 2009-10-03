package org.joe_e.servlet.mail.notjoe_e;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Mechanism for interacting with Postfix in a secure way. This class can not be created
 * by Joe-E code, but it's instance methods can be accessed by Joe-E.
 * 
 * Provides methods for adding virtual accounts to the postfix database
 * 
 * Note: this class is not Joe-E code.
 * @author akshay
 *
 * @TODO: validate names of users to write to the virtual_mailbox_recipients
 */
public class PostfixClient {
	
	private final String recipients = "/etc/postfix/virtual_mailbox_recipients";
	private final String hostname = "boink.joe-e.org";

	/**
	 * Updates the postfix virtual mailboxes database
	 * This method scans all the files in the accounts directory
	 * and checks that they are in /etc/postfix/virtual_mailbox_recipients
	 * if the username is not there, it will add an entry to that file and
	 * run sudo postmap /etc/postfix/virtual_mailbox_recipients
	 * @deprecated
	 * 
	 */
	public void updateDatabase(File accounts) {
		
	}
	
	/**
	 * Adds the specified username to the virtual_mailbox_recipients file
	 * and runs the postmap command
	 * 
	 * @param accounts
	 * @param username
	 */
	public boolean updateDatabase(String username) {
		// TODO: again do we have to verify that username is safe?
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(recipients, true));
			writer.write(username+"@"+hostname+"\t"+hostname+"/"+username+"/Maildir/");
			writer.newLine();
			writer.close();
			
			Process p = Runtime.getRuntime().exec("/usr/sbin/postmap /etc/postfix/virtual_mailbox_recipients");
			p.waitFor();
			if (p.exitValue() == 0) {
				return true;
			}
			return false;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * for testing purposes.
	 * @param args
	 */
	public static void main (String[] args) {
		PostfixClient client = new PostfixClient();
		try {
			client.updateDatabase("asdfg");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
